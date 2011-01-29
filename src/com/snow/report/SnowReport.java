/**
 * 
 */
package com.snow.report;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get the snow report information from the pipes server.
 */
public class SnowReport {
	String resortName;
	String snowReport;
	String weatherImage;
	
	public SnowReport(String resort, String report, String weather) {
		resortName = resort;
		snowReport = report;
		weatherImage = weather;
	}
	
	static LinkedList<SnowReport> getSnowReport() throws UnsupportedEncodingException {
		HttpParams myParams = new BasicHttpParams();
		 
		// Setup Client
        HttpConnectionParams.setConnectionTimeout(myParams, 10000);
        HttpConnectionParams.setSoTimeout(myParams, 10000);
        DefaultHttpClient httpClient = new DefaultHttpClient(myParams);
        
        // Call Client
        String webServiceUrl = "http://pipes.yahoo.com/pipes/pipe.run?_id=463f79e92ff4293c5a6a3025336d00c9&_render=json";
        HttpGet request = new HttpGet(webServiceUrl);
        HttpResponse response = null;
        
        try	{
        	response = httpClient.execute(request);  
        	HttpEntity entity = response.getEntity();

        	String snowReportResponse = EntityUtils.toString(entity);
        	return parseSnowReport(snowReportResponse);
        } catch (Exception e) {
        	LinkedList<SnowReport> results = new LinkedList<SnowReport>();
        	SnowReport errorResort = new SnowReport("HttpRequest Fail: ", e.getMessage(), null);       	
        	results.add(errorResort);
        	
        	return results;
        }
	}
	
	static LinkedList<SnowReport> parseSnowReport(String snowReportText) throws JSONException {
		try	{
			JSONObject snowReportJson = new JSONObject(snowReportText);
			int feedCount = snowReportJson.getInt("count");
			JSONObject feedBody =  snowReportJson.getJSONObject("value");
			JSONArray feedItems = feedBody.getJSONArray("items");
			LinkedList<SnowReport> results = new LinkedList<SnowReport>();
			// Loop through the feed
			if (feedItems != null) {
				for (int i=0; i<feedCount; i++) {
					JSONObject feed = feedItems.getJSONObject(i);
					
					String resortName = feed.getString("name");
					String snowReport = feed.getString("description");
					
					String reportImage = null;
										
					JSONArray weatherList = feed.getJSONArray("weatherList");
					if(weatherList != null) {
						JSONObject weatherItem = weatherList.getJSONObject(0);
						JSONObject weatherData = weatherItem.getJSONObject("data");
						JSONObject parameters = weatherData.getJSONObject("parameters");
						JSONObject conditionsIcon = parameters.getJSONObject("conditions-icon");
						JSONArray iconLink = conditionsIcon.getJSONArray("icon-link");
						
						if(iconLink != null && iconLink.length() > 0) {							
							reportImage = iconLink.getString(0);						
						}
					}
					
					results.add(new SnowReport(resortName, snowReport, reportImage));
				}
			}
			
	    	return results;		
		} catch (Exception e) {
        	LinkedList<SnowReport> results = new LinkedList<SnowReport>();
        	SnowReport errorResort = new SnowReport("Request Parse Fail: ", e.getMessage(), null);  
        	results.add(errorResort);
        	
        	return results;
	    }	
	}	
	
	
}
