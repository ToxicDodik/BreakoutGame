import acm.util.*;

/**
 * Один із трьох рівнів гри. Що вищий рівень, то більше цеглин,
 * швидший м'яч, вужча ракетка і частіше випадають погані бонуси.
 *
 * @author Khvesyk Zhenia
 */
public class Level {

    /** Номери рівнів */
    public static final int EASY = 1;
    public static final int MEDIUM = 2;
    public static final int HARD = 3;

    /** Рівень 1: мало цеглин, повільний м'яч, широка ракетка, тільки хороші бонуси */
    private static final int EASY_ROWS = 6;
    private static final double EASY_SPEED = 3.0;
    private static final double EASY_PADDLE = 80;
    private static final double EASY_BAD_CHANCE = 0;

    /** Рівень 2: з'являється погана ракетка */
    private static final int MEDIUM_ROWS = 8;
    private static final double MEDIUM_SPEED = 4.0;
    private static final double MEDIUM_PADDLE = 65;
    private static final double MEDIUM_BAD_CHANCE = 0.35;

    /** Рівень 3: усі цеглини, найшвидший м'яч, погані бонуси частіше */
    private static final int HARD_ROWS = 10;
    private static final double HARD_SPEED = 5.0;
    private static final double HARD_PADDLE = 55;
    private static final double HARD_BAD_CHANCE = 0.5;

    /** Шанс, що з розбитої цеглини випаде бонус */
    private static final double BONUS_CHANCE = 0.25;

    /** Скільки є видів хороших бонусів */
    private static final int GOOD_KINDS = 3;

    /** Номер рівня */
    private int number;

    /** Скільки рядів цеглин */
    private int rows;

    /** Початкова швидкість м'яча */
    private double ballSpeed;

    /** Ширина ракетки */
    private double paddleWidth;

    /** Шанс, що бонус буде поганим */
    private double badBonusChance;

    /**
     * Створює рівень за номером і заповнює його параметри.
     *
     * @param number номер рівня від 1 до 3
     */
    public Level(int number) {
        this.number = number;
        switch (number) {
            case EASY:
                rows = EASY_ROWS;
                ballSpeed = EASY_SPEED;
                paddleWidth = EASY_PADDLE;
                badBonusChance = EASY_BAD_CHANCE;
                break;
            case MEDIUM:
                rows = MEDIUM_ROWS;
                ballSpeed = MEDIUM_SPEED;
                paddleWidth = MEDIUM_PADDLE;
                badBonusChance = MEDIUM_BAD_CHANCE;
                break;
            default:
                rows = HARD_ROWS;
                ballSpeed = HARD_SPEED;
                paddleWidth = HARD_PADDLE;
                badBonusChance = HARD_BAD_CHANCE;
                break;
        }
    }

    /** @return номер рівня */
    public int getNumber() {
        return number;
    }

    /** @return скільки рядів цеглин */
    public int getRows() {
        return rows;
    }

    /** @return початкова швидкість м'яча по вертикалі */
    public double getBallSpeed() {
        return ballSpeed;
    }

    /** @return ширина ракетки */
    public double getPaddleWidth() {
        return paddleWidth;
    }

    /** @return шанс, що з цеглини випаде бонус (від 0 до 1) */
    public double getBonusChance() {
        return BONUS_CHANCE;
    }

    /**
     * Вибирає випадковий бонус для цього рівня. На рівні 1 тільки хороші,
     * далі погані додаються поступово.
     *
     * @param rgen генератор випадкових чисел
     * @return вид бонусу
     */
    public BonusType randomBonus(RandomGenerator rgen) {
        if (rgen.nextBoolean(badBonusChance)) {
            return randomBadBonus(rgen);
        }
        return randomGoodBonus(rgen);
    }

    /**
     * Випадковий хороший бонус: ширша ракетка, повільніший м'яч або +1 життя.
     *
     * @param rgen генератор випадкових чисел
     * @return хороший бонус
     */
    private BonusType randomGoodBonus(RandomGenerator rgen) {
        switch (rgen.nextInt(GOOD_KINDS)) {
            case 0:
                return BonusType.WIDE_PADDLE;
            case 1:
                return BonusType.SLOW_BALL;
            default:
                return BonusType.EXTRA_LIFE;
        }
    }

    /**
     * Випадковий поганий бонус: на рівні 2 тільки вужча ракетка,
     * на рівні 3 ще й швидший м'яч.
     *
     * @param rgen генератор випадкових чисел
     * @return поганий бонус
     */
    private BonusType randomBadBonus(RandomGenerator rgen) {
        if (number == HARD && rgen.nextBoolean()) {
            return BonusType.FAST_BALL;
        }
        return BonusType.NARROW_PADDLE;
    }
}
