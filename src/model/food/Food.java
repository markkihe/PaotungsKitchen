package model.food;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.Sprites;

/**
 * class แม่ของอาหารทุกเมนู
 * ชื่อ เวลาทำ และราคาขายเก็บที่นี่ ส่วนหน้าตา (drawShape) แต่ละเมนูวาดเอง
 */
public abstract class Food {

    private final String name;
    private final int cookTimeMs;
    private final int price;

    public Food(String name, int cookTimeMs, int price) {
        this.name = name;
        this.cookTimeMs = cookTimeMs;
        this.price = price;
    }

    public String getName() { return name; }
    public int getCookTime() { return cookTimeMs; }
    public int getPrice() { return price; }

    /** เป็นเมนูเดียวกันหรือไม่ (เทียบจากชื่อ) */
    public boolean isSameDish(Food other) {
        return other != null && name.equals(other.name);
    }

    /** กินได้หรือไม่ (อาหารปกติกินได้ อาหารไหม้ override ให้เป็น false) */
    public boolean isEdible() {
        return true;
    }

    /** ชื่อไฟล์รูปใน assets/images/ เช่น food_burger.png หรือ food_burger_0.png, _1, ... (เคลื่อนไหว) */
    protected String getSpriteName() {
        return "food_" + name.toLowerCase();
    }

    /**
     * ทำอาหารจากของชิ้นนี้: วัตถุดิบดิบจะคืนเมนูที่ทำได้ อาหารที่ทำแล้วคืน null (ทำซ้ำไม่ได้)
     * เตาเรียก method นี้โดยไม่ต้องรู้ว่าของในมือเชฟเป็นอะไร (Polymorphism)
     */
    public Food cook() {
        return null;
    }

    /** ชื่อรูปสำรอง ถ้าไม่มีรูปหลัก (ค่าเริ่มต้น: ไม่มี) */
    protected String getFallbackSpriteName() {
        return null;
    }

    /**
     * วาดไอคอน 24x24: ถ้ามีรูปใช้รูป ถ้าไม่มีให้ลูกวาดรูปทรงเอง
     * (Template Method: ขั้นตอนกำหนดที่แม่ รายละเอียดให้ลูกเติม)
     */
    public final void drawIcon(Graphics2D g, int x, int y) {
        BufferedImage image = Sprites.getAnimated(getSpriteName(), 120);
        if (image == null && getFallbackSpriteName() != null) {
            image = Sprites.get(getFallbackSpriteName());
        }
        if (image != null) {
            g.drawImage(image, x, y, 24, 24, null);
        } else {
            drawShape(g, x, y);
        }
    }

    /** วาดด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    protected abstract void drawShape(Graphics2D g, int x, int y);
}
