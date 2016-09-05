package com.chy.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.chy.spider.impls.NotStartWithFilter;
import com.chy.spider.impls.RegexFilter;
import com.chy.spider.impls.StartWithFilter;
import com.chy.spider.inters.LinkFilter;
import com.chy.spider.utils.*;

/**
 * web spider main class
 * 
 * @author chengyang
 * 
 */
public class SafeCrawler {

	private CrawURI seedUrl;
	private static SafeTodoQueue todo;
	private static VisitedQueue visited;
	private static Log log = LogFactory.getLog(SafeCrawler.class);
	private int maxDepth = 3;
	private int maxVisitNum = 10;
	private LinkFilter filter;

	// private Object lock=new Object();

	public LinkFilter getFilter() {
		return filter;
	}

	public void setFilter(LinkFilter filter) {
		this.filter = filter;
	}

	public int getMaxVisitNum() {
		return maxVisitNum;
	}

	public void setMaxVisitNum(int maxVisitNum) {
		this.maxVisitNum = maxVisitNum;
	}

	public int getMaxDepth() {
		return maxDepth;
	}

	public void setMaxDepth(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	public static SafeTodoQueue getTodo() {
		return todo;
	}

	public static VisitedQueue getVisited() {
		return visited;
	}

	public CrawURI getSeedURI() {
		return seedUrl;
	}

	public SafeCrawler(CrawURI seedUrl) {
		todo = new SafeTodoQueue();
		visited = new VisitedQueue();
		this.seedUrl = seedUrl;
		try {
			todo.addUrl(seedUrl);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.filter = new LinkFilter() {

			@Override
			public boolean accept(String uri) {
				return true;
			}
		};
	}

	public SafeCrawler(CrawURI seedUrl, int maxDepth) {
		this(seedUrl);
		this.maxDepth = maxDepth;
	}

	/**
	 * 
	 * @param seedUrl
	 *            the first url
	 * @param maxDepth
	 *            使用宽度优先算法，搜索最大深度
	 * @param maxVisitNum
	 *            访问的最多网页数量
	 */
	public SafeCrawler(CrawURI seedUrl, int maxDepth, int maxVisitNum) {
		this(seedUrl, maxDepth);
		this.maxVisitNum = maxVisitNum;
		if (log.isInfoEnabled()) {
			log.info("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:"
					+ this.maxDepth + " maxVisitNum:" + this.maxVisitNum);
		}
	}

	public SafeCrawler(CrawURI seedUrl, int maxDepth, int maxVisitNum,
			LinkFilter filter) {
		this(seedUrl, maxDepth, maxVisitNum);
		this.filter = filter;
		if (log.isInfoEnabled()) {
			log.info("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:"
					+ this.maxDepth + " maxVisitNum:" + this.maxVisitNum);
		}
	}

	public void start() {
		CrawlerRunner[] runners = new CrawlerRunner[5];
		ExecutorService execServ = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 5; i++) {
			runners[i] = new CrawlerRunner(this);
			execServ.execute(runners[i]);
		}

		execServ.shutdown();

	}

	public void crawling(LinkFilter filter) {

		while (visited.totalVisited() < getMaxVisitNum()) {
			visitOneUri(filter);
			// !getTodo().isEmpty() &&
		}

	}

	private boolean visitOneUri(LinkFilter filter) {
		boolean ret=false;
		CrawURI next = null;

		try {
			next = getTodo().removeUri();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (next != null) {
			URI nextUri = next.getUri();
			int currDepth = next.getDepth();
			if (currDepth <= getMaxDepth()) {

				getVisited().addVisitedUrl(nextUri);

				if (log.isInfoEnabled()) {
					log.info("visited table add uri("+currDepth+"): " + nextUri);
				}
				String html = PageParser.getHtmlPage(nextUri);
				if (log.isDebugEnabled()) {
					log.debug("PageParser getHtmlPage: " + nextUri);
				}

				if (currDepth + 1 <= getMaxDepth()) {

					List<URI> list = PageParser.parseWebPage(html);
					if (log.isDebugEnabled()) {
						log.debug("PageParser parseWebPage: " + nextUri);
					}
					Iterator<URI> it = list.iterator();
					while (it.hasNext()) {
						URI parseduri = nextUri.resolve(it.next());
						CrawURI nUri = new CrawURI(parseduri, currDepth + 1);
						if (!SafeCrawler.getVisited().contains(parseduri)
								&& !SafeCrawler.getTodo().contains(nUri)
								&& filter.accept(parseduri.toString())) {
							try {
								SafeCrawler.getTodo().addUrl(nUri);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
					ret=true;
				}else{
					ret=false;
				}

			}
		}
		return ret;
	}

	/**
	 * @param args
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) {

		CrawURI seed = null;
		try {
			seed = new CrawURI(new URI("http://home.dcits.com"));
			// new URI("https://www.zhihu.com/question/26488686"));
			// new URI("http://zhuanlan.zhihu.com/100offer/19788061"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		LinkFilter filter = new StartWithFilter("http://home.dcits.com");
		LinkFilter filter2 = new RegexFilter("http://home.dcits.com");
		SafeCrawler crawler = new SafeCrawler(seed, 2, 10, filter);
		crawler.start();

		log.info("main-Thread is done ..............................................");

	}

	private class CrawlerRunner implements Runnable {

		private SafeCrawler crawler;
		private boolean endFlag=false;
		
		public void setEnd(){
			endFlag=true;
		}
		public boolean isEnd(){
			return endFlag;
		}

		//
		public CrawlerRunner(SafeCrawler crawler) {
			this.crawler = crawler;
		}

		@Override
		public void run() {
			
			while (SafeCrawler.getVisited().totalVisited() < getMaxVisitNum()
					&& !isEnd()) {
				boolean ret=crawler.visitOneUri(crawler.getFilter());
				if(!ret && SafeCrawler.todo.isEmpty()){
					setEnd();
				}
				
			}

//			visitOneUri(filter);
//			crawler.crawling(crawler.getFilter());
			if (log.isInfoEnabled()) {
				log.info("CrawThread-" + Thread.currentThread().getName()
						+ " is done ......");
			}
		}

	}

}
