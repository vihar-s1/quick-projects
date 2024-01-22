package BeatBoxChat;

import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class BeatBoxChat {
    private static int PORT = 5244;
    private static String SERVER = "127.0.0.1";
    private JFrame frame;
    private JPanel mainPanel;
    private ArrayList<JCheckBox> checkBoxes;
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;
    private JLabel tempoLabel;
    private final int beatCount = 16;

    /* CHAT RELATED FIELDS START HERE */
    private JList<String> incomingList;
    private JTextField userMessage;
    private int messagesSent;
    private Vector<String> listVector = new Vector<>();
    private String userName;
    private Socket serverSock;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private HashMap<String, boolean[]> otherSequencesMap = new HashMap<>();
//    private Sequence mySequence = null;
    /* CHAT RELATED FIELDS END HERE */

    String[] instrumentLabels = {
      "Bass Drum", "Closed Hi-Hat", "Open Hi-Hat", "Acoustic Snare",
      "Crash Cymbal", "Hand Clap", "High Tom", "Hi Bongo",
      "Maracas", "Whistle", "Low Conga", "Cowbell",
      "Vibraslap", "Low-mid Tom", "High Agogo", "Open Hi Conga"
    };
    int[] instrumentKeys = {35, 42, 46, 38, 49, 39, 50, 60, 70, 72, 64, 56, 58, 47, 67, 63};


    public static void main(String[] args) {
        BeatBoxChat beatBox = new BeatBoxChat();
        beatBox.startUp(args[0]);
    }


    public static int getPORT() {
        return PORT;
    }

    public static boolean setPORT(int PORT) {
        if (1024 > PORT || PORT > 65535) return false;
        BeatBoxChat.PORT = PORT;
        return true;
    }

    public void startUp(String name) {
        this.userName = name;
        // opening connection to the server
        try {
            this.serverSock = new Socket(BeatBoxChat.SERVER, BeatBoxChat.PORT);
            System.out.println("Connected to Server at: " + serverSock);
            this.outputStream = new ObjectOutputStream(serverSock.getOutputStream());
            this.inputStream = new ObjectInputStream(serverSock.getInputStream());
            Thread remote = new Thread(new RemoteReader());
            remote.start();
        }
        catch (Exception ex) {
            System.out.println("Could not connect - You'll have to play alone.");
        }
        setUpMidi();
        runGUI();
    } // end starUp()

    public void runGUI(){
        this.frame = new JFrame("Java BeatBox Chat - " + this.userName);
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

        this.tempoLabel = new JLabel("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        buttonBox.add(this.tempoLabel);

        /*CHAT RELATED CODE STARTS HERE*/
        this.userMessage = new JTextField();
        this.userMessage.setToolTipText("Type Your Message Here");
        buttonBox.add(this.userMessage);

        JButton shareBeats = new JButton("Share Beats");
        shareBeats.addActionListener(new ShareBeatsListener());
        buttonBox.add(shareBeats);

        this.incomingList = new JList<>();
        this.incomingList.addListSelectionListener(new IncomingListSelectionListener());
        this.incomingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(this.incomingList);
        buttonBox.add(listScroll);
        this.incomingList.setListData(this.listVector); // no data to start with
        /*CHAT RELATED CODE ENDS HERE*/

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
        // make sure the checkbox and the instrument labels have the same vertical gap
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

        this.frame.setBounds(50,50 ,300, 300);
        this.frame.pack();
        this.frame.setVisible(true);

    } // END runGUI method

    public void setUpMidi() {
        try{
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            this.sequence = new Sequence(Sequence.PPQ, 4);
            this.track = sequence.createTrack();
            sequencer.setTempoInBPM(120);
        }
        catch (Exception ex) {ex.printStackTrace();}
    } // end setUpMidi()

    public void rebuildSequence() {
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
            this.track.add(createEvent(176, 1, 127, 0, 16));
        } // outer for closed

        // guarantees at least one beat at the final tick to make sure beatbox covers all the beats before looping
        track.add(createEvent(192, 9, 1, 0, this.beatCount - 1));
    } // end rebuildSequence()

    public void playSequence() {
        try{
            this.sequencer.setSequence(this.sequence);
            this.sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            this.sequencer.setTempoInBPM(120);
            this.sequencer.start();
        }
        catch(Exception ex) {ex.printStackTrace();}
    } // end playTrack()

    public MidiEvent createEvent(int command, int channel, int one, int two, int tick){
        MidiEvent event = null;
        try{
            ShortMessage message = new ShortMessage();
            message.setMessage(command, channel, one, two);
            event = new MidiEvent(message, tick);
        }
        catch (Exception ex) { ex.printStackTrace(); }

        return event;
    } // end makeEvent()

    public void makeTracks(int[] beatState){
        // runs for all beats for a given instrument
        for (int i=0; i<this.beatCount; i++){
            int key = beatState[i];
            if (key == 0) continue;

            this.track.add(createEvent(144, 9, key, 100, i));
            this.track.add(createEvent(128, 9, key, 100, i+1));
        }
    } // end makeTracks()


    public void changeSequence(boolean[] checkBoxState) {
        for (int i=0; i<checkBoxState.length; i++)
            checkBoxes.get(i).setSelected(checkBoxState[i]);
    } // end changeSequence()

    public class StartListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            rebuildSequence();
            playSequence();
        }
    } // end StartListener Class

    public class StopListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            sequencer.stop();
        }
    } // end StopListener Class

    public class UpTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(tempoFactor + 0.05f);
            tempoLabel.setText("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        }
    } // end UpTempoListener class

    public class DownTempoListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            float tempoFactor = sequencer.getTempoFactor();
            sequencer.setTempoFactor(tempoFactor - 0.05f);
            tempoLabel.setText("Tempo Factor - %.2f".formatted(sequencer.getTempoFactor()));
        }
    } // end DownTempoListener class

    public class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            sequencer.stop();
            for (JCheckBox checkBox : checkBoxes){
                checkBox.setSelected(false);
            }
        }
    } // end ResetListener Class

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
    } // end SaveBeatsListener class

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
            rebuildSequence();
            playSequence();
        }
    } // end OpenBeatsListener Class

    public class ShareBeatsListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            boolean[] checkboxState = new boolean[checkBoxes.size()];
            for (int i=0; i< checkBoxes.size(); i++) {
                checkboxState[i] = checkBoxes.get(i).isSelected();
            }
            try {
                outputStream.writeObject(userName + messagesSent++ + ": " + userMessage.getText());
                outputStream.writeObject(checkboxState);
            }
            catch (Exception ex) {
                System.out.println("Sorry dude. Could not send message and beats to the server");
            }
            userMessage.setText("");
        }
    } // end ShareBeatsListener class

    public class IncomingListSelectionListener implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) return;
            String selected = incomingList.getSelectedValue();
            if (selected == null) return;

            // go to the map and change the sequence
            boolean[] selectedState = otherSequencesMap.get(selected);
            changeSequence(selectedState);
            sequencer.stop();
            rebuildSequence();
            playSequence();
        }
    }

    public class RemoteReader implements Runnable {
        boolean[] checkBoxState = null;
        String nameToShow = null;
        Object obj = null;
        public void run() {
            try {
//                while((obj = inputStream.readObject()) != null){
                while (serverSock.isConnected()) {
                    System.out.println("waiting for server message...");
                    obj = inputStream.readObject();
                    if (obj == null) continue;
                    System.out.println("got an object from server");

                    nameToShow = (String)obj;
                    checkBoxState = (boolean[]) inputStream.readObject();
                    otherSequencesMap.put(nameToShow, checkBoxState);
                    listVector.add(nameToShow);
                    incomingList.setListData(listVector);
                }
            }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    } // end RemoteReader class
}
