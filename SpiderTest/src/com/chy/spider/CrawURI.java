package com.chy.spider;

import java.net.URI;

public class CrawURI {

	private URI uri;
	private int depth=1;
	
	public CrawURI(URI uri) {
		this.uri=uri;
	}
	public CrawURI(URI uri,int level) {
		this.uri=uri;
		this.depth=level;
	}
	
	public URI getUri() {
		return uri;
	}
	public void setUri(URI uri) {
		this.uri = uri;
	}
	public int getDepth() {
		return depth;
	}
	public void setDepth(int level) {
		this.depth = level;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.uri.equals(((CrawURI)obj).getUri()) ;
	}

	
}
