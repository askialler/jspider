package com.chy.spider.config;

import com.chy.spider.utils.ConfigHelper;

public class Config {

	public static final int DEFAULT_MAX_DEPTH=Integer.parseInt(ConfigHelper.getParameter("defaultMaxDepth"));
	public static final int DEFAULT_MAX_VISIT_NUM=Integer.parseInt(ConfigHelper.getParameter("defaultMaxVisitNum"));
	public static final String HANDLER_IMPL=ConfigHelper.getParameter("com.chy.spider.handler.impl");

	public static final boolean USE_PROXY=Boolean.parseBoolean(ConfigHelper.getParameter("proxy.useProxy"));
	public static final String PROXY_IP=ConfigHelper.getParameter("proxy.hostip");
	public static final int PROXY_PORT=Integer.parseInt(ConfigHelper.getParameter("proxy.port"));
	public static final String PROXY_TYPE=ConfigHelper.getParameter("proxy.type");
	
	public static final int CRAWLER_THREADS=Integer.parseInt(ConfigHelper.getParameter("crawler.threads"));

}
