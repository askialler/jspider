package com.chy.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SafeTodoQueue implements Iterable<CrawURI> {
	public SafeTodoQueue() {
		todoList = new LinkedBlockingQueue<CrawURI>();
	}

	private Object lock = new Object();

	private BlockingQueue<CrawURI> todoList;

	public  void addUrl(CrawURI uri) throws InterruptedException {
		todoList.put(uri);
	}

	public  CrawURI removeUri() throws InterruptedException {

		return todoList.take();

	}

	public  boolean isEmpty() {

		return todoList.isEmpty();

	}

	public  boolean contains(CrawURI uri) {
		return todoList.contains(uri);
	}

//	public  CrawURI getUrl() {
//		return todoList.get(0);
//
//		// return todoList.get(0);
//	}

	public static void main(String[] args) throws URISyntaxException {

	}

	@Override
	public Iterator<CrawURI> iterator() {
		return todoList.iterator();
	}

}
