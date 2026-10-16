package model.upgrade;

import model.GameModifiers;
import model.GameStats;

/**
 * class แม่ของอัปเกรดในร้านค้าหลังจบวัน
 * ทุกอัปเกรดมีชื่อ ราคา และเลเวล ส่วน "ผล" ของมัน (apply) แต่ละตัวเขียนเอง
 */
public abstract class Upgrade {

    private final String name;
    private final String description;
    private final int basePrice;
    private final int maxLevel;
    private int level;

    public Upgrade(String name, String description, int basePrice, int maxLevel) {
        this.name = name;
        this.description = description;
        this.basePrice = basePrice;
        this.maxLevel = maxLevel;
    }

    /** ใส่ผลของอัปเกรดนี้ลงใน modifiers ตามเลเวลปัจจุบัน */
    public abstract void apply(GameModifiers modifiers);

    /** ราคาแพงขึ้นตามเลเวล: เลเวลถัดไป = basePrice x (level + 1) */
    public int getPrice() {
        return basePrice * (level + 1);
    }

    public boolean isMaxed() {
        return level >= maxLevel;
    }

    /** ซื้อได้เมื่อยังไม่เต็ม และมีเงินพอ */
    public boolean tryBuy(GameStats stats) {
        if (isMaxed()) {
            return false;
        }
        if (!stats.spend(getPrice())) {
            return false;
        }
        level++;
        return true;
    }

    public void reset() {
        level = 0;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getLevel() { return level; }
    public int getMaxLevel() { return maxLevel; }
}
