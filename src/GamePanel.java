import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;

public class GamePanel extends JPanel implements ActionListener
{
	private final JFrame frame;
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 600;
	private static final int UNIT_SIZE = 25;
	private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	private static final int DELAY = 150;
	private final int[] x = new int[GAME_UNITS];
	private final int[] y = new int[GAME_UNITS];
	private int bodyParts = 6;
	private int score = 0;
	private int bestScore = 0;
	private int appleX = 0;
	private int appleY = 0;
	// R = Right, L = Left, U = Up, D = Down
	private char direction = 'R';
	private boolean running = false;
	private Timer timer;
	private Random random;

	public GamePanel(JFrame frame)
	{
		this.frame = frame;
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();

	}

	public void startGame()
	{
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g)
	{
		for(int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++)
		{
			g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
			g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
		}
		if(running)
		{
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			for(int i = 0; i < bodyParts; i++)
			{
				if(i == 0)
				{
					g.setColor(Color.green);
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else
				{
					g.setColor(new Color(45, 180, 0));
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
		} else
		{
			gameOver(g);
		}
	}

	public void newApple()
	{
		appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		appleY = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
	}

	public void move()
	{
		for(int i = bodyParts; i > 0; i--)
		{
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		switch (direction)
		{
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}

	public void checkApple()
	{
		if(x[0] == appleX && y[0] == appleY)
		{
			bodyParts++;
			score++;
			setBestScore();
			newApple();
		}
	}

	public void checkCollisions()
	{
		for(int i = bodyParts; i > 0; i--)
		{
			if((x[0] == x[i]) && (y[0] == y[i]))
			{
				running = false;
			}
			if(x[0] < 0 || x[0] > (SCREEN_WIDTH - UNIT_SIZE) || y[0] < 0 || y[0] > (SCREEN_HEIGHT - UNIT_SIZE))
			{
				running = false;
			}
			if(!running)
			{
				timer.stop();
			}
		}
	}

	public void gameOver(Graphics g)
	{
		JDialog dialog = new JDialog(frame);

		String currentScoreMessage = "Punktzahl: " + score;
		String bestScoreMessage = "Beste Punktzahl: " + getBestScore();
		JLabel scoreLabel = new JLabel(currentScoreMessage);
		JLabel bestScoreLabel = new JLabel(bestScoreMessage);
		bestScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JPanel textsPanel = new JPanel();
		textsPanel.setLayout(new BorderLayout());
		textsPanel.add(scoreLabel, BorderLayout.NORTH);
		textsPanel.add(bestScoreLabel, BorderLayout.SOUTH);

		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout());

		JButton restartButton = new JButton("Neustarten");
		restartButton.setFocusable(false);
		restartButton.setBackground(Color.green);
		restartButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				dialog.dispose();
				restartGame();
			}
		});
		JButton exitButton = new JButton("Beenden");
		exitButton.setFocusable(false);
		exitButton.setBackground(Color.red);
		exitButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				frame.dispose();
			}
		});

		buttonsPanel.add(restartButton);
		buttonsPanel.add(exitButton);

		dialog.setResizable(false);
		dialog.setLayout(new GridLayout(2, 1));

		dialog.add(textsPanel);
		dialog.add(buttonsPanel);

		dialog.pack();
		dialog.setSize(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 4);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(frame);
	}

	private int getBestScore()
	{
		return bestScore;
	}

	private void setBestScore()
	{
		bestScore = score > bestScore ? score : bestScore;
	}

	private void restartGame()
	{
		bodyParts = 6;
		score = 0;
		direction = 'R';
		for(int i = 0; i < x.length; i++)
		{
			x[i] = 0;
			y[i] = 0;
		}
		repaint();
		startGame();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if(running)
		{
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}

	public class MyKeyAdapter extends KeyAdapter
	{
		@Override
		public void keyPressed(KeyEvent e)
		{
			switch (e.getKeyCode())
			{
			case KeyEvent.VK_LEFT:
				direction = directionNotOpposite('L') ? 'L' : direction;
				break;
			case KeyEvent.VK_RIGHT:
				direction = directionNotOpposite('R') ? 'R' : direction;
				break;
			case KeyEvent.VK_UP:
				direction = directionNotOpposite('U') ? 'U' : direction;
				break;
			case KeyEvent.VK_DOWN:
				direction = directionNotOpposite('D') ? 'D' : direction;
				break;
			}
		}

		private boolean directionNotOpposite(char nextDirection)
		{
			if(nextDirection == 'L' && direction != 'R')
			{
				return true;
			}
			if(nextDirection == 'R' && direction != 'L')
			{
				return true;
			}
			if(nextDirection == 'U' && direction != 'D')
			{
				return true;
			}
			if(nextDirection == 'D' && direction != 'U')
			{
				return true;
			}
			return false;
		}
	}
}
