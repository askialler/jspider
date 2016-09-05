package com.chy.spider;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

public class VisitedQueue {

	private Set<URI> visited;

	public VisitedQueue() {
		visited = new HashSet<URI>();
	}

	public synchronized boolean isVisited(URI uri) {
		return visited.contains(uri);
	}

	public synchronized void addVisitedUrl(URI uri) {
		visited.add(uri);
	}

	public synchronized boolean contains(URI uri){
		return visited.contains(uri);
	}
	
	public  synchronized int totalVisited() {
		return visited.size();
	}

}
