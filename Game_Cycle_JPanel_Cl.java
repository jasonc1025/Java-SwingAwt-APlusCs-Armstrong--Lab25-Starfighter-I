//(c) A+ Computer Science
//www.apluscompsci.com
//Name -

import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Game_Cycle_JPanel_Cl extends JPanel implements KeyListener, Runnable
{

    //y- too fast and choppy- 		playerMe_Ob = new Sprite_Movable_Collidable_Cl(310,450,5);
    //y- 	private Sprite_Movable_Collidable_Cl playerMe_Ob = new Sprite_Movable_Collidable_Cl(310,450,1);
    // * Speed was '1'
	private Sprite_Movable_Collidable_Cl playerMe_Ob = new Sprite_Movable_Collidable_Cl( "/images/ship.jpg", (int)(Game_Main_JFrame_Cl.WIDTH * 0.50), (int)(Game_Main_JFrame_Cl.HEIGHT * 0.80),100,100,2);

    private PlayerBots_Cl playerBots_Ob;
	private Missiles_Cl missiles_Ob;

    //	private Long cycle_ProjectileLast_NanoTime = new Long( 0 );
    //	private Long cycle_Last_NanoTime = new Long( 0 );
    //	private Long cycle_Current_NanoTime = new Long( 0 );

    // * IMPORTANT: 1 sec = 1 x 10^9 nano-sec
    // * IMPORTANT: To avoid 'java: integer number too large' error, require 'l' for 64bit otherwise 32bit default
    // * Projectile at 1/10 sec frequency
	private long gameCycle_Projectile_Prev_NanoSec = 0l;
	private long gameCycle_Prev_NanoSec = 0l;
	private long gameCycle_Curr_NanoSec = 0l;
    private long gameCycle_Fps_NanoSec = 0l;
    private long gameCycle_Projectile_Per_Sec = 10;
    private double gameCycle_Period_Sec = 0.0;
    private long gameCycle_DelayFactor_MilliSec = 10; // 5msec too fast, 20msec kindof slow, 10msec seems right


	private boolean[] keys;
	private BufferedImage back;

	public Game_Cycle_JPanel_Cl(JFrame par)
	{
		keys = new boolean[5];

		setBackground(Color.black);

		playerBots_Ob = new PlayerBots_Cl(100);

		missiles_Ob = new Missiles_Cl();

		this.addKeyListener(this);
		new Thread(this).start();

		setVisible(true);
	}

   public void update(Graphics window)
   {
	   paint(window);
   }

	public void paint( Graphics window )
	{

		//
		// * Timer Update
		//

		// * Calculate timer since last GameEngine Cycle
		gameCycle_Curr_NanoSec = System.nanoTime();
		gameCycle_Period_Sec = (gameCycle_Curr_NanoSec - gameCycle_Prev_NanoSec) / 1000000000.0;
		gameCycle_Fps_NanoSec = Math.round(1/ gameCycle_Period_Sec);
		gameCycle_Prev_NanoSec = gameCycle_Curr_NanoSec;
		//y- debug- System.out.println("> "+ gameCycle_Fps_NanoSec.value);
		//y- System.out.println("> FPS: "+ gameCycle_Fps_NanoSec);


		//set up the double buffering to make the game animation nice and smooth
		Graphics2D twoDGraph = (Graphics2D)window;

		//take a snap shop of the current screen and same it as an image
		//that is the exact same width and height as the current screen
		if(back==null){
		  Game_Main_JFrame_Cl.HEIGHT = this.getHeight();
	      Game_Main_JFrame_Cl.WIDTH = this.getWidth();
	      //System.out.println(Game_Main_JFrame_Cl.WIDTH);
		   back = (BufferedImage)(createImage(this.getWidth(),this.getHeight()));
	   }

		//create a graphics reference to the back ground image
		//we will draw all changes on the background image
		Graphics graphToBack = back.createGraphics();

		graphToBack.setColor(Color.BLACK);
		graphToBack.fillRect(0,0,this.getWidth(),this.getHeight());
        graphToBack.setColor(Color.WHITE);
        graphToBack.setFont(new Font("Dialog", Font.PLAIN, 48));
        graphToBack.drawString("Game_Main_JFrame_Cl ", 50, 50 );
        //y- graphToBack.drawString( "> FPS: " + String.valueOf(gameCycle_Fps_NanoSec), 50, 100 );
        DecimalFormat dfTemp = new DecimalFormat("000");
        graphToBack.drawString( "> FPS: " + dfTemp.format(gameCycle_Fps_NanoSec), 50, 100 );
        //todo
        graphToBack.drawString( "> SCORE: " + dfTemp.format(Game_Main_JFrame_Cl.SCORE), 50, 150 );


		if(keys[0] == true)
		{
			playerMe_Ob.move("LEFT");
		}
		if(keys[1] == true)
		{
			playerMe_Ob.move("RIGHT");
		}
		if(keys[2] == true)
		{
			playerMe_Ob.move("UP");
		}
		if(keys[3] == true)
		{
			playerMe_Ob.move("DOWN");
		}
        // * IMPORTANT: 1 sec = 1 x 10^9 nano-sec
        // * IMPORTANT: To avoid 'java: integer number too large' error, require 'l' for 64bit otherwise 32bit default
        // * Projectile at 1/10 sec frequency (or (1/10 * Math.pow(10,9)) nanosec)
        // ** '1.0' required for decimal division
		if( (keys[4] == true) && ( gameCycle_Curr_NanoSec - gameCycle_Projectile_Prev_NanoSec > (1.0/gameCycle_Projectile_Per_Sec * Math.pow(10,9)) ) )
		{
			//y- missiles_Ob.add(new Sprite_Movable_Collidable_Cl(playerMe_Ob.getX()+playerMe_Ob.getWidth()/2-5, playerMe_Ob.getY(), 10, 10, 5));
            Sprite_Movable_Collidable_Cl missileTemp = new Sprite_Movable_Collidable_Cl("/images/Circle-Green-20x20.png");
            //y- missiles_Ob.add(new Sprite_Movable_Collidable_Cl( "/images/Circle-Green-20x20.png",playerMe_Ob.getX()+playerMe_Ob.getWidth()/2-10, playerMe_Ob.getY()-10, 5));
            missileTemp.setImageSize(10,10);
            missileTemp.setPos(playerMe_Ob.getX()+ playerMe_Ob.getWidth()/2-(missileTemp.getWidth()/2), playerMe_Ob.getY()-(missileTemp.getHeight()/2));
            missileTemp.setSpeed(5);
            //y missiles_Ob.add(new Sprite_Movable_Collidable_Cl( "/images/Circle-Green-20x20.png",playerMe_Ob.getX()+playerMe_Ob.getWidth()/2-10, playerMe_Ob.getY()-10, 5));
            missiles_Ob.add(missileTemp);
			//y turn off to allow continuous fire- keys[4] = false;
            gameCycle_Projectile_Prev_NanoSec = gameCycle_Curr_NanoSec;

			try {
				// Open an audio input stream.
				//o- URL url = this.getClass().getClassLoader().getResource("gameover.wav");
                //n- URL url = this.getClass().getClassLoader().getResource("/images/Laser-SoundBible-602495617.wav");
                URL url = this.getClass().getClassLoader().getResource("Laser-SoundBible-602495617.wav");
				AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				// Get a sound clip resource.
				Clip clip = AudioSystem.getClip();
				// Open audio clip and load samples from the audio input stream.
				clip.open(audioIn);
				clip.start();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}

		missiles_Ob.move();
		missiles_Ob.draw(graphToBack);

		playerMe_Ob.draw(graphToBack);

		playerBots_Ob.move();
		playerBots_Ob.draw(graphToBack);

		//collision detection
		missiles_Ob.cleanEmUp();
		playerBots_Ob.removeDeadOnes(playerMe_Ob, missiles_Ob.getList());


		twoDGraph.drawImage(back, null, 0, 0);
		back = null;
	}


	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			keys[0] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			keys[1] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			keys[2] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			keys[3] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			keys[4] = true;
		}
		repaint();
	}

	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
		{
			keys[0] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
		{
			keys[1] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_UP)
		{
			keys[2] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
		{
			keys[3] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE)
		{
			keys[4] = false;
		}
		repaint();
	}

	public void keyTyped(KeyEvent e)
	{

	}

   public void run()
   {
   	try
   	{
   		while(true)
   		{
			//y- Thread.currentThread().sleep(5);
			//y- Thread.currentThread().sleep(20);
            //y- Thread.currentThread().sleep(10);  // 5msec too fast, 20msec kindof slow, 10msec seems right
            Thread.currentThread().sleep(gameCycle_DelayFactor_MilliSec);  // 5msec too fast, 20msec kindof slow, 10msec seems right
            repaint();
         }
      }catch(Exception e)
      {
      }
  	}
}

