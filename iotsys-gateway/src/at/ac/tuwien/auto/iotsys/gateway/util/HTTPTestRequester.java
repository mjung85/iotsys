package at.ac.tuwien.auto.iotsys.gateway.util;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

public class HTTPTestRequester {

	private static String host = "[2001:629:2500:60:1:2:0:b2]";
	private static String url = "http://" + host + ":8080/VirtualDevices/virtualLight/";
	private static String watchServiceMakeUrl = "http://" + host + ":8080/watchService/make";
	private static String watchServiceUrl = "http://" + host + ":8080/watch0/add";
	private static String watchServicePollRefresh = "http://" + host + ":8080/watch0/pollRefresh";
	private static String groupCommJoinUrl = "http://" + host + ":8080/VirtualDevices/virtualLight/value/groupComm/joinGroup";
	private static String groupCommLeaveUrl = "http://" + host + ":8080/VirtualDevices/virtualLight/value/groupComm/leaveGroup";

	private static String payload = "{\"is\":\"iot:LightSwitchActuator\",\"nodes\":[{\"val\":true,\"tag\":\"bool\",\"writable\":true,\"name\":\"value\",\"displayName\":\"On/Off\",\"href\":\"value\"},{\"is\":\"iot:GroupComm\",\"tag\":\"ref\",\"name\":\"value groupComm\",\"href\":\"value/groupComm\"}],\"tag\":\"obj\",\"href\":\"/VirtualDevices/virtualLight/\"}"; 
	private static String groupCommPayload = "{\"val\":\"FF15::1\",\"tag\":\"str\"}";
	
	public static void main(String[] args) {
	  TestMode mode = TestMode.READ;
	   	  
	  HttpClient httpclient = new DefaultHttpClient();
	  	  	 
      try {
    	
    	  if(mode == TestMode.WATCH_SERVICE){
    		  // prepare watch        	  
//    		  HttpPost httpPost = new HttpPost(watchServiceMakeUrl);
//    		  httpPost.setHeader("content-type", "application/json");
//    		  httpPost.setHeader("accept", "application/json");
//    		  ResponseHandler<String> responseHandler = new BasicResponseHandler();
//              String responseBody = httpclient.execute(httpPost, responseHandler);
//              System.out.println("----------------------------------------");
//              System.out.println(responseBody);
//              System.out.println("----------------------------------------");
             
    		  HttpPost httpPost = new HttpPost(watchServiceUrl);
    		  httpPost.setHeader("content-type", "application/json");
    		  httpPost.setHeader("accept", "application/json");
    		  StringEntity entity = new StringEntity("{\"is\":\"obix:WatchIn\",\"nodes\":[{\"nodes\":[{\"val\":\"/VirtualDevices/virtualLight\",\"tag\":\"uri\"},{\"val\":\"/VirtualDevices/virtualTemperatureSensor\",\"tag\":\"uri\"},{\"val\":\"/VirtualDevices/smartmeter\",\"tag\":\"uri\"},{\"val\":\"/VirtualDevices/virtualPushButton\",\"tag\":\"uri\"}],\"tag\":\"list\",\"name\":\"hrefs\"}],\"tag\":\"obj\"}");
    		  httpPost.setEntity(entity);
    		  ResponseHandler<String> responseHandler = new BasicResponseHandler();
    		  String responseBody = httpclient.execute(httpPost, responseHandler);
              System.out.println("----------------------------------------");
              System.out.println(responseBody);
              System.out.println("----------------------------------------");    
                                           
    	  }
    	  for(int i=0; i< 1000; i++){
    		 if(mode == TestMode.READ){
		          HttpGet httpget = new HttpGet(url);
		          httpget.setHeader("content-type", "application/json");
		          httpget.setHeader("accept", "application/json");	
		          
		          System.out.println("executing read" + i + "," + httpget.getURI());	
		          // Create a response handler
		          ResponseHandler<String> responseHandler = new BasicResponseHandler();
		          String responseBody = httpclient.execute(httpget, responseHandler);
		          System.out.println("----------------------------------------");
		          System.out.println(responseBody);
		          System.out.println("----------------------------------------");
    		 }
    		 else if(mode == TestMode.WRITE){
    			 HttpPut httpPut = new HttpPut(url);
    			 httpPut.setHeader("content-type", "application/json");
    			 httpPut.setHeader("accept", "application/json");	
    			 StringEntity entity = new StringEntity(payload);
    			// entity.setChunked(true);
		         httpPut.setEntity(entity); 
		         System.out.println("executing write " + i + "," + httpPut.getURI());	
		         // Create a response handler
		         ResponseHandler<String> responseHandler = new BasicResponseHandler();
		         String responseBody = httpclient.execute(httpPut, responseHandler);
		         System.out.println("----------------------------------------");
		         System.out.println(responseBody);
		         System.out.println("----------------------------------------");
    		 }else if(mode == TestMode.WATCH_SERVICE){
    			 HttpPost httpPost = new HttpPost(watchServicePollRefresh);	       		
	       		 httpPost.setHeader("accept", "application/json");
	       		
	       		 ResponseHandler<String> responseHandler = new BasicResponseHandler();
	       		 String responseBody = httpclient.execute(httpPost, responseHandler);
                 System.out.println("----------------------------------------");
                 System.out.println(responseBody);
                 System.out.println("----------------------------------------");    
    		 }
    		 else if(mode == TestMode.GROUP_COMM && i%2 == 0){
    			 HttpPost httpPost = new HttpPost(groupCommJoinUrl);	       		
	       		 httpPost.setHeader("accept", "application/json");
	       		 httpPost.setHeader("content-type", "application/json");
	       		 StringEntity entity = new StringEntity(groupCommPayload);
    			 // entity.setChunked(true);
	       		 httpPost.setEntity(entity); 
	       		 ResponseHandler<String> responseHandler = new BasicResponseHandler();
	       		 String responseBody = httpclient.execute(httpPost, responseHandler);
                 System.out.println("----------------------------------------");
                 System.out.println(responseBody);
                 System.out.println("----------------------------------------");
    		 }
    		 else if(mode == TestMode.GROUP_COMM && i%2 == 1){
    			 HttpPost httpPost = new HttpPost(groupCommLeaveUrl);	       		
	       		 httpPost.setHeader("accept", "application/json");
	       		 httpPost.setHeader("content-type", "application/json");
	       		 StringEntity entity = new StringEntity(groupCommPayload);
    			 // entity.setChunked(true);
	       		 httpPost.setEntity(entity); 
	       		 ResponseHandler<String> responseHandler = new BasicResponseHandler();
	       		 String responseBody = httpclient.execute(httpPost, responseHandler);
                 System.out.println("----------------------------------------");
                 System.out.println(responseBody);
                 System.out.println("----------------------------------------");
    		 }
    	  }
      } catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} finally {
          // When HttpClient instance is no longer needed,
          // shut down the connection manager to ensure
          // immediate deallocation of all system resources
          httpclient.getConnectionManager().shutdown();
      }
  }
}

enum TestMode {
	WRITE, READ, GROUP_MANAGEMENT, WATCH_SERVICE, GROUP_COMM
}
