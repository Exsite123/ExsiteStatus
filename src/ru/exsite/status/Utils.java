package ru.exsite.status;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Utils {
	public static void setStatus(String status, int id, String token) throws Exception { 
		String Message = urlEncode(status);
		URLConnection connection = new URL("https://api.vk.com/method/status.set?text="+Message+"&group_id="+id+"&access_token="+token).openConnection();
	    InputStream is = connection.getInputStream();
	    InputStreamReader reader = new InputStreamReader(is);
	    char[] buffer = new char[256];
	    int rc;
	    StringBuilder sb = new StringBuilder();
	    while ((rc = reader.read(buffer)) != -1)
	        sb.append(buffer, 0, rc);
	    reader.close();
	    String response = getJson(sb.toString());
	    if(response != null) {
	    	System.out.println("[ExsiteStatus] Error: "+response);
	    }
	}
	
	public static String urlEncode(final String text) {
	    try {
	        return URLEncoder.encode(text, "UTF-8");
	    } catch (UnsupportedEncodingException e) {
	        return text;
	    }
	}
	
	public static String getUnixTime(long l, String format) {
		SimpleDateFormat SDF = new SimpleDateFormat(format);
		String date = SDF.format(new Date(l));
		if(date != null) {
			return date;
		}
		return null;
	}
	
	public static String getJson(String Json) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject jsonObj = (JSONObject) parser.parse(Json);
		JSONObject jsonresponse = (JSONObject) jsonObj.get("error");
		if(jsonresponse != null) {
			return String.valueOf(jsonresponse.get("error_msg"));
		}
		return null;
	}
}
