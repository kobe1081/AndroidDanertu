package wl.codelibrary.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Build.VERSION;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetInfoUtil {

	private final int NETWORK_CLASS_UNKNOWN = 0x123;
	private final int NETWORK_CLASS_2_G = 0x124;
	private final int NETWORK_CLASS_3_G = 0x125;
	private final int NETWORK_CLASS_4_G = 0x126;

	private static final class AppHolder{
		private static NetInfoUtil netInfoUtil = new NetInfoUtil();
	}

	public static NetInfoUtil getInstance(){
		return AppHolder.netInfoUtil;
	}

	@TargetApi(Build.VERSION_CODES.FROYO)
	/**
	 * @param networkType
	 * @return
	 */
	public final int getNetworkClass(int networkType) {
		int result = NETWORK_CLASS_UNKNOWN;
		if (networkType == TelephonyManager.NETWORK_TYPE_GPRS
				||networkType == TelephonyManager.NETWORK_TYPE_EDGE
				||networkType == TelephonyManager.NETWORK_TYPE_CDMA
				||networkType == TelephonyManager.NETWORK_TYPE_1xRTT
				||networkType == TelephonyManager.NETWORK_TYPE_IDEN) {

			result = NETWORK_CLASS_2_G;
		}else if(networkType == TelephonyManager.NETWORK_TYPE_UMTS
				||networkType == TelephonyManager.NETWORK_TYPE_EVDO_0
				||networkType == TelephonyManager.NETWORK_TYPE_EVDO_A
				||networkType == TelephonyManager.NETWORK_TYPE_HSDPA
				||networkType == TelephonyManager.NETWORK_TYPE_HSUPA
				||networkType == TelephonyManager.NETWORK_TYPE_HSPA){
			result = NETWORK_CLASS_3_G;

		}else if(VERSION.SDK_INT >= 9
				&& networkType == TelephonyManager.NETWORK_TYPE_EVDO_B){
			result = NETWORK_CLASS_3_G;

		}else if(VERSION.SDK_INT >= 11
				&& networkType == TelephonyManager.NETWORK_TYPE_EHRPD){
			result = NETWORK_CLASS_3_G;

		}else if(VERSION.SDK_INT >= 13
				&& networkType == TelephonyManager.NETWORK_TYPE_HSPAP){
			result = NETWORK_CLASS_3_G;

		}else if(VERSION.SDK_INT >= 11
				&& networkType == TelephonyManager.NETWORK_TYPE_LTE){
			result = NETWORK_CLASS_4_G;

		}
		return result;
	}

	/**
	 * @param ip ??????????????www.baidu.com
	 * @return ???IP???
	 */
	public final String ping(String ip) {

		String result = null;
		try {
//			String ip = "www.baidu.com";// ping ????????????????????????????
			Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);// ping???1??
			// ???ping??????????????
			InputStream input = p.getInputStream();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					input));
			StringBuffer stringBuffer = new StringBuffer();
			String content = "";
			String ipLine = "";
			while ((content = in.readLine()) != null) {
				stringBuffer.append(content);
				Pattern pattern = Pattern
						.compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))");
				Matcher matcher = pattern.matcher(stringBuffer.toString());
				if (matcher.find()) {
					ipLine = matcher.group();
				}
			}
			Log.d("------ping-----",
					"result content : " + stringBuffer.toString());
			// ping????
			int status = p.waitFor();
			if (status == 0 || ipLine.length() > 6) {
				result = "success";
				return ipLine;
			} else {
				result = "failed";
			}
		} catch (IOException e) {
			result = "IOException";
		} catch (InterruptedException e) {
			result = "InterruptedException";
		} finally {
			Log.d("----result---", "result = " + result);
		}
		return null;
	}

	/**
	 * ???????ip
	 *
	 * @return
	 */
	public final String getLocalIPAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()
							&& (inetAddress instanceof Inet4Address)) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return null;
	}

}
