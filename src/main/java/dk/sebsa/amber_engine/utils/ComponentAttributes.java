package dk.sebsa.amber_engine.utils;

import java.lang.reflect.Field;

import dk.sebsa.amber.entity.Component;

public class ComponentAttributes {
	public Object component;
	public Class<?> c;
	public String[] fields;
	public int height;
	
	public ComponentAttributes(Object o) {
		this.component = o;
		c = o.getClass();
		
		Field[] f = c.getFields();
		if(o instanceof Component) fields = new String[f.length - 1];
		else fields = new String[f.length];
		
		for(int i = 0; i < fields.length; i++) {
			String[] splitName = f[i].getType().toString().split("\\.");
			fields[i] = f[i].getName() + " " + splitName[splitName.length-1];
			
			if(i < fields.length - 1) height += 24;
			else height += 22;
		}
	}
}	
