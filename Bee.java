package com.tarena.shoot;
import java.util.Random;

public class Bee extends FlyingObject implements Award{
	private int xSpeed = 1;//x-axis speed
	private int ySpeed = 2;//y-axis speed
	private int awardType;//type of award
	
	public Bee(){
		image = ShootGame.bee;
		width = image.getWidth();
		height = image.getHeight();
		y = -height;
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-width);
		awardType = rand.nextInt(2);
	}
	public void step(){
		x+=xSpeed;
		y+=ySpeed;
		if (x<0){
			xSpeed = 1;
		}
		if(x>ShootGame.WIDTH-width){
			xSpeed = -1;
		}
	}
	public int getType(){//define type of award
		return awardType;
	}
	
	public boolean outOfBound(){
		return y>ShootGame.HEIGHT;
	}
}
