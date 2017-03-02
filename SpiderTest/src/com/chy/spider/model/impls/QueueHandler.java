package com.chy.spider.model.impls;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chy.spider.CrawURI;
import com.chy.spider.SafeCrawler;
import com.chy.spider.SafeTodoQueue;
import com.chy.spider.VisitedQueue;
import com.chy.spider.model.interfaces.CrawleHandler;

public class QueueHandler implements CrawleHandler {

	private static SafeTodoQueue todo;
	private static VisitedQueue visited;
	private static Logger logger=LoggerFactory.getLogger(SafeCrawler.class);
	
	public static SafeTodoQueue getTodo() {
		return todo;
	}

	public static VisitedQueue getVisited() {
		return visited;
	}
	
	public QueueHandler(CrawURI seedUrl) {

		todo = new SafeTodoQueue();
		visited = new VisitedQueue();
		try {
			todo.addUrl(seedUrl);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void addTodo(CrawURI crawUri) {
		try {
			todo.addUrl(crawUri);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public CrawURI removeTodo() {
		CrawURI crawUri=null;
		try {
			crawUri= todo.removeUri();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return crawUri;
	}

	@Override
	public void addVisited(CrawURI crawUri) {
		visited.addVisitedUrl(crawUri);
	}

	@Override
	public int totalVisited() {
		return visited.totalVisited();
	}

	@Override
	public boolean visitedContains(CrawURI crawUri) {
		return visited.contains(crawUri);
	}

	@Override
	public boolean todoContains(CrawURI crawUri) {
		return todo.contains(crawUri);
	}

	@Override
	public boolean isEmpty() {
		return todo.isEmpty();
	}

}
