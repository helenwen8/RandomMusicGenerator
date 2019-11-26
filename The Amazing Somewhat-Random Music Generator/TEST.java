import java.util.Random;
import javax.sound.midi.*;
/**
 * Write a description of class TEST here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class TEST
{
    // instance variables - replace the example below with your own
    private int x;
    private static Random rand = new Random();
    /**
     * Constructor for objects of class TEST
     */
    public TEST()
    {
        // initialise instance variables
        x = 0;
    }

    /**
     * An example of a method - replace this comment with your own
     * 
     * @param  y   a sample parameter for a method
     * @return     the sum of x and y 
     */
    public static void main (String [] args)
    {
        Randomization test = new Randomization();
        // for (int i = 0; i < 50; i++)
        // {
            // int octave;
            // double temp = rand.nextGaussian();
            // if (temp < 0)
                // octave = (int) (temp - 0.5);
            // else 
                // octave = (int) (temp + 0.5);
            // System.out.println(temp + " " + octave);    
        // }
        // for (int i = 0; i < 50; i++)
            // System.out.println(rand.nextInt(25) - 12);  
        // for (int i = 0; i < 50; i++)
        // {
            // System.out.println(test.nextRest() + "  ");
        // }
        
// try
// {
// Sequencer sequencer = MidiSystem.getSequencer();
// sequencer.open();
// Sequence seq = test.pianoAndGuitar(true);
// sequencer.setSequence(seq);
// sequencer.start();
// }
// catch (Exception e)
// {}
        
//         for (int i = 0; i < 10; i++)
//         {
//         Sequence seq = test.pianoAndGuitar(true);
//         test.playMusic();
//         try
//         {
//         Thread.sleep(10000);
//         }
//         catch (Exception e)
//         {}
//         test.stopMusic();
//         try
//         {
//         Thread.sleep(500);
//         }
//         catch (Exception e)
//         {}
//         }
                
                // try
                // {
                    
                    // Synthesizer synth = MidiSystem.getSynthesizer();
                    // synth.open();
                    // MidiChannel [] chan = synth.getChannels();
                    // for (int i = 35; i <= 81; i++)
                    // {
                        // chan[9].programChange(0,24);
                        // chan[9].noteOn(i, 100);
                        // Thread.sleep(1000); 
                        // chan[9].noteOff(i, 100);
                        // System.out.println(i);
                    // }
                        
                // }
                
                // catch (Exception e)
                // {}
// try{
// Sequencer sequencer = MidiSystem.getSequencer();
// sequencer.open();
// Sequence seq = test.drum(1.25);
// sequencer.setSequence(seq);
// sequencer.start();
// }
// catch (Exception e)
// {}
    }
    
}
