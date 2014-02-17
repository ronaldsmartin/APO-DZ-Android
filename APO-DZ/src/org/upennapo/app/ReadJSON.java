/**
 * 
 */
package org.upennapo.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author Ronald Martin
 * Using tutorial from http://www.vogella.com/tutorials/AndroidJSON/article.html
 */
public class ReadJSON {
	
	public static User getBrotherData(String urlString, String firstName, String lastName) {
		
		for (User brother : parseSpreadsheetJson(urlString)) {
			if (brother.Last_Name.equalsIgnoreCase(lastName) && 
					(brother.First_Name.equalsIgnoreCase(firstName))) {
				return brother;
			}
		}
		
		return null;
	}
	
	private static User[] parseSpreadsheetJson(String jsonData) {
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject map = parser.parse(jsonData).getAsJsonObject();
		JsonArray brotherJsonArray = map.getAsJsonArray("records");
		User[] brotherSheet = gson.fromJson(brotherJsonArray, User[].class);
		
		return brotherSheet;
	}
	
	public static Brother[] getDirectoryData(String urlString) {
		return parseDirectoryJson(downloadJsonData(urlString));
	}
	
	private static Brother[] parseDirectoryJson(String jsonData) {
		Gson gson = new Gson();
		JsonParser parser = new JsonParser();
		JsonObject map = parser.parse(jsonData).getAsJsonObject();
		JsonArray directoryJsonArray = map.getAsJsonArray("ActiveBrotherDirectory");
		Brother[] directory = gson.fromJson(directoryJsonArray, Brother[].class);
		
		return directory;
	}
	
	public static String downloadJsonData(String urlString) {
		StringBuilder builder = new StringBuilder();
	    HttpClient client = new DefaultHttpClient();
	    HttpGet httpGet = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				
			}
	    } catch (ClientProtocolException e) {
	    	e.printStackTrace();
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
		
		return builder.toString();
	}
}
