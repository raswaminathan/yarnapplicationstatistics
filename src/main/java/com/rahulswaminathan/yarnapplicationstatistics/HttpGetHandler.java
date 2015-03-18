package com.rahulswaminathan.yarnapplicationstatistics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by rahulswaminathan on 1/30/15.
 */
public class HttpGetHandler {

    private final String USER_AGENT = "Mozilla/5.0";
    private String url;
    public HttpGetHandler() {
        this.url = null;
    }

    public HttpGetHandler(String url) {
        this.url = url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String sendGet()  {
        //    HttpClient client = HttpClientBuilder.create().build();
        //    HttpGet request = new HttpGet(url);
        try {
            if (url == null) {
                System.err.println("URL IS NULL");
                return null;
            }

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", USER_AGENT);

            // add request header
            //   request.addHeader("User-Agent", USER_AGENT);

	/*/HttpResponse response = null;
	try {
        	response = client.execute(request);
	} catch(Exception e) {
		System.out.println("Failed Request: " + response);
		e.printStackTrace();
		System.exit(1);
	}

        BufferedReader rd = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));*/

            int code = con.getResponseCode();
            //System.out.println("Get request to " + url + " responded with a code: " + code);

            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));

            StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            rd.close();
            return result.toString();
        }catch(Exception e) {
            System.err.println("EXCEPTION: " + e.toString());
            return null;
        }
    }
}