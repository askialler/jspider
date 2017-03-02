package com.chy.spider.model.interfaces;

import java.net.URI;

import com.chy.spider.CrawURI;

public interface CrawleHandler {

	public void addTodo(CrawURI crawUri);
	public CrawURI removeTodo();
	public void addVisited(CrawURI crawUri);
	public int totalVisited();
	public boolean visitedContains(CrawURI crawUri);
	public boolean todoContains(CrawURI crawUri);
	public boolean isEmpty();
	
}
