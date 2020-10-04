package dk.sebsa.amber_engine;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class BootLoader {
	private static JFrame frame;
	private static Container pane;
	private static String returnString = null;
	
	public static String init() {
		if(returnString != null) return returnString;
		
		try {
			createWindow();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		while(returnString == null) {
			try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
			
			if(frame == null) break;
		}
		
		frame.dispose();
		return returnString;
	}

	private static void createWindow() throws IOException {
		frame = new JFrame("Blackfur Engine");
		frame.pack();
		
		frame.setSize(300, 350);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pane = frame.getContentPane();
		
		createPanel();
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private static void createPanel() throws IOException {
		//final BufferedImage image = ImageIO.read(BootLoader.class.getResourceAsStream("/textures/BootLoad.png"));
		JPanel panel = new JPanel() {
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				//if(image != null) g.drawImage(image, 10, 10, 263, 215, null);
			}
		};
		panel.setLayout(null);
		
		File file = new File(ProjectManager.workspaceDir);
		file.mkdir();
		File[] temp = file.listFiles(File::isDirectory);
		List<String> dirs = new ArrayList<String>();
		
		for(int i = 0; i < temp.length; i++) {
			if(!temp[i].getName().equals("null")) dirs.add(temp[i].getName());
		}
		dirs.add("New Project");
		
		String[] output = new String[dirs.size()];
		dirs.toArray(output);
		
		JComboBox<String> projectBox = new JComboBox<String>(output);
		projectBox.setBounds(70, 240, 150, 25);
		
		JButton begin = new JButton("Begin");
		begin.setBounds(100, 276, 80, 25);
		
		JTextArea input = new JTextArea("My Project");
		input.setBackground(Color.decode("#ECECEC"));
		input.setBorder(BorderFactory.createLineBorder(Color.decode("#728190")));
		input.setForeground(Color.BLACK);
        
		begin.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String c = e.getActionCommand();
				if(c.equals("Begin")) {
					String selectedString = (String) projectBox.getSelectedItem();
					if(selectedString.equals("New Project")) {
						begin.setBounds(100, 240, 80, 25);
						begin.setText("Create");
						projectBox.setBounds(-100, 0, 1, 1);
						
						input.setBounds(70, 276, 150, 25);
						//input.setText("JI");
					}
					else returnString = selectedString;
				}
				else if(c.equals("Create")) {
					returnString = input.getText();
				}
			}
		});
		panel.add(projectBox);
		panel.add(begin);
		panel.add(input);
		pane.add(panel);
	}
}
