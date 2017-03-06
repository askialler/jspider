package com.chy.spider.utils;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chy.spider.config.Config;

public class PageParser {

	private static HttpClient client;
//	private static Log log = LogFactory.getLog(PageParser.class);
	private static Logger logger=LoggerFactory.getLogger(PageParser.class);

	public static HttpClient getHttpClient() {
		
		HttpHost proxy = null;
		if(Config.USE_PROXY){
			proxy = new HttpHost("10.126.3.161", 3128,"http");
		}
		
		SSLContext sslCxt = null;
		try {
			sslCxt = SSLContexts.custom().useSSL().loadTrustMaterial(null, new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslCxt,
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
		Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
		PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(registry);
		connMgr.setMaxTotal(50);
		connMgr.setDefaultMaxPerRoute(50);
		RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000)
				.setSocketTimeout(10000).setProxy(proxy).build();
		client = HttpClients.custom().setConnectionManager(connMgr).setDefaultRequestConfig(config).build();
		if (logger.isDebugEnabled()) {
			logger.debug("httpclient initialize successfully");
		}
		// }

		
		return client;
	}

	public PageParser() {

	}

	public static String getHtmlPage(URI uri) {

		HttpGet httpget = new HttpGet(uri);
		// httpget.setURI(uri);
		// StringBuilder sb = new StringBuilder();
		// BufferedInputStream ins = null;

		// httpget.addHeader(new BasicHeader("Connection", "Keep-Alive"));
		// Header[] reqhs= httpget.getAllHeaders();
		// for(Header hd:reqhs){
		// log.info("request header: "+hd.toString());
		// }
		ResponseHandler<String> rh = new ResponseHandler<String>() {

			@Override
			public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {

				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() >= 400) {
					throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
				}
				HttpEntity entity = response.getEntity();
				if (entity == null) {
					throw new ClientProtocolException("response contains no content");
				}

				StringBuilder sb = new StringBuilder();
				BufferedInputStream ins = null;
				ins = new BufferedInputStream(entity.getContent());

				int temp = 0;
				byte[] buffer = new byte[1024];
				while (-1 != (temp = ins.read(buffer, 0, 1024))) {
					sb.append(new String(buffer, 0, temp, "utf-8"));
				}

				return sb.toString();
			}
		};
		String content = null;
		try {
			content = PageParser.getHttpClient().execute(httpget, rh);
		} catch (HttpResponseException e) {
			if (logger.isErrorEnabled()) {
				logger.error("http request error:", e);
			}
			// e.printStackTrace();
		} catch (ClientProtocolException e) {
			if (logger.isErrorEnabled()) {
				logger.error("http request error:", e);
			}
			// e.printStackTrace();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("http request error:", e);
			}
			// e.printStackTrace();
		}

		// try {
		// HttpResponse resp = PageParser.getHttpClient().execute(httpget);
		//
		// if (resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
		// ins = new BufferedInputStream(resp
		// .getEntity().getContent());
		//
		// int temp = 0;
		// byte[] buffer = new byte[1024];
		// while (-1 != (temp = ins.read(buffer, 0, 1024))) {
		// sb.append(new String(buffer, 0, temp, "utf-8"));
		// }
		//// Header[] hs= resp.getAllHeaders();
		//// for(Header hd:hs){
		//// log.info(hd.toString());
		//// }
		//
		// }
		//
		// } catch (ClientProtocolException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// // e.printStackTrace();
		// if (log.isErrorEnabled()) {
		// log.error("request webpage error: " + uri);
		// e.printStackTrace();
		// }
		// } finally {
		// if (log.isDebugEnabled()) {
		// log.debug("gethtmlpage end, httpget abort...");
		// }
		// try {
		// ins.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// httpget.abort();
		// }
		return content;
	}

	// public static ArrayList<URI> parseWebPage(String html) {
	//
	// return parseWebPage(html, new LinkFilter() {
	//
	// @Override
	// public ArrayList<URI> doFilter(ArrayList<URI> list) {
	// return list;
	// }
	// });
	// }

	public static List<URI> parseWebPage(String html) {

		Document rootDoc = Jsoup.parse(html);
		List<URI> list = new LinkedList<URI>();
		Elements eles = rootDoc.getElementsByTag("a");
		if (eles != null) {
			Iterator<Element> it = eles.iterator();
			while (it.hasNext()) {
				String temp = it.next().attr("href").trim();
				if (temp.matches("^http.*") || temp.matches("^[/.].*")) {
					try {
						list.add(new URI(temp));
					} catch (URISyntaxException e) {
						if (logger.isErrorEnabled()) {
							logger.error("Url syntax error, excluded", e);
						}
						// e.printStackTrace();
					}
				}
			}
			// list = filter.doFilter(list);
		}
		return list;
	}

	public static void writeFile(String filepath, String html) {

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(filepath);
			fos.write(html.getBytes("utf-8"));

			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		URI baseUri = null;
		URI uri = null;
		try {
			baseUri = new URI("http://www.example.org/domains");
			uri = new URI("http://www.example.org/domains");
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		String html = getHtmlPage(uri);
		System.out.println(html);
		List<URI> array = parseWebPage(html);

		for (int i = 0; i < array.size(); i++) {
			System.out.println(array.get(i));
			System.out.println(baseUri.resolve(array.get(i)));

		}

		// writeFile("D:\\temp\\test.html", html);
	}

}
