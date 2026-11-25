import acm.util.*;

import java.applet.AudioClip;

/**
 * Звуки і музика. Файли лежать у папці sounds, а завантажує їх
 * MediaTools.loadAudioClip, як у завданні до лабораторної.
 *
 * @author Khvesyk Zhenia
 */
public class Sounds {

    /** Окремі звуки */
    private AudioClip bounce;
    private AudioClip click;
    private AudioClip bonusGood;
    private AudioClip bonusBad;
    private AudioClip win;
    private AudioClip lose;
    private AudioClip damage;

    /** Фонова мелодія */
    private AudioClip music;

    /**
     * Завантажує всі звуки. MediaTools сам шукає файли в папці sounds.
     */
    public Sounds() {
        bounce = MediaTools.loadAudioClip("bounce.wav");
        click = MediaTools.loadAudioClip("click.wav");
        bonusGood = MediaTools.loadAudioClip("bonus_good.wav");
        bonusBad = MediaTools.loadAudioClip("bonus_bad.wav");
        win = MediaTools.loadAudioClip("win.wav");
        lose = MediaTools.loadAudioClip("lose.wav");
        damage = MediaTools.loadAudioClip("damage.wav");
        music = MediaTools.loadAudioClip("music.wav");
    }

    /** Звук відбивання м'яча */
    public void playBounce() {
        bounce.play();
    }

    /** Звук натискання кнопки */
    public void playClick() {
        click.play();
    }

    /** Запускає фонову мелодію по колу */
    public void startMusic() {
        music.loop();
    }

    /** Зупиняє фонову мелодію */
    public void stopMusic() {
        music.stop();
    }

    /** Звук урона */
    public void playDamage() {
        damage.play();
    }

    /**
     * Звук бонусу: веселий для хорошого, сумний для поганого.
     *
     * @param good true, якщо бонус полегшує гру
     */
    public void playBonus(boolean good) {
        if (good) {
            bonusGood.play();
        } else {
            bonusBad.play();
        }
    }

    /** Звук перемоги */
    public void playWin() {
        win.play();
    }

    /** Звук поразки */
    public void playLose() {
        lose.play();
    }
}
