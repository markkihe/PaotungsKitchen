package ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * จดจำว่าตอนนี้ผู้เล่นกดปุ่มอะไรอยู่
 * GamePanel จะมาถามค่าจาก class นี้ทุกรอบของ game loop
 */
public class KeyHandler extends KeyAdapter {

    // ปุ่มที่ "กดค้าง" ได้ (true ตลอดเวลาที่ยังกดอยู่)
    private boolean up, down, left, right;
    private boolean sprint;

    // ปุ่มที่ "กดครั้งเดียว" (ใช้แล้วต้องล้างค่าทิ้ง)
    private boolean interactPressed;
    private boolean pausePressed;
    private boolean confirmPressed;
    private boolean mutePressed;
    private int menuChoice; // 0 = ไม่ได้กด, 1-4 = ตัวเลือกที่กด

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = true;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = true;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = true;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = true;
            case KeyEvent.VK_SHIFT -> sprint = true;
            case KeyEvent.VK_SPACE -> interactPressed = true;
            case KeyEvent.VK_P -> pausePressed = true;
            case KeyEvent.VK_ENTER -> confirmPressed = true;
            case KeyEvent.VK_M -> mutePressed = true;
            case KeyEvent.VK_1, KeyEvent.VK_NUMPAD1 -> menuChoice = 1;
            case KeyEvent.VK_2, KeyEvent.VK_NUMPAD2 -> menuChoice = 2;
            case KeyEvent.VK_3, KeyEvent.VK_NUMPAD3 -> menuChoice = 3;
            case KeyEvent.VK_4, KeyEvent.VK_NUMPAD4 -> menuChoice = 4;
            default -> { } // ปุ่มอื่นไม่สนใจ
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W, KeyEvent.VK_UP -> up = false;
            case KeyEvent.VK_S, KeyEvent.VK_DOWN -> down = false;
            case KeyEvent.VK_A, KeyEvent.VK_LEFT -> left = false;
            case KeyEvent.VK_D, KeyEvent.VK_RIGHT -> right = false;
            case KeyEvent.VK_SHIFT -> sprint = false;
            default -> { }
        }
    }

    public boolean isUp() { return up; }
    public boolean isDown() { return down; }
    public boolean isLeft() { return left; }
    public boolean isRight() { return right; }
    public boolean isSprint() { return sprint; }

    /** คืนค่าว่ากด Space หรือไม่ แล้วล้างค่าทิ้ง เพื่อให้กด 1 ครั้ง = ทำ 1 ครั้ง */
    public boolean consumeInteract() {
        boolean pressed = interactPressed;
        interactPressed = false;
        return pressed;
    }

    public boolean consumePauseToggle() {
        boolean pressed = pausePressed;
        pausePressed = false;
        return pressed;
    }

    public boolean consumeConfirm() {
        boolean pressed = confirmPressed;
        confirmPressed = false;
        return pressed;
    }

    public boolean consumeMute() {
        boolean pressed = mutePressed;
        mutePressed = false;
        return pressed;
    }

    public int consumeMenuChoice() {
        int choice = menuChoice;
        menuChoice = 0;
        return choice;
    }

    /**
     * ล้างเฉพาะปุ่ม "กดครั้งเดียว" แต่ปุ่มเดินที่กดค้างยังอยู่
     * ใช้ตอนเปลี่ยนฉาก: เชฟเดินต่อได้ทันที ไม่ต้องรอ key repeat ของระบบ (ที่ทำให้ดูค้าง)
     */
    public void clearPresses() {
        interactPressed = false;
        pausePressed = false;
        confirmPressed = false;
        mutePressed = false;
        menuChoice = 0;
    }

    /** ล้างทุกปุ่ม ใช้ตอนเปลี่ยนสถานะเกม เพื่อกันปุ่มค้าง */
    public void reset() {
        up = down = left = right = false;
        sprint = false;
        interactPressed = false;
        pausePressed = false;
        confirmPressed = false;
        mutePressed = false;
        menuChoice = 0;
    }
}
