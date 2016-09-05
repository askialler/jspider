package com.chy.socket.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketServerTest {

	public static void main(String[] args) {
		ServerSocket servSock=null;
		try {
			servSock=new ServerSocket(10060);
			
			while(true){
				System.out.println("tcp server started,port:10060");
				Socket clientSock=servSock.accept();
				System.out.println("client address:"+((InetSocketAddress)clientSock.getRemoteSocketAddress()).getHostName());
//				System.out.println("socket:"+so2.getLocalAddress()+":"+so2.getLocalPort());

				InputStream bin=clientSock.getInputStream();
				OutputStream out=clientSock.getOutputStream();
				int last=0;
				int totalBytes=0;
				StringBuilder sb=new StringBuilder();
				byte[] temp=new byte[1024];
				while(-1!=(last=bin.read(temp))){
					totalBytes+=last;
					out.write(temp, 0, last);
					sb.append(new String(temp,0,last));
				}
				clientSock.shutdownOutput();
				System.out.println("receive message:"+sb+",total bytes:"+totalBytes);
//				out.write(sb.toString().getBytes());
//				System.out.println("server write:"+sb);
//				clientSock.close();

//				so2.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				servSock.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
