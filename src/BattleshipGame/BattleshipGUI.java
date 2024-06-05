package BattleshipGame;

import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.List;

public class BattleshipGUI {
    private static final int PADDING = 20;
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel sidePanel;
    private JPanel gameBoardButtons;
    private final BattleshipCLI game;
    private final int gridSize, shipCount;
    private final int vSpace, hSpace;

    private Vector<String> guessResponsesVector;
    private JList<String> guessResponsesArea;

    public static void main(String[] args) {
        new BattleshipGUI(7, 4).go();
    }

    public BattleshipGUI(int gridSize, int shipCount) {
        this.game = new BattleshipCLI(gridSize, shipCount);
        this.gridSize = gridSize;
        this.shipCount = shipCount;
        this.vSpace = this.hSpace = 1;
        this.setUpGUI();
    }

    public BattleshipGUI(int gridSize, int shipCount, int vSpace, int hSpace) {
        this.gridSize = gridSize;
        this.shipCount = shipCount;
        this.vSpace = vSpace;
        this.hSpace = hSpace;
        this.game = new BattleshipCLI(gridSize, shipCount);
    }

    public void go() {
        this.frame.setBounds(100,100 ,500, 500);
//        this.frame.pack();
        this.frame.setVisible(true);
    }

    public void setUpGUI() {
        this.frame = new JFrame("BattleShip");
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.mainPanel = new JPanel(new BorderLayout());
        this.mainPanel.setBorder(BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING));
        this.frame.getContentPane().add(BorderLayout.CENTER, this.mainPanel);

        GridLayout sidePanelGridLayout = new GridLayout();
        sidePanelGridLayout.setHgap(this.hSpace);
        this.sidePanel = new JPanel(sidePanelGridLayout);
        this.sidePanel.setBorder(BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING));
        this.frame.getContentPane().add(BorderLayout.EAST, this.sidePanel);


        /* CREATING ROW LABELS ON THE FRAME */
        JPanel rowLabelPanel = new JPanel(new GridLayout(1, this.gridSize, this.vSpace, this.hSpace));
        for (int i=0; i<this.gridSize; i++){
            rowLabelPanel.add( new JLabel(Integer.toString(i), JLabel.CENTER) );
        }
        this.mainPanel.add(BorderLayout.NORTH, rowLabelPanel);
        /* END CREATING ROW LABELS ON THE FRAME */


        /* CREATING BUTTONS ON THE FRAME */
         this.gameBoardButtons = new JPanel(new GridLayout(this.gridSize, this.gridSize, this.vSpace, this.hSpace));
        int totalButtons = this.gridSize * this.gridSize;


        for (int i=0; i<totalButtons; i++){
            JButton button = new JButton();
            button.addActionListener(new BoardCellListener(i/gridSize, i%gridSize));
            gameBoardButtons.add(button);
        }
        this.mainPanel.add(BorderLayout.CENTER, gameBoardButtons);
        /* END CREATING BUTTONS ON THE FRAME */


        /* CREATING COLUMN LABELS ON THE FRAME */
        JPanel columnLabelPanel = new JPanel(new GridLayout(this.gridSize, 1, this.vSpace, this.hSpace));
        for (int i=0; i<this.gridSize; i++){
            columnLabelPanel.add( new JLabel(Integer.toString(i)) );
        }
        this.mainPanel.add(BorderLayout.WEST, columnLabelPanel);
        /* END CREATING COLUMN LABELS ON THE FRAME */


        /* CREATING DISPLAY PANEL FOR RESPONSES AT FRAME BOTTOM */
        JPanel bottomPanel = new JPanel(new BorderLayout(this.hSpace, this.vSpace));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(PADDING,PADDING,PADDING,PADDING));
        this.mainPanel.add(BorderLayout.SOUTH, bottomPanel);

        this.guessResponsesVector = new Vector<>();
        this.guessResponsesArea = new JList<>();
        JScrollPane guessResponseScroll = new JScrollPane(this.guessResponsesArea);
//        guessResponseScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        guessResponseScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        bottomPanel.add(BorderLayout.NORTH,  new JLabel("Output Display:", JLabel.CENTER));
        bottomPanel.add(BorderLayout.CENTER, guessResponseScroll);
        /* END CREATING DISPLAY PANEL FOR RESPONSES AT FRAME BOTTOM */


        /* CREATING SIDE PANEL LABELS TO DISPLAY GAME STATS*/
        this.sidePanel.add( new JLabel("TARGETS: ", JLabel.CENTER) );
        for (String name : this.game.getTargetNames()){
            this.sidePanel.add(new JLabel(name, JLabel.CENTER));
        }


        JButton newGameButton = new JButton("New Game");
        newGameButton.addActionListener(e -> {
            this.game.setUpGame();
            for (Component component : this.gameBoardButtons.getComponents()){
                JButton button = (JButton) component;
                button.setText("");
                button.setEnabled(true);
            }

            List<String> names = this.game.getTargetNames();
            for (int i=0; i<names.size(); i++){
                ((JLabel)this.sidePanel.getComponent(i+1)).setText(names.get(i));
            }
            this.guessResponsesVector.clear();
            this.guessResponsesArea.setListData(this.guessResponsesVector);
        });

        this.sidePanel.add(newGameButton);
        // rows count set like this to ensure compact layout matching the grid and output display
        sidePanelGridLayout.setRows(this.gridSize * 2);
        /* END CREATING SIDE PANEL LABELS TO DISPLAY GAME STATS*/


        /* CREATING MENU BAR */
//        JMenuBar menuBar = new JMenuBar();
//        JMenu fileMenu = new JMenu("File");
//        menuBar.add(fileMenu);
//
//        JMenuItem changeGridSize = new JMenuItem("Grid Size");
//        changeGridSize.addActionListener(new GridSizeListener());
//        fileMenu.add(changeGridSize);
//        fileMenu.addSeparator();
//        JMenuItem changeShipCount = new JMenuItem("Ship Count");
//        fileMenu.add(changeShipCount);
//
//        this.frame.setJMenuBar(menuBar);
        /* END CREATING MENU BAR */

    } // end setUpGUI()

    private void gameOver() {
        // set all not clicked buttons to "-". --> Guaranteed to be empty since all ships are discovered
        // disable all buttons

        for (Component component : gameBoardButtons.getComponents()){
            JButton button = (JButton) component;

            if (button.getText().isEmpty()){
                button.setText("-");
            }
            button.setEnabled(false);
        }
        // show win game message / popup?
        guessResponsesVector.addFirst("You killed all warships in " + game.getGuessCount() + " guesses!!");
        double hitRate = this.game.getHitCount() / (double) this.game.getGuessCount();
        guessResponsesVector.addFirst("Hit Rate: %.3f".formatted(hitRate*100));
        guessResponsesArea.setListData(guessResponsesVector);
    } // end gameOver()

    public class BoardCellListener implements ActionListener {
        private final int row, col;
        private static final String alphabets = "abcdefghijklmnopqrstuvwxyz";
        public BoardCellListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(@NotNull ActionEvent event) {
            if ( !(event.getSource() instanceof JButton button) ) return;

            String guess = alphabets.charAt(row) + Integer.toString(col);

            String response = game.checkUserGuess(guess);

            if (response.equals("miss"))
                button.setText("-");
            else{
                button.setText("X");
//                button.setBackground(new Color(255,0,0, 40));
            }

            button.setEnabled(false);
            guessResponsesVector.addFirst(response);

            if (game.isGameOver())
                gameOver();

            guessResponsesArea.setListData(guessResponsesVector);
        }
    } // end BoardCellListener class
}
