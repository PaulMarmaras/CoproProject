package bounceball;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.Timer;

public class BouncyBall implements ActionListener, MouseListener, KeyListener, ImageObserver
{

	public static BouncyBall bouncyBall;

	public final int WIDTH = 800, HEIGHT = 800;

	public Renderer renderer;

	public Rectangle ball;

	public ArrayList<Rectangle> walls;

	public int ticks, yMotion, score;

	public int gameState;

	public Random rand;

	public Image BallPic;
	
	public BouncyBall()
	{
		JFrame jframe = new JFrame();
		Timer timer = new Timer(20, this);

		renderer = new Renderer();
		rand = new Random();
		
		ImageIcon i = new ImageIcon("C:/Users/Oyah/workspace/BouncyBall Marmaras/Images/cutmypic.png");
		BallPic = i.getImage();

		jframe.add(renderer);
		jframe.setTitle("Bouncy Ball");
		jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jframe.setSize(WIDTH, HEIGHT);
		jframe.addMouseListener(this);
		jframe.addKeyListener(this);
		jframe.setResizable(false);
		jframe.setVisible(true);

		ball = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
		walls = new ArrayList<Rectangle>();

		addColumn(true);
		addColumn(true);
		addColumn(true);
		addColumn(true);

		timer.start();
	}

	public void addColumn(boolean start)
	{
		int space = 300;
		int width = 100;
		int height = 50 + rand.nextInt(300);

		if (start)
		{
			walls.add(new Rectangle(WIDTH + 100 + walls.size() * 300, 800 - height - 120, width, height));
			walls.add(new Rectangle(WIDTH + 100 + (walls.size() - 1) * 300, 0, width, 800 - height - space));
		}
		else
		{
			walls.add(new Rectangle(walls.get(walls.size() - 1).x + 600, 800 - height - 120, width, height));
			walls.add(new Rectangle(walls.get(walls.size() - 1).x, 0, width, 800 - height - space));
		}
	}

	public void paintColumn(Graphics g, Rectangle column)
	{
		g.setColor(Color.green.darker());
		g.fillRect(column.x, column.y, column.width, column.height);
	}

	public void jump()
	{
		if (gameState==2)
		{
			ball = new Rectangle(WIDTH / 2 - 10, HEIGHT / 2 - 10, 20, 20);
			walls.clear();
			yMotion = 0;
			score = 0;

			addColumn(true);
			addColumn(true);
			addColumn(true);
			addColumn(true);

			gameState=0;
		}

		if (gameState==0)
		{
			gameState=1;
		}
		else if (gameState==1)
		{
			if (yMotion > 0)
			{
				yMotion = 0;
			}

			yMotion -= 10;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		int speed = 10;

		ticks+=5;

		if (gameState==1)
		{
			for (int i = 0; i < walls.size(); i++)
			{
				Rectangle column = walls.get(i);

				column.x -= speed;
			}

			if (ticks % 2 == 0 && yMotion < 15)
			{
				yMotion += 2;
			}

			for (int i = 0; i < walls.size(); i++)
			{
				Rectangle column = walls.get(i);

				if (column.x + column.width < 0)
				{
					walls.remove(column);

					if (column.y == 0)
					{
						addColumn(false);
					}
				}
			}

			ball.y += yMotion;

			for (Rectangle column : walls)
			{
				if (column.y == 0 && ball.x + ball.width / 2 > column.x + column.width / 2 - 10 && ball.x + ball.width / 2 < column.x + column.width / 2 + 10)
				{
					score++;
				}

				if (column.intersects(ball))
				{
					gameState=2;

					if (ball.x <= column.x)
					{
						ball.x = column.x - ball.width;

					}
					else
					{
						if (column.y != 0)
						{
							ball.y = column.y - ball.height;
						}
						else if (ball.y < column.height)
						{
							ball.y = column.height;
						}
					}
				}
			}

			if (ball.y > HEIGHT - 120 || ball.y < 0)
			{
				gameState=2;
			}

			if (ball.y + yMotion >= HEIGHT - 120)
			{
				ball.y = HEIGHT - 120 - ball.height;
				gameState=2;
			}
		}

		renderer.repaint();
	}

	public void repaint(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, WIDTH, HEIGHT);

		g.setColor(Color.orange);
		g.fillRect(0, HEIGHT - 120, WIDTH, 120);

		g.setColor(Color.green);
		g.fillRect(0, HEIGHT - 120, WIDTH, 20);
		
		g.drawImage(BallPic, ball.x, ball.y, this);

		for (Rectangle column : walls)
		{
			paintColumn(g, column);
		}

		g.setColor(Color.CYAN);
		g.setFont(new Font("Arial", 1, 100));

		if (gameState==0)
		{
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", 1, 80));
			g.drawString("Bouncy Ball", WIDTH / 2 - 240, HEIGHT / 2 - 300);
			
			
			g.setColor(Color.CYAN);
			g.setFont(new Font("Arial", 1, 60));
			g.drawString("Press Space to Play", WIDTH / 2 - 280, HEIGHT / 2 - 100);
			g.drawString("Press Escape to Exit", WIDTH / 2 - 280, HEIGHT / 2 - 25);
		}

		if (gameState==2)
		{
			g.setColor(Color.RED);
			g.setFont(new Font("Arial", 1, 100));
			g.drawString("Game Over!", WIDTH / 2 - 250  , HEIGHT / 2 - 100);
			
			g.setFont(new Font("Arial", 1, 30));
			g.setColor(Color.CYAN);
			g.drawString("Press Space to Play again", WIDTH / 2 - 160, HEIGHT / 2 - 55);
			g.drawString("Press Escape to Exit.", WIDTH / 2 - 160, HEIGHT / 2 );
		}

		if (gameState==1)
		{
			g.setFont(new Font("Arial", 1, 50));
			g.setColor(Color.RED);
			g.drawString(String.valueOf(score), WIDTH / 2 - 25, 100);
		}
	}

	public static void main(String[] args)
	{
		bouncyBall = new BouncyBall();
	}

	@Override
	public void mouseClicked(MouseEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			jump();
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e)
	{
	}

	@Override
	public void mouseReleased(MouseEvent e)
	{
	}

	@Override
	public void mouseEntered(MouseEvent e)
	{
	}

	@Override
	public void mouseExited(MouseEvent e)
	{
	}

	@Override
	public void keyTyped(KeyEvent e)
	{

	}

	@Override
	public void keyPressed(KeyEvent e)
	
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (gameState==0))
		{
			System.exit(0);
		}
		else if (e.getKeyCode() == KeyEvent.VK_ESCAPE && (gameState==2))
		{
			System.exit(0);
		}
		
		
	}

	@Override
	public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
		// TODO Auto-generated method stub
		return false;
	}

}