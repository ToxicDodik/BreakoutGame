/*
 * File: MenuButton.java
 *
 * Кнопка для меню в стилі 8-біт: прямокутник з тінню і написом.
 * Назва MenuButton, а не Button, бо в Java вже є клас java.awt.Button,
 * і через import java.awt.* вони плутались.
 */

import acm.graphics.*;

/**
 * Кнопка меню.
 *
 * @author Khvesyk Zhenia
 */
public class MenuButton extends GCompound {

    /** На скільки зсунута тінь кнопки */
    private static final double SHADOW = 4;

    /**
     * Створює кнопку: спочатку тінь, на ній прямокутник, по центру напис.
     *
     * @param text напис на кнопці
     * @param width ширина
     * @param height висота
     */
    public MenuButton(String text, double width, double height) {
        GRect shadow = new GRect(width, height);
        shadow.setFilled(true);
        shadow.setColor(Palette.BUTTON_SHADOW);
        add(shadow, SHADOW, SHADOW);

        GRect body = new GRect(width, height);
        body.setFilled(true);
        body.setColor(Palette.BUTTON);
        add(body);

        GLabel label = new GLabel(text);
        label.setFont(Palette.TEXT_FONT);
        label.setColor(Palette.TEXT);
        add(label, (width - label.getWidth()) / 2, (height + label.getAscent()) / 2 - 2);
    }
}
