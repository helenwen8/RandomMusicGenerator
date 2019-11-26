import javax.sound.midi.*;
import javax.sound.sampled.*;

import java.util.Random;
import java.util.Arrays;
/**
 * The program that does all of the music generating work
 */
public class Randomization
{
    private boolean acoustic = true; // true if acoustic false otherwise
    private int ratio = 1;       // 0 - 10

    private Random rand = new Random();
    
    public int [] majorScale;
    private int [] majorPattern = {2, 2, 1, 2, 2, 2, 1};
    private int [] majorChord;
    private int [] chordPattern = {2, 2, 4};
    private final int MAX_TICKS = 32 * 100;     // 8 measure of 4 beats 

    ShortMessage bass, tom1, tom2, tom3, snare, crash, ride, rideBell, hiHat, special;
    ShortMessage [] fills = new ShortMessage [6];
    
    private Sequencer sequencer;
    private Sequence seq;
    private Track music;
    public Randomization () // initialize when the applet starts
    {
        try 
        {
            sequencer = MidiSystem.getSequencer();
            seq = new Sequence (Sequence.PPQ,  100); // 100 ticks per quarter note/beat // 50 - eighth notes    25 - 16th notes     
            music = seq.createTrack();
            sequencer.open();
            sequencer.setTempoInBPM(120);
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        }
        catch (Exception e)
        {
        }
    }

    public Sequence pianoAndGuitar (boolean check)
    {
        //Check instrument 
        ShortMessage ins = new ShortMessage();
        try
        {
            if (check)
            {   if (acoustic)
                    ins.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 0, 0);
                else
                    ins.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 4, 0);
            }
            else
            {
                if (acoustic)
                    ins.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 25, 0);
                else
                    ins.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 26, 0);
            }
        }
        catch (Exception e)
        {
        }
        MidiEvent insChange = new MidiEvent (ins, 0);
        music.add(insChange);

        // ignore the chord function right now - below is the melody function
        int currentNote = -1;    // current note pitch
        int noteValue = 0;      // current note's timing value  
        int currentNoteValue = 0;   // how many ticks into the track so far

        ShortMessage note;
        while (currentNoteValue <= MAX_TICKS)
        {
            if (currentNote == -1)      // when first starting up a sequence
                currentNote = determineStartingNote();
            else
                currentNote = nextNote(currentNote);
            try
            {   
                if (nextRest()) // return true if there's a rest
                    note = new ShortMessage (ShortMessage.NOTE_OFF, 0, 0, 0);
                else
                    note = new ShortMessage (ShortMessage.NOTE_ON, 0, currentNote, 100);

                noteValue = nextNoteValue();

                if (noteValue + currentNoteValue > MAX_TICKS)       // check if out of bound
                {
                    currentNoteValue = MAX_TICKS + 1;
                }                                                  // second chance to m
                else
                {
                    MidiEvent noteOn = new MidiEvent (note, currentNoteValue);
                    music.add(noteOn);
                    MidiEvent noteOff = new MidiEvent (new ShortMessage (ShortMessage.NOTE_OFF, 0, currentNote, 100), (currentNoteValue + noteValue) / 2);
                    music.add(noteOff);
                    if (rand.nextInt(ratio) + 1>= 3)
                    {
                        int [] chords = nextChordValue(currentNote);
                        for (int i = 0; i < chords.length; i++)
                        {
                            MidiEvent chordOn = new MidiEvent (new ShortMessage (ShortMessage.NOTE_ON, 0, chords[i], 50), currentNoteValue);
                            music.add(chordOn);
                            MidiEvent chordOff = new MidiEvent (new ShortMessage (ShortMessage.NOTE_OFF, 0, chords[i], 50), (currentNoteValue + noteValue) / 2);
                            music.add(chordOff);
                        }
                    }
                    currentNoteValue += noteValue;
                }
            }
            catch (InvalidMidiDataException e)
            {   
            }
        }

        return seq;
    }

    public Sequence drum ()
    {
        //Set up acoustic/electric sound
        setDrumAcoustic(acoustic);
        try
        { 
            ShortMessage ins = new ShortMessage();
            if (acoustic)
                ins.setMessage(ShortMessage.PROGRAM_CHANGE, 9, 0, 100);   // acoustic drumset
            else
                ins.setMessage(ShortMessage.PROGRAM_CHANGE, 9, 24, 100);  // electric drumset
            
            MidiEvent insChange = new MidiEvent (ins, 0);
            music.add(insChange);
        }
        catch (Exception e)
        {}
        
        // Base sound
        int [] beats = new int [4 * 8];
        for (int i = 0; i < beats.length; i++)
            beats[i] = 100 * (i);

        int [] heaviness = new int [8];   
        for (int i = 0; i < heaviness.length; i++)
            heaviness[i] = rand.nextInt(5) + 1;

        for (int i = 0; i < beats.length; i++)
        {
            ShortMessage [] soundArr = nextDrumSound(((i % 4) + 1), heaviness[i / 4]);
            for (int a = 0; a < soundArr.length; a++)
                if (soundArr[a] != null)
                    music.add(new MidiEvent (soundArr[a], beats[i]));
        }
        
        // Creating fills!!
        for (int i = 1; i < beats.length; i++)
        {
            if (rand.nextDouble() * heaviness[(i - 1)/ 4] > 1.5)
            {
                MidiEvent [] fills = nextDrumFill(beats[i - 1], beats[i]);
                for (int d = 0; d < fills.length; d++)
                    music.add(fills[d]);
            }
        }
        return seq;
    }
    private int determineStartingNote ()
    {
        int note = rand.nextInt(12) + 36;    // 0 is C while 11 is B
        int count = 0;
        for (int i = note; i < 84; i += 12)
            count++;
        majorScale = new int [(count - 1) * 7];
        majorScale[0] = note;
        for (int i = 1; i < majorScale.length; i++)
            majorScale[i] = majorScale[i - 1] + majorPattern[i % 7];

        int octave = (int) (rand.nextGaussian() + 0.5);
        boolean sign = rand.nextBoolean();
        if (sign)
            note = -note;
        int startingNote = (60 + (note)) + (12 * octave);
        return startingNote;
    }

    private int nextNote(int previous)
    // problem: always going up never going below previous for some reason
    // interval needs to be changed - it changes every time something changes
    // 
    {
        int currIndex = Arrays.binarySearch(majorScale, previous);
        int note = (int) (rand.nextGaussian() * 3 + currIndex + 0.5);
        if (rand.nextInt(4) == rand.nextInt(4))
        {
            double temp = rand.nextGaussian();
            if (temp < 0)
                temp -= 0.5;
            else
                temp += 0.5;

            int octave = (int) temp;
            note += octave * 7;
        }
        if (note < majorScale.length && note > 0)
            return majorScale[note];
        else
        {
            if (note < 0)
                return Math.abs(note);
            else
                return nextNote(previous - 60); 
        }
    }

    private int nextNoteValue () 
    {  
        int [] melodyArr = { 75, 12, 25, 50, 100 };
        double temp = rand.nextGaussian();
        if (temp > 0)
            temp += 0.5;
        else
            temp -= 0.5;
        int index = Math.abs((int) (temp + 2));
        if (index < melodyArr.length)
            return melodyArr[index];
        else
            return nextNoteValue();
    }

    private boolean nextRest ()      // 1/16 chance of getting a rest
    {
        return rand.nextInt(8) == rand.nextInt(8);
    }

    private int [] nextChordValue (int current)
    {
        int [] noteValue = new int [rand.nextInt(4) + 1];
        int currIndex = Math.abs(Arrays.binarySearch(majorScale, current));
        System.out.println("lit " + current);
        System.out.println(currIndex);
        for (int i = 0; i < noteValue.length; i++)
        {
            if (i < 3)
            {
                if (currIndex + chordPattern[i % 3] < majorScale.length )
                    noteValue[i] = majorScale[currIndex + chordPattern[i % 3]];
            }
            else if (i > 3)
            {
                if (currIndex - 7 > 0 )
                    noteValue[i] = majorScale[currIndex - chordPattern[2 - i % 3]];
            }
        }
        
        return noteValue;
    }
    
    public void setDrumAcoustic (boolean acoustic)
    {
        try
        {
            bass = new ShortMessage (ShortMessage.NOTE_ON, 9, 35, 100);
            tom1 = new ShortMessage (ShortMessage.NOTE_ON, 9, 48, 100);     // highest pitch
            tom2 = new ShortMessage (ShortMessage.NOTE_ON, 9, 45, 100);
            tom3 = new ShortMessage (ShortMessage.NOTE_ON, 9, 41, 100);     // lowest pitch

            ride = new ShortMessage (ShortMessage.NOTE_ON, 9, 59, 100);
            rideBell = new ShortMessage (ShortMessage.NOTE_ON, 9, 53, 100);
            crash = new ShortMessage (ShortMessage.NOTE_ON, 9, 57, 100);
            hiHat = new ShortMessage (ShortMessage.NOTE_ON, 9, 42, 100);    // closed
        }
        catch (Exception e)
        {}
        if (acoustic)
        {
            try
            {
                snare = new ShortMessage (ShortMessage.NOTE_ON, 9, 40, 100);
                special = new ShortMessage (ShortMessage.NOTE_ON, 9, 56, 100);  // cowbell 
            }
            catch (Exception e)
            {}
        }
        else
        {
            try
            {
                snare = new ShortMessage (ShortMessage.NOTE_ON, 9, 38, 100);
                special = new ShortMessage (ShortMessage.NOTE_ON, 9, 39, 100);  // hand clap
            }
            catch (Exception e)
            {}
        }
        fills[0] = tom1;
        fills[1] = tom2;
        fills[2] = tom3;
        fills[3] = rideBell;
        fills[4] = hiHat;
        fills[5] = special;
    }

    private ShortMessage[] nextDrumSound (int beat, int heaviness)     // default heaviness should be 1 or 2, max should be at most 5
    {
        ShortMessage [] drumArr = new ShortMessage[4];     // 0 - bass     1 - hihat pedal   2 - one hand   3 - the other hand 
        //  bass drum rule - no overriding necessary
        if (beat == 1 || beat == 3)
            drumArr[0] = bass;
        // snare drum rule - may be override by toms
        if (beat == 2 || beat == 4)
            drumArr[3] = snare;
        else if (heaviness > 3 )
            drumArr[3] = snare;

        // ride & ridebell rule - may be override by crash
        if (heaviness >= 4)
            drumArr[2] = rideBell;
        else if (heaviness == 3)
            if (rand.nextBoolean())
                drumArr[2] = ride;
            else
                drumArr[2] = rideBell;

        // crash cymbal rule - override all other cymbals
        if (beat == 1)
        {
            if (heaviness > 1)
                drumArr[2] = crash;
        }
        else
        {
            if (heaviness > 5)
                drumArr[2] = crash;
        }

        // pedal hihat rule - filler
        if (heaviness >= 2)
            drumArr[1] = hiHat;

        return drumArr;
    }
    
    private MidiEvent [] nextDrumFill (int startBeat, int finishBeat)
    {
        MidiEvent [] drumFill = new MidiEvent [rand.nextInt(ratio) * 2 ];
        if (drumFill.length == 0)
            return drumFill;
        int difference = (finishBeat - startBeat) / drumFill.length;
        int currentBeat = startBeat;
        for (int i = 0; i < drumFill.length; i++)
        {
            int temp = rand.nextInt(fills.length);
            drumFill[i] = new MidiEvent (fills[temp], currentBeat);
            currentBeat += difference;
        }
        return drumFill;
    }

    public void setAcoustic (boolean check)
    {
        acoustic = check;
    }

    public void setRatio (int num)
    {
        ratio = num;
    }

    public void playMusic() // not sure if this is working
    {
        try
        {
            sequencer.setSequence(seq);
            sequencer.start();
        }
        catch (Exception e)
        {}
    }
    public void stopMusic()
    {
        sequencer.stop();
        seq.deleteTrack(music);     // bye bye forever my master piece
        music = seq.createTrack();
    }

}
