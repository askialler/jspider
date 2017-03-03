package com.chy.spider.test;

import com.chy.spider.CrawURI;
import com.chy.spider.model.impls.DBQueueHnadler;

public class Test {

	public static void main(String[] args) {
		DBQueueHnadler handler=new DBQueueHnadler(new CrawURI("http://mebook.cc/"));
		CrawURI uri= handler.removeTodo();
		System.out.println(uri);
		handler.addVisited(uri);
		handler.addTodo(new CrawURI("http://www.baidu.com/"));
		System.out.println("totalVisited: "+ handler.totalVisited());
//		System.out.println("uri's md5:"+uri.getMd5());
		System.out.println("visited contains uri: "+handler.visitedContains(uri));
		System.out.println("todo contains uri: "+handler.todoContains(uri));
		System.out.println("isempty: "+handler.isEmpty());
	}
}
