package model;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * class แม่ของทุกสิ่งที่อยู่บนจอเกม (เชฟ ลูกค้า เตา โต๊ะ)
 * เป็น abstract เพราะ "สิ่งของบนจอ" เฉย ๆ ไม่มีจริง ต้องเป็นของชนิดใดชนิดหนึ่งเสมอ
 */
public abstract class GameObject {

    /** ระยะเวลาของ 1 รอบ game loop (มิลลิวินาที) ทุก object อัปเดตตามจังหวะนี้ */
    public static final int TICK_MS = 16;

    // protected = subclass เข้าถึงได้ แต่ class อื่นภายนอกเข้าไม่ได้
    protected int x;
    protected int y;
    protected int width;
    protected int height;

    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /** อัปเดตสถานะ 1 ครั้งต่อ 1 รอบของ game loop (ลูกต้องเขียนเอง) */
    public abstract void update();

    /** วาดตัวเองลงจอ (ลูกต้องเขียนเอง) */
    public abstract void draw(Graphics2D g);

    /** กรอบสี่เหลี่ยมสำหรับตรวจการชน */
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /** อยู่ใกล้กรอบสี่เหลี่ยม target ภายในระยะ range พิกเซลหรือไม่ (ใช้ตอนกด Space) */
    public boolean isNear(Rectangle target, int range) {
        Rectangle area = new Rectangle(x - range, y - range, width + range * 2, height + range * 2);
        return area.intersects(target);
    }

    /** ย้ายไปตำแหน่งใหม่ทันที (ใช้ตอนเชฟเดินผ่านประตูไปอีกฉาก) */
    public void moveTo(int newX, int newY) {
        x = newX;
        y = newY;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}
