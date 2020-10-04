package dk.sebsa.amber_engine;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Loading extends JFrame {
	public JProgressBar bar;
	public JLabel text1;
	public JLabel text2;
	private static final long serialVersionUID = 2L;
	
	public Loading()
	{
		// Init
	    this.setTitle("Loading");
	    this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    this.setResizable(false);
	    
	    this.setLocationRelativeTo(null);
	    this.setSize(400, 120);
	    
	    // Panel
	    JPanel panel = new JPanel();
		panel.setLayout(null);
	    
	    // Bar
	    bar = new JProgressBar();
	    bar.setBounds(10, 45, 360, 20);
	    bar.setValue(0);
	    
	    // Text
	    text1 = new JLabel("", JLabel.CENTER);
	    text1.setBounds(0,0,400,20);
	    
	    text2 = new JLabel("", JLabel.CENTER);
	    text2.setBounds(0,20,400,20);
	    
	    // End off
	    panel.add(bar);
	    panel.add(text1);
	    panel.add(text2);
	    this.getContentPane().add(panel);
	    
	    this.setLocationRelativeTo(null);
	    this.setVisible(true);
	    this.setAlwaysOnTop(true);
	}
	
	public void reset(String title) {
		this.setTitle(title);
		text1.setText(title);
		setStatus("", 0);
	}
	
	public void setStatus(String doing, int p) {
		text2.setText(doing);
		bar.setValue(p);
	}
	
	public void close() {
		this.dispose();
	}
}
