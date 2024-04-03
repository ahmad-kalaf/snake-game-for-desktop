import java.awt.*;
import java.awt.event.*;
import java.time.Duration;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener 
{
	private static final int SCREEN_WIDTH = 600;
	private static final int SCREEN_HEIGHT = 600;
	private static final int UNIT_SIZE = 25;
	private static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	private static final int DELAY = 75;
	private final int[] x = new int[GAME_UNITS];
	private final int[] y = new int[GAME_UNITS];
	private int bodyParts = 6;
	private int applesEaten = 0;
	private int appleX = 0;
	private int appleY = 0;
	// R = Right, L = Left, U = Up, D = Down
	private char direction = 'R';
	private boolean running = false;
	private Timer timer;
	private Random random;
	
	public GamePanel()
	{
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
		if(running)
		{
			g.setColor(Color.red);
			g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
			for(int i=0; i < bodyParts; i++)
			{
				if(i == 0)
				{
					g.setColor(Color.green);
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
				else
				{
					g.setColor(new Color(45,180,0));
					g.fillOval(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
		}
		else
		{
			gameOver(g);
		}
	}
	public void newApple()
	{
		appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
	}
	public void move() 
	{
		for(int i=bodyParts;i>0;i--)
		{
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		switch(direction)
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
			applesEaten++;
			newApple();
		}
	}
	public void checkCollisions()
	{
		for(int i=bodyParts;i>0;i--)
		{
			if((x[0]==x[i])&&(y[0]==y[i]))
			{
				running = false;
			}
			if(x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT)
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
			switch(e.getKeyCode())
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
