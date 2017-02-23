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
import com.chy.spider.impls.StartWithFilter;
import com.chy.spider.inters.LinkFilter;
import com.chy.spider.utils.*;

/**
 * web spider main class
 * 
 * @author chengyang
 * 
 */
public class CopyOfCrawler {

	private CrawURI seedUrl;
	private TodoQueue todo;
	private VisitedQueue visited;
	private static Log log = LogFactory.getLog(CopyOfCrawler.class);
	private int maxDepth = 3;
	private int maxVisitNum = 10;
	private LinkFilter filter;
//	private Object lock=new Object();

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

	public TodoQueue getTodo() {
		return todo;
	}

	public VisitedQueue getVisited() {
		return visited;
	}

	public CrawURI getSeedURI() {
		return seedUrl;
	}

	public CopyOfCrawler(CrawURI seedUrl) {
		todo = new TodoQueue();
		visited = new VisitedQueue();
		this.seedUrl = seedUrl;
		todo.addUrl(seedUrl);
		this.filter = new LinkFilter() {

			@Override
			public boolean accept(String uri) {
				return true;
			}
		};
	}

	public CopyOfCrawler(CrawURI seedUrl, int maxDepth) {
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
	public CopyOfCrawler(CrawURI seedUrl, int maxDepth, int maxVisitNum) {
		this(seedUrl, maxDepth);
		this.maxVisitNum = maxVisitNum;
		if (log.isDebugEnabled()) {
			log.debug("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:"
					+ this.maxDepth + " maxVisitNum:" + this.maxVisitNum);
		}
	}

	public CopyOfCrawler(CrawURI seedUrl, int maxDepth, int maxVisitNum,
			LinkFilter filter) {
		this(seedUrl, maxDepth, maxVisitNum);
		this.filter = filter;
		if (log.isDebugEnabled()) {
			log.debug("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:"
					+ this.maxDepth + " maxVisitNum:" + this.maxVisitNum);
		}
	}

	public void start() {
		// CrawlerRunner runner = new CrawlerRunner(this);
		// ExecutorService execServ = Executors.newFixedThreadPool(5);
		// execServ.execute(runner);
		// //
		// execServ.shutdown();
		// new Thread(runner).start();
		// try {
		// Thread.sleep(500);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// new Thread(runner).start();
		// new Thread(runner).start();
		// new Thread(runner).start();
		// new Thread(runner).start();
		crawling(filter);
	}

	public void crawling(LinkFilter filter) {
		ExecutorService execServ = Executors.newFixedThreadPool(1);
//		synchronized (getTodo()) {
			while (!getTodo().isEmpty()
					&& visited.totalVisited() < getMaxVisitNum()) {
				// visitOneUri(filter);
				execServ.execute(new CrawlerRunner());

			}
//		}	

		execServ.shutdown();

	}

	private void visitOneUri(LinkFilter filter) {

//		synchronized (getTodo()) {
//		CrawURI next = getTodo().getUrl();
			CrawURI next = getTodo().removeUri();
//			if(next!=null){
				URI nextUri = next.getUri();
				int currLevel = next.getDepth();
				if (currLevel <= getMaxDepth()) {

					getVisited().addVisitedUrl(nextUri);
					if (log.isDebugEnabled()) {
						log.debug("visited table add uri: " + nextUri);
					}
					String html = PageParser.getHtmlPage(nextUri);

					List<URI> list = PageParser.parseWebPage(html);
					Iterator<URI> it = list.iterator();
					while (it.hasNext()) {
						URI parseduri = nextUri.resolve(it.next());
						CrawURI nUri = new CrawURI(parseduri, currLevel + 1);
						if (!getVisited().contains(parseduri)
								&& !getTodo().contains(nUri)
								&& filter.accept(parseduri.toString())) {
							getTodo().addUrl(nUri);
							// if(log.isDebugEnabled()){
							// log.debug("todo table add uri: "+parseduri);
							// }
						}
					}
					
				}
//			}
//			getTodo().removeUri();
//		}

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
		CopyOfCrawler crawler = new CopyOfCrawler(seed, 5, 1, filter);
		crawler.start();

		log.debug("main-Thread is done ..............................................");

	}

	private class CrawlerRunner implements Runnable {

//		private Crawler crawler;
//
//		//
//		public CrawlerRunner(Crawler crawler) {
//			this.crawler = crawler;
//		}

		@Override
		public void run() {

			visitOneUri(filter);
			// crawler.crawling(crawler.getFilter());
			// if(log.isDebugEnabled()){
			// log.debug("CrawThread-"+
			// Thread.currentThread().getName()+" is done ......");
			// }
		}

	}

}
