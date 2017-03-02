package com.chy.spider.config;

import com.chy.spider.utils.ConfigHelper;

public class Config {

	public static final int DefaultMaxDepth=Integer.parseInt(ConfigHelper.getParameter("defaultMaxDepth"));
	public static final int DefaultMaxVisitNum=Integer.parseInt(ConfigHelper.getParameter("defaultMaxVisitNum"));
	public static final String handlerImpl=ConfigHelper.getParameter("com.chy.spider.handler.impl");
}
