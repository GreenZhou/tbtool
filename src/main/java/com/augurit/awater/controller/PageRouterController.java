package com.augurit.awater.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class PageRouterController {
	
	/*登录*/
	@RequestMapping(value = "/login")
	public String login(HttpServletRequest request,
			HttpServletResponse response) {
		
		return "login";
	}	

	/*主页*/
	@RequestMapping(value = "/index.do")
	public String Index(HttpServletRequest request,
			HttpServletResponse response) {
		
		return "index";
	}
	
}
