package dk.sebsa.amber.entity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dk.sebsa.amber.Entity;
import dk.sebsa.amber.graph.Sprite;
import dk.sebsa.amber.math.Vector2f;

public class WorldManager {
	private static World currentWorld;
	
	public static final World getWorld() {
		return currentWorld;
	}
	

	public static void openWorld(World world)  {
		currentWorld = world;
		
		FileReader fr;
		try { fr = new FileReader(world.path); } catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		Entity.clear();
		BufferedReader br = new BufferedReader(fr);
		String line;
		
		
		try {
			Entity e = null;
			Component c = null;
			
			while((line = br.readLine()) != null) {
				line = line.replace("\t", "");
				String[] split = line.split("\"");
				
				if(line.startsWith("<e")) e = createEntity(split);
				else if(line.startsWith("<c")) c = e.addComponent(ComponentImporter.getComponent(split[1]));
				else if(line.startsWith("<v")) {
					String[] sep = line.split("<v ")[1].split("=");
					try {
						setVar(c, sep[0], sep[1].split("\"")[1]);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException
							| IllegalAccessException e1) {
						e1.printStackTrace();
					}
					
				}
				else if(line.startsWith("<p")) e.parent(Entity.find(split[1]));
			}
			Entity.recalculate();
			
			br.close();
			fr.close();
		} catch (IOException e1) { e1.printStackTrace(); }	
	}

	private static void setVar(Component c, String field, String v) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field f = c.getClass().getField(field);
		if(f == null) return;
		
		String[] splitName = f.getType().toString().split("\\.");
		String dataType = splitName[splitName.length - 1];

		if(dataType.equals("String")) { f.set(c, v); return; }
		else if(dataType.equals("int")) { f.set(c, Integer.parseInt(v)); return; }
		else if(dataType.equals("Vector2f")) { String[] split = v.split(" "); f.set(c, new Vector2f(Float.parseFloat(split[0]), Float.parseFloat(split[1]))); return; }
		else if(dataType.equals("Sprite")) {  f.set(c, Sprite.getSprite(v)); return; }
		else if(dataType.equals("boolean")) { f.set(c, Boolean.parseBoolean(v)); return; }
		else if(dataType.equals("float")) { f.set(c, Float.parseFloat(v)); return; }
	}

	private static Entity createEntity(String[] split) {
		Entity e = new Entity(split[1]);
		
		String[] tSplit = split[3].split(" ");
		e.setPosition(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));
		
		tSplit = split[5].split(" ");
		e.setPosition(Float.parseFloat(tSplit[0]), Float.parseFloat(tSplit[1]));
		
		e.setRotation(Float.parseFloat(split[7]));
		e.setId(split[9]);
		
		e.depth = Float.parseFloat(split[11]);
		e.tag = split[13];
		
		return e;
	}
	
	public static void saveWorld() throws IOException {
		File f = new File(currentWorld.path);
		FileWriter fw = new FileWriter(f);
		
		// Get entities
		List<Entity> entities = new ArrayList<Entity>();
		
		Entity master = Entity.master();
		entities.add(master);
		
		while(entities.size() > 0) {
			Entity e = entities.get(0);
			List<Entity> children = e.getChildren();
			
			if(children.size() > 0) for(int i = 0; i < children.size(); i++) {
				entities.add(0, children.get(i));
			}
			
			if(e == master) { entities.remove(entities.size()-1); continue; }
			
			writeTransform(e, fw);
			
			List<Component> c = e.getComponents();
			for(int i = 0; i < c.size(); i++) { writeComponent(c.get(i), fw); }
			if(e.getParent() != null) {
				fw.write("\t<p id=\"" + e.getParent().getId() + "\">\n</e>\n");
			} else fw.write("</e>\n");
			entities.remove(e);
		}
		fw.close();
		currentWorld.saved = true;
	}
	
	private static void writeComponent(Component c, FileWriter fw) throws IOException {
		Class<?> cls = c.getClass();
		String line = "\t<c name=\"" + cls.getSimpleName() + "\">\n";
		fw.write(line);
		
		Field[] fields = cls.getFields();
		for(Field field : fields) {
			line = "\t\t<v " + field.getName() + "=\"";
			
			try {
				String[] t = field.getType().toString().split("\\.");
				if(t[t.length - 1].equals("Vector2f")) {
					Vector2f v = (Vector2f) field.get(c);
					line += v.x + " " + v.y;
				} else if(t[t.length - 1].equals("Sprite")) {
					if(field.get(c) != null) line += ((Sprite) field.get(c)).name;
				} else line += field.get(c);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			
			fw.write(line + "\">\n");
		}
	}

	private static void writeTransform(Entity e, FileWriter fw) throws IOException {
		String line = "<e name=\"" + e.name + "\" ";
		line += "pos=\"" + e.getPosition().x + " " + e.getPosition().y + "\" ";
		line += "scale=\"" + e.getScale().x + " " + e.getScale().y + "\" ";
		line += "r=\"" + e.getRotation() + "\" ";
		line += "i=\"" + e.getId() + "\" ";
		line += "d=\"" + e.depth + "\" ";
		line += "t=\"" + e.tag + "\">\n";
		fw.write(line);
	}
}
