import javax.swing.*;
import javax.swing.event.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*;

import javax.imageio.ImageIO;
import java.io.*;

import java.text.NumberFormat;

import java.beans.*;


/**
 * Class to set up the GUI of the class
 */
public class MainGUI extends JApplet 
{
    private JPanel primary, primary1, primary2, advancedPanel, ratioPanel, toUsePanel;
    
    private JButton drum, guitar, piano, stop;
    private JToggleButton acoustic, electric; 
    private JLabel ratioText, light, heavy;
    private JSlider ratio;
    
    private Randomization randomSystem = new Randomization();
    
    final Dimension SCREEN_SIZE = Toolkit.getDefaultToolkit().getScreenSize();
    final int HEIGHT = (int) SCREEN_SIZE.getHeight();
    final int WIDTH = (int) SCREEN_SIZE.getWidth();
    
    Image pianoPic, guitarPic, drumPic;
    /**
     * The "main" method to set up the whole GUI 
     */
    public MainGUI() 
    {
        primary1 = new JPanel (new FlowLayout(FlowLayout.CENTER, (WIDTH - 300 * 2 - 150) / 4, 0));
        primary1.setPreferredSize(new Dimension (WIDTH, HEIGHT / 2));
        primary1.setOpaque(true);
        primary1.setBackground(new Color (253, 251, 240));
        
        primary2 = new JPanel (new FlowLayout(FlowLayout.CENTER,  (WIDTH - 300 * 2 - 150) / 4, 0));
        primary2.setPreferredSize(new Dimension (WIDTH, HEIGHT / 2));
        primary2.setOpaque(true);
        primary2.setBackground(new Color (253, 251, 240));
        
        // piano, panel, guitar, advanced setting or README, drum, acoustic or advanced setting
        setupButtons();         // set up the three instrument buttons
        primary1.add(piano);    
        primary1.add(stop);
        primary1.add(guitar);
        primary2.add(setupToUsePanel());
        primary2.add(drum);
        primary2.add(setupAdvancedPanel());
        
        primary = new JPanel (new FlowLayout(FlowLayout.CENTER, 0, 0));
        primary.setPreferredSize(SCREEN_SIZE);
        primary.setOpaque(true);
        primary.setBackground(new Color (253, 251, 240));
        
        primary.add(primary1);
        primary.add(primary2);
    }

    public JPanel getPanel()
    {
        return primary;
    }
    
    private void setupButtons()     // the panel that has piano, guitar, and drum; also the stop music button
    {
        // Create the instrument buttons
        try
        {
            pianoPic = (BufferedImage) ImageIO.read(new File("C:/Users/Helen/Desktop/piano.png"));
            guitarPic = (BufferedImage) ImageIO.read(new File("C:/Users/Helen/Desktop/guitar.png"));
            drumPic = (BufferedImage) ImageIO.read(new File("C:/Users/Helen/Desktop/drumset.png"));
            
            // pianoPic = (BufferedImage) ImageIO.read(new File("H://My Documents/10th Grade/AP Computer Science/Operation Create/piano.png"));
            // guitarPic = (BufferedImage) ImageIO.read(new File("H://My Documents/10th Grade/AP Computer Science/Operation Create/guitar.png"));
            // drumPic = (BufferedImage) ImageIO.read(new File("H://My Documents/10th Grade/AP Computer Science/Operation Create/drumset.png"));
            
            // pianoPic = (BufferedImage) ImageIO.read(new File("/Users/Helen/JAVA/piano.png"));
            // guitarPic = (BufferedImage) ImageIO.read(new File("/Users/Helen/JAVA/guitar.png"));
            // drumPic = (BufferedImage) ImageIO.read(new File("/Users/Helen/JAVA/drumset.png"));
        }
        catch (Exception e)
        {
            System.out.println("crap");
        }
        pianoPic = pianoPic.getScaledInstance(300, 300, Image.SCALE_AREA_AVERAGING);
        guitarPic = guitarPic.getScaledInstance(300, 300, Image.SCALE_AREA_AVERAGING);
        drumPic = drumPic.getScaledInstance(300, 300, Image.SCALE_AREA_AVERAGING);
        
        piano = new JButton (new ImageIcon (pianoPic));
        guitar = new JButton (new ImageIcon (guitarPic));
        drum = new JButton (new ImageIcon (drumPic));
        
        piano.setBorder(BorderFactory.createEmptyBorder());
        guitar.setBorder(BorderFactory.createEmptyBorder());
        drum.setBorder(BorderFactory.createEmptyBorder());
        
        piano.setBackground(new Color (253, 251, 240));
        guitar.setBackground(new Color (253, 251, 240));;
        drum.setBackground(new Color (253, 251, 240));
        
        InstrumentListener instrumentListener = new InstrumentListener();
        piano.addActionListener(instrumentListener);
        guitar.addActionListener(instrumentListener);
        drum.addActionListener(instrumentListener);
        
        // Set up the stop button
        stop = new JButton ("Stop Music");
        stop.setPreferredSize(new Dimension (150, 50));
        
        stop.setBackground(new Color (249, 243, 221));
        stop.setBorder(BorderFactory.createEtchedBorder());
        stop.setFont(new Font("Courier", Font.PLAIN, 20));
        
        stop.addActionListener(new ButtonListener());
        
        // Set up button group for this thing
        ButtonGroup insGroup = new ButtonGroup();
        insGroup.add(piano);
        insGroup.add(guitar);
        insGroup.add(drum);
        insGroup.add(stop);
    }
    
    private JPanel setupAdvancedPanel()
    {
        advancedPanel = new JPanel ((new FlowLayout (FlowLayout.CENTER, 50, 50)));
        advancedPanel.setPreferredSize(new Dimension (300, 300));
        advancedPanel.setOpaque(false);
        
        // Set up acoustic/electric buttons
        acoustic = new JToggleButton ("Acoustic", true);
        electric = new JToggleButton ("Electric");
        acoustic.setPreferredSize(new Dimension (100, 50));
        electric.setPreferredSize(new Dimension (100, 50));
        
        
        acoustic.setBackground(new Color (249, 243, 221));
        acoustic.setBorder(BorderFactory.createEtchedBorder());
        acoustic.setFont(new Font("Courier", Font.PLAIN, 20));
        electric.setBackground(new Color (249, 243, 221));
        electric.setBorder(BorderFactory.createEtchedBorder());
        electric.setFont(new Font("Courier", Font.PLAIN, 20));
        
        
        AcousticListener acousticListener = new AcousticListener();
        acoustic.addActionListener(acousticListener);
        electric.addActionListener(acousticListener);
        
        ButtonGroup acousticGroup = new ButtonGroup();
        acousticGroup.add(acoustic);
        acousticGroup.add(electric);
        
        advancedPanel.add(acoustic);
        advancedPanel.add(electric);
        
        // Set up BPM/ratio buttons
        //advancedPanel.add(setupBPMPanel());
        advancedPanel.add(setupRatioPanel());
        
        return advancedPanel;
    }
    
    private JPanel setupRatioPanel()
    {
        ratioPanel = new JPanel (new FlowLayout (FlowLayout.CENTER, 100, 0));
        ratioPanel.setPreferredSize(new Dimension (300, 150));
        ratioPanel.setOpaque(false);
        
        ratio = new JSlider(1, 5, 1);
        ratio.setPreferredSize(new Dimension (200, 20));
        ratio.setPaintTicks(true);
        ratio.setOpaque(false);
        
        ratio.addChangeListener(new RatioListener());
        
        light = new JLabel ("Lighter");
        heavy = new JLabel ("Heavier");
        light.setFont(new Font("Courier", Font.PLAIN, 12));
        heavy.setFont(new Font("Courier", Font.PLAIN, 12));
        light.setPreferredSize(new Dimension (50, 20));
        heavy.setPreferredSize(new Dimension (50, 20));
        light.setAlignmentX(JComponent.LEFT_ALIGNMENT);
        heavy.setAlignmentX(JComponent.RIGHT_ALIGNMENT);
        
        
        ratioPanel.add(light);
        ratioPanel.add(heavy);
        ratioPanel.add(ratio);
        
        return ratioPanel;
    }
    
    private JPanel setupToUsePanel ()
    {
        toUsePanel = new JPanel (new FlowLayout (FlowLayout.LEFT, 0, 0));
        toUsePanel.setPreferredSize(new Dimension (300, 300));
        toUsePanel.setOpaque(false);
        
        JLabel label1 = new JLabel ("<html>Click on one of the instrument to generate music!</html>");
        JLabel label2 = new JLabel ("<html>Click \"Stop Music\" to stop looping the music clip.</html>");
        JLabel label3 = new JLabel ("<html>Click on Acoustic/Electric to change the sound of the instrument.</html>");
        JLabel label4 = new JLabel ("<html>Use the slider to change the complexity of the music.</html>");
        
        label1.setFont(new Font("Courier", Font.PLAIN, 16));
        label2.setFont(new Font("Courier", Font.PLAIN, 16));
        label3.setFont(new Font("Courier", Font.PLAIN, 16));
        label4.setFont(new Font("Courier", Font.PLAIN, 16));
        
        label1.setPreferredSize(new Dimension (300, 60));
        label2.setPreferredSize(new Dimension (300, 60));
        label3.setPreferredSize(new Dimension (300, 60));
        label4.setPreferredSize(new Dimension (300, 60));
        
        toUsePanel.add(label1);
        toUsePanel.add(label2);
        toUsePanel.add(label3);
        toUsePanel.add(label4);
        
        
        return toUsePanel;
     }
    private class AcousticListener implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            Object source = event.getSource();
            randomSystem.stopMusic();
            if (source == acoustic)
                randomSystem.setAcoustic(true);
            else if (source == electric)
                randomSystem.setAcoustic(false);
        }
    }
    
    private class InstrumentListener implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            Object source = event.getSource();
            randomSystem.stopMusic();
            if (source == piano)
                randomSystem.pianoAndGuitar(true);
            else if (source == guitar)
                randomSystem.pianoAndGuitar(false);
            else if (source == drum)
                randomSystem.drum();
                
            randomSystem.playMusic();
       }
    }
    private class ButtonListener implements ActionListener
    {
        public void actionPerformed (ActionEvent event)
        {
            randomSystem.stopMusic();
        }
    }
    private class RatioListener implements ChangeListener
    {
        public void stateChanged (ChangeEvent e)
        {
            JSlider source = (JSlider) e.getSource();
            randomSystem.setRatio(source.getValue());
        }
    }
}
