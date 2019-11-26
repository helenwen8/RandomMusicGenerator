import javax.swing.*;

/**
 * Write a description of class execution here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class execution
{
    
    public static void main (String [] args)
    {
        JFrame frame = new JFrame ("The Amazing Somewhat-Random Music Generator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        MainGUI gui = new MainGUI();
        
        frame.getContentPane().add(gui.getPanel());
        
        frame.pack();
        frame.setVisible(true);
        
    }
}
