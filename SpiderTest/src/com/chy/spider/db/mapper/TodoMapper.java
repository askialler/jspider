package com.chy.spider.db.mapper;

import com.chy.spider.CrawURI;

public interface TodoMapper {

	public CrawURI getTodoByMD5(String md5);
	public CrawURI getVisitedByMD5(String md5);
	public CrawURI get1stTodoCrawURI();
	public void addTodoCrawURI(CrawURI crawUri);
	public void addVisitedCrawURI(CrawURI uri);
	public void deleteTodoCrawURI(String md5);
	public int getTodoTotal();
	public int getVisitedTotal();
}
