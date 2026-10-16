package model;

import java.awt.Rectangle;

/**
 * "ความสามารถ" ที่เชฟเดินไปกด Space ใส่ได้
 * เตาและโต๊ะ implement interface นี้ ส่วนเชฟกับลูกค้าไม่ต้อง
 */
public interface Interactable {

    /** ตำแหน่งของ object (GameObject มี method นี้ให้อยู่แล้ว) */
    Rectangle getBounds();

    /**
     * ทำงานเมื่อเชฟกด Space ใกล้ ๆ
     * @return ข้อความที่จะแสดงบนจอ บอกผู้เล่นว่าเกิดอะไรขึ้น
     */
    String interact(Chef chef, GameStats stats);
}
