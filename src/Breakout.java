/*
 * File: Breakout.java
 * -------------------
 * Name:
 * Section Leader:
 * 
 * This file will eventually implement the game of Breakout.
 */

import acm.graphics.*;
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

	/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

	/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

	/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

	/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

	/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

	/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

	/** Separation between bricks */
	private static final int BRICK_SEP = 4;

	/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

	/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

	/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

	/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 100;

	/** Bricks colors */
	private static final Color RED =  new Color(194, 70, 103);
	private static final Color ORANGE = new Color(219, 117, 66);
	private static final Color YELLOW = new Color(204, 158, 48);
	private static final Color GREEN = new Color(46, 152, 134);
	private static final Color CYAN = new Color(64, 111, 177);
	
	/** Animation cycle delay */
	private static final int DELAY = 10;
	
	/** Random number generator for setting initial ball velocity */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	/** The player paddle */
	private GRect paddle;
	
	/** The game ball */
	private GOval ball;
	
	/** Ball velocity */
	private double vx, vy;

	/** Registers the mouse listeners during game initialization. */
	public void init() {
	    addMouseListeners();
	}
	
	/** Moves the paddle horizontally with the mouse. */
	public void mouseMoved(MouseEvent event) {
		if (paddle == null) {
			return;
		}
		
		double x = event.getX() - paddle.getWidth() / 2.0;

	    if (x < 0) {
	        x = 0;
	    } 
	    else if (x + paddle.getWidth() > WIDTH) {
	        x = WIDTH - paddle.getWidth();
	    }
		
	    paddle.move(x - paddle.getX(), 0);
	}
	
	/** Runs the Breakout program. */
	public void run() {
		setupGame();
		runGame();
	}
	
	/** Sets up the game objects. */
	private void setupGame() {
	    createBricks();
	    createPaddle();
		createBall();		
	}
	
	/** Creates the rows of bricks. */
	private void createBricks() {
		for (int row = 0; row < NBRICK_ROWS; row++) {
			int y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
			Color rowColor = getRowColor(row);
			
			for (int brickIndex = 0; brickIndex < NBRICKS_PER_ROW; brickIndex++) {
				int x = brickIndex * (BRICK_WIDTH + BRICK_SEP);
				
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		        brick.setFilled(true);
		        brick.setColor(rowColor);
		        add(brick);
			}
		}
	}
	
	/**
	 * Returns the color for a given brick row.
	 *
	 * @param row the index of the brick row
	 * @return the color assigned to that row
	 */
	private Color getRowColor(int row) {
	    int colorCount = 5;
	    int baseRowsPerColor = NBRICK_ROWS / colorCount;
	    int colorsWithExtraRow = NBRICK_ROWS % colorCount;
	    int rowsInLargerGroups = colorsWithExtraRow * (baseRowsPerColor + 1);

	    int colorIndex;
	    
	    if (row < rowsInLargerGroups) {
	        colorIndex = row / (baseRowsPerColor + 1);
	    } 
	    else {
	        colorIndex = colorsWithExtraRow + (row - rowsInLargerGroups) / baseRowsPerColor;
	    }

	    Color rowColor = new Color(0, 0, 0, 0);
	    
	    switch (colorIndex) {
	        case 0:
	            rowColor = RED;
	            break;
	        case 1:
	            rowColor = ORANGE;
	            break;
	        case 2:
	            rowColor = YELLOW;
	            break;
	        case 3:
	            rowColor = GREEN;
	            break;
	        case 4:
	            rowColor = CYAN;
	            break;
	    }

	    return rowColor;
	}
	
	/** Creates the paddle. */
    private void createPaddle() {
    	double x = WIDTH / 2 - PADDLE_WIDTH / 2;
        double y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;
        
    	paddle = new GRect(x, y, PADDLE_WIDTH, PADDLE_HEIGHT);
    	paddle.setFilled(true);
    	add(paddle);
    }
	
    /** Creates the ball. */
    private void createBall() {
		ball = new GOval(0, 0, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		add(ball);
    }
	
    /** Runs the main game loop. */
	private void runGame() {
		while (true) {
			double startX = (WIDTH - ball.getWidth()) / 2.0;
			double startY = (HEIGHT - ball.getHeight()) / 2.0;
			ball.setLocation(startX, startY);

			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			vy = 3.0;

			while (ball.getY() < HEIGHT) {
				moveBall();
				pause(DELAY);
			}
		}
	}
	
	/** Moves the ball one step and handles collisions. */
	private void moveBall() {
	    double previousPositionX = ball.getX();
	    double previousPositionY = ball.getY() + ball.getHeight();

	    handleWallCollision();
	    ball.move(vx, vy);

	    GObject hitObject = getCollidingObject();
	    
	    if (hitObject == null) {
	        return;
	    }

	    if (hitObject == paddle) {
	        handlePaddleCollision(previousPositionY);
	    } 
	    else {
	        handleBrickCollision(hitObject, previousPositionX);
	    }
	}
	
	/** Bounces the ball off the side walls and the ceiling. */
	private void handleWallCollision() {
		double x = ball.getX();
		double y = ball.getY();

		if (x + vx < 0 || x + vx + ball.getWidth() > WIDTH) {
			vx = -vx;
		}

		if (y + vy < 0) {
			vy = -vy;
		}
	}

	/** Bounces the ball upward if it hits the top of the paddle. */
	private void handlePaddleCollision(double prevBottom) {
		if (vy > 0 && prevBottom <= paddle.getY()) {
			vy = -vy;
		}
	}

	/** Bounces the ball off a brick and removes the brick. */
	private void handleBrickCollision(GObject brick, double prevX) {
		if (prevX + ball.getWidth() <= brick.getX() || prevX >= brick.getX() + brick.getWidth()) {
			vx = -vx;
		} else {
			vy = -vy;
		}

		remove(brick);
	}
	
	/**
	 * Checks the four corners of the ball's bounding box for a collision.
	 *
	 * @return the first object found, or null if all four corners are clear
	 */
	private GObject getCollidingObject() {
	    double left = ball.getX();
	    double top = ball.getY();
	    double right = left + ball.getWidth();
	    double bottom = top + ball.getHeight();

	    GObject object = getElementAt(left, top);
	    
	    if (object != null) {
	        return object;
	    }

	    object = getElementAt(right, top);
	    
	    if (object != null) {
	        return object;
	    }

	    object = getElementAt(left, bottom);
	    
	    if (object != null) {
	        return object;
	    }

	    return getElementAt(right, bottom);
	}
}
