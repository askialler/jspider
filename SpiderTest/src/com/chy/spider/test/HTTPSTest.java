package com.chy.spider.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.DatatypeConverter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;

import sun.misc.BASE64Encoder;

//import org.json.JSONObject;

public class HTTPSTest {

	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

	public static void main(String[] args) {

		// httpstest2();
		// httpsRequest("https://www.zhihu.com/question/26488686","GET",null);
		// try {
		// String text=httpsRequest2("https://www.zhihu.com/question/26488686");
		// System.out.println(text);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		// httpstest1();
		// try {
		// URI uri=new URI("http://mebook.cc/10166.html#respond");
		// URIBuilder ub=new URIBuilder(uri);
		// ub.setFragment(null);
		// uri=ub.build();
		// System.out.println(uri.toString());
		// } catch (URISyntaxException e) {
		// e.printStackTrace();
		// }

		String str = "a";
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("md5");
			md.reset();
			digest = md.digest(str.getBytes());
			// base64 
			// BASE64Encoder be=new BASE64Encoder();
			// String base64 = be.encode(digest);
//			long begin = System.currentTimeMillis();
			StringBuilder result = new StringBuilder(digest.length * 2);
			// String a1=DatatypeConverter.printHexBinary(digest);
			for (byte b : digest) {
				result.append(hexCode[(b >> 4) & 0xF]);
				result.append(hexCode[(b & 0xF)]);
			}

//			long end = System.currentTimeMillis();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

	}

	public static void httpstest1() {
		try {

			SSLContext sslCxt = SSLContexts.custom().useSSL().loadTrustMaterial(null, new TrustStrategy() {

				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslCxt,
					SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
					.register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", sslsf).build();
			PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(registry);

			CloseableHttpClient client = HttpClients.custom().setConnectionManager(connMgr)
					// .setSSLSocketFactory(sslsf)
					// .setHostnameVerifier(
					// SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
					.build();
			String url = "https://www.zhihu.com/question/26488686";
			// String url =
			// "https://www.zhihu.com/question/26488686#answer-42329886";

			HttpGet hGet = new HttpGet(url);
			HttpResponse resp = client.execute(hGet);
			HttpEntity entity = resp.getEntity();
			if (null != entity) {
				String responseContent = EntityUtils.toString(entity, "UTF-8");
				// JSONObject demoJson = new JSONObject(responseContent);
				System.out.println(responseContent);
				// EntityUtils.consume(entity);
			}

		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void httpstest2() {

		// URLConnection conn=url.openConnection();
		// HttpURLConnection conn2=url.openConnection();
		InputStreamReader isr = null;
		try {
			URI uri = new URI("https://www.zhihu.com/question/26488686");
			URL url = uri.toURL();
			HttpsURLConnection httpsConn = (HttpsURLConnection) url.openConnection();
			httpsConn.setDoInput(true);
			httpsConn.setDoOutput(true);
			httpsConn.setRequestMethod("GET");

			isr = new InputStreamReader(httpsConn.getInputStream());

			int readResp = isr.read();
			while (readResp != -1) {
				System.out.println((char) readResp);
				readResp = isr.read();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} finally {

			try {
				isr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new TrustAnyTrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setSSLSocketFactory(ssf);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod(requestMethod);

			// 当outputStr不为null时向输出流写数据
			if (null != outputStr) {
				OutputStream outputStream = conn.getOutputStream();
				// 注意编码格式
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}
			// 从输入流读取返回内容
			InputStream inputStream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();
			return buffer.toString();
		} catch (ConnectException ce) {
			System.out.println("连接超时：" + ce);
			// log.error("连接超时：{}", ce);
		} catch (Exception e) {
			System.out.println("https请求异常：" + e);
			e.printStackTrace();
			// log.error("https请求异常：{}", e);
		}
		return null;
	}

	public static String httpsRequest2(String url) throws Exception {
		InputStream in = null;
		OutputStream out = null;
		byte[] buffer = new byte[4096];
		String str_return = "";
		try {
			// URL console = new URL(url);
			URL console = new URL(new String(url.getBytes("utf-8")));

			HttpURLConnection conn = (HttpURLConnection) console.openConnection();
			// 如果是https
			if (conn instanceof HttpsURLConnection) {
				SSLContext sc = SSLContext.getInstance("TLS");
				sc.init(null, new TrustManager[] { new TrustAnyTrustManager() }, new java.security.SecureRandom());
				((HttpsURLConnection) conn).setSSLSocketFactory(sc.getSocketFactory());
				((HttpsURLConnection) conn).setHostnameVerifier(new TrustAnyHostnameVerifier());
			}
			// conn.setRequestProperty("Content-type", "text/html");
			// conn.setRequestProperty("Accept-Charset", "GBK");
			// conn.setRequestProperty("contentType", "GBK");
			conn.setRequestMethod("GET");
			// conn.setDoOutput(true);
			// conn.setRequestProperty("User-Agent", "directclient");
			// PrintWriter outdate = new PrintWriter(new
			// OutputStreamWriter(conn.getOutputStream(),"utf-8"));
			// outdate.println(url);
			// outdate.close();
			conn.connect();
			InputStream is = conn.getInputStream();
			DataInputStream indata = new DataInputStream(is);
			String ret = "";

			while (ret != null) {
				ret = indata.readLine();
				if (ret != null && !ret.trim().equals("")) {
					str_return = str_return + new String(ret.getBytes("ISO-8859-1"), "utf-8");
				}
			}
			conn.disconnect();
		} catch (Exception e) {
			throw e;
		} finally {
			try {
				in.close();
			} catch (Exception e) {
			}
			try {
				out.close();
			} catch (Exception e) {
			}
		}
		return str_return;
	}
}

class TrustAnyTrustManager implements X509TrustManager {

	@Override
	public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

	}

	@Override
	public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {

	}

	@Override
	public X509Certificate[] getAcceptedIssuers() {
		// TODO Auto-generated method stub
		return new X509Certificate[] {};
	}

}

class TrustAnyHostnameVerifier implements HostnameVerifier {
	public boolean verify(String hostname, SSLSession session) {
		return true;
	}
}
