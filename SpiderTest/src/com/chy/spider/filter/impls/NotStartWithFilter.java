package com.chy.spider.filter.impls;

import com.chy.spider.filter.LinkFilter;

public class NotStartWithFilter implements LinkFilter {

	private String filterStr="";
	
	public NotStartWithFilter(){
		
	}
	
	public NotStartWithFilter(String filterStr){
		this.filterStr=filterStr;
	}
	
	@Override
	public boolean accept(String uri) {
		return !uri.startsWith(filterStr);
	}

}
