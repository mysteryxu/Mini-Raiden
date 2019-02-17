package com.tarena.shoot;
import java.util.Random;

//Enemy's plane
public class Airplane extends FlyingObject implements Enemy{
	private int speed = 2;//flying speed
	
	public Airplane(){

		image = ShootGame.airplane;
		width = image.getWidth();
		height = image.getHeight();
		y = -height; 
		Random rand = new Random();
		x = rand.nextInt(ShootGame.WIDTH-width);
	}
	public void step(){
		y+=speed;
	}
	public int getScore(){//get 5 points
		return 5;
	}
	
	public boolean outOfBound(){
		return y>ShootGame.HEIGHT;
	}
}
