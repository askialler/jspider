package com.chy.spider.utils;

import org.apache.http.HttpHost;

import com.chy.spider.config.Config;

public class ProxyFactory {

	public static HttpHost createProxy(String host,int port,String type){
		
		return new HttpHost(Config.PROXY_IP, Config.PROXY_PORT, Config.PROXY_TYPE);
	}
}
