package rs.ac.ftn.pdfparsing;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mcavallo.opencloud.Cloud;
import org.mcavallo.opencloud.Tag;

public class TestOpenCloud {
	JPanel panel;
	JFrame frame;
    public void show(Cloud cloud) {
        frame = new JFrame(TestOpenCloud.class.getSimpleName());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel = new JPanel();        
        panel.setBackground(Color.lightGray);
        Random random = new Random();
        for (Tag tag : cloud.tags()) {
            final JLabel label = new JLabel(tag.getName());
            label.setOpaque(false);
            label.setFont(label.getFont().deriveFont((float) tag.getWeight() * 20));
            label.setForeground(Color.BLACK);
            panel.add(label);
        }
        frame.add(panel);
        frame.setSize(800, 800);
        //frame.setMaximizedBounds(new Rectangle(800, 800));
        //frame.pack();
        frame.setVisible(true);        
        
    }    

    public void saveImage(String fileName) {
	    BufferedImage bi = new BufferedImage(panel.getSize().width, panel.getSize().height, BufferedImage.TYPE_INT_ARGB); 
	    Graphics g = bi.createGraphics();
	    panel.paint(g);  //this == JComponent
	    g.dispose();
	    try{ImageIO.write(bi,"png",new File(fileName));}catch (Exception e) {}
	    frame.setVisible(false);
    }
}