package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.food.Food;

/**
 * ถังขยะ: เดินมากด Space เพื่อทิ้งอาหารที่ถืออยู่
 * ใช้ตอนลูกค้าเดินออกไปแล้ว หรือทำเมนูผิด
 */
public class TrashBin extends GameObject implements Interactable {

    private static final int OPEN_TICKS = 36;      // ฝาเปิดค้าง ~0.6 วินาที
    private static final int HALF_OPEN_TICKS = 5;  // ช่วงฝาแง้ม ตอนเปิดและตอนปิด
    // รูปสูงกว่ากรอบชน เพราะมีที่ว่างด้านบนไว้ให้ฝายกขึ้น
    private static final int SPRITE_HEIGHT = 65;

    private int openTimer;   // > 0 = ฝากำลังเปิดอยู่

    public TrashBin(int x, int y) {
        super(x, y, 50, 54);
    }

    @Override
    public String interact(Chef chef, GameStats stats) {
        if (!chef.hasFood()) {
            return "Trash bin: throw away food here";
        }
        Food food = chef.handOver();
        openTimer = OPEN_TICKS; // เปิดฝา
        return "Threw away " + food.getName();
    }

    /** นับถอยหลังเวลาเปิดฝา (abstract method ของ GameObject ที่ต้องเขียน) */
    @Override
    public void update() {
        if (openTimer > 0) {
            openTimer--;
        }
    }

    /** ปิด -> แง้ม -> เปิดสุด -> แง้ม -> ปิด */
    private String getSpriteName() {
        if (openTimer == 0) {
            return "trash";
        }
        boolean opening = openTimer > OPEN_TICKS - HALF_OPEN_TICKS;
        boolean closing = openTimer <= HALF_OPEN_TICKS;
        return (opening || closing) ? "trash_open_0" : "trash_open_1";
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage image = Sprites.get(getSpriteName());
        if (image == null) {
            image = Sprites.get("trash");
        }
        if (image != null) {
            g.drawImage(image, x, y + height - SPRITE_HEIGHT, width, SPRITE_HEIGHT, null);
            return;
        }
        // ไม่มีรูป: วาดด้วยรูปทรง
        // ตัวถัง
        g.setColor(new Color(110, 130, 120));
        g.fillRoundRect(x + 4, y + 10, width - 8, height - 10, 8, 8);
        // ร่องแนวตั้ง
        g.setColor(new Color(85, 100, 92));
        for (int i = 0; i < 3; i++) {
            g.fillRect(x + 14 + i * 9, y + 18, 3, height - 22);
        }
        // ฝา
        g.setColor(new Color(80, 95, 88));
        g.fillRoundRect(x, y + 4, width, 9, 6, 6);
        g.fillRect(x + 19, y, 12, 6);
    }
}
