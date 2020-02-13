import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.GridBagConstraints;
import java.awt.Component;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.util.Date;
import java.awt.Image;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import java.util.*;
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.util.Random;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import javax.imageio.ImageIO;
import javax.lang.model.util.ElementScanner6;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import java.io.File; 
import java.io.FileWriter;
import java.util.Scanner;

public class Minefield extends JFrame
{
	public Stopwatch sw = new Stopwatch();

	final int ROWS = 8;
	final int COLS = 8;
	public int iClicksStartGame;
	public int BOMBS;
	public int bombsTotaal;
	private String sDifficulty;
	private MSCell field[][];
	private JButton[][] grid = new JButton[ROWS+2][COLS+2];
	private String highScoresTime;
	private JPanel panel1 = new JPanel();
	private JPanel highScoresPanel = new JPanel();
	private JButton hello = new JButton("Start Over");
	private JLabel bombCounter = new JLabel("Bombs");
	private JPanel gridPanel = new JPanel();
	private JLabel tempLabel;
	private JTextArea highScores = new JTextArea();
	private final GridBagLayout layout = new GridBagLayout();;
	private final GridBagConstraints gbc = new GridBagConstraints();;
	private JFrame frame = new JFrame();
	private Object[] possibilities = {"Easy", "Medium", "Hard"};
	private MouseHandler mh = new MouseHandler();
	private JLabel tf_time = new JLabel("Time");
	private JScrollPane scroll;
	ImageIcon smileyScaled = new ImageIcon("Smiley.png");
	ImageIcon unhappyScaled = new ImageIcon("Unhappy.png");
	ImageIcon bombClickedScaled = new ImageIcon("ClickedCellBomb.png");
	ImageIcon bombUnclickedClickedScaled = new ImageIcon("OtherBombsUnclicked.png");
	ImageIcon unClickedCell = new ImageIcon("UnClickedCell.png");
	ImageIcon borderCell = new ImageIcon("Border.png");
	ImageIcon clickedCell = new ImageIcon("ClickedCell.png");
	ImageIcon clickedCell1 = new ImageIcon("ClickedCell1.png");
	ImageIcon clickedCell2 = new ImageIcon("ClickedCell2.png");
	ImageIcon clickedCell3 = new ImageIcon("ClickedCell3.png");
	ImageIcon clickedCell4 = new ImageIcon("ClickedCell4.png");
	ImageIcon clickedCell5 = new ImageIcon("ClickedCell5.png");
	ImageIcon clickedCell6 = new ImageIcon("ClickedCell6.png");
	ImageIcon clickedCell7 = new ImageIcon("ClickedCell7.png");
	ImageIcon clickedCell8 = new ImageIcon("ClickedCell8.png");
	ImageIcon cellFlagged = new ImageIcon("Flagged.png");

	public Minefield()
	{
		// GUI components
		super("MineSweeper");
		setLayout(new BorderLayout());
		panel1.add(bombCounter);
		add(panel1,BorderLayout.NORTH);
		hello.setText("");
		hello.setBorder(BorderFactory.createEmptyBorder());
		hello.setIcon(smileyScaled);
		hello.addMouseListener (mh);	
		panel1.add(hello);
		panel1.add(tf_time);
		add(panel1,BorderLayout.NORTH);
		scroll = new JScrollPane(highScores);
		scroll.setPreferredSize(new Dimension(0, 100));
		add(scroll,BorderLayout.SOUTH);
		highScores.setEditable(false);
		gridPanel.setLayout(layout);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		int row, col;
		for (row=0; row<ROWS+2; row++)
		{
			for (col = 0; col<COLS+2; col++)
			{
				gbc.gridx = row;
				gbc.gridy = col;
				grid[row][col] = new JButton(row +","+col);
				grid[row][col].setPreferredSize(new Dimension(400, 400));
				gridPanel.add(grid[row][col],gbc);
				grid[row][col].addMouseListener (mh);		
			}
		}
		add(gridPanel,BorderLayout.CENTER);
		field= new MSCell[ROWS+2][COLS+2];
		restartGame();
	}

	public void restartGame()
	{
		iClicksStartGame = 0;
		String s = (String)JOptionPane.showInputDialog(frame, "Choose your difficulty:", "Difficulty", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Easy");
		//If a string was returned, say so.
		if ((s == null) || (s.length() == 0)) {
			JOptionPane.showMessageDialog(frame, "No difficulty chosen. Easy is set as default!","Warning",JOptionPane.WARNING_MESSAGE);
			BOMBS = 4;
		}
		else if(s == "Easy")
		{
			BOMBS = 4;
			sDifficulty	= "Easy";
		}
		else if(s == "Medium")
		{
			BOMBS = 8;
			sDifficulty	= "Medium";
		}
		else // Hard
		{
			BOMBS = 16;
			sDifficulty	= "Hard";
		}
		tf_time.setText("Time");
		bombsTotaal = BOMBS;
		bombCounter.setText(BOMBS + " Bombs");
		hello.setIcon(smileyScaled);
		gridPanel.removeAll();
		int row, col;
		for (row=0; row<ROWS+2; row++)
		{
			for (col = 0; col<COLS+2; col++)
			{
				gbc.gridx = row;
				gbc.gridy = col;
				grid[row][col] = new JButton(row +","+col);
				grid[row][col].setPreferredSize(new Dimension(400, 400));
				gridPanel.add(grid[row][col],gbc);
				grid[row][col].addMouseListener (mh);		
			}
		}
		highScores.setText("High Scores:\n");
		try
		{
			File file = new File(sDifficulty + ".txt"); 
			Scanner sc = new Scanner(file);
			while (sc.hasNextLine()) 
			{
				highScores.append("\n" + (sc.nextLine()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		initialiseField();
		plantBombs();
		computeNumbers();
		displayField();
	}

	public void playSound(String soundName)
	{
		try 
		{
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundName).getAbsoluteFile( ));
			Clip clip = AudioSystem.getClip( );
			clip.open(audioInputStream);
			clip.start( );
		}
		catch(Exception ex)
		{
			System.out.println("Error with playing sound.");
			ex.printStackTrace( );
		}
	}

	public void initialiseField()
	{
		for (int i=0; i<ROWS+2;i++)
		{
			for (int j=0; j<COLS+2; j++)
			{
				field[i][j]=new MSCell();
			}
		}
	}
	
	public void plantBombs()
	{
		Random randomNumbers = new Random();
		int bombsPlanted=0;
		int bombRow = 0;
		int bombCol = 0;
		while (bombsPlanted < BOMBS)
		{
			bombRow = randomNumbers.nextInt(ROWS)+1; //nextInt(n) produce random number from 0-(n-1) we want a number from 1-(ROWS)
			bombCol = randomNumbers.nextInt(COLS)+1;
			if (!field[bombRow][bombCol].isBomb()) // If not yet bomb
			{
				field[bombRow][bombCol].setBomb();
				System.out.println( bombRow +" " + bombCol);
				bombsPlanted++;
			}
		}
	}

	public void computeNumbers()
	{
		int countBombs= 0;
		for (int i=1; i<=ROWS;i++)
		{
			for (int j=1; j<=COLS; j++)
			{	
	            countBombs=0;
				if (!field[i][j].isBomb())
				{
					for (int a=-1; a<=1; a++)
					{
						for (int b=-1; b<=1; b++)
						{
							if (field[i+a][j+b].isBomb() )
							{
								countBombs++;
							}
						}
							
					}	
					field[i][j].setValue(countBombs);
				}
			}
		}
	}
	
	public void displayField()
	{
		for (int i=0; i<=ROWS+1;i++)
		{
			for (int j=0; j<=COLS+1; j++)
			{  
				if (i == 0 || j == 0 || i == ROWS+1 || j == COLS+1) //Om raampie te maak
				{
					grid[i][j].setText("");
					grid[i][j].setBorder(BorderFactory.createEmptyBorder());
					grid[i][j].setIcon(borderCell);
				}
				else
				{
					grid[i][j].setText("");
					grid[i][j].setBorder(BorderFactory.createEmptyBorder());
					grid[i][j].setIcon(unClickedCell);
				}
				// Moet hier stel aan einde
				if(field[i][j].isBomb())
				{
					grid[i][j].setText("B");
				}
			}
			repaint();
			revalidate();
		}
	}

	public void checkGameWon()
	{
		int iTotal = 0;
		for (int j=1; j<=ROWS; j++)
		{
			for (int k=1; k<=COLS; k++)
			{
				if (field[j][k].isBomb() == false)
				{	
					if (field[j][k].isRevealed() == true)
					{
						iTotal++;
					}
				}
				else
				{
					if (field[j][k].isFlagged() == true)
					{
						iTotal++;
					}
				}
			}
		}
		if (iTotal == ROWS*COLS)
		{
			sw.stop();
			playSound("Won.wav");
			JOptionPane.showMessageDialog(null, "YOU WON", "GAME OVER", JOptionPane.WARNING_MESSAGE);
			// Add to High Scores
			highScoresTime = tf_time.getText().substring((6));
			String data = "";
			String sLyn = "";
			SLL<Integer> highScoresList = new SLL<Integer>();
			try 
			{
				File file = new File(sDifficulty + ".txt");
				Scanner sc = new Scanner(file);
				while (sc.hasNextLine()) 
				{
					data = sc.nextLine();
					highScoresList.intsertSorted(new Integer(Integer.parseInt(data)));
				}    
				highScoresList.intsertSorted(new Integer(Integer.parseInt(highScoresTime)));

				FileWriter fw = new FileWriter(file);
				sLyn = highScoresList.saveLinkedList();
				fw.write(sLyn);          
				fw.close(); 
			} 
			catch(Exception e) {
				e.printStackTrace();
			}
			restartGame();
		}
	}
	
	public void showAllBombs()
	{
		for (int i=0; i<=ROWS+1;i++)
		{
			for (int j=0; j<=COLS+1; j++)
			{  
				if (field[i][j].toString() == "B")
				{
					grid[i][j].setText("");
					grid[i][j].setBorder(BorderFactory.createEmptyBorder());
					grid[i][j].setIcon(bombUnclickedClickedScaled);
				}
			}
		}
	}

	public void openCell(int r, int c)
	{
		tempLabel= new JLabel("@");
		if(field[r][c].isRevealed() == false && field[r][c].getValue() == 0 && r!=0 && r!= ROWS+1 && c!=0 && c!=COLS+1)
		{
			field[r][c].setRevealed();
			gbc.gridx = r;
			gbc.gridy = c;
			gridPanel.remove(grid[r][c]);
			tempLabel.setText("");
			gridPanel.add(tempLabel,gbc);
			tempLabel.setBorder(BorderFactory.createEmptyBorder());
			tempLabel.setIcon(clickedCell);
			for (int a=-1; a<=1; a++)
			{
				for (int b=-1; b<=1; b++)
				{
					openCell(r + a, c + b);
				}
			}
		}
		if(field[r][c].isBomb())
		{
			return;
		}
		if(field[r][c].isRevealed() == false && field[r][c].getValue() > 0)
		{
			field[r][c].setRevealed();
			gbc.gridx = r;
			gbc.gridy = c;
			gridPanel.remove(grid[r][c]);
			tempLabel.setText("");
			gridPanel.add(tempLabel,gbc);
			tempLabel.setBorder(BorderFactory.createEmptyBorder());
			if (field[r][c].getValue() == 1)
			{
				tempLabel.setIcon(clickedCell1);
			} else if (field[r][c].getValue() == 2)
			{
				tempLabel.setIcon(clickedCell2);
			} else if (field[r][c].getValue() == 3)
			{
				tempLabel.setIcon(clickedCell3);
			} else if (field[r][c].getValue() == 4)
			{
				tempLabel.setIcon(clickedCell4);
			} else if (field[r][c].getValue() == 5)
			{
				tempLabel.setIcon(clickedCell5);
			} else if (field[r][c].getValue() == 6)
			{
				tempLabel.setIcon(clickedCell6);
			} else if (field[r][c].getValue() == 7)
			{
				tempLabel.setIcon(clickedCell7);
			}
			return;
		}
	}

	public class Stopwatch extends JFrame implements Runnable {
 
        long startTime;
        Thread updater;
        boolean isRunning = false;
        long a = 0;
        Runnable displayUpdater = new Runnable() {
 
            public void run() {
                displayElapsedTime(a);
                a++;
            }
        };
 
        public void stop() {
            long elapsed = a;
            isRunning = false;
            try {
                updater.join();
            } catch (InterruptedException ie) {
            }
            displayElapsedTime(elapsed);
            a = 0;
        }
 
        private void displayElapsedTime(long elapsedTime) {
 
            if (elapsedTime >= 0 && elapsedTime < 9) {
                tf_time.setText("TIME: 00" + elapsedTime);
            } else if (elapsedTime > 9 && elapsedTime < 99) {
                tf_time.setText("TIME: 0" + elapsedTime);
            } else if (elapsedTime > 99 && elapsedTime < 999) {
                tf_time.setText("TIME: " + elapsedTime);
            }
        }
 
        public void run() {
            try {
                while (isRunning) {
                    SwingUtilities.invokeAndWait(displayUpdater);
                    Thread.sleep(1000);
                }
            } catch (java.lang.reflect.InvocationTargetException ite) {
                ite.printStackTrace(System.err);
            } catch (InterruptedException ie) {
            }
        }

        public void Start() {
            startTime = System.currentTimeMillis();
            isRunning = true;
            updater = new Thread(this);
            updater.start();
        }
    }

	// GUI event handlers
	// inner class for MouseListener
	private class MouseHandler implements MouseListener
	{
		@Override 
		 public void mouseClicked(MouseEvent me) 
		 {
			//left button
			if (me.getButton()== MouseEvent.BUTTON1)
			{
				Object o= me.getSource();
				tempLabel= new JLabel("@");
				for (int r=1; r<=ROWS; r++)
					for (int c = 1; c<=COLS; c++)
					{
						if 	(grid[r][c] == (JButton) o)
						{
							if (field[r][c].toString() == "F")
							{
								playSound("Click.wav");
								field[r][c].setFlagged(false);
								grid[r][c].setText("");
								grid[r][c].setBorder(BorderFactory.createEmptyBorder());
								grid[r][c].setIcon(unClickedCell);
								bombsTotaal = bombsTotaal + 1;
								bombCounter.setText(bombsTotaal + " Bombs");
								repaint();
								revalidate();
							}
							else 
							{	
								if(iClicksStartGame == 0)
								{
									sw.Start();
									iClicksStartGame++;
								}
								gbc.gridx = r;
								gbc.gridy = c;
								gridPanel.remove(grid[r][c]);
								tempLabel.setText("");
								gridPanel.add(tempLabel,gbc);
								if (field[r][c].isBomb() == false)
								{
									tempLabel.setBorder(BorderFactory.createEmptyBorder());
									if (field[r][c].getValue() == 1)
									{
										tempLabel.setIcon(clickedCell1);
									} else if (field[r][c].getValue() == 2)
									{
										tempLabel.setIcon(clickedCell2);
									} else if (field[r][c].getValue() == 3)
									{
										tempLabel.setIcon(clickedCell3);
									} else if (field[r][c].getValue() == 4)
									{
										tempLabel.setIcon(clickedCell4);
									} else if (field[r][c].getValue() == 5)
									{
										tempLabel.setIcon(clickedCell5);
									} else if (field[r][c].getValue() == 6)
									{
										tempLabel.setIcon(clickedCell6);
									} else if (field[r][c].getValue() == 7)
									{
										tempLabel.setIcon(clickedCell7);
									}

									openCell(r, c);
									field[r][c].setRevealed();
								}
								repaint();
								revalidate();
								if (field[r][c].toString() == "B")
								{
									//tempLabel.setBorder(BorderFactory.createEmptyBorder());
									tempLabel.setIcon(bombClickedScaled);
									//hello.setBorder(BorderFactory.createEmptyBorder());
									hello.setIcon(unhappyScaled);
									sw.stop();
									// Show All bombs
									showAllBombs();
									JOptionPane.showMessageDialog(null, "CLICKED ON A BOMB", "GAME OVER", JOptionPane.WARNING_MESSAGE);
									playSound("BombClicked.wav");
								}
								playSound("Click.wav");
								// If al bombs are flagged and all cells revealed
								checkGameWon();
							}
						}
					}
				if 	(hello == (JButton) o)
				{
					restartGame();
				}
			}
			// right button
			if (me.getButton()== MouseEvent.BUTTON3)
			{
				Object o= me.getSource();
				tempLabel= new JLabel("@");
				for (int r=1; r<=ROWS; r++)
					for (int c = 1; c<=COLS; c++)
					{
						if 	(grid[r][c] == (JButton) o)
						{
							playSound("Flagged.wav");
							gbc.gridx = r;
							gbc.gridy = c;

							grid[r][c].setText("");
							grid[r][c].setBorder(BorderFactory.createEmptyBorder());
							grid[r][c].setIcon(cellFlagged);

							field[r][c].setFlagged(true);
							bombsTotaal = bombsTotaal - 1;
							bombCounter.setText(bombsTotaal + " Bombs");
							revalidate();
							repaint();
							// If al bombs are flagged and all cells revealed
							checkGameWon();
						}
					}
			}	 
		}
		public void mousePressed(MouseEvent me) {}
		public void mouseReleased(MouseEvent me) {}
		public void mouseEntered(MouseEvent me) {}
		public void mouseExited(MouseEvent me) {}
	}	
}