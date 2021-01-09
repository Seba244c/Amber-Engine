package dk.sebsa.amber_engine;

import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.swing.JButton;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dk.sebsa.amber_engine.utils.Downloader;

public class AutoUpdate {
	private static final CloseableHttpClient httpClient = HttpClients.createDefault();
	private static JSONObject download;
	
	private static JFrame frame;
	private static Container pane;
	private static byte userWantUpdate = 0;
	private static byte con = 0;
	
	public static boolean needUpdate() {
		if(download == null) {
			String json = "";
			try {
				json = getHTML("https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json");
			} catch (IOException e) { e.printStackTrace(); }
			if(json == null) return false;
			
			try {
				download = new JSONObject(json);
			} catch(JSONException e) { e.printStackTrace(); return false; }
		}
		return !Main.editorVersion.equals(download.getString("newestRelease"));
	}
	
	public static JSONArray getChangelog() {
		if(download == null) {
			String json = "";
			try {
				json = getHTML("https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json");
			} catch (IOException e) { e.printStackTrace(); }
			if(json == null) return new JSONArray();
			
			try {
				download = new JSONObject(json);
			} catch(JSONException e) { e.printStackTrace(); return new JSONArray(); }
		}
		
		if(Main.editorVersion.contains("-SNAPSHOT")) return download.getJSONArray("changeLog-" + Main.editorVersion.split("-")[0]);
		return download.getJSONArray("changeLog-" + Main.editorVersion);
	}
	
	public static String getUpdateName() {
		if(download == null) {
			String json = "";
			try {
				json = getHTML("https://raw.githubusercontent.com/Seba244c/Amber-Engine/master/download.json");
			} catch (IOException e) { e.printStackTrace(); }
			if(json == null) return "";
			
			try {
				download = new JSONObject(json);
			} catch(JSONException e) { e.printStackTrace(); return ""; }
		}
		
		if(Main.editorVersion.contains("-SNAPSHOT")) return download.getString("name-" + Main.editorVersion.split("-")[0]);
		return download.getString("name-" + Main.editorVersion);
	}
	
	public static void test() {
		initWindow();
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		frame.dispose();
	}
	
	public static void update() {
		// Window
		initWindow();
		
		if(userWantUpdate == 1) {
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
		frame.dispose();
	}
	
	private static void initWindow() {
		frame = new JFrame("Amber Engine Updater");
		frame.pack();
		
		frame.setSize(300, 350);
		frame.setResizable(false);
		frame.setIconImages(Main.swingIcon);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane = frame.getContentPane();
		
		createPanel();
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
		
		// Want to update
		JLabel h = new JLabel("Update avaible", JLabel.CENTER);
		h.setFont(new Font("Serif", Font.PLAIN, 26));
		h.setBounds(5, 10, 280, 40);
		
		JLabel h2 = new JLabel("Wanna Update?", JLabel.CENTER);
		h2.setFont(new Font("Serif", Font.PLAIN, 20));
		h2.setBounds(5, 35, 280, 40);
		
		JLabel g = new JLabel("<html>From: "+Main.editorVersion+",<br/>To: " +download.getString("newestRelease") + "</html>", JLabel.CENTER);
		g.setFont(new Font("Serif", Font.PLAIN, 18));
		g.setBounds(5, 55, 280, 80);
		
		JButton y = new JButton("Yes");
		y.setBounds(110, 250, 80, 20);
		JButton n = new JButton("No");
		n.setBounds(110, 280, 80, 20);
		
		y.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String c = e.getActionCommand();
				if(c.equals("Yes")) {					
					// Remove YN, remove h2
					y.setBounds(-10, -10, 1, 1);
					n.setBounds(-10, -10, 1, 1);
					h2.setBounds(-10, -10, 1, 1);
					
					// Change text
					h.setText("Updating Amber Engine");
					
					userWantUpdate = 1;
					con = 1;
				}
			}
		});
		
		n.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String c = e.getActionCommand();
				if(c.equals("No")) {
					con = 1;
				}
			}
		});
		
		panel.add(h);
		panel.add(g);
		panel.add(y);
		panel.add(n);
		panel.add(h2);
		pane.add(panel);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		while(con == 0) {
			try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
			
			if(frame == null) break;
		}
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
