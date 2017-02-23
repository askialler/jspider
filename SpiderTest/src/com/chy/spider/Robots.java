package com.chy.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;

import com.chy.spider.filter.LinkFilter;
import com.chy.spider.filter.impls.NotStartWithFilter;
import com.chy.spider.utils.PageParser;

/**
 * 根据网站的robots.txt文件处理抓取范围
 * 
 * @author chengyang
 *
 */
public class Robots {
	
	private static Log log = LogFactory.getLog(Robots.class);

	public static List<LinkFilter> parseRobots(URI robotUri) {

		String content = PageParser.getHtmlPage(robotUri);
		BufferedReader reader = new BufferedReader(new StringReader(content));
		List<LinkFilter> list = new LinkedList<LinkFilter>();
		while (true) {
			String line;
			try {
				line = reader.readLine();
				if (line != null) {
					if (line.startsWith("Disallow:")) {
						String path = line.split(":")[1].trim();
						URIBuilder ub = new URIBuilder(robotUri);
						try {
							String filterStr = ub.setPath(path).build().toString();
							if(log.isInfoEnabled()){
								log.info("add robots filter:"+filterStr);
							}
							list.add(new NotStartWithFilter(filterStr));
						} catch (URISyntaxException e) {
							e.printStackTrace();
						}

					}
				} else {
					break;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return list;
	}

	public static void main(String[] args) {
		try {
			parseRobots(new URI("http://mebook.cc/robots.txt"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
