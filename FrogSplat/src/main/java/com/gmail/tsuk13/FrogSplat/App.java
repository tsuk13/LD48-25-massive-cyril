package com.gmail.tsuk13.FrogSplat;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.text.html.HTMLDocument.Iterator;

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
	int frogSpeed = 60;
	int time = 0;
	LinkedList<Frog> frogs = new LinkedList<Frog>();
	Random rnd = new Random();
	
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
	}
	
	public void loop(){
		while(!Display.isCloseRequested()){
    		//GameLoop
			update();
			drawBackground();
			ListIterator<Frog> fIt = frogs.listIterator();
			while(fIt.hasNext()){
				Frog f = fIt.next();
				f.update();
				f.draw();
			}
			//key Polling
			if(Mouse.isButtonDown(0)){
				int mouseX = Mouse.getX();
				int mouseY = Mouse.getY();
				System.out.println("Mouse: " + mouseX + ", " + mouseY);
			}
    		//Syncronizing stuff
			Display.sync(60);
			Display.update();
    	}
    	Display.destroy();
    }
	
	public void end(){
		Display.destroy();
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
    
    public void update(){
    	if(time == 60){
    		frogs.add(new Frog(rnd.nextInt(5)));
    		time = 0;
    	}
    	time++;
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
    		colorR = colorG = colorB = .5f;
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
    		ret[0] = (int)(x - topLeftX);
    		ret[1] = (int)(xSize/rowsize);
    		return ret;
    	}
    	
    	public int[] toGridY(){
    		int[] ret = new int[2];
    		ret[0] = (int)(y - topLeftY);
    		ret[1] = (int)(ySize/columnsize);
    		return ret;
    	}
    	
    	public void move(double x, double y){
    		this.x = this.x + x;
    		this.y = this.y + y;
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
    	
}
