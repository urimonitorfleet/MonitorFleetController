package edu.uri.ele.capstone.monitorfleet.util;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * General class to hold static utility methods
 * 
 * @author bkintz
 *
 */
public class Utilities {
	
	/**
	 * Determine if a URL exists on the current network
	 * 
	 * @param url The URL to check
	 * @return 
	 */
	public static boolean UrlExists(String url){
		try {
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("HEAD");
			return (conn.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			return false;
		}
	}
}
