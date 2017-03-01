package com.chy.spider;

import java.net.URI;
import java.net.URISyntaxException;

import com.chy.spider.utils.MD5Encoder;

public class CrawURI {

	private int id=0;
	private URI uri;
	private int depth=1;
	private String md5;
	
	/**
	 * @return the md5
	 */
	public String getMd5() {
		return md5;
	}
	/**
	 * @param md5 the md5 to set
	 */
	public void setMd5(String md5) {
		this.md5 = md5;
	}
	public CrawURI(String uri) {
		try {
			this.uri=new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.md5=MD5Encoder.getMD5Digest(uri.toString());
	}
	public CrawURI(String uri,int depth) {
		this(uri);
		this.depth=depth;
	}
	public CrawURI(String uri,String md5,Integer depth){
		this(uri);
		this.md5=md5;
		this.depth=depth;
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
	public void setDepth(int depth) {
		this.depth = depth;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		return this.md5.equals(((CrawURI)obj).getMd5()) ;
	}
	
	@Override
	public String toString(){
		return "CrawURI[url: "+this.uri+", md5: "+this.md5+", depth:"+this.depth+"]";
	}
	

	
}
