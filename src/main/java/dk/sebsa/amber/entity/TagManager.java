package dk.sebsa.amber.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TagManager {
	private static byte loaded = 0;
	private static List<String> tags = new ArrayList<>();
	public static String path = "";
	
	public static void loadTags(String filePath) {
		// Run once
		if(loaded == 1) return;
		else loaded = 1;
		
		// Load file
		path = filePath;
		BufferedReader br = null;
		
		try {
			if(filePath.startsWith("/")) {
				InputStreamReader isr =  new InputStreamReader(TagManager.class.getResourceAsStream(filePath));
				br = new BufferedReader(isr);
			} else {
				File file = new File(filePath);
				
				br = new BufferedReader(new FileReader(file));
			}
			
			// Get tags
			tags.add("Untagged");
			while(true) {
				String line = br.readLine();
				if(line==null) break;
				if(line.equals("\n")) continue;
				tags.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			if(br!=null) br.close();
		} catch (IOException e) {e.printStackTrace(); }
	}
	
	public static final List<String> getTags() {
		return tags;
	}
	
	public static void addTag(String tag) {
		if(!tags.contains(tag)) tags.add(tag);
	}
	
	public static void removeTag(String tag) {
		tags.remove(tag);
	}

	public static void save(String filePath) {
		if(filePath.startsWith("/")) return;
	    
	    try {
	    	FileWriter fileWriter = new FileWriter(filePath);
	    	
	    	for(String tag : tags) {
	    		if(!tag.equals("Untagged")) fileWriter.write(tag+"\n");
	    	}
	    	
			fileWriter.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
}
