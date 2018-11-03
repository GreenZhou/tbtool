package com.augurit.awater.interceptor;

import com.augurit.awater.InProcessContext;
import com.augurit.awater.RequestMsg;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.User;
import com.augurit.awater.exception.AppException;
import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 说    明： 客户端请求拦截器
 * 创 建 人： 周卫鹏
 * 创建日期： 2016-10-18 22:45
 * 修改说明：
 */
public class ReqInterceptor implements HandlerInterceptor {

    private final static Logger LOGGER = Logger.getLogger(ReqInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object o) throws Exception {
		// 设置CORS跨域问题处理
		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		resp.setHeader("Access-Control-Allow-Credentials", "true");
		resp.setHeader("P3P", "CP=CAO PSA OUR");
		if (req.getHeader("Access-Control-Request-Method") != null
				&& "OPTIONS".equals(req.getMethod())) {
			resp.addHeader("Access-Control-Allow-Methods", "POST,GET,TRACE,OPTIONS");
			resp.addHeader("Access-Control-Allow-Headers", "Content-Type,Origin,Accept");
			resp.addHeader("Access-Control-Max-Age", "120");
		}

        // 上传文件请求，直接跳过
    	if(req.getMethod().equals("OPTIONS") || (!Strings.isNullOrEmpty(req.getContentType()) && req.getContentType().contains("multipart"))) {
			return true;
		}

    	String reqData = getRequestData(req);

	    LOGGER.info("接收客户端[" + req.getRemoteAddr() + "]的请求， 其请求报文：\n" + reqData);

        RequestMsg requestMsg = null;
        try {
            requestMsg = new RequestMsg(reqData);

	        // 如果是非登录接口，对其进行token校验
			if(!"/user/login".equals(req.getServletPath()) && !"/menu/list".equals(req.getServletPath())) {
				User user = (User) req.getSession().getAttribute(requestMsg.getToken());
				if(user == null) {
					throw new AppException(RespCodeMsgDepository.TOKEN_INVALID, "token无效");
				}
			}

			// 请求响应消息上下文存储请求数据
	        InProcessContext.setRequestMsg(requestMsg);

	        // 请求响应消息上下文存储请求分页数据
	        InProcessContext.setPageParameter();

        } catch (Exception e) {
	        if(e instanceof AppException) {
		        LOGGER.error(e);

		        // 记录响应数据
		        LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n"
				        + ((AppException) e).getDepository().toResponseMsg().toString());

		        respDataToClient(((AppException) e).getDepository().toResponseMsg(), req, resp);

	        } else {
		        LOGGER.error("构造请求数据对象失败...", e);

		        // 记录响应数据
		        LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n"
				        + RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg().toString());

		        respDataToClient(RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg(), req, resp);
	        }

	        return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse resp, Object o, ModelAndView modelAndView) throws Exception {
	    try {
		    ResponseMsg responseMsg = InProcessContext.getResponseMsg();
		    if(responseMsg == null) {
			    LOGGER.error("没返回响应消息对象给客户端...");

			    // 记录响应数据
			    LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n"
					    + RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg().toString());

			    respDataToClient(RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg(), req, resp);

			    return;
		    }

		    // 记录响应数据
		    LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n"
				    + Strings.nullToEmpty(InProcessContext.getResponseMsg().toString()));

		    respDataToClient(responseMsg, req, resp);

	    } catch (Exception e) {
		    LOGGER.error("返回响应数据对象失败...", e);

		    // 记录响应数据
		    LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n"
				    + RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg().toString());

		    respDataToClient(RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg(), req, resp);
	    }
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse resp, Object o, Exception e) throws Exception {

	    // 清除整个线程绑定的对象数据
        InProcessContext.clearAll();
    }

    /**
     * 从请求HTTPServlet对象中获取客户端的JSON报文
     *
     * @param req http请求对象
     * @return
     */
    private String getRequestData(HttpServletRequest req) {
        String reqData = null;

        try {
            List<String> strs = IOUtils.readLines(req.getInputStream(), Charset.forName("UTF-8"));

            StringBuilder sb = new StringBuilder();
            for(String s : strs) {
                sb.append(s);
            }

            reqData = sb.toString();

        } catch (Exception e) {
            LOGGER.error("Please ingore it...", e);
        }

        return reqData;
    }

	/**
	 * 返回客户端的JSON报文至HTTPServlet响应对象中
	 * @param responseMsg 对象确保非空
	 * @param resp HTTP响应对象
	 */
	private void respDataToClient(ResponseMsg responseMsg, HttpServletRequest req, HttpServletResponse resp) {
		try {
			resp.setCharacterEncoding("utf-8");

			IOUtils.write(responseMsg.toString(), resp.getOutputStream(), Charsets.UTF_8);
			resp.setStatus(HttpStatus.SC_OK);
			resp.getOutputStream().flush();
		} catch(Exception e) {
			// Ingore it
		}
	}
}
