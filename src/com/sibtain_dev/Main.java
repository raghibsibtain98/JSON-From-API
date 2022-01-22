package com.sibtain_dev;

import org.json.JSONObject;
import org.w3c.dom.ls.LSOutput;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class Main {
    public static TreeMap<Integer, String[]> mp;
    public static void parser(String responseBody)
    {
        JSONObject body = new JSONObject(responseBody);
        //extracting the products object and the number of objects in product object
        JSONObject products = body.getJSONObject("products");

        //iterator will get the keys from the JSON which is also an object
        Iterator itr = products.keys();
        mp = new TreeMap<Integer, String[]>();
        //int count = 0;
        while(itr.hasNext())
        {
            String key = (String) itr.next();
            JSONObject device = products.getJSONObject(key);
            int popularity = Integer.parseInt((String)device.get("popularity"));
            HashMap<String,String> nmp = new HashMap<>();
            String[] arr = new String[]{(String)device.get("title"),(String) device.get("price")};
            mp.put(popularity, arr);
            //System.out.println(device.get("title") + " " + device.get("price"));
            //count++;
        }
        //System.out.println(count);
    }

    private static HttpsURLConnection connection;

    public static void main(String[] args) {
        BufferedReader reader;
        String line; //to read each line
        StringBuffer responseContent = new StringBuffer();

        try {
            URL url = new URL("https://s3.amazonaws.com/open-to-cors/assignment.json");

            //opening the connection
            connection = (HttpsURLConnection) url.openConnection();

            //getting response from connection
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(15000); //if connection not successful after 12 secs then return a timeout
            connection.setReadTimeout(15000);

            int status = connection.getResponseCode();
            //System.out.println(status);
            //from http url connection response that we get is an input stream so in order to
            //read input stream we call a buffer reader

            if(status>299){
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            else{
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            while((line=reader.readLine())!=null){
                responseContent.append(line);
            }
            reader.close();
            parser(responseContent.toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        /*HttpClient client = HttpClient.newHttpClient();
        HttpRequest request  = HttpRequest.newBuilder().uri(URI.create("https://s3.amazonaws.com/open-to-cors/assignment.json")).build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                .join();*/
        Set<Integer> it = mp.descendingKeySet();
        System.out.format("%55s %10s","Title","Price");
        System.out.println("");
        for(int i: it)
        {
            System.out.format("%55s %10s",mp.get(i)[0],mp.get(i)[1]);
            System.out.println("");
        }
    }
}
