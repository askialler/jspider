package com.chy.spider.main;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chy.spider.CrawURI;
import com.chy.spider.SafeCrawler;
import com.chy.spider.impls.RegexFilter;
import com.chy.spider.inters.LinkFilter;

public class StartCrawler {
	
	private static Log log = LogFactory.getLog(StartCrawler.class);

	public static void main(String[] args) {

		CrawURI seed = null;
		try {
			seed = new CrawURI(new URI("http://mebook.cc/"));
			// new URI("https://www.zhihu.com/question/26488686"));
			// new URI("http://zhuanlan.zhihu.com/100offer/19788061"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		// LinkFilter filter = new StartWithFilter("http://mebook.cc/");
		LinkFilter filter2 = new RegexFilter("http://mebook.cc/.*");
		SafeCrawler crawler = new SafeCrawler(seed, 5, 30, filter2);
		crawler.start();

		log.info("main-Thread is done ..............................................");
	}

}
