package dk.sebsa.amber_engine.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import dk.sebsa.amber.entity.Component;
import dk.sebsa.amber.entity.components.SpriteRenderer;

public class ComponentImporter {
	private static List<Component> importedClasses = new ArrayList<Component>();
	private static int i;
	
	public static List<Component> getImportedClasses() {return importedClasses;}
	
	public static void init() {
		importedClasses.add(new SpriteRenderer());
	}
	
	public static Component importClass(String path)
	{
		InputStream stream = null;
		try {stream = new FileInputStream(path);}
		catch (FileNotFoundException e) {e.printStackTrace();}
		if(stream == null) return null;
		
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		String seperator = System.getProperty("line.separator");
		String tempProperty = System.getProperty("java.io.tmpdir");
		
		String[] name = path.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
		Path srcPath = Paths.get(tempProperty, name[name.length - 1]);
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		try {Files.write(srcPath, reader.lines().collect(Collectors.joining(seperator)).getBytes(StandardCharsets.UTF_8));}
		catch (IOException e) {e.printStackTrace();}
		compiler.run(null, null, null, srcPath.toString());
		Path p = srcPath.getParent().resolve(name[name.length - 1].split("\\.")[0] + ".class");
		
		URL classURL = null;
		try {classURL = p.getParent().toFile().toURI().toURL();}
		catch (MalformedURLException e) {e.printStackTrace();}
		if(classURL == null) return null;
		
		URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] {classURL});
		
		Class<?> myClass = null;
		try {myClass = classLoader.loadClass(name[name.length - 1].split("\\.")[0]);}
		catch(ClassNotFoundException e1) {e1.printStackTrace();}
		if(myClass == null) return null;
		
		if(!Component.class.isAssignableFrom(Component.class)) return null;
		
		Component l = null;
		try{l = (Component) myClass.getConstructor().newInstance();}
		catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {e.printStackTrace();}
		if(l == null) return null;
		
		
		for(i = 0; i < importedClasses.size(); i++)
		{
			if(importedClasses.get(i).getName().equals(l.getName()))
			{
				importedClasses.set(i, l);
				return l;
			}
		}
		importedClasses.add(l);
		return l;
	}
	
	public static Component getComponent(String name)
	{
		for(i = 0; i < importedClasses.size(); i++)
		{
			if(importedClasses.get(i).getName().equals(name))
			{
				try {return importedClasses.get(i).getClass().getConstructor().newInstance();}
				catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {e.printStackTrace();}
			}
		}
		return null;
	}
	
	public static void cleanUp()
	{
		String path;
		File javaFile;
		File classFile;
		
		for(i = 0; i < importedClasses.size(); i++)
		{
			Component l = importedClasses.get(i);
			path = l.getClass().getProtectionDomain().getCodeSource().getLocation() + l.getName();
			javaFile = new File(path + ".java");
			classFile = new File(path + ".class");
			
			javaFile.delete();
			classFile.delete();
		}
	}
}
