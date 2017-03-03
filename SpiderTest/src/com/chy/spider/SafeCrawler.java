package com.chy.spider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chy.spider.config.Config;
import com.chy.spider.filter.LinkFilter;
import com.chy.spider.model.impls.QueueHandler;
import com.chy.spider.model.interfaces.CrawleHandler;
import com.chy.spider.utils.*;

import sun.org.mozilla.javascript.internal.GeneratedClassLoader;

/**
 * web spider main class
 * 
 * @author chengyang
 * 
 */
public class SafeCrawler {

	private CrawleHandler handler;
	private CrawURI seedUrl;
	private static Logger logger = LoggerFactory.getLogger(SafeCrawler.class);
	// private static Log log = LogFactory.getLog(SafeCrawler.class);
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

		this.seedUrl = seedUrl;

		try {

			try {
				Class<?> clazz = Class.forName(Config.handlerImpl);
				Constructor<?> cons = clazz.getConstructor(CrawURI.class);
				handler = (CrawleHandler) cons.newInstance(seedUrl);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// handler = new QueueHandler(seedUrl);

		this.maxDepth = maxDepth;
		this.maxVisitNum = maxVisitNum;
		this.filters = filters;
		if (logger.isInfoEnabled()) {
			logger.info("seedUrl:" + seedUrl.getUri().toString() + " maxDepth:" + this.maxDepth + " maxVisitNum:"
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
		boolean end = false;

		while (!end) {
			short wait = 0;
			for (int i = 0; i < runners.length; i++) {
				wait += runners[i].isWait();
			}
			
			boolean tmp=true;
			for (int i = 0; i < runners.length; i++) {
				tmp = tmp && runners[i].isEnd();
			}
			end=tmp;
			
			if (wait == runners.length) {
				end = true;
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}


		}

	}

	// public void crawling(List<LinkFilter> filters) {
	//
	// while (handler.totalVisited() < getMaxVisitNum()) {
	// visitOneUri(filters);
	// }
	//
	// }

	private boolean visitOneUri(List<LinkFilter> filters) {
		boolean ret = false;
		CrawURI next = null;

		// get next uri from todo table
		next = handler.removeTodo();

		if (next != null) {
			URI nextUri = next.getUri();
			int currDepth = next.getDepth();
			if (currDepth <= getMaxDepth()) {

				handler.addVisited(next);

				if (logger.isInfoEnabled()) {
					logger.info("visited table add: " + next.toString());
				}
				String html = PageParser.getHtmlPage(nextUri);
				if (logger.isDebugEnabled()) {
					logger.debug("PageParser getHtmlPage: " + nextUri);
				}

				if (currDepth + 1 <= getMaxDepth() && html != null) {

					List<URI> list = PageParser.parseWebPage(html);
					if (logger.isDebugEnabled()) {
						logger.debug("PageParser parseWebPage: " + nextUri);
					}
					Iterator<URI> it = list.iterator();
					while (it.hasNext()) {
						// resolve方法将URI解析成绝对路径
						URI parsedUri = nextUri.resolve(it.next());
						// 去掉url片段标识(#frag)
						try {
							parsedUri = new URIBuilder(parsedUri).setFragment(null).build();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
						CrawURI nUri = new CrawURI(parsedUri.toString(), currDepth + 1);

						boolean isAccept = true;
						for (LinkFilter f : filters) {
							isAccept = isAccept && f.accept(parsedUri.toString());
						}

						if (!handler.visitedContains(nUri) && !handler.todoContains(nUri) && isAccept) {
							handler.addTodo(nUri);
							logger.info("add todo uri: " + nUri);

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

	private class CrawlerRunner implements Runnable {

		//TODO change to Callble
		
		private SafeCrawler crawler;
		private boolean endFlag = false;
		private short waitFlag = 0;

		public void setEnd() {
			endFlag = true;
		}

		public boolean isEnd() {
			return endFlag;
		}

		public void setWait() {
			waitFlag = 1;
		}

		public void cancelWait() {
			waitFlag = 0;
		}

		public short isWait() {
			return waitFlag;
		}

		//
		public CrawlerRunner(SafeCrawler crawler) {
			this.crawler = crawler;
		}

		@Override
		public void run() {

			while (!isEnd()) {
				boolean ret = crawler.visitOneUri(crawler.getFilters());
				if (handler.totalVisited() >= getMaxVisitNum()) {
					setEnd();
				}
				if (!ret && handler.isEmpty()) {
					setWait();
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					cancelWait();
				}

			}

			// visitOneUri(filter);
			// crawler.crawling(crawler.getFilter());
			if (logger.isInfoEnabled()) {
				logger.info("CrawThread-" + Thread.currentThread().getName() + " is done ......");
			}
		}

	}

}
