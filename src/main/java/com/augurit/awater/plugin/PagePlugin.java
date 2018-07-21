package com.augurit.awater.plugin;

import com.augurit.awater.InProcessContext;
import com.augurit.awater.PageParameter;
import com.google.common.base.Strings;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 说    明： MyBatis分页插件
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-18 21:52
 * 修改说明：
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class})})
public class PagePlugin implements Interceptor {

    private static final Logger LOGGER = Logger.getLogger(PagePlugin.class);
    private String pageIdMatch;
    private String dialect;
    private static final String DEFAULT_PAGEID_MATCH = ".*List.*";
    private static final String DEFAULT_DIALECT = "MYSQL";
    private static final String DEFAULT_MYSQL_DIALECT = "MYSQL";
    private static final String DEFAULT_ORACLE_DIALECT = "ORACLE";
    private static final ObjectFactory DEFAULT_OBJECT_FACTORY = new DefaultObjectFactory();
    private static final ObjectWrapperFactory DEFAULT_OBJECT_WRAPPER_FACTORY = new DefaultObjectWrapperFactory();
    private static final ReflectorFactory DEFAULT_REFLECTOR_FACTORY = new DefaultReflectorFactory();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaStatementHandler = MetaObject.forObject(statementHandler, DEFAULT_OBJECT_FACTORY,
                DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);

        // 分离代理对象链
        while(metaStatementHandler.hasGetter("h")) {
            metaStatementHandler = MetaObject.forObject(metaStatementHandler.getValue("h"), DEFAULT_OBJECT_FACTORY,
                    DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }

        // target即是StatementHandler的子类, 如SimpleStatementHandler
        while(metaStatementHandler.hasGetter("target")) {
            metaStatementHandler = MetaObject.forObject(metaStatementHandler.getValue("target"), DEFAULT_OBJECT_FACTORY,
                    DEFAULT_OBJECT_WRAPPER_FACTORY, DEFAULT_REFLECTOR_FACTORY);
        }

        MappedStatement mappedStatement = (MappedStatement) metaStatementHandler.getValue("delegate.mappedStatement");

        if(mappedStatement.getId().matches(pageIdMatch)) {
            BoundSql boundSql = (BoundSql) metaStatementHandler.getValue("delegate.boundSql");
            PageParameter page = InProcessContext.getPageParameter();

            setPageParameter((Configuration) metaStatementHandler.getValue("delegate.configuration"),
                    boundSql, mappedStatement, (Connection) invocation.getArgs()[0], page);

            metaStatementHandler.setValue("delegate.boundSql.sql", buildPageSql(boundSql.getSql(), page, dialect));

            // 采用物理分页后, 不需要mybatis的内存分页了
                metaStatementHandler.setValue("delegate.rowBounds.offset", RowBounds.NO_ROW_OFFSET);
            metaStatementHandler.setValue("delegate.rowBounds.limit", RowBounds.NO_ROW_LIMIT);
        }

        return invocation.proceed();
    }

    public void setPageParameter(Configuration configuration, BoundSql boundSql, MappedStatement mappedStatement,
                                 Connection conn, PageParameter page) {
        String countSql = "select count(0) from ( " + boundSql.getSql() + " ) temp";
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
            countStmt = conn.prepareStatement(countSql);
            List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
            BoundSql countBoundSql =
                    new BoundSql(configuration, countSql, parameterMappings, boundSql.getParameterObject());

            if(parameterMappings != null) {
                for (ParameterMapping parameterMapping : parameterMappings) {
                    if (boundSql.hasAdditionalParameter(parameterMapping.getProperty())) {
                        countBoundSql.setAdditionalParameter(parameterMapping.getProperty(),
                                boundSql.getAdditionalParameter(parameterMapping.getProperty()));
                    } else {
                        //  试着从parameterObject中获取对应value
                        Object paramObj = boundSql.getParameterObject();
                        if (paramObj != null) {
                            if (paramObj instanceof Map) {
                                countBoundSql.setAdditionalParameter(parameterMapping.getProperty(),
                                        ((Map) paramObj).get(parameterMapping.getProperty()));
                            } else {
                                countBoundSql.setAdditionalParameter(parameterMapping.getProperty(), paramObj);
                            }
                        }
                    }
                }
            }

            ParameterHandler hanler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), countBoundSql);
            hanler.setParameters(countStmt);

            rs = countStmt.executeQuery();
            int totalCount = 0;
            while(rs.next()) {
                totalCount = rs.getInt(1);
            }

            page.setTotalCount(totalCount);

        } catch (SQLException e) {
            LOGGER.error("请忽略该异常信息...", e);
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    LOGGER.error("关闭ResultSet对象失败! 请忽略该异常信息...", e);
                }
            }

            if(countStmt != null) {
                try {
                    countStmt.close();
                } catch (SQLException e) {
                    LOGGER.error("关闭Statement对象失败! 请忽略该异常信息...", e);
                }
            }

        }

    }


    private String buildPageSql(String sql, PageParameter page, String dialect) {
        return DEFAULT_ORACLE_DIALECT.equalsIgnoreCase(dialect)? buildPageSqlForOracle(sql, page) : buildPageSqlForMysql(sql, page);

    }

    private String buildPageSqlForOracle(String sql, PageParameter page) {
        StringBuilder pageSql = new StringBuilder();

        pageSql.append("select * from ( ");
        pageSql.append("select temp.*, rownum row_id from ( " + sql + " ) temp where rownum <= " + (page.getCurrentIndex() + page.getShowSize()));
        pageSql.append(") where row_id > " + page.getCurrentIndex());

        return pageSql.toString();
    }

    private String buildPageSqlForMysql(String sql, PageParameter page) {
        StringBuilder pageSql = new StringBuilder();

        pageSql.append(sql);
        pageSql.append(" limit " + page.getCurrentIndex() + "," + (page.getCurrentIndex() + page.getShowSize()));

        return pageSql.toString();
    }

    @Override
    public Object plugin(Object target) {
        // 当目标类是StatementHandler类型时，才包装目标类，否者直接返回目标本身,减少目标被代理的次数
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {
        pageIdMatch = properties.getProperty("pageIdMatch");
        if(Strings.isNullOrEmpty(pageIdMatch)) {
            LOGGER.info("没有配置pageIdMatch分页拦截, 采用默认分页拦截配置");
            pageIdMatch = DEFAULT_PAGEID_MATCH;
        }

        dialect = properties.getProperty("dialect");
        if(Strings.isNullOrEmpty(dialect)) {
            LOGGER.info("没有配置pageIdMatch分页拦截, 采用默认分页拦截配置");
            dialect = DEFAULT_DIALECT;
        }
    }
}
