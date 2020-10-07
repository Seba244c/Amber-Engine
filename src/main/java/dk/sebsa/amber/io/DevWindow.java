package dk.sebsa.amber.io;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.PrintStream;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import dk.sebsa.amber.util.Logger;
import dk.sebsa.amber.util.TextAreaOutputStream;

public class DevWindow {
	public static boolean useDevWindow;
	
	private static JFrame frame;
	private static PrintStream ps;
	private static JTextPane console;
	private static JTextPane performance;
	private static JToolBar toolBar;
	private static JScrollPane consoleComp;
	private static Style performanceStyle;
	private static DecimalFormat aftFormat;
	private static DefaultStyledDocument performanceDocument;
	
	public static void useDevWindow(String title) {
		if(useDevWindow) return;
		useDevWindow = true;
		Logger.infoLog("DevWindow", "Enabling Dev Window with title: " + title);
		
		// Create jFrame, textpanes and style document
		frame = new JFrame(title);
		DefaultStyledDocument document = new DefaultStyledDocument();
		performanceDocument = new DefaultStyledDocument();
		console = new JTextPane(document);    
		performance = new JTextPane(performanceDocument);
		
		// Configure console window
		console.setForeground(Color.WHITE);
		console.setBackground(Color.DARK_GRAY);
		console.setEditable(false);
		
		// Configure performance window
		performance.setForeground(Color.WHITE);
		performance.setBackground(Color.DARK_GRAY);
		performance.setEditable(false);
		
		// performance document
		StyleContext context = new StyleContext();
        performanceStyle = context.addStyle("statStyle", null);
		StyleConstants.setForeground(performanceStyle, Color.white);
        
        // Create and configure TextOutputStream
        TextAreaOutputStream taos = new TextAreaOutputStream(console, document);
        ps = new PrintStream(taos);
        System.setOut(ps);
        System.setErr(ps);
        
        // Create window selection panel
        toolBar = new JToolBar("Tool Bar");
        toolBar.setFloatable(false);
        
        // Create action listener
        ActionListener al = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("s1")) {
					frame.remove(performance);
					frame.add(consoleComp);
					frame.pack();
					frame.setSize(600, 155);
				} else if(e.getActionCommand().equals("s2")) {
					frame.remove(consoleComp);
					frame.add(performance);
					frame.pack();
					frame.setSize(600, 155);
				}
			}
		};
        
        // Buttons
        JButton consoleButton = new JButton("Console");
        toolBar.add(consoleButton);
        consoleButton.setActionCommand("s1");
        consoleButton.addActionListener(al);
        
        JButton performanceButton = new JButton("Performance");
        toolBar.add(performanceButton);
        performanceButton.setActionCommand("s2");
        performanceButton.addActionListener(al);
        
        
        // Configure jFrame
        consoleComp = new JScrollPane( console );
        frame.add( consoleComp );
        frame.add(toolBar, BorderLayout.PAGE_START);
        frame.pack();
        frame.setVisible( true );
        frame.setSize(600, 155);
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        // AFL
        aftFormat = new DecimalFormat("#.#####");
        Logger.infoLog("DevWindow", "Created Dev Window");
	}
	
	public static void destroyDevWindow() {
		Logger.infoLog("DevWindow", "Destroying Dev Window");
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		ps.close();
	}
	
	public static void update(int fps, double afl) {
		try {
			performanceDocument.replace(0, performanceDocument.getLength(), "Frames Per Second: "+fps+"\nAverege Frame Length: "+aftFormat.format(afl)+"s", performanceStyle);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/*@Deprecated
	public static void report(int FPS, String afl) throws BadLocationException {
		if(!useDevWindow) return;
		
	}*/
}
