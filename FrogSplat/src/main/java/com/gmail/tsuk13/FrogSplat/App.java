package com.gmail.tsuk13.FrogSplat;

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
			Entity e = new Entity(50,50,20,20);
			e.draw();
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
    
    //My utility classes
    public class Entity{
    	public double x;
    	public double y;
    	public double xSize;
    	public double ySize;
    	
    	public Entity(double x, double y, double xSize, double ySize){
    		this.x = x;
    		this.y = y;
    		this.xSize = xSize;
    		this.ySize = ySize;
    	}
    	
    	public void draw(){
    		GL11.glColor3f(.5f, .5f, 1f);
			GL11.glBegin(GL11.GL_QUADS);
				GL11.glVertex2d(x, y);
				GL11.glVertex2d(x+xSize, y);
				GL11.glVertex2d(x+xSize, y+ySize);
				GL11.glVertex2d(x, y+ySize);
			GL11.glEnd();
    	}
    	
    	
    }
    	
}
