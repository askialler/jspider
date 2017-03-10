package com.chy.spider.filter.impls;

import com.chy.spider.filter.LinkFilter;
import java.util.regex.*;

public class RegexFilter implements LinkFilter {

	private String regexStr;
	
	public RegexFilter(String regexStr){
		this.regexStr=regexStr;
	}

	@Override
	public boolean accept(String uri) {
		// Pattern 表示编译后的正则表达式
		Pattern p=Pattern.compile(regexStr);
		Matcher m=p.matcher(uri);
		return m.matches();
	}
	


}
