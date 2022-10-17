package com.wangshanhai.power.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 上下文操作工具
 * @author Shmily
 */
public class HttpContextUtils {
	/**
	 * 获取当前用户请求
	 * @return
	 */
	public static HttpServletRequest getHttpServletRequest() {
		RequestAttributes servletRequestAttributes = RequestContextHolder.getRequestAttributes();
		if(servletRequestAttributes==null){
			Logger.error("[getHttpServletRequest]-ServletRequestAttributes is null");
			return null;
		}
		return ((ServletRequestAttributes)servletRequestAttributes).getRequest();
	}
}
