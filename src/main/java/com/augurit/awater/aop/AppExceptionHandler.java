package com.augurit.awater.aop;

import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.exception.AppException;
import com.google.common.base.Charsets;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 说    明： 全局异常处理类
 * 创 建 人： ebo
 * 创建日期： 2017-10-19 17:29
 * 修改说明：
 */
@ControllerAdvice
public class AppExceptionHandler {

	private static final Logger LOGGER = Logger.getLogger(AppExceptionHandler.class);

	/**
	 * 全局处理Exception
	 * @param ex 异常
	 * @return
	 */
	@ExceptionHandler(value = {Exception.class})
	@ResponseBody
	public void handleOtherExceptions(final Exception ex, HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.error(ex);

		InProcessContext.clearAll();

		ResponseMsg responseMsg = RespCodeMsgDepository.SERVER_INTERNAL_ERROR.toResponseMsg();

		try {
			if (ex instanceof AppException && (((AppException) ex).getDepository()) != null) {
				responseMsg = ((AppException) ex).getDepository().toResponseMsg();
			}
		} catch (Exception e) {
			LOGGER.error("异常处理过程出现新异常...", e);
		}

		// 记录响应数据
		LOGGER.info("响应客户端[" + req.getRemoteAddr() + "]的请求， 其响应报文：\n" + responseMsg.toString());
		try {
			resp.setStatus(HttpStatus.SC_OK);
			resp.setCharacterEncoding("utf-8");
			IOUtils.write(responseMsg.toString(), resp.getOutputStream(), Charsets.UTF_8);
			resp.getOutputStream().flush();
		} catch(Exception e) {
			// 忽略
		}
	}
}
