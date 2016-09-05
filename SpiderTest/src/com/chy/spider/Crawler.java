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
@Deprecated
public class Crawler {

	private CrawURI seedUrl;
	private static TodoQueue todo;
	private static VisitedQueue visited;
	private static Log log = LogFactory.getLog(Crawler.class);
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

	public synchronized static TodoQueue getTodo() {
		return todo;
	}

	public VisitedQueue getVisited() {
		return visited;
	}

	public CrawURI getSeedURI() {
		return seedUrl;
	}

	public Crawler(CrawURI seedUrl) {
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

	public Crawler(CrawURI seedUrl, int maxDepth) {
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
	public Crawler(CrawURI seedUrl, int maxDepth, int maxVisitNum) {
		this(seedUrl, maxDepth);
		this.maxVisitNum = maxVisitNum;
		if (log.isInfoEnabled()) {
			log.info("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:"
					+ this.maxDepth + " maxVisitNum:" + this.maxVisitNum);
		}
	}

	public Crawler(CrawURI seedUrl, int maxDepth, int maxVisitNum,
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
		// CrawlerRunner runner = new CrawlerRunner(this);
		// ExecutorService execServ = Executors.newFixedThreadPool(5);
		// execServ.execute(runner);
		// //
		execServ.shutdown();
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
		// crawling(filter);
	}

	public void crawling(LinkFilter filter) {
		// ExecutorService execServ = Executors.newFixedThreadPool(1);
		// synchronized (this) {
		// if (getTodo().isEmpty()) {
		// try {
		// Thread.sleep(300);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		// }
		while (visited.totalVisited() < getMaxVisitNum()) {
			visitOneUri(filter);
			// !getTodo().isEmpty() &&
		}

	}

	private void visitOneUri(LinkFilter filter) {

		// synchronized (this) {
		// CrawURI next = getTodo().getUrl();
		CrawURI next = null;
		synchronized (this) {
			while (getTodo().isEmpty()) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			next = getTodo().removeUri();
			// this.notifyAll();
		}

		if (next != null) {
			URI nextUri = next.getUri();
			int currLevel = next.getDepth();
			if (currLevel <= getMaxDepth()) {

				synchronized (this) {
					getVisited().addVisitedUrl(nextUri);
				}

				if (log.isInfoEnabled()) {
					log.info("visited table add uri: " + nextUri);
				}
				String html = PageParser.getHtmlPage(nextUri);
				if (log.isDebugEnabled()) {
					log.debug("PageParser getHtmlPage: " + nextUri);
				}
				if (currLevel + 1 <= getMaxDepth()) {

					List<URI> list = PageParser.parseWebPage(html);
					if (log.isDebugEnabled()) {
						log.debug("PageParser parseWebPage: " + nextUri);
					}
					Iterator<URI> it = list.iterator();
					while (it.hasNext()) {
						URI parseduri = nextUri.resolve(it.next());
						CrawURI nUri = new CrawURI(parseduri, currLevel + 1);
						synchronized (this) {
							if (!getVisited().contains(parseduri)
									&& !getTodo().contains(nUri)
									&& filter.accept(parseduri.toString())
									&& (currLevel + 1) < getMaxDepth()) {
								getTodo().addUrl(nUri);

								// if(log.isDebugEnabled()){
								// log.debug("todo table add uri: "+parseduri);
								// }
								
							}
							this.notifyAll();
						}
					}

				}

			}
		}


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
		Crawler crawler = new Crawler(seed, 5, 3000, filter);
		crawler.start();

		log.info("main-Thread is done ..............................................");

	}

	private class CrawlerRunner implements Runnable {

		private Crawler crawler;

		//
		public CrawlerRunner(Crawler crawler) {
			this.crawler = crawler;
		}

		@Override
		public void run() {

//			visitOneUri(filter);
			crawler.crawling(crawler.getFilter());
			if (log.isInfoEnabled()) {
				log.info("CrawThread-" + Thread.currentThread().getName()
						+ " is done ......");
			}
		}

	}

}
