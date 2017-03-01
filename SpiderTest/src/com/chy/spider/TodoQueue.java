package com.chy.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TodoQueue implements Iterable<CrawURI> {
	public TodoQueue() {
		todoList = new LinkedList<CrawURI>();
	}

	private Object lock = new Object();

	private List<CrawURI> todoList;

	public  void addUrl(CrawURI uri) {
		todoList.add(uri);

	}

	public  CrawURI removeUri() {

		return todoList.remove(0);

	}

	public  boolean isEmpty() {

		return todoList.isEmpty();

	}

	public  boolean contains(CrawURI uri) {
		return todoList.contains(uri);
	}

	public  CrawURI getUrl() {
		return todoList.get(0);

		// return todoList.get(0);
	}

	public static void main(String[] args) throws URISyntaxException {

	}

	@Override
	public Iterator<CrawURI> iterator() {
		return todoList.iterator();
	}

}
