package com.chy.socket.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketClient {

	public static void main(String[] args) {
		Socket socket=null;
		try {
			socket=new Socket();
			socket.connect(new InetSocketAddress("127.0.0.1", 10060));
			System.out.println("Connected to server...sending echo string");
//			System.out.println("socket client:"+socket.getLocalAddress()+":"+socket.getLocalPort());
			OutputStream bout=socket.getOutputStream();
			InputStream in=socket.getInputStream();
			byte[] data="hello,this is socket client.".getBytes();
			bout.write(data);
			socket.shutdownOutput();
//			bw.flush();
//			bw.close();
			
			int totalBytesRcvd=0;
//			int bytesRcvd=0;
//			while(totalBytesRcvd<data.length){
//				if ((bytesRcvd = in.read(data, totalBytesRcvd, data.length-totalBytesRcvd)) == -1)
//					throw new SocketException("Connection closed prematurely");
//				totalBytesRcvd+=bytesRcvd;
//			}
			
			System.out.println("client send:"+new String(data));
			int x=0;
			StringBuilder sb=new StringBuilder();
			byte[] temp=new byte[1024];
			while(-1!=(x=in.read(temp, 0, 1024))){
				totalBytesRcvd+=x;
				sb.append(new String(temp,0,x));
			}
			System.out.println("client receive:"+sb);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
