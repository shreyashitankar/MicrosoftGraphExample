// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.

package graphtutorial;

import java.util.LinkedList;
import java.util.List;
import java.util.*;
import com.microsoft.graph.logger.DefaultLogger;
import com.microsoft.graph.logger.LoggerLevel;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.Message;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.Option;
import com.microsoft.graph.options.QueryOption;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.graph.requests.extensions.IEventCollectionPage;
import com.microsoft.graph.requests.extensions.IMessageCollectionPage;
import com.microsoft.graph.requests.extensions.IUserCollectionPage;
import com.microsoft.graph.http.CustomRequest;
import java.io.BufferedInputStream;
import org.json.*;
import com.microsoft.graph.options.HeaderOption;
import java.util.stream.*;
import java.util.stream.Stream;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.ByteArrayOutputStream;
//import src.main.resources.org.apache.commons.io.FileUtils;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
/**
 * Graph
 */
public class Graph {
	
    private static IGraphServiceClient graphClient = null;
    private static SimpleAuthProvider authProvider = null;
    private static CustomRequest CR = null;
    private static GraphServiceClient crclient = null;
    
    private static void ensureGraphClient(String accessToken) {
        if (graphClient == null) {
            // Create the auth provider
            authProvider = new SimpleAuthProvider(accessToken);

            // Create default logger to only log errors
            DefaultLogger logger = new DefaultLogger();
            logger.setLoggingLevel(LoggerLevel.ERROR);

            // Build a Graph client
            graphClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .logger(logger)
                .buildClient();
        }
       
    }

    public static List<User> getUser(String accessToken) {
        ensureGraphClient(accessToken);

        // GET /me to get authenticated user
        IUserCollectionPage me = graphClient
            .users()
            .buildRequest()
            .get();

        return me.getCurrentPage();
    }

    // <GetEventsSnippet>
    public static List<Message> getEvents(String accessToken) {
        ensureGraphClient(accessToken);

//        // Use QueryOption to specify the $orderby query parameter
//        final List<Option> options = new LinkedList<Option>();
//        // Sort results by createdDateTime, get newest first
//        options.add(new QueryOption("orderby", "createdDateTime DESC"));

        // GET /me/events
//        IEventCollectionPage eventPage = graphClient
//            .me()
//            .events()
//            .buildRequest(options)
//            .select("subject,organizer,start,end")
//            .get();
        
        IMessageCollectionPage eventPage = graphClient.me().messages()
        		.buildRequest()
        		.select("sender,subject,body,id")
        		.get();
        
        return eventPage.getCurrentPage();
    }
    
    
    
    
    
    public static void returnmime(String messageId,String accessToken, String userid) {
    	ensureGraphClient(accessToken);
    	
    	if(messageId != null) {
    		String URL = "https://graph.microsoft.com/v1.0/me/messages/"+messageId+"/$value";
    	
    	try {
    		URL url = new URL(URL);
    		HttpURLConnection http = (HttpURLConnection) url.openConnection();
    		http.setRequestMethod("GET");
            http.addRequestProperty("Accept", "application/json;odata.metadata=none");
            http.addRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Authorization", "Bearer "+accessToken);
            http.setDoOutput(true);
            http.setDoInput(true);
            int responseCode = http.getResponseCode();
            System.out.println("GET Response Code :: " + responseCode);
            BufferedReader in = new BufferedReader(new InputStreamReader(
					http.getInputStream()));
            
            String s = null;
            String filename = messageId.substring(messageId.length() - 9);
			filename = filename.substring(0, filename.length() - 1);
			System.out.println(filename);
			String toSave = "src/main/resources/"+filename+".eml";
			
			File myfile = new File(toSave);        
	        FileUtils.touch(myfile);
	        BufferedWriter out = new BufferedWriter( 
                    new FileWriter(myfile)); 
	        String newLine = System.getProperty("line.separator");
            while ((s=in.readLine())!=null)
                {
            			out.write(s);
            			out.write(newLine);
                       System.out.println(s);
                }
           
            
            
            out.close();
        
		}catch (Exception e) {
			System.out.println("get not WORKED *X*"+e);
		}
            
    	}  
    }

}