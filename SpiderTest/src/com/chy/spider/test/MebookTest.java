package com.chy.spider.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.chy.spider.utils.PageParser;

public class MebookTest {

	private static Log log = LogFactory.getLog(MebookTest.class);

	public static void main(String[] args) throws ClientProtocolException,
			IOException {

		CloseableHttpClient client = createSSLClientDefault();
		HttpGet get = new HttpGet("http://mebook.cc/");

		// HttpGet get = new HttpGet("https://www.12306.cn/mormhweb/");
		CloseableHttpResponse response = client.execute(get);
		HttpEntity entity = response.getEntity();
		InputStream input = entity.getContent();
		OutputStream output = new FileOutputStream("D:/dat.txt");
		int len = 0;
		byte[] buff = new byte[1024];
		while (-1 != (len = input.read(buff))) {
			output.write(buff, 0, len);
		}
	}

	public static CloseableHttpClient createSSLClientDefault() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);
			// sslsf.ALLOW_ALL_HOSTNAME_VERIFIER
			return HttpClients
					.custom().setSSLSocketFactory(SSLConnectionSocketFactory.getSystemSocketFactory())
					.setHostnameVerifier(
							SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
					.build();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		}
		return HttpClients.createDefault();
	}
}
