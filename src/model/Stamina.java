package model;

/**
 * เกจพลังงานสำหรับวิ่ง (Sprint)
 * วิ่งแล้วลด หยุดวิ่งแล้วฟื้น ถ้าหมดเกลี้ยงต้องรอฟื้นถึง 30% ก่อนวิ่งได้อีก
 */
public class Stamina {

    public static final double MAX = 100.0;
    private static final double DRAIN_PER_TICK = 0.6;   // วิ่งได้ประมาณ 2.7 วินาที
    private static final double REGEN_PER_TICK = 0.4;   // ฟื้นเต็มประมาณ 4 วินาที
    private static final double RECOVER_AT = 30.0;

    private double value = MAX;
    private boolean exhausted;
    private boolean active;

    /**
     * เรียกทุกรอบ game loop
     * @param wantsSprint ผู้เล่นกด Shift และกำลังเดินอยู่หรือไม่
     * @return true ถ้ารอบนี้ได้วิ่งจริง
     */
    public boolean update(boolean wantsSprint) {
        if (exhausted && value >= RECOVER_AT) {
            exhausted = false;
        }

        active = wantsSprint && !exhausted;
        if (active) {
            value -= DRAIN_PER_TICK;
            if (value <= 0) {
                value = 0;
                exhausted = true;
                active = false;
            }
        } else {
            value = Math.min(MAX, value + REGEN_PER_TICK);
        }
        return active;
    }

    public double getRatio() { return value / MAX; }
    public boolean isExhausted() { return exhausted; }
    public boolean isFull() { return value >= MAX; }
}
