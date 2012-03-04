package edu.uri.ele.capstone.monitorfleet.util;

import java.net.HttpURLConnection;
import java.net.URL;

public class Utilities {
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
