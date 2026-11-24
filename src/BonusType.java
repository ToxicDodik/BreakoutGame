/**
 * Вид бонусу. Кожен вид знає, хороший він чи ні, і який значок малювати.
 *
 * @author Khvesyk Zhenia
 */
public enum BonusType {
    WIDE_PADDLE, SLOW_BALL, EXTRA_LIFE, NARROW_PADDLE, FAST_BALL;

    /**
     * Чи полегшує бонус гру.
     *
     * @return true для хороших бонусів, false для поганих
     */
    public boolean isGood() {
        return this != NARROW_PADDLE && this != FAST_BALL;
    }

    /**
     * Короткий значок, який пишеться на бонусі.
     *
     * @return значок бонусу
     */
    public String getSymbol() {
        switch (this) {
            case WIDE_PADDLE:
                return "<->";
            case SLOW_BALL:
                return "S";
            case EXTRA_LIFE:
                return "+1";
            case NARROW_PADDLE:
                return ">-<";
            default:
                return "F"; // швидший м'яч
        }
    }
}
