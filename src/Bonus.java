import acm.graphics.*;

/**
 * Бонус, що падає вниз: кольорова капсула зі значком.
 *
 * @author Khvesyk Zhenia
 */
public class Bonus extends GCompound {

    /** Розміри капсули */
    private static final double WIDTH = 30;
    private static final double HEIGHT = 14;

    /** На скільки пікселів бонус падає за один крок */
    private static final double FALL_SPEED = 2;

    /** Вид бонусу */
    private BonusType type;

    /**
     * Створює бонус: зелена або червона капсула зі значком посередині.
     *
     * @param type вид бонусу
     */
    public Bonus(BonusType type) {
        this.type = type;
        GRect body = new GRect(WIDTH, HEIGHT);
        body.setFilled(true);
        if (type.isGood()) {
            body.setColor(Palette.GOOD);
        } else {
            body.setColor(Palette.BAD);
        }
        add(body);

        GLabel sign = new GLabel(type.getSymbol());
        sign.setFont(Palette.SMALL_FONT);
        sign.setColor(Palette.BACKGROUND);
        add(sign, (WIDTH - sign.getWidth()) / 2, (HEIGHT + sign.getAscent()) / 2 - 1);
    }

    /** @return вид бонусу */
    public BonusType getType() {
        return type;
    }

    /**
     * Зсуває бонус на один крок вниз.
     */
    public void fall() {
        move(0, FALL_SPEED);
    }
}
