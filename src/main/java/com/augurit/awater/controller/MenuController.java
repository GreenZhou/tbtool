package com.augurit.awater.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.augurit.awater.DefaultIdGenerator;
import com.augurit.awater.InProcessContext;
import com.augurit.awater.RespCodeMsgDepository;
import com.augurit.awater.ResponseMsg;
import com.augurit.awater.entity.MenuInfo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuController {
	//  管理员用户类型
	private final static int ADMINISTRATOR = 0;
	//  普通员工用户类型
	private final static int STAFF = 1;
	//  普通买家用户类型
	private final static int CUSTOMER = 2;

	private static final MenuInfo taskMenu = new MenuInfo();
	private static final MenuInfo taskDetailMenu = new MenuInfo();
	private static final MenuInfo userMenu = new MenuInfo();

	static {
		// 初始化taskMenu
		taskMenu.setId(DefaultIdGenerator.getIdForStr());
		taskMenu.setComponent("Task");
		taskMenu.setIconCls("");
		taskMenu.setName("任务");
		taskMenu.setPath("/home/task");

		// 初始化taskDetailMenu
		taskDetailMenu.setId(DefaultIdGenerator.getIdForStr());
		taskDetailMenu.setComponent("TDetail");
		taskDetailMenu.setIconCls("");
		taskDetailMenu.setName("任务详情");
		taskDetailMenu.setPath(":id");

		List<MenuInfo> details = Lists.<MenuInfo>newArrayList();
		details.add(taskDetailMenu);
		taskMenu.setChildren(details);

		userMenu.setId(DefaultIdGenerator.getIdForStr());
		userMenu.setComponent("User");
		userMenu.setIconCls("");
		userMenu.setName("员工");
		userMenu.setPath("/home/user");
		userMenu.setChildren(Lists.<MenuInfo>newArrayList());
	}

	@RequestMapping("/list")
	@ResponseBody
	public void listCustomers(HttpServletRequest req) {
		ResponseMsg responseMsg = null;

		List<MenuInfo> menus = new ArrayList<MenuInfo>();
		/*
		String token = InProcessContext.getRequestMsg().getToken();
		User user = (User) req.getSession().getAttribute(token);
		if(user == null) {
			responseMsg = RespCodeMsgDepository.TOKEN_INVALID.toResponseMsg();
		} else {
			menus.add(taskDetailMenu);
			if(user.getUserType() == ADMINISTRATOR) {
				menus.add(taskMenu);
				menus.add(userMenu);
			} else if(user.getUserType() == STAFF) {
				menus.add(taskMenu);
			}
		*/
			//menus.add(taskDetailMenu);
			menus.add(taskMenu);
			menus.add(userMenu);

			List<MenuInfo> list = new ArrayList<MenuInfo>();
			MenuInfo root = new MenuInfo();
			root.setIconCls("");
			root.setName("新目录");
			root.setComponent("Home");
			root.setPath("/home");
			root.setChildren(menus);
			list.add(root);
			JSONArray results = (JSONArray) JSONArray.toJSON(list);
			JSONObject ret = new JSONObject();
			ret.put("totalCount", menus.size());
			ret.put("pageSize", menus.size());
			ret.put("list", results);
			responseMsg = ResponseMsg.ResponseMsgBuilder.build(RespCodeMsgDepository.SUCCESS, ret);
		//}

		InProcessContext.setResponseMsg(responseMsg);
	}
}
