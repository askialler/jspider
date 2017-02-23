package com.chy.spider.filter;


public interface LinkFilter {

	public boolean accept(String uri);
	
//	public List<URI> doFilter(List<URI> todo);
}
