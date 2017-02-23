package com.chy.spider.filter.impls;

import com.chy.spider.filter.LinkFilter;

public class RegexFilter implements LinkFilter {

	private String regexStr;
	
	public RegexFilter(String regexStr){
		this.regexStr=regexStr;
	}

	@Override
	public boolean accept(String uri) {

		return uri.matches(regexStr);
	}
	


}
