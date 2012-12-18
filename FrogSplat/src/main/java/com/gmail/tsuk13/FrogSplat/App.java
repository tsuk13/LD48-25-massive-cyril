package com.gmail.tsuk13.FrogSplat;


import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public int xSize = 800;
	public int ySize = 600;
	double rowsize = 37;
	double columnsize = 37;
	double numCols = 9;
	double numRows = 13;
	double topLeftX = (xSize - (columnsize * numCols))/2;
	double topLeftY = 0;
	//game tweaking settingd
	int frogSpeed = 60;
	int frogSpeedIncrease = 1;
	int frogTime = 350;
	int frogTimeIncrease = 5;
	////////
	int time = 0;
	double cardsY = topLeftY + numRows*rowsize + 5;
	double cardsYSize = ySize - 5 - cardsY;
	double cardsX = 5;
	double cardsXSize = (xSize - 5) / 5;
	LinkedList<Frog> frogs = new LinkedList<Frog>();
	LinkedList<Goal> goals = new LinkedList<Goal>();
	LinkedList<Car> cars = new LinkedList<Car>();
	LinkedList<Card> cards = new LinkedList<Card>();
	Random rnd = new Random();
	boolean isCardHeld = false;
	Card cardHeld;
	int points = 25;
	//font studds
	
	public void start(){
		try {
			Display.setDisplayMode(new DisplayMode(xSize,ySize));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		//Initialization of OpenGL
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0, xSize, ySize, 0, 0, 1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		//Initialization of Game Elements
		for(int i = 0; i < 5; i++){
			goals.add(new Goal(i));
		}
		int i = 0;
		cards.add(new Card(cardsX + cardsXSize * i, cardsY, cardsXSize - 5, cardsYSize, 1, 1, 1));
		cards.getLast().colorR = 0f;
		cards.getLast().colorG = 0f;
		cards.getLast().colorB = 1f;
		i++;
		cards.add(new Card(cardsX + cardsXSize * i, cardsY, cardsXSize - 5, cardsYSize, 2, 1, 2));
		cards.getLast().colorR = .25f;
		cards.getLast().colorG = 0f;
		cards.getLast().colorB = .75f;
		i++;
		cards.add(new Card(cardsX + cardsXSize * i, cardsY, cardsXSize - 5, cardsYSize, 3, 2, 3));
		cards.getLast().colorR = .5f;
		cards.getLast().colorG = 0f;
		cards.getLast().colorB = .5f;
		i++;
		cards.add(new Card(cardsX + cardsXSize * i, cardsY, cardsXSize - 5, cardsYSize, 3, 3, 4));
		cards.getLast().colorR = .75f;
		cards.getLast().colorG = 0f;
		cards.getLast().colorB = .25f;
		i++;
		cards.add(new Card(cardsX + cardsXSize * i, cardsY, cardsXSize - 5, cardsYSize, 2, 2, 5));
		cards.getLast().colorR = 1f;
		cards.getLast().colorG = 0f;
		cards.getLast().colorB = 0f;
		
	}
	
	public void loop(){
		while(!Display.isCloseRequested()){
			//clean Screen
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
    		//GameLoop
			update();
			drawBackground();
			//frogs
			ListIterator<Frog> fIt = frogs.listIterator();
			while(fIt.hasNext()){
				Frog f = fIt.next();
				f.update();
				f.draw();
			}
			//cars
			ListIterator<Car> cIt = cars.listIterator();
			while(cIt.hasNext()){
				Car c = cIt.next();
				c.update();
				if(c.x > topLeftX + numCols*columnsize || c.x + c.xSize < topLeftX){
					int tmp = cIt.previousIndex();
					cars.remove(tmp);
					cIt = cars.listIterator(tmp);
				}
				c.draw();
			}
			//goals
			ListIterator<Goal> gIt = goals.listIterator();
			while(gIt.hasNext()){
				Goal g = gIt.next();
				g.update();
				g.draw();
			}
			drawForeground();
			//key Polling
			if(Mouse.isButtonDown(0)){
				int mouseX = getMouseX();
				int mouseY = getMouseY();
				ListIterator<Card> crdIt = cards.listIterator();
				while(crdIt.hasNext()){
					Card c = crdIt.next();
					if(c.isCard(mouseX, mouseY)){
						isCardHeld = true;
						cardHeld = c;
					}
						
				}
				if(isCardHeld){
					cardHeld.mouseDraw(mouseX, mouseY);
				}
				//System.out.println("Mouse: " + mouseX + ", " + mouseY);
			}
			else if(isCardHeld){
				isCardHeld = false;
				int mouseX = getMouseX();
				int mouseY = getMouseY();
				int dir = 0;
				int lane;
				lane = (int) (mouseY / rowsize);
				if(lane > 1 && lane < 13){
					if(mouseX > xSize/2)
						dir = 1;
					cars.add(new Car(lane,  cardHeld.size, cardHeld.speed, dir));
					cars.getLast().colorR = cardHeld.colorR;
					cars.getLast().colorG = cardHeld.colorG;
					cars.getLast().colorB = cardHeld.colorB;
					points -= cardHeld.cost;
				}
			}
    		//Syncronizing stuff
			Display.sync(60);
			Display.update();
    	}
    	Display.destroy();
    }
	
	public void end(){
		Display.destroy();
		System.exit(0);
	}
	
    public static void main( String[] args ){
    	App app = new App();
    	app.start();
    	app.loop();
    	app.end();
    }
    
    public void drawBackground(){
    	float color = .25f;
    	for(int i = 0; i < numRows; i++, color += .05){
    		GL11.glColor3f(color, color, color);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(topLeftX, topLeftY + i * rowsize );
				GL11.glVertex2d(topLeftX, topLeftY + (i + 1) * rowsize);
				GL11.glVertex2d(topLeftX + numCols * columnsize, topLeftY + (i + 1) * rowsize);
				GL11.glVertex2d(topLeftX + numCols * columnsize, topLeftY + i * rowsize);
			GL11.glEnd();
    	}
    	
    }
    
    public void drawForeground(){
    	//side panel backgrounds
    	float color = 0f;
    	GL11.glColor3f(color,color,color);
    	GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex2d(0,0);
			GL11.glVertex2d(topLeftX, 0);
			GL11.glVertex2d(topLeftX, ySize);
			GL11.glVertex2d(0, ySize);

    		GL11.glVertex2d(xSize, 0);
    		GL11.glVertex2d(xSize, ySize);
    		GL11.glVertex2d(topLeftX + numCols * columnsize, ySize);
    		GL11.glVertex2d(topLeftX + numCols * columnsize, 0);
    	GL11.glEnd();
    	//Points
    	int rows = points / 5 + 1;
    	double pointSize = (numRows * rowsize) / rows;
    	double tmp = topLeftX / 5;
    	if (tmp < pointSize)
    		pointSize = tmp;
    	pointSize -= 10;
    	GL11.glColor3f(1f, 1f, 0f);
    	GL11.glBegin(GL11.GL_QUADS);
    	int xStart = 5;
    	int yStart = 5;
    	int p = 0;
    	for(; p < points; ){
    		for(int j = 0; j < 5 && p < points; j++, p++){
    			GL11.glVertex2d(xStart, yStart);
				GL11.glVertex2d(xStart + pointSize, yStart);
				GL11.glVertex2d(xStart + pointSize, yStart + pointSize);
				GL11.glVertex2d(xStart, yStart + pointSize);
				xStart += pointSize + 5;
    		}
    		xStart = 5;
    		yStart += pointSize + 5;
    	}
    	
    	//cards
		ListIterator<Card> cIt = cards.listIterator();
		while(cIt.hasNext()){
			Card c = cIt.next();
			c.draw();
		}
    }
    
    public void update(){
    	if(time == frogTime){
    		//test frogs
    		frogs.add(new Frog(rnd.nextInt(5)));
    		//test cars
    		//cars.add(new Car(rnd.nextInt(11) + 1, 2, 1, rnd.nextInt(2)));
    		time = 0;
    	}
    	time++;
    	//goals
		ListIterator<Goal> gIt = goals.listIterator();
		while(gIt.hasNext()){
			Goal g = gIt.next();
			if(!g.scored){
				break;
			}
			end();
		}
    }
    
    public  int getMouseX(){
    	return Mouse.getX();
    }
    
    public int getMouseY(){
    	return ySize - Mouse.getY();
    }
    
    //My utility classes
    public class Entity{
    	public double x;
    	public double y;
    	public double xSize;
    	public double ySize;
    	public float colorR;
    	public float colorG;
    	public float colorB;
    	
    	public Entity(double x, double y, double xSize, double ySize){
    		this.x = x;
    		this.y = y;
    		this.xSize = xSize;
    		this.ySize = ySize;
    		colorR = colorG =.5f;
    		colorB = 1f;
    	}
    	
    	public void draw(){
    		GL11.glColor3f(colorR, colorG, colorB);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(x, y);
				GL11.glVertex2d(x+xSize, y);
				GL11.glVertex2d(x+xSize, y+ySize);
				GL11.glVertex2d(x, y+ySize);
			GL11.glEnd();
    	}
    	
    	public int[] toGridX(){
    		int[] ret = new int[2];
    		double actualx =(x - topLeftX)/columnsize;
    		if(actualx < 0)
        		ret[0] = (int)(actualx) - 1;
    		else
    			ret[0] = (int)(actualx);
    		ret[1] = (int)(xSize/columnsize);
    		if(Math.abs(actualx - (double)ret[0]) > .1)
    			ret[1]++;
    		return ret;
    	}
    	
    	public int[] toGridY(){
    		int[] ret = new int[2];
    		ret[0] = (int)((y - topLeftY)/rowsize);
    		ret[1] = (int)(ySize/columnsize);
    		return ret;
    	}
    	
    	public void move(double x, double y){
    		this.x = this.x + x;
    		this.y = this.y + y;
    	}
    	
    	public boolean isCollide(Entity e){
    		int[] selfX = this.toGridX();
    		int[] selfY = this.toGridY();
    		int[] trgtX = e.toGridX();
    		int[] trgtY = e.toGridY();
    		for(int i = 0; i < selfX[1]; i++)
    			for(int j = 0; j < selfY[1]; j++)
    				for(int ti = 0; ti < trgtX[1]; ti++)
    					for(int tj = 0; tj < trgtY[1]; tj++){
    						if(selfX[0] + i == trgtX[0] + ti && selfY[0] + j == trgtY[0] + tj)
    							return true;
    			}
    		return false;
    	}
    	
    	
    }
    
    public class Frog extends Entity{
    	int lane;
    	int time = 0;

		public Frog(int lane) {
			super(topLeftX + lane * 2 * columnsize, topLeftY + (numRows - 1) * rowsize, columnsize, rowsize);
			this.lane = lane;
			colorR = colorB = 0;
			colorG = 1;
		}
		
		public void update(){
			if(time >= frogSpeed){
				time = 0;
				move(0, -rowsize);
			}
			time++;
		}
    	
    }
    
    public class Goal extends Entity{
    	int lane;
    	boolean scored = false;

		public Goal(int lane) {
			super(topLeftX + lane * 2 * columnsize, topLeftY, columnsize, rowsize);
			this.lane = lane;
			colorB =0;
			colorR = 142/255f;
			colorG = 178/255f;
		}
		
		public void update(){
			ListIterator<Frog> fIt = frogs.listIterator();
			while(fIt.hasNext()){
				Frog f = fIt.next();
				if(this.isCollide(f)){
					//System.out.println("Goal at lane: " + this.lane);
					scored = true;
					int tmp = fIt.previousIndex();
					frogs.remove(tmp);
					fIt = frogs.listIterator(tmp);
				}
			}
		}
		public void draw(){
			super.draw();
			if(scored){
				colorR = colorG = colorB = 0f;
			}
		}
    	
    }
    
    public class Car extends Entity{
    	int lane;
    	double speed;
    	int dir;

		public Car(int lane, int size, int speed, int dir) { //dir: 0 is from left 1 is from right
			super(topLeftX - columnsize * size + dir * ((numCols + size) * columnsize), topLeftY + lane * rowsize, columnsize * size, rowsize);
			this.lane = lane;
			this.speed = speed;
			this.dir = dir;
		}
		
		public void update(){
			if(dir == 0)
				move(speed, 0);
			else
				move(-speed, 0);
			ListIterator<Frog> fIt = frogs.listIterator();
			while(fIt.hasNext()){
				Frog f = fIt.next();
				if(this.isCollide(f)){
					points += 3;
					frogSpeed -=frogSpeedIncrease;
					frogTime -= frogTimeIncrease;
					int tmp = fIt.previousIndex();
					frogs.remove(tmp);
					fIt = frogs.listIterator(tmp);
				}
			}
		}
    	
    }
    
    public class Card extends Entity{

    	boolean isGrabbed;
    	int speed;
    	int size;
    	int cost;
    	
		public Card(double x, double y, double xSize, double ySize, int speed, int size, int cost) {
			super(x, y, xSize, ySize);
			this.speed = speed;
			this.size = size;
			isGrabbed = false;
			this.cost = cost;
		}
		
		public boolean isCard(double x, double y){
			if(x > this.x && x < this.x + xSize && y > this.y && y < this.y + ySize)
				return true;
			return false;
		}
		
		public void draw(){
			//base
			GL11.glColor3f(.5f, .5f, .5f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(x, y);
				GL11.glVertex2d(x+xSize, y);
				GL11.glVertex2d(x+xSize, y+ySize);
				GL11.glVertex2d(x, y+ySize);
			GL11.glEnd();
			//car
			mouseDraw( (int)(x + xSize / 2),(int)( y + ySize / 2)   );
			//points
			GL11.glColor3f(1f, 1f, 0f);
			GL11.glBegin(GL11.GL_QUADS);
			double pointSize = xSize / 5 - 10;
			double xStart = x + 5;
			double yStart = y + ySize - 5 - pointSize;
			for(int i = 0; i < cost; i++){
				GL11.glVertex2d(xStart, yStart);
				GL11.glVertex2d(xStart + pointSize, yStart);
				GL11.glVertex2d(xStart + pointSize, yStart + pointSize);
				GL11.glVertex2d(xStart, yStart + pointSize);
				xStart += pointSize + 5;
			}
			GL11.glEnd();
		}
		public void mouseDraw(int xin, int yin){
			int x = (int) (xin - columnsize * size * .5);
			int y = (int) (yin - rowsize * .5);
			GL11.glColor3f(colorR, colorG, colorB);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(x, y);
				GL11.glVertex2d(x+columnsize * size, y);
				GL11.glVertex2d(x+columnsize * size, y+rowsize);
				GL11.glVertex2d(x, y+rowsize);
			GL11.glEnd();
			
		}
		
		
    	
    }
    	
}
