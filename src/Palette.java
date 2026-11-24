import java.awt.*;

/**
 * Кольорова схема і шрифти гри в стилі 8-біт.
 *
 * @author Khvesyk Zhenia
 */
public class Palette {

    /** Фон: чорний, як на старих приставках */
    public static final Color BACKGROUND = new Color(0, 0, 0);

    /** Ракетка */
    public static final Color PADDLE = new Color(252, 252, 252);

    /** М'яч */
    public static final Color BALL = new Color(248, 184, 0);

    /** Звичайний текст */
    public static final Color TEXT = new Color(252, 252, 252);

    /** Заголовки */
    public static final Color TITLE = new Color(248, 184, 0);

    /** Сердечка */
    public static final Color HEART = new Color(228, 0, 88);

    /** Хороші бонуси і напис "перемога" */
    public static final Color GOOD = new Color(88, 248, 152);

    /** Погані бонуси і напис "поразка" */
    public static final Color BAD = new Color(248, 56, 0);

    /** Кнопка і її тінь */
    public static final Color BUTTON = new Color(0, 120, 248);
    public static final Color BUTTON_SHADOW = new Color(104, 68, 252);

    /** Кольори рядів цеглин, зверху вниз */
    private static final Color ROW_COLOR_1 = new Color(216, 0, 204);  // малиновий
    private static final Color ROW_COLOR_2 = new Color(104, 68, 252); // фіолетовий
    private static final Color ROW_COLOR_3 = new Color(0, 120, 248);  // синій
    private static final Color ROW_COLOR_4 = new Color(60, 188, 252); // небесний
    private static final Color ROW_COLOR_5 = new Color(88, 248, 152); // м'ятний

    /** Скільки різних кольорів цеглин */
    public static final int COLOR_COUNT = 5;

    /** Скільки рядів підряд мають один колір */
    public static final int ROWS_PER_COLOR = 2;

    /** Шрифти: моноширинні і жирні, щоб було схоже на старі ігри */
    public static final String TITLE_FONT = "Monospaced-bold-48";
    public static final String BIG_FONT = "Monospaced-bold-30";
    public static final String TEXT_FONT = "Monospaced-bold-14";
    public static final String SMALL_FONT = "Monospaced-bold-12";

    /**
     * Колір ряду цеглин.
     *
     * @param row номер ряду, рахуючи з 0 зверху
     * @return колір цеглин цього ряду
     */
    public static Color brickColor(int row) {
        switch (row / ROWS_PER_COLOR) {
            case 0:
                return ROW_COLOR_1;
            case 1:
                return ROW_COLOR_2;
            case 2:
                return ROW_COLOR_3;
            case 3:
                return ROW_COLOR_4;
            default:
                return ROW_COLOR_5;
        }
    }
}
