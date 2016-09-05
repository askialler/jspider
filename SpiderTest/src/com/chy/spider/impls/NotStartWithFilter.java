package com.chy.spider.impls;

import com.chy.spider.inters.LinkFilter;

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
