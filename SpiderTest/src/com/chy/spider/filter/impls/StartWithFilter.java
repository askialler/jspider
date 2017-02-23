package com.chy.spider.filter.impls;


import com.chy.spider.filter.LinkFilter;

public class StartWithFilter implements LinkFilter {
	private String filterStr="";
	
	public StartWithFilter(){
		
	}
	
	public StartWithFilter(String filterStr){
		this.filterStr=filterStr;
	}

	@Override
	public boolean accept(String uri) {
		return uri.startsWith(filterStr);
	}

//	@Override
//	public List<URI> doFilter(List<URI> list) {
//		
//		Iterator<URI> it=list.iterator();
//		List<URI> rList=new LinkedList<URI>();
//		while(it.hasNext()){
//			URI uri=it.next();
//			if(!uri.toString().startsWith(filterStr)){
//				rList.add(uri);
//			}
//		}
//		
//		return rList;
//		// return false;
//	}

}
