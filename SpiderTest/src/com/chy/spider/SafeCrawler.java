package com.chy.spider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import com.chy.spider.config.Config;
import com.chy.spider.filter.LinkFilter;
import com.chy.spider.filter.impls.NotStartWithFilter;
import com.chy.spider.filter.impls.RegexFilter;
import com.chy.spider.filter.impls.StartWithFilter;
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
	private List<LinkFilter> filters;

	// private Object lock=new Object();

	public List<LinkFilter> getFilters() {
		return filters;
	}

	public void setFilters(List<LinkFilter> filters) {
		this.filters = filters;
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

		this(seedUrl, Config.DefaultMaxDepth, Config.DefaultMaxVisitNum, new LinkedList<LinkFilter>());

		new LinkFilter() {
			@Override
			public boolean accept(String uri) {
				return true;
			}
		};
	}

	public SafeCrawler(CrawURI seedUrl, int maxDepth) {

		this(seedUrl, maxDepth, Config.DefaultMaxVisitNum, new LinkedList<LinkFilter>());
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

		this(seedUrl, maxDepth, maxVisitNum, new LinkedList<LinkFilter>());
	}

	/**
	 * 
	 * @param seedUrl
	 *            the first url
	 * @param maxDepth
	 *            使用宽度优先算法，搜索最大深度
	 * @param maxVisitNum
	 *            访问的最多网页数量
	 * @param filter
	 *            过滤器，过滤需要抓取的url
	 */
	public SafeCrawler(CrawURI seedUrl, int maxDepth, int maxVisitNum, List<LinkFilter> filters) {

		todo = new SafeTodoQueue();
		visited = new VisitedQueue();
		this.seedUrl = seedUrl;
		try {
			todo.addUrl(seedUrl);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.maxDepth = maxDepth;
		this.maxVisitNum = maxVisitNum;
		this.filters = filters;
		if (log.isInfoEnabled()) {
			log.info("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:" + this.maxDepth + " maxVisitNum:"
					+ this.maxVisitNum);
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

	public void crawling(List<LinkFilter> filters) {

		while (visited.totalVisited() < getMaxVisitNum()) {
			visitOneUri(filters);
			// !getTodo().isEmpty() &&
		}

	}

	private boolean visitOneUri(List<LinkFilter> filters) {
		boolean ret = false;
		CrawURI next = null;

		try {
			// get next uri from todo table
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
					log.info("visited table add uri(" + currDepth + "): " + nextUri);
				}
				String html = PageParser.getHtmlPage(nextUri);
				if (log.isDebugEnabled()) {
					log.debug("PageParser getHtmlPage: " + nextUri);
				}

				if (currDepth + 1 <= getMaxDepth() && html != null) {

					List<URI> list = PageParser.parseWebPage(html);
					if (log.isDebugEnabled()) {
						log.debug("PageParser parseWebPage: " + nextUri);
					}
					Iterator<URI> it = list.iterator();
					while (it.hasNext()) {
						// resolve方法将URI解析成绝对路径
						URI parsedUri = nextUri.resolve(it.next());
						// 去掉url片段标识(#frag)
						URIBuilder ub=new URIBuilder(parsedUri);
						try {
							parsedUri=ub.setFragment(null).build();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						CrawURI nUri = new CrawURI(parsedUri, currDepth + 1);

						boolean isAccept = true;
						for (LinkFilter f : filters) {
							isAccept = isAccept && f.accept(parsedUri.toString());
						}

						if (!SafeCrawler.getVisited().contains(parsedUri) && !SafeCrawler.getTodo().contains(nUri)
								&& isAccept) {
							try {
								SafeCrawler.getTodo().addUrl(nUri);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

						}
					}
					ret = true;
				} else {
					ret = false;
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

//		CrawURI seed = null;
//		try {
//			seed = new CrawURI(new URI("http://mebook.cc/"));
//			// new URI("https://www.zhihu.com/question/26488686"));
//			// new URI("http://zhuanlan.zhihu.com/100offer/19788061"));
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//		}
//		// LinkFilter filter = new StartWithFilter("http://mebook.cc/");
//		LinkFilter filter2 = new RegexFilter("http://mebook.cc/.*");
//		SafeCrawler crawler = new SafeCrawler(seed, 5, 30, filter2);
//		crawler.start();
//
//		log.info("main-Thread is done ..............................................");

	}

	private class CrawlerRunner implements Runnable {

		private SafeCrawler crawler;
		private boolean endFlag = false;

		public void setEnd() {
			endFlag = true;
		}

		public boolean isEnd() {
			return endFlag;
		}

		//
		public CrawlerRunner(SafeCrawler crawler) {
			this.crawler = crawler;
		}

		@Override
		public void run() {

			while (SafeCrawler.getVisited().totalVisited() < getMaxVisitNum() && !isEnd()) {
				boolean ret = crawler.visitOneUri(crawler.getFilters());
				if (!ret && SafeCrawler.todo.isEmpty()) {
					setEnd();
				}

			}

			// visitOneUri(filter);
			// crawler.crawling(crawler.getFilter());
			if (log.isInfoEnabled()) {
				log.info("CrawThread-" + Thread.currentThread().getName() + " is done ......");
			}
		}

	}

}
