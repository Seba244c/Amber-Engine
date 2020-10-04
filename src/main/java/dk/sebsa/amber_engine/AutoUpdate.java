package dk.sebsa.amber_engine;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class AutoUpdate {
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	
	public static boolean needUpdate() {
		String json = "";
		try {
			json = getHTML("https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json");
		} catch (IOException e) { e.printStackTrace(); }
		if(json == null) return false;
		
		JSONObject obj = new JSONObject(json);
		return !ProjectManager.editorVersion.equals(obj.getString("newestRelease"));
	}
	
	public static String getHTML(String url) throws IOException {
	    HttpGet request = new HttpGet(url);
        // add request headers
        request.addHeader(HttpHeaders.USER_AGENT, "AmberEngine");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            
            if (entity != null) { return EntityUtils.toString(entity); }
        }
	    return null;
	}
	
	public static void close() {
        try {
			httpClient.close();
		} catch (IOException e) { e.printStackTrace(); }
    }
}
