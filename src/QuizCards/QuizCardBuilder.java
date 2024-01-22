package QuizCards;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import java.awt.*;

public class QuizCardBuilder {
    private JTextArea questionBox;
    private JTextArea answerBox;
    private ArrayList<QuizCard> cardList;
    private JFrame frame;

    public static void main(String[] args) {
        QuizCardBuilder builder = new QuizCardBuilder();
        builder.go();
    }

    public void go(){
        // Building GUI
        this.frame = new JFrame("Quiz Card Builder");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.frame.setFont(bigFont);

        Font bigFont = new Font("sanserif", Font.BOLD, 24);
        JPanel mainPanel = new JPanel();
        this.questionBox = new JTextArea(6, 20);
        this.questionBox.setLineWrap(true);
        this.questionBox.setWrapStyleWord(true);
        this.questionBox.setFont(bigFont);

        JScrollPane queScroll = new JScrollPane(this.questionBox);
        queScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        queScroll .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        this.answerBox = new JTextArea(6, 20);
        this.answerBox.setLineWrap(true);
        this.answerBox.setWrapStyleWord(true);
        this.answerBox.setFont(bigFont);

        JScrollPane ansScroll = new JScrollPane(answerBox);
        ansScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        ansScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JButton nextButton = new JButton("Next Card");
        this.cardList = new ArrayList<>();

        JLabel qLabel = new JLabel("Question:" );
        JLabel aLabel = new JLabel("Answer:");
        mainPanel.add(qLabel);
        mainPanel.add(queScroll);
        mainPanel.add(aLabel);
        mainPanel.add(ansScroll);
        mainPanel.add(nextButton);
        nextButton.addActionListener(new NextCardListener());

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem saveMenuItem = new JMenuItem("Save");
        newMenuItem.addActionListener(new NewMenuListener());


        saveMenuItem.addActionListener(new SaveMenuListener());
        fileMenu.add(newMenuItem);
        fileMenu.add(saveMenuItem);
        menuBar.add(fileMenu);
        frame.setJMenuBar(menuBar);
        frame.getContentPane().add(BorderLayout.CENTER, mainPanel);
        frame.setSize(500,600);
        frame.setVisible(true);

    } // end go()

    public class NextCardListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            QuizCard card = new QuizCard(questionBox.getText(), answerBox.getText());
            cardList.add(card);
            clearCard();
        }
    }

    public class SaveMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            QuizCard card = new QuizCard(questionBox.getText(), answerBox.getText());
            cardList.add(card);

            JFileChooser fileSave = new JFileChooser();
            fileSave.showSaveDialog(frame);
            saveFile(fileSave.getSelectedFile());
        }
    }

    public class NewMenuListener implements ActionListener {
        public void actionPerformed(ActionEvent event){
            cardList.clear();
            clearCard();
        }
    }

    private void clearCard() {
        this.questionBox.setText("");
        this.answerBox.setText("");
        this.questionBox.requestFocus();
    }

    private void saveFile (File file) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (QuizCard card: cardList){
                writer.write(card.getQuestion() + "/");
                writer.write(card.getAnswer() + "\n");
            }
            writer.close();
        }
        catch(IOException ex){
            System.out.println("Could not write the card list out");
            ex.printStackTrace();
        }
    }
}
