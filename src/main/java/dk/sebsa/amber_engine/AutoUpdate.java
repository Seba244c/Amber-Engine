package dk.sebsa.amber_engine;

import java.awt.Container;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import dk.sebsa.amber_engine.utils.Downloader;

public class AutoUpdate {
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	private static JSONObject download;
	
	private static JFrame frame;
	private static Container pane;
	
	public static boolean needUpdate() {
		String json = "";
		try {
			json = getHTML("https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json");
		} catch (IOException e) { e.printStackTrace(); }
		if(json == null) return false;
		
		download = new JSONObject(json);
		return !ProjectManager.editorVersion.equals(download.getString("newestRelease"));
	}
	
	public static void update() {
		// Window
		initWindow();
		
		// Download
		Downloader downloader = new Downloader();
		try {
			new File(ProjectManager.workspaceDir + ".temp").mkdir();
			downloader.download(new URL(download.getString("download")), new File(ProjectManager.workspaceDir+".temp/newest.jar"));
			downloader.download(new URL(download.getString("updater")), new File(ProjectManager.workspaceDir+".temp/updater.jar"));
		} catch (MalformedURLException | JSONException e) { e.printStackTrace(); }
		
		// Close
		frame.dispose();
		
		// Run
		Runtime rt = Runtime.getRuntime();
		try {
			String thisFile = AutoUpdate.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
			String updater = ProjectManager.workspaceDir+".temp/updater.jar";
			rt.exec("java -jar \"" + updater + "\" \"" + thisFile +"\"");
		} catch (IOException | URISyntaxException e) { e.printStackTrace(); }
		
		System.exit(0);
	}
	
	private static void initWindow() {
		frame = new JFrame("Amber Engine Updater");
		frame.pack();
		
		frame.setSize(300, 350);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane = frame.getContentPane();
		
		createPanel();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static void createPanel() {
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 3L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
		};
		panel.setLayout(null);
		
		panel.add(new JLabel("Updating Amber Engine", JLabel.CENTER));
		pane.add(panel);
	}
	
	public static String getHTML(String url) throws IOException {
	    HttpGet request = new HttpGet(url);
        // add request headers
        request.addHeader(HttpHeaders.USER_AGENT, "AmberEngine");

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            HttpEntity entity = response.getEntity();
            
            if (entity != null) { return EntityUtils.toString(entity); }
        } catch (UnknownHostException e) {
			return null;
		}
	    return null;
	}
	
	public static void close() {
        try {
			httpClient.close();
		} catch (IOException e) { e.printStackTrace(); }
    }
}
