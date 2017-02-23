package com.chy.spider.main;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chy.spider.CrawURI;
import com.chy.spider.Robots;
import com.chy.spider.SafeCrawler;
import com.chy.spider.filter.LinkFilter;
import com.chy.spider.filter.impls.NotStartWithFilter;
import com.chy.spider.filter.impls.RegexFilter;
import com.chy.spider.filter.impls.StartWithFilter;

public class StartCrawler {
	
	private static Log log = LogFactory.getLog(StartCrawler.class);

	public static void main(String[] args) {

		CrawURI seed = null;
		try {
			String seedUri="http://mebook.cc/";
			seed = new CrawURI(new URI(seedUri));
			// new URI("https://www.zhihu.com/question/26488686"));
			// new URI("http://zhuanlan.zhihu.com/100offer/19788061"));
			List<LinkFilter> list=Robots.parseRobots(new URI(seedUri+"robots.txt"));
			list.add(new StartWithFilter("http://mebook.cc/"));
			list.add(new NotStartWithFilter("http://mebook.cc/category"));
			SafeCrawler crawler = new SafeCrawler(seed, 5, 30, list);
			crawler.start();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		// LinkFilter filter = new StartWithFilter("http://mebook.cc/");
//		LinkFilter filter2 = new RegexFilter("http://mebook.cc/.*");



		log.info("main-Thread is done ..............................................");
	}

}
