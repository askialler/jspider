package com.chy.spider.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5Encoder {

//	private static Log log = LogFactory.getLog(MD5Encoder.class);
	private static Logger logger=LoggerFactory.getLogger(MD5Encoder.class);
	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

	public static String getMD5Digest(String url) {

		StringBuilder result = null;
		try {
			byte[] digest = null;
			MessageDigest md = MessageDigest.getInstance("md5");
			md.reset();
			digest = md.digest(url.getBytes());
			// base64
			// BASE64Encoder be=new BASE64Encoder();
			// String base64 = be.encode(digest);
			// long begin = System.currentTimeMillis();
			result = new StringBuilder(digest.length * 2);
			// String a1=DatatypeConverter.printHexBinary(digest);
			for (byte b : digest) {
				result.append(hexCode[(b >> 4) & 0xF]);
				result.append(hexCode[(b & 0xF)]);
			}

			if (logger.isDebugEnabled()) {
				logger.debug("url: " + url + " ,md5 digest: " + result.toString());
			}
			// long end = System.currentTimeMillis();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
