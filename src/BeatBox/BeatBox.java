package BeatBox;

import javax.sound.midi.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class BeatBox {
    private JPanel mainPanel;
    private ArrayList<JCheckBox> checkBoxes;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private JFrame frame;
    private JLabel tempoLabel;

    private final int beatCount = 16;

    public static void main(String[] args) {
        new  BeatBox().runGUI();
    }

    String[] instrumentLabels = {
      "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
      "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
      "Maracas", "Whistle", "Low Conga", "Cowbell",
      "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };
    int[] instrumentKeys = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};

    public void runGUI(){
        this.frame = new JFrame("Java BeatBox");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BorderLayout layout = new BorderLayout();
        JPanel background = new JPanel(layout);
        background.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        checkBoxes = new ArrayList<>();
        Box buttonBox = new Box(BoxLayout.Y_AXIS);

        String[] buttonText = {"Start", "Stop", "Increase Tempo", "Decrease Tempo", "Reset", "Save Beats", "Open"};
        ActionListener[] buttonListeners = {
                new StartListener(), new StopListener(), new UpTempoListener(),
                new DownTempoListener(), new ResetListener(), new SaveBeatsListener(),
                new OpenBeatsListener()
        };

        // Creating buttons
        for (int i=0; i<buttonText.length; i++){
            JButton button = new JButton(buttonText[i]);
            button.addActionListener(buttonListeners[i]);
            buttonBox.add(button);
        }

//        Box nameBox = new Box(BoxLayout.Y_AXIS);
         JPanel nameBox = new JPanel(new GridLayout(this.instrumentLabels.length, 1));
        for (int i=0; i<16; i++) {
            nameBox.add(new Label(this.instrumentLabels[i]));
        }

        // get nameBox's layout manager, cast it to its special subclass, and set the vertical gap to 1
        background.add(BorderLayout.EAST, buttonBox);
        background.add(BorderLayout.WEST, nameBox);

        this.frame.getContentPane().add(background);

        GridLayout grid = new GridLayout(this.instrumentKeys.length, this.beatCount);
        grid.setVgap(2);
        grid.setHgap(2);
        ((GridLayout) nameBox.getLayout()).setVgap(grid.getVgap());
        this.mainPanel = new JPanel(grid);

        background.add(BorderLayout.CENTER, this.mainPanel);

        int totalCheckBoxes = this.instrumentKeys.length * this.beatCount;
        for (int i=0; i<totalCheckBoxes; i++){
            JCheckBox checkBox = new JCheckBox();
            checkBox.setSelected(false);
            this.checkBoxes.add(checkBox);
            mainPanel.add(checkBox);
        }

        setUpMidi();
        this.tempoLabel = new JLabel("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        buttonBox.add(this.tempoLabel);

        this.frame.setBounds(50,50 ,300, 300);
        this.frame.pack();
        this.frame.setVisible(true);

    } // END setUpGUI method

    public void setUpMidi() {
        try{
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.sequence = new Sequence(Sequence.PPQ, 4);
            this.track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception ex) {ex.printStackTrace();}
    }

    public void resetTrack() {
        int[] trackList;

        this.sequence.deleteTrack(this.track);
        this.track = sequence.createTrack();

        // runs for all beats, for all instruments
        for (int i=0; i<this.instrumentKeys.length; i++){
            trackList = new int[this.beatCount];
            int key = this.instrumentKeys[i];

            for (int j=0; j<this.beatCount; j++){
                // identical to this.checkBoxesMatrix[i][j] if checkBoxes was a 2D matrix named checkBoxesMatrix
                JCheckBox checkBox = this.checkBoxes.get(j + (this.beatCount * i));
                trackList[j] = checkBox.isSelected() ? key : 0;
            }

            // add the beats for the current instrument to the track
            makeTracks(trackList);
            // adding the controllerEvent MidiEvent
            this.track.add(makeEvent(176, 1, 127, 0, 16));
        } // outer for closed

        // guarantees at least one beat at the final tick to make sure beatbox covers all the beats before looping
        track.add(makeEvent(192, 9, 1, 0, this.beatCount - 1));
    }

    public void playTrack() {
        try{
            this.sequencer.setSequence(this.sequence);
            this.sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            this.sequencer.setTempoInBPM(120);
            this.sequencer.start();
        }
        catch(Exception ex) {ex.printStackTrace();}
    }

    public MidiEvent makeEvent(int command, int channel, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, one, two);
            event = new MidiEvent(message, tick);
        }
        catch (Exception ex) { ex.printStackTrace(); }

        return event;
    }

    public void makeTracks(int[] beatState){
        // runs for all beats for a given instrument
        for (int i=0; i<this.beatCount; i++){
            int key = beatState[i];
            if (key == 0) continue;

            this.track.add(makeEvent(144, 9, key, 100, i));
            this.track.add(makeEvent(128, 9, key, 100, i+1));
        }
    }

    public class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            resetTrack();
            playTrack();
        }
    }

    public class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            sequencer.stop();
        }
    }

    public class UpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(tempoFactor + 0.05f);
            tempoLabel.setText("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        }
    }

    public class DownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(tempoFactor - 0.05f);
            tempoLabel.setText("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        }
    }

    public class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            sequencer.stop();
            for (JCheckBox checkBox : checkBoxes){
                checkBox.setSelected(false);
            }
        }
    }

    public class SaveBeatsListener implements ActionListener {
        public void actionPerformed(ActionEvent a){
            boolean[] checkBoxState =  new boolean[checkBoxes.size()];

            for (int i=0; i<checkBoxes.size(); i++) checkBoxState[i] = checkBoxes.get(i).isSelected();

            try{
                JFileChooser fileSave = new JFileChooser();
                fileSave.showSaveDialog(frame);
                ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(fileSave.getSelectedFile()) );
                outStream.writeObject(checkBoxState);
                outStream.close();
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }

    public class OpenBeatsListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean[] checkboxState = null;
            try{
                JFileChooser fileOpen = new JFileChooser();
                fileOpen.showOpenDialog(frame);
                ObjectInputStream inStream = new ObjectInputStream( new FileInputStream(fileOpen.getSelectedFile()) );
                checkboxState = (boolean[]) inStream.readObject();
            }
            catch (Exception ex) {ex.printStackTrace();}

            for (int i = 0; i < Objects.requireNonNull(checkboxState).length; i++){
                checkBoxes.get(i).setSelected(checkboxState[i]);
            }
            sequencer.stop();
        }
    }
}
