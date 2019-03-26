package piano;

import java.awt.event.*;
import java.awt.*;
import java.util.logging.*;
import javax.sound.midi.*;
import javax.swing.*;

public class MiniPiano extends JPanel implements KeyListener {
    private Synthesizer synthesizer;
    private MidiChannel channel;

    private String blackKeys = "WE TYU ";
    private String whiteKeys = "ASDFGHJ";
    private String allKeys = "AWSEDFTGYHUJ";

    private int octave = 5;
    private boolean[] keyOn = new boolean[allKeys.length()];
    public static final int keyInOct =12;

    public MiniPiano() {
        addKeyListener(this);
        startSynthesizer();
    }

    private void startSynthesizer() {
        try {
            synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[0];
        } catch (MidiUnavailableException e){
            //e.printStackTrace();
            //System.exit(-1);
            Logger.getLogger(MiniPiano.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.translate(50, 50);
        g.drawString("Octave: " + octave, 0, -10);
        final int whiteKeyWight = 40;
        final int whiteKeyHeight = 100;
        for (int i = 0; i < whiteKeys.length(); i++) {
            if (keyOn[allKeys.indexOf(whiteKeys.charAt(i))]){
                g.setColor(Color.BLUE);
            } else{
                g.setColor(Color.WHITE);
            }
            g.fillRect(i*whiteKeyWight, 0, whiteKeyWight,whiteKeyHeight);
            g.setColor(Color.BLACK);
            g.drawRect(i*whiteKeyWight,0,whiteKeyWight,whiteKeyHeight);
            g.drawString(" " + whiteKeys.charAt(i), i*whiteKeyWight + 10, whiteKeyHeight - 10);
        }
        final int blackKeyWidth = whiteKeyWight / 2;
        final int blackKeyHeight = whiteKeyHeight / 2;
        for (int i = 0; i < blackKeys.length();i++){
            if (blackKeys.charAt(i) == ' ') {
                continue;
            }
            if (keyOn[allKeys.indexOf(blackKeys.charAt(i))]) {
                g.setColor(Color.BLUE);
            } else {
                g.setColor(Color.BLACK);
            }
            int x = (i + 1) * whiteKeyWight - blackKeyWidth / 2;
            g.fillRect(x,1,blackKeyWidth,blackKeyHeight);
            g.drawString(" " + blackKeys.charAt(i), x+2, blackKeyHeight-5);
            g.drawString("For change octave click + or -", whiteKeyWight, whiteKeyHeight + 25);
        }
    }

    public void keyTyped(KeyEvent e) {
        // do nothing
    }

    public void keyPressed(KeyEvent i){
        repaint();
        //change octave
        if (i.getKeyChar() == '+') {
            octave = octave < 8 ? octave + 1 : 8;
        }
        else if (i.getKeyChar() == '-') {
            octave = octave > 0 ? octave - 1 : 0;
        }

        int notePress = allKeys.indexOf( (char) i.getKeyCode());
        if (notePress < 0 || notePress > allKeys.length() || keyOn[notePress]){
            return;
        }
        keyOn[notePress] = true;
        channel.noteOn(octave * keyInOct + notePress,90);
    }

    public void keyReleased(KeyEvent i){
        repaint();
        int notePress = allKeys.indexOf( (char) i.getKeyCode());
        if (notePress < 0 || notePress > allKeys.length()){
            return;
        }
        keyOn[notePress] = false;
        channel.noteOff(octave * keyInOct + notePress);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MiniPiano piano = new MiniPiano();
                JFrame frame = new JFrame();
                frame.setSize(400, 300);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.getContentPane().add(piano);
                frame.setVisible(true);
                piano.requestFocus();
            }
        });
    }
}
