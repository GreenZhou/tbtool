package com.augurit.awater.exception;

import com.augurit.awater.RespCodeMsgDepository;
import com.google.common.base.Strings;

/**
 * 说    明：
 * 创 建 人： ebo
 * 创建日期： 2017-10-19 09:55
 * 修改说明：
 */
public class AppException extends RuntimeException {

	private RespCodeMsgDepository depository;

	public AppException() {
		this(RespCodeMsgDepository.SERVER_INTERNAL_ERROR, null);
	}

	public AppException(String logMsg) {
		this(null, logMsg);
	}

	public AppException(Exception parent) {
		this(null, null, parent);
	}

	public AppException(String logMsg, Exception parent) {
		this(null, logMsg, parent);
	}

	public AppException(RespCodeMsgDepository depository, String logMsg) {
		super(Strings.nullToEmpty(logMsg));

		if(depository == null) {
			this.depository = RespCodeMsgDepository.SERVER_INTERNAL_ERROR;
		}

		this.depository = depository;
	}

	public AppException(RespCodeMsgDepository depository, String logMsg, Exception parent) {
		super(Strings.nullToEmpty(logMsg), parent);

		if(depository == null) {
			this.depository = RespCodeMsgDepository.SERVER_INTERNAL_ERROR;
		}

		this.depository = depository;
	}

	public RespCodeMsgDepository getDepository() {
		return depository;
	}

	public void setDepository(RespCodeMsgDepository depository) {
		this.depository = depository;
	}
}
