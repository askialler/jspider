package com.chy.spider.db.mapper;

import com.chy.spider.CrawURI;

public interface TodoMapper {

	public CrawURI getCrawURI(int id);

	public void insertCrawURI(CrawURI uri);
//	public void insertCrawURI(String url,String mdd5,int depth);

}
