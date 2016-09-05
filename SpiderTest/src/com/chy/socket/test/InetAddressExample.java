package com.chy.socket.test;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class InetAddressExample {

	/**
	 * get the MAC Address
	 * 
	 * @param interface1
	 * @return
	 * @throws SocketException
	 */
	public static String getMACAddress(NetworkInterface interface1)
			throws SocketException {
		StringBuilder macStr = new StringBuilder();
		byte[] macbyte = interface1.getHardwareAddress();
		if (macbyte != null) {
			int flag = 0;
			for (byte b : macbyte) {
				int i = b;
				if (i < 0) {
					i += 256;
				}
				macStr.append(String.format("%02x", i));
				if (flag < 5) {
					macStr.append(":");
					flag++;
				}
			}
		}
		return macStr.toString();
	}

	public static void main(String[] args) throws UnsupportedEncodingException {
		try {
			Enumeration<NetworkInterface> iflist = NetworkInterface
					.getNetworkInterfaces();
			if (iflist != null) {
				while (iflist.hasMoreElements()) {
					NetworkInterface ni = iflist.nextElement();
					System.out.println("NetworkInterface getDisplayName: "
							+ ni.getDisplayName());
					System.out.println("NetworkInterface getName: "
							+ ni.getName());
					byte[] mac = ni.getHardwareAddress();
					if (mac != null) {
						System.out.println("NetworkInterface mac address: "
								+ getMACAddress(ni));
					}
					Enumeration<InetAddress> addrList = ni.getInetAddresses();
					while (addrList.hasMoreElements()) {
						InetAddress addr = addrList.nextElement();
						System.out
								.print("\taddress: "
										+ ((addr instanceof Inet4Address) ? "ipv4 "
												: addr instanceof Inet6Address ? "ipv6 "
														: "? "));
						System.out.println("getHostAddress: "
								+ addr.getHostAddress());
					}
				}
				
				try {
					NetworkInterface ni = NetworkInterface.getByInetAddress(InetAddress.getByName("192.168.252.22"));
					System.out.println(ni.getDisplayName());
					System.out.println(getMACAddress(ni));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
