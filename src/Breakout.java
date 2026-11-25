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

	/** Animation cycle delay */
	private static final int DELAY = 10;

    /** Initial lives count */
    private static final int INITIAL_LIVES_COUNT = 3;
	
	/** Random number generator for setting initial ball velocity */
	private RandomGenerator rgen = RandomGenerator.getInstance();
	
	/** The player paddle */
	private GRect paddle;
	
	/** The game ball */
	private GOval ball;
	
	/** Ball velocity */
	private double vx, vy;

    /** Remaining lives count */
    private int remainingLives;

    /** Скільки цеглин залишилось на полі */
    private int bricksLeft;

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
		setBackground(Palette.BACKGROUND);
		sounds = new Sounds();
		sounds.startMusic();
		while (true) {
			int levelNumber = showMenu();
			removeAll();
			startLevel(new Level(levelNumber));
			setupGame();
			boolean won = runGame();
			showResult(won);
		}
	}
	
	/** Sets up the game objects. */
	private void setupGame() {
	    createBricks();
	    createPaddle();
		createBall();		
	}
	
	/** Creates the rows of bricks. */
	private void createBricks() {
        bricksLeft = currentLevel.getRows() * NBRICKS_PER_ROW;
        //bricksLeft = 1;

		for (int row = 0; row < currentLevel.getRows(); row++) {
			int y = BRICK_Y_OFFSET + row * (BRICK_HEIGHT + BRICK_SEP);
			Color rowColor = Palette.brickColor(row);
			
			for (int brickIndex = 0; brickIndex < NBRICKS_PER_ROW; brickIndex++) {
				int x = brickIndex * (BRICK_WIDTH + BRICK_SEP);
				
				GRect brick = new GRect(x, y, BRICK_WIDTH, BRICK_HEIGHT);
		        brick.setFilled(true);
		        brick.setColor(rowColor);
		        add(brick);
			}
		}
	}
	
	/** Creates the paddle. */
    private void createPaddle() {
		double paddleWidth = currentLevel.getPaddleWidth();
		double x = WIDTH / 2 - paddleWidth / 2;
		double y = HEIGHT - PADDLE_Y_OFFSET - PADDLE_HEIGHT;

		paddle = new GRect(x, y, paddleWidth, PADDLE_HEIGHT);
		paddle.setFilled(true);
		paddle.setColor(Palette.PADDLE);
    	add(paddle);
    }
	
    /** Creates the ball. */
    private void createBall() {
		ball = new GOval(0, 0, BALL_RADIUS, BALL_RADIUS);
		ball.setFilled(true);
		ball.setColor(Palette.BALL);
		add(ball);
    }
	
	/**
	 * Runs the main game loop.
	 *
	 * @return true, якщо рівень виграно, false якщо програно
	 *
	 */
	private boolean runGame() {
        while (remainingLives > 0 && bricksLeft > 0) {
			double startX = (WIDTH - ball.getWidth()) / 2.0;
			double startY = (HEIGHT - ball.getHeight()) / 2.0;
			ball.setLocation(startX, startY);

			vx = rgen.nextDouble(1.0, 3.0);
			if (rgen.nextBoolean(0.5)) {
				vx = -vx;
			}
			vy = currentLevel.getBallSpeed();

			while (ball.getY() < HEIGHT && bricksLeft > 0) {
				moveBall();
				moveBonus();
				pause(DELAY);
			}

            if (bricksLeft > 0) {
                remainingLives--;
                sounds.playDamage();
                hud.setLives(remainingLives);
            }
		}

        return bricksLeft == 0;
	}
	
	/** Moves the ball one step and handles collisions. */
	private void moveBall() {
	    double previousPositionX = ball.getX();
	    double previousPositionY = ball.getY() + ball.getHeight();

	    handleWallCollision();
	    ball.move(vx, vy);

	    GObject hitObject = getCollidingObject();
	    
		if (hitObject == null || isHudOrBonus(hitObject)) {
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
			sounds.playBounce();
		}

		if (y + vy < 0) {
			vy = -vy;
			sounds.playBounce();
		}
	}

	/** Bounces the ball upward if it hits the top of the paddle. */
	private void handlePaddleCollision(double prevBottom) {
		if (vy > 0 && prevBottom <= paddle.getY()) {
			vy = -vy;
			sounds.playBounce();
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
        bricksLeft--;
		sounds.playBounce();
		brickBroken(brick);
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

    /*
     * ЧАСТИНА 2: меню, рівні, бонуси, сердечка, очки, звуки (Zhenia)
     */

    /** Назва гри на стартовій сторінці, кожна літера свого кольору */
    private static final String TITLE = "BREAKOUT";

    /** Де стоять назва і підпис на стартовій сторінці */
    private static final int TITLE_Y = 130;
    private static final int SUBTITLE_Y = 165;

    /** Кнопки: розмір, де перша і крок між кнопками */
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 40;
    private static final int FIRST_BUTTON_Y = 215;
    private static final int BUTTON_STEP = 60;

    /** Додатковий відступ перед кнопкою правил */
    private static final int RULES_BUTTON_GAP = 20;

    /** Як часто перевіряємо, чи був клік, мс */
    private static final int CLICK_WAIT = 20;

    /** Сторінка правил. заголовок, перший рядок, крок і відступи зліва */
    private static final int RULES_TITLE_Y = 60;
    private static final int RULES_FIRST_Y = 105;
    private static final int RULES_LINE_STEP = 24;
    private static final int RULES_LEFT = 25;
    private static final int BONUS_TEXT_LEFT = 70;

    /** Екран результату. заголовок, рядок з очками, кнопка */
    private static final int RESULT_TITLE_Y = 220;
    private static final int RESULT_LINE_Y = 270;
    private static final int RESULT_BUTTON_Y = 330;

    /** Скільки показувати поле після кінця рівня, мс */
    private static final int RESULT_DELAY = 800;

    /** Очки. нижній колір цеглин дає 10, кожен колір вище на 10 більше */
    private static final int POINTS_STEP = 10;

    /** Бонуси. у скільки разів міняємо ракетку і швидкість м'яча */
    private static final double WIDE_FACTOR = 1.5;
    private static final double NARROW_FACTOR = 0.7;
    private static final double SLOW_FACTOR = 0.75;
    private static final double FAST_FACTOR = 1.3;

    /** Межі ширини ракетки, щоб бонуси не зробили її величезною або крихітною */
    private static final double MAX_PADDLE_WIDTH = 150;
    private static final double MIN_PADDLE_WIDTH = 30;

    /**
     * Показує стартову сторінку з кнопками рівнів і правил
     * і чекає, поки гравець вибере рівень.
     *
     * @return номер рівня від 1 до 3
     */
    private int showMenu() {
        while (true) {
            drawMenu();
            GObject choice = waitForMenuChoice();
            sounds.playClick();
            if (choice == level1Button) {
                return Level.EASY;
            }
            if (choice == level2Button) {
                return Level.MEDIUM;
            }
            if (choice == level3Button) {
                return Level.HARD;
            }
            showRules(); // лишилась тільки кнопка правил, після них знову меню
        }
    }

    /**
     * Малює стартову сторінку
     */
    private void drawMenu() {
        removeAll();
        drawTitle();
        addCenteredLabel("8-BIT EDITION", Palette.TEXT_FONT, Palette.TEXT, SUBTITLE_Y);
        level1Button = addButton("РІВЕНЬ 1", FIRST_BUTTON_Y);
        level2Button = addButton("РІВЕНЬ 2", FIRST_BUTTON_Y + BUTTON_STEP);
        level3Button = addButton("РІВЕНЬ 3", FIRST_BUTTON_Y + 2 * BUTTON_STEP);
        rulesButton = addButton("ПРАВИЛА", FIRST_BUTTON_Y + 3 * BUTTON_STEP + RULES_BUTTON_GAP);
    }

    /**
     * Пише назву гри по літері, кожну кольором з палітри цеглин.
     */
    private void drawTitle() {
        GLabel whole = new GLabel(TITLE);
        whole.setFont(Palette.TITLE_FONT);
        double x = (getWidth() - whole.getWidth()) / 2;
        for (int i = 0; i < TITLE.length(); i++) {
            GLabel letter = new GLabel("" + TITLE.charAt(i));
            letter.setFont(Palette.TITLE_FONT);
            letter.setColor(Palette.brickColor((i % Palette.COLOR_COUNT) * Palette.ROWS_PER_COLOR));
            add(letter, x, TITLE_Y);
            x += letter.getWidth();
        }
    }

    /**
     * Чекає, поки гравець натисне одну з кнопок меню.
     *
     * @return кнопка, яку натиснули
     */
    private GObject waitForMenuChoice() {
        clicked = null;
        while (clicked != level1Button && clicked != level2Button
                && clicked != level3Button && clicked != rulesButton) {
            pause(CLICK_WAIT);
        }
        return clicked;
    }

    /**
     * Чекає, поки гравець натисне саме цю кнопку.
     *
     * @param button кнопка, яку чекаємо
     */
    private void waitForButton(MenuButton button) {
        clicked = null;
        while (clicked != button) {
            pause(CLICK_WAIT);
        }
        sounds.playClick();
    }

    /**
     * Показує правила гри і значки бонусів, поки гравець не натисне "Назад".
     */
    private void showRules() {
        removeAll();
        addCenteredLabel("ПРАВИЛА", Palette.BIG_FONT, Palette.TITLE, RULES_TITLE_Y);
        int y = RULES_FIRST_Y;
        y = addRulesLine("Рухай ракетку мишкою.", y);
        y = addRulesLine("Відбивай м'яч і розбий усі цеглини.", y);
        y = addRulesLine("М'яч упав униз: мінус сердечко.", y);
        y = addRulesLine("Сердечка скінчились: поразка.", y);
        y = addRulesLine("Верхні цеглини дають більше очок.", y);
        y += RULES_LINE_STEP;
        y = addRulesLine("З цеглин падають бонуси, лови їх:", y);
        y = addBonusLine(BonusType.WIDE_PADDLE, "ширша ракетка", y);
        y = addBonusLine(BonusType.SLOW_BALL, "повільніший м'яч", y);
        y = addBonusLine(BonusType.EXTRA_LIFE, "ще одне сердечко", y);
        y = addBonusLine(BonusType.NARROW_PADDLE, "вужча ракетка (з рівня 2)", y);
        y = addBonusLine(BonusType.FAST_BALL, "швидший м'яч (рівень 3)", y);
        MenuButton backButton = addButton("НАЗАД", y + RULES_LINE_STEP);
        waitForButton(backButton);
    }

    /**
     * Пише один рядок правил.
     *
     * @param text текст рядка
     * @param y де писати
     * @return де писати наступний рядок
     */
    private int addRulesLine(String text, int y) {
        GLabel line = new GLabel(text);
        line.setFont(Palette.TEXT_FONT);
        line.setColor(Palette.TEXT);
        add(line, RULES_LEFT, y);
        return y + RULES_LINE_STEP;
    }

    /**
     * Малює значок бонусу і пише поруч, що він робить.
     *
     * @param type вид бонусу
     * @param text що робить бонус
     * @param y де писати
     * @return де писати наступний рядок
     */
    private int addBonusLine(BonusType type, String text, int y) {
        Bonus icon = new Bonus(type);
        add(icon, RULES_LEFT, y - icon.getHeight());
        GLabel line = new GLabel(text);
        line.setFont(Palette.TEXT_FONT);
        line.setColor(Palette.TEXT);
        add(line, BONUS_TEXT_LEFT, y);
        return y + RULES_LINE_STEP;
    }

    /**
     * Пише напис по центру вікна.
     *
     * @param text текст
     * @param font шрифт
     * @param color колір
     * @param y висота рядка
     */
    private void addCenteredLabel(String text, String font, Color color, int y) {
        GLabel label = new GLabel(text);
        label.setFont(font);
        label.setColor(color);
        add(label, (getWidth() - label.getWidth()) / 2, y);
    }

    /**
     * Додає кнопку по центру вікна.
     *
     * @param text напис на кнопці
     * @param y де верх кнопки
     * @return створена кнопка
     */
    private MenuButton addButton(String text, int y) {
        MenuButton button = new MenuButton(text, BUTTON_WIDTH, BUTTON_HEIGHT);
        add(button, (getWidth() - BUTTON_WIDTH) / 2, y);
        return button;
    }

    /**
     * Готує рівень: запам'ятовує його, прибирає старий бонус,
     * додає сердечка, номер рівня і рахунок.
     *
     * @param level рівень, який зараз почнеться
     */
    private void startLevel(Level level) {
        currentLevel = level;
        bonus = null;
        remainingLives = INITIAL_LIVES_COUNT;
        hud = new Hud(level.getNumber(), getWidth());
        add(hud);
    }

    /**
     * Викликає handleBrickCollision, коли цеглина розбита: додає очки
     * і інколи випускає бонус з місця цеглини.
     *
     * @param brick розбита цеглина
     */
    private void brickBroken(GObject brick) {
        hud.addScore(pointsFor(brick));
        if (bonus == null && rgen.nextBoolean(currentLevel.getBonusChance())) {
            bonus = new Bonus(currentLevel.randomBonus(rgen));
            double x = brick.getX() + (brick.getWidth() - bonus.getWidth()) / 2;
            add(bonus, x, brick.getY());
        }
    }

    /**
     * Скільки очок дає цеглина
     * Ряд рахуємо з висоти цеглини.
     *
     * @param brick цеглина
     * @return очки: від 10 за нижній колір до 50 за верхній
     */
    private int pointsFor(GObject brick) {
        int row = (int) ((brick.getY() - BRICK_Y_OFFSET) / (BRICK_HEIGHT + BRICK_SEP));
        int colorFromTop = row / Palette.ROWS_PER_COLOR;
        if (colorFromTop < 0) {
            colorFromTop = 0;
        }
        if (colorFromTop >= Palette.COLOR_COUNT) {
            colorFromTop = Palette.COLOR_COUNT - 1;
        }
        return (Palette.COLOR_COUNT - colorFromTop) * POINTS_STEP;
    }

    /**
     * Викликає runGame на кожному кроці циклу: бонус падає,
     * і якщо ракетка його зловила, спрацьовує. Пропущений бонус зникає.
     */
    private void moveBonus() {
        if (bonus == null) {
            return;
        }
        bonus.fall();
        if (isCaught(bonus)) {
            applyBonus(bonus.getType());
            remove(bonus);
            bonus = null;
        } else if (bonus.getY() > getHeight()) {
            remove(bonus);
            bonus = null;
        }
    }

    /**
     * Чи це сердечка з очками або бонус, а не цеглина.
     * moveBall перевіряє це, щоб м'яч не розбивав їх.
     *
     * @param obj об'єкт, у який врізався м'яч
     * @return true, якщо це панель угорі або бонус
     */
    private boolean isHudOrBonus(GObject obj) {
        return obj == hud || obj == bonus;
    }

    /**
     * Чи торкається бонус ракетки.
     *
     * @param b бонус
     * @return true, якщо бонус і ракетка накладаються
     */
    private boolean isCaught(Bonus b) {
        if (paddle == null) {
            return false;
        }
        boolean touchesY = b.getY() + b.getHeight() >= paddle.getY()
                && b.getY() <= paddle.getY() + paddle.getHeight();
        boolean touchesX = b.getX() + b.getWidth() >= paddle.getX()
                && b.getX() <= paddle.getX() + paddle.getWidth();
        return touchesX && touchesY;
    }

    /**
     * Вмикає дію бонусу і грає його звук.
     *
     * @param type вид бонусу
     */
    private void applyBonus(BonusType type) {
        switch (type) {
            case WIDE_PADDLE:
                resizePaddle(WIDE_FACTOR);
                break;
            case NARROW_PADDLE:
                resizePaddle(NARROW_FACTOR);
                break;
            case SLOW_BALL:
                changeBallSpeed(SLOW_FACTOR);
                break;
            case FAST_BALL:
                changeBallSpeed(FAST_FACTOR);
                break;
            case EXTRA_LIFE:
                remainingLives++;
                hud.setLives(remainingLives);
                break;
            default:
                break;
        }
        sounds.playBonus(type.isGood());
    }

    /**
     * Міняє ширину ракетки в кілька разів, але в межах від MIN до MAX.
     *
     * @param factor у скільки разів змінити ширину
     */
    private void resizePaddle(double factor) {
        double width = paddle.getWidth() * factor;
        if (width > MAX_PADDLE_WIDTH) {
            width = MAX_PADDLE_WIDTH;
        }
        if (width < MIN_PADDLE_WIDTH) {
            width = MIN_PADDLE_WIDTH;
        }
        double centerX = paddle.getX() + paddle.getWidth() / 2;
        paddle.setSize(width, paddle.getHeight());
        paddle.setLocation(centerX - width / 2, paddle.getY()); // центр лишається на місці
    }

    /**
     * Міняє швидкість м'яча в кілька разів, напрям лишається той самий.
     *
     * @param factor у скільки разів змінити швидкість
     */
    private void changeBallSpeed(double factor) {
        vx *= factor;
        vy *= factor;
    }

    /**
     * Показує екран перемоги або поразки з рівнем і очками
     * і чекає, поки гравець натисне Меню.
     *
     * @param won true, якщо гравець виграв
     */
    private void showResult(boolean won) {
        int score = hud.getScore();
        sounds.stopMusic();
        if (won) {
            sounds.playWin();
        } else {
            sounds.playLose();
        }
        pause(RESULT_DELAY);
        removeAll();
        if (won) {
            addCenteredLabel("ПЕРЕМОГА!", Palette.BIG_FONT, Palette.GOOD, RESULT_TITLE_Y);
        } else {
            addCenteredLabel("ПОРАЗКА", Palette.BIG_FONT, Palette.BAD, RESULT_TITLE_Y);
        }
        addCenteredLabel("РІВЕНЬ " + currentLevel.getNumber() + "   ОЧКИ " + score,
                Palette.TEXT_FONT, Palette.TEXT, RESULT_LINE_Y);
        MenuButton menuButton = addButton("МЕНЮ", RESULT_BUTTON_Y);
        waitForButton(menuButton);
        sounds.startMusic();
    }

    /**
     * Запам'ятовує, по чому клікнули. Потрібно для кнопок.
     *
     * @param e подія миші
     */
    public void mouseClicked(MouseEvent e) {
        clicked = getElementAt(e.getX(), e.getY());
    }

    /** Рівень, який зараз грається */
    private Level currentLevel;

    /** Сердечка, рівень і рахунок угорі */
    private Hud hud;

    /** Усі звуки і музика */
    private Sounds sounds;

    /** Бонус, що зараз падає (null, якщо немає) */
    private Bonus bonus;

    /** Об'єкт, по якому клікнули останнім (для кнопок) */
    private GObject clicked;

    /** Кнопки стартової сторінки */
    private MenuButton level1Button;
    private MenuButton level2Button;
    private MenuButton level3Button;
    private MenuButton rulesButton;
}
