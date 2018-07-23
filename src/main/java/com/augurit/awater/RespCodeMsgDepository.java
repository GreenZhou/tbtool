package com.augurit.awater;

/**
 * 说    明： 响应信息
 *           里面的message不应该包含细节信息, 细节信息应该记录到日志当中，而不应该暴露给用户界面
 *           比如： 数据库插入ID重复不应该反馈到界面上，而应该用系统内部错误替代
 * 创 建 人： 周卫鹏
 * 创建日期： 2017-10-18 11:03
 * 修改说明：
 */
public enum RespCodeMsgDepository {
    SUCCESS("0000", "响应成功"),
    SERVER_INTERNAL_ERROR("0001", "服务器内部系统错误"),
    SERVER_404_ERROR("0002", "资源未找到"),
    REQUEST_DATA_ERROR("0003", "请求数据格式不正确"),
    USER_LOGIN_ERROR("0004", "用户登录失败"),
    USER_NOT_FOUND("0005", "该用户不存在"),
    USER_LOGIN_FAIL("0006", "用户名或密码错误"),
    USER_LOGOUT_FAIL("0007", "用户退出登录失败"),
    TOKEN_INVALID("0008", "token无效"),
    LACK_PRIVILEGIER("0009", "用户权限不够"),
    DELETE_TASK_ERROR("0010", "任务已经被认领分派出去，无法删除"),
    ABANDON_TASK_ERROR("0011", "任务已经被认领了，无法废弃"),
    ;

    private final String code;
    private final String message;

    RespCodeMsgDepository(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /**
     * 构造出异常信息报文
     */
    public ResponseMsg toResponseMsg() {
        return ResponseMsg.ResponseMsgBuilder.build(this, null);
    }
}