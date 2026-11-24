import acm.graphics.*;

/**
 * Сердечка, рівень і очки.
 *
 * @author Khvesyk Zhenia
 */
public class Hud extends GCompound {

    /** Відступ від країв вікна */
    private static final double MARGIN = 10;

    /** Відстань між сердечками */
    private static final double HEART_GAP = 6;

    /** Скільки сердечок видно завжди (3 життя на початку рівня) */
    private static final int HEART_SLOTS = 3;

    /** Скільки сердечок показуємо максимум (решту пишемо числом) */
    private static final int MAX_HEARTS = 5;

    /** Картинки сердечок */
    private static final String FULL_HEART = "images/heart_full.png";
    private static final String EMPTY_HEART = "images/heart_empty.png";

    /** Скільки очок набрано */
    private int score;

    /** Ширина вікна, щоб поставити рахунок справа */
    private double width;

    /** Сердечка */
    private GCompound hearts;

    /** Напис з очками */
    private GLabel scoreLabel;

    /**
     * Створює панель з нулем очок.
     *
     * @param levelNumber номер рівня
     * @param width ширина вікна
     */
    public Hud(int levelNumber, double width) {
        this.width = width;
        hearts = new GCompound();
        add(hearts, MARGIN, MARGIN);

        GLabel levelLabel = new GLabel("РІВЕНЬ " + levelNumber);
        levelLabel.setFont(Palette.SMALL_FONT);
        levelLabel.setColor(Palette.TITLE);
        add(levelLabel, (width - levelLabel.getWidth()) / 2, MARGIN + levelLabel.getAscent());

        scoreLabel = new GLabel("");
        scoreLabel.setFont(Palette.SMALL_FONT);
        scoreLabel.setColor(Palette.TEXT);
        add(scoreLabel);
        updateScore();
        setLives(HEART_SLOTS); // на початку рівня 3 червоні сердечка
    }

    /**
     * Перемальовує сердечка: спочатку червоні,
     * потім сірі . Завжди видно хоча б HEART_SLOTS сердечок,
     * а з бонусом "+1 життя" їх стає більше. Якщо життів більше,
     * ніж MAX_HEARTS, поруч пишемо, скільки ще.
     *
     * @param lives скільки життів лишилось
     */
    public void setLives(int lives) {
        hearts.removeAll();
        int slots = HEART_SLOTS;
        if (lives > slots) {
            slots = lives;
        }
        double x = 0;
        for (int i = 0; i < slots && i < MAX_HEARTS; i++) {
            GImage heart;
            if (i < lives) {
                heart = new GImage(FULL_HEART);
            } else {
                heart = new GImage(EMPTY_HEART);
            }
            hearts.add(heart, x, 0);
            x += heart.getWidth() + HEART_GAP;
        }
        if (lives > MAX_HEARTS) {
            GLabel more = new GLabel("+" + (lives - MAX_HEARTS));
            more.setFont(Palette.SMALL_FONT);
            more.setColor(Palette.HEART);
            hearts.add(more, x, more.getAscent());
        }
    }

    /**
     * Додає очки і оновлює напис.
     *
     * @param points скільки очок додати
     */
    public void addScore(int points) {
        score += points;
        updateScore();
    }

    /** @return скільки очок набрано */
    public int getScore() {
        return score;
    }

    /**
     * Пише рахунок і ставить напис біля правого краю.
     */
    private void updateScore() {
        scoreLabel.setLabel("ОЧКИ " + score);
        scoreLabel.setLocation(width - MARGIN - scoreLabel.getWidth(), MARGIN + scoreLabel.getAscent());
    }
}
