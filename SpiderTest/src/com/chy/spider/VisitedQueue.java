package com.chy.spider;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class VisitedQueue {

	private Set<CrawURI> visited;

	public VisitedQueue() {
		visited = new HashSet<CrawURI>();
	}

	public synchronized boolean isVisited(CrawURI crawUri) {
		return visited.contains(crawUri);
	}

	public synchronized void addVisitedUrl(CrawURI crawUri) {
		visited.add(crawUri);
	}

	public synchronized boolean contains(CrawURI crawUri){
		return visited.contains(crawUri);
	}
	
	public  synchronized int totalVisited() {
		return visited.size();
	}

}
