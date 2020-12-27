package me.ddquin;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class GUIMinesweeper {
    private JFrame frame;
    private JLabel startLabel, flagLabel, timeLabel;
    private JTextField widthText, heightText, minesText;
    private JPanel startMenuPanel, gamePanel, gridPanel;
    private int time;
    private Timer timer;
    private Board board;

    public GUIMinesweeper() {
        makeFrame();
    }

    public class TileButton extends JButton {

        public int x;
        public int y;

        public TileButton(int x, int y) {
            super();
            this.x = x;
            this.y = y;
        }

        public TileButton(int x, int y, String name) {
            super(name);
            this.x = x;
            this.y = y;
        }

    }

    private void makeFrame() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 300));

        switchToStart();

        frame.setLocation(200, 200);
        frame.setVisible(true);
    }

    private void switchToStart() {
        Container contentPane = frame.getContentPane();

        //First row for setting width
        widthText = new JTextField();
        JLabel widthLabel = new JLabel("Enter in the width of the board.");
        widthLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel widthPanel = new JPanel(new GridLayout(1, 2));
        widthPanel.add(widthLabel);
        widthPanel.add(widthText);

        //Second row for setting height
        heightText = new JTextField();
        JLabel heightLabel = new JLabel("Enter in the height of the board.");
        heightLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel heightPanel = new JPanel(new GridLayout(1, 2));
        heightPanel.add(heightLabel);
        heightPanel.add(heightText);

        //Third row for setting number of mines
        minesText = new JTextField();
        JLabel minesLabel = new JLabel("Enter in the number of mines.");
        minesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel minesPanel = new JPanel(new GridLayout(1, 2));
        minesPanel.add(minesLabel);
        minesPanel.add(minesText);

        //Fourth row for start button and helpful text
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> checkIfCanStartGame());
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new KeyEventDispatcher() {
                    @Override
                    public boolean dispatchKeyEvent(KeyEvent e) {
                        if (startMenuPanel.isVisible()) {
                            if (e.getID() == KeyEvent.KEY_PRESSED) {
                                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                                    checkIfCanStartGame();
                                }
                            }
                        }
                        return false;
                    }
                });
        startLabel = new JLabel("Enter in values and press start");
        startLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel startPanel = new JPanel(new GridLayout(1, 2));
        startPanel.add(startLabel);
        startPanel.add(startButton);

        //Connect all four rows
        startMenuPanel = new JPanel();
        startMenuPanel.setLayout(new BoxLayout(startMenuPanel, BoxLayout.Y_AXIS));
        startMenuPanel.add(widthPanel);
        startMenuPanel.add(heightPanel);
        startMenuPanel.add(minesPanel);
        startMenuPanel.add(startPanel);
        contentPane.add(startMenuPanel, BorderLayout.CENTER);
    }

    private void checkIfCanStartGame() {
        int width;
        int height;
        int mines;
        try {
            width = Integer.parseInt(widthText.getText());
            height = Integer.parseInt(heightText.getText());
            mines = Integer.parseInt(minesText.getText());

            if (width < 1 || height < 1 || mines < 1) {
                startLabel.setText("All fields must be greater than 0");
                startLabel.setForeground(Color.RED);
                return;
            }
            if (mines > ((width * height) - 1)) {
                startLabel.setText("Number of mines can not exceed " + ((width * height) - 1));
                startLabel.setForeground(Color.RED);
                return;
            }
            startLabel.setText("Enter in values and press start");
            startLabel.setForeground(Color.BLACK);
            board = new Board(width, height, mines);
            switchToGame();
        } catch (NumberFormatException e) {
            startLabel.setText("Please make sure all fields are numbers");
            startLabel.setForeground(Color.RED);
        }
    }

    private void switchToGame() {
        Container contentPane = frame.getContentPane();
        startMenuPanel.setVisible(false);

        //Make top panel
        JPanel topPanel = new JPanel(new GridLayout(1, 3));
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> switchBackToStart());
        flagLabel = new JLabel("Flags: " + board.getFlags());
        flagLabel.setHorizontalAlignment(SwingConstants.CENTER);
        timeLabel = new JLabel("Time take: 0");
        timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        topPanel.add(flagLabel);
        topPanel.add(timeLabel);
        topPanel.add(quitButton);

        //Set up timer
        time = 0;
        int delay = 1000; //milliseconds
        ActionListener taskPerformer = evt -> {
            time++;
            timeLabel.setText("Time taken: " + time);
        };
        timer = new Timer(delay, taskPerformer);
        timer.start();

        // make grid panel
        gridPanel = new JPanel(new GridLayout(board.getHeight(), board.getWidth()));
        redrawGrid();
        gamePanel = new JPanel();
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));
        gamePanel.add(topPanel);
        gamePanel.add(gridPanel);
        contentPane.add(gamePanel, BorderLayout.CENTER);

        frame.setSize(board.getWidth() * 50, board.getHeight() * 50);
    }

    private void switchBackToStart() {
        timer.stop();
        gamePanel.setVisible(false);
        startMenuPanel.setVisible(true);
        frame.setSize(new Dimension(400, 300));
        frame.setLocation(200, 200);
    }


    private void redrawGrid() {
        int i = 0;
        flagLabel.setText("Flags: " + board.getFlags());
        //Remove every thing from the grid and add it all again
        gridPanel.removeAll();
        for (int row = 0; row < board.getHeight(); row++) {
            for (int column = 0; column < board.getWidth(); column++) {
                TileButton tileButton = new TileButton(column, row);
                tileButton.setPreferredSize(new Dimension(50, 50));
                tileButton.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        getTileClicked(e);
                    }
                });
                gridPanel.add(tileButton, i);
                Tile tile = board.getTiles()[row][column];
                // If the tile is revealed and it is an empty tile then grey it out
                if (!tile.isHidden() && tile.getMinesAdjacent() == 0 && !tile.isMine()) {
                    tileButton.setEnabled(false);
                }
                setTileImage(tileButton, tile);
                i++;
            }
        }
    }


    public void playSound(final String url) {
        try {
            InputStream is = getClass().getResourceAsStream(url);
            InputStream bufferedIs = new BufferedInputStream(is);
            AudioInputStream aIn = AudioSystem.getAudioInputStream(bufferedIs);
            Clip clip = AudioSystem.getClip();
            clip.open(aIn);
            clip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }

    private void getTileClicked(MouseEvent e) {
        TileButton tileButton = (TileButton) e.getSource();
        //If left clicked mouse then sweep tile
        if (e.getButton() == MouseEvent.BUTTON1) {
            Board.GameStatus status = board.sweepTile(tileButton.x, tileButton.y);
            switch (status) {
                case WON:
                case LOST:
                    if (status == Board.GameStatus.WON) {
                        playSound("/sounds/win.wav");
                    } else {
                        playSound("/sounds/hitMine.wav");
                    }
                    timer.stop();
                    int selectedValue = JOptionPane.showConfirmDialog(frame.getContentPane(), "You " + (status == Board.GameStatus.WON ? "won" : "lost") + ", your final time was " + time + " seconds.\n" +
                            "Do you want to try again (Yes),  or go back to the main menu (No), or look at the board (Cancel).");
                    board.makeAllVisible();
                    // Selected value of 1 means No so go back to main menu
                    if (selectedValue == 1) {
                        switchBackToStart();
                        return;
                        // Value of 0 so picked yes and start game
                    } else if (selectedValue == 0) {
                        switchBackToStart();
                        checkIfCanStartGame();
                        return;
                    }
                    // Otherwise do nothing
                    break;
                case HIT_ONE:
                    playSound("/sounds/hitOne.wav");
                    break;
                case HIT_LOTS:
                    playSound("/sounds/hitLots.wav");
                    break;
                case NOTHING: //This is called when the user clicks a revealed tile
                    break;
            }
        } else if (e.getButton() == MouseEvent.BUTTON3) { ///If right clicked then flag tile
            Board.GameStatus status = board.flagTile(tileButton.x, tileButton.y);
            if (status == Board.GameStatus.FLAG) playSound("/sounds/flag.wav");
        }
        //Draw the grid again after every click
        redrawGrid();
    }

    //Helper method for getting image icon
    protected ImageIcon createImageIcon(String path,
                                        String description) {
        URL imgURL = getClass().getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }


    //Helper method for setting the correct image on the tile button
    public void setTileImage(TileButton tileButton, Tile tile) {
        if (tile.isFlagged()) {
            tileButton.setIcon(createImageIcon("/images/flag.png", "flag"));
            return;
        }
        if (tile.isHidden()) {
            return;
        }
        if (tile.isMine()) {
            tileButton.setIcon(createImageIcon("/images/mine.png", "mine"));
            return;
        }
        if (tile.getMinesAdjacent() == 0) {
            return;
        }
        tileButton.setIcon(createImageIcon("/images/" + tile.getMinesAdjacent() + ".png", "one"));
    }


}
