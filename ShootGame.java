package com.tarena.shoot;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;//load picture
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Random;
import java.util.Arrays;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.*;

public class ShootGame extends JPanel {
	public static final int WIDTH = 335;
	public static final int HEIGHT = 580;
	
	public static BufferedImage background;//only appears once
	public static BufferedImage start;
	public static BufferedImage gameover;
	public static BufferedImage pause;
	public static BufferedImage airplane;
	public static BufferedImage bee;
	public static BufferedImage bullet;
	public static BufferedImage hero0;
	public static BufferedImage hero1;
	
	public Hero hero = new Hero();
	public Bullet[] bullets = {}; 
	public FlyingObject[] flyings = {};//airplane,bee
	
	private int score = 0;
	
	private int state;
	public static final int START=0;
	public static final int RUNNING =1;
	public static final int PAUSE=2;
	public static final int GAME_OVER=3;
	
	public ShootGame(){
		/*
		flyings = new FlyingObject[2];
		flyings[0] = new Airplane();
		flyings[1] = new Bee();
		
		bullets = new Bullet[1];
		bullets[0] = new Bullet(160,350);
		*/
	}
	
	static{//load picture
		try {
			background = ImageIO.read(ShootGame.class.getResource("background.png"));
			start = ImageIO.read(ShootGame.class.getResource("start.png"));
			gameover = ImageIO.read(ShootGame.class.getResource("gameover.png"));
			pause = ImageIO.read(ShootGame.class.getResource("pause.png"));
			airplane = ImageIO.read(ShootGame.class.getResource("airplane.png"));
			bee = ImageIO.read(ShootGame.class.getResource("bee.png"));
			bullet = ImageIO.read(ShootGame.class.getResource("bullet.png"));
			hero0 = ImageIO.read(ShootGame.class.getResource("hero0.png"));
			hero1 = ImageIO.read(ShootGame.class.getResource("hero1.png"));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//overwrite paint
	public void paint(Graphics g){
		g.drawImage(background,0, 0,null);	
		paintHero(g);
		paintBullets(g);
		paintFlyingObjects(g);
		paintScore(g);
		paintState(g);
	}
	public void paintState(Graphics g){
		switch(state){
		case START:
			g.drawImage(start, 0, 0, null);
			break;
		case PAUSE:
			g.drawImage(pause, 0, 0, null);
			break;
		case GAME_OVER:
			g.drawImage(gameover, 0, 0, null);
			break;
			
			
		}
	}
	
	public void paintScore(Graphics g){
		int x = 10;
		int y = 25;
		g.setFont(new Font(Font.SANS_SERIF,Font.BOLD,15));
		g.setColor(new Color(0x553300) );
		g.drawString("SCORE: "+score,x,y);
		g.drawString("LIFE: "+hero.getLife(),x,y+20);
	}
	public void paintHero(Graphics g){
		g.drawImage(hero.image, hero.x, hero.y, null);
	}
	
	public void paintBullets(Graphics g){
		for(int i = 0;i<bullets.length;i++){
			Bullet b = bullets[i];
			g.drawImage(b.image, b.x, b.y, null);
		}
	}
	
	public void paintFlyingObjects(Graphics g){
		for(int i =0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			g.drawImage(f.image, f.x, f.y, null);
		}
	}
	
	private Timer timer;
	private int interval = 10;
	public void  action(){
		MouseAdapter l = new MouseAdapter(){//upcast
			public void mouseMoved(MouseEvent e){

				if(state ==RUNNING){
					int x = e.getX();
					int y = e.getY();
					hero.moveTo(x,y);
				}

				
			}
			
			public void mouseClicked(MouseEvent e){
				switch(state){
				case START:
					state = RUNNING;
					break;
				case GAME_OVER:
					hero = new Hero();
					flyings = new FlyingObject[0];
					bullets = new Bullet[0];
					score =0;
					state = START;
					break;
				}
			}

			public void mouseExited(MouseEvent e){
				if(state!=GAME_OVER&&state!=START){
					state = PAUSE;
				}
			}
			
			public void mouseEntered(MouseEvent e){
				if(state == PAUSE){
					state = RUNNING;
				}
			}
		
		};
		this.addMouseMotionListener(l);
		this.addMouseListener(l);
		
		timer = new Timer();
		timer.schedule(new TimerTask(){
			public void run(){//execute run method every 10ms
				if(state == RUNNING){
					enterAction();
					stepAction();
					shootAction();
					bangAction();
					outOfBoundsAction();
					checkGameOverAction();
				}

				repaint();//refresh
			}
		},interval,interval);//trigger at arranged time;
	}

	public void checkGameOverAction(){
		if(isGameOver()){
			if(isGameOver()){
				state = GAME_OVER;
			}
		}
	}
	public boolean isGameOver(){
		for(int i =0;i<flyings.length;i++){
			int index = -1;
			FlyingObject obj = flyings[i];
			if(hero.hit(obj)){
				hero.subtractLife();
				hero.setDoubleFire(0);
				index = i;
			}
			if(index != -1){
				FlyingObject t = flyings[index];
				flyings[index] = flyings[flyings.length-1];
				flyings[flyings.length-1]=t;
				flyings = Arrays.copyOf(flyings, flyings.length-1);
			}
		}
		return hero.getLife()<=0;
	}
	public void outOfBoundsAction(){
		
		int index =0;
		FlyingObject[] flyingLives = new FlyingObject[flyings.length];
		for(int i=0;i<flyings.length;i++){
			FlyingObject f = flyings[i];
			if(!f.outOfBound()){
				flyingLives[index++] = f;
			}
		}
		flyings = Arrays.copyOf(flyingLives, index);
		
		//delete outbounded bullets
		index = 0;
		Bullet[] bulletLives = new Bullet[bullets.length];
		for(int i=0;i<bullets.length;i++){
			Bullet b = bullets[i];
			if(!b.outOfBound()){
				bulletLives[index++]=b;
			}
		}
		bullets = Arrays.copyOf(bulletLives,index);
	}
	public void bangAction(){
		for(int i=0;i<bullets.length;i++){
			Bullet b = bullets[i]; 
			bang(b);
		}
	}
	
	
	public void bang(Bullet b){
		int index = -1;
		for (int i=0;i<flyings.length;i++){
			FlyingObject obj = flyings[i];
			if(obj.shootBy(b)){
				index = i;
				break;
			}
		}
		
		
		//get points and award
		if(index!=-1){
			FlyingObject one = flyings[index];
			FlyingObject t = flyings[index];
			flyings[index] = flyings[flyings.length-1];
			flyings[flyings.length-1]=t;
			flyings = Arrays.copyOf(flyings, flyings.length-1);
			
			if(one instanceof Enemy){
				score += ((Enemy) one).getScore();
			} 
			else if (one instanceof Award){
				Award a = (Award) one;
				int type = a.getType();
				switch(type){
				case Award.DOUBLE_FIRE:
					hero.addDoubleFire();
					break;
				case Award.LIFE:
					hero.addLife();
					break;
				}
			}
			
		}
	}
	
	int shootIndex = 0;
	public void shootAction(){
		shootIndex++;
		if(shootIndex%30==0){
			Bullet[] bs = hero.shoot();
			bullets = Arrays.copyOf(bullets,bullets.length+bs.length);//expand
			System.arraycopy(bs, 0, bullets, bullets.length-bs.length, bs.length);//add
		}
	}
	
	int flyEnteredIndex = 0;
	public void enterAction(){//enter frame
		flyEnteredIndex++;
		if(flyEnteredIndex%40==0){
			FlyingObject obj = nextOne();
			flyings = Arrays.copyOf(flyings, flyings.length+1);
			flyings[flyings.length-1]=obj;
		}
	}
	
	public static FlyingObject nextOne(){
		Random rand = new Random();
		int type = rand.nextInt(20);
		if(type==0){
			return new Bee();
		}
		else{
			return new Airplane();
		}
	}
	public void stepAction(){
		for(int i=0;i<flyings.length;i++){//airplane/bee makes one step
			flyings[i].step();
		}
		for(int i=0;i<bullets.length;i++){
			bullets[i].step();
		}
		hero.step();
	}
	public static void main(String[] args) {
		JFrame frame = new JFrame("Fly");//frame
		ShootGame game = new ShootGame();
		frame.add(game);//add panel into frame
		frame.setSize(WIDTH, HEIGHT);
		frame.setAlwaysOnTop(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//close frame and stop process
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);//make frame visible
		game.action();
	}

}
