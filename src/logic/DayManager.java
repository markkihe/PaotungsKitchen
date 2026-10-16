package logic;

/**
 * จัดการวัน: เวลาที่เหลือ ค่าเช่าร้าน และความยากที่เพิ่มขึ้นทุกวัน
 * ถ้าอยากปรับความยากของเกม แก้ตัวเลขในไฟล์นี้ไฟล์เดียว
 */
public class DayManager {

    private static final int DAY_LENGTH_MS = 90_000; // 1 วัน = 90 วินาที (นับเฉพาะตอนร้านเปิด)
    private static final int PREP_LENGTH_MS = 30_000; // ช่วงเช้าซื้อของก่อนเปิดร้าน

    private int day;
    private int timeLeftMs;
    private int prepLeftMs;
    private boolean open;          // ร้านเปิดหรือยัง (ยังไม่เปิด = ช่วงเตรียมของตอนเช้า)
    private boolean justOpened;    // เพิ่งเปิดร้านรอบนี้ (ให้ GamePanel แสดงข้อความ)

    public DayManager() {
        reset();
    }

    public void reset() {
        day = 1;
        startDay();
    }

    public void nextDay() {
        day++;
        startDay();
    }

    /** ทุกวันเริ่มที่ช่วงเช้า: ร้านยังปิด มีเวลาซื้อของ */
    private void startDay() {
        timeLeftMs = DAY_LENGTH_MS;
        prepLeftMs = PREP_LENGTH_MS;
        open = false;
        justOpened = false;
    }

    /** ช่วงเช้านับเวลาเตรียมของ หมดแล้วเปิดร้านเอง / ร้านเปิดแล้วนับเวลาของวัน */
    public void update(int deltaMs) {
        if (!open) {
            prepLeftMs = Math.max(0, prepLeftMs - deltaMs);
            if (prepLeftMs == 0) {
                openShop();
            }
            return;
        }
        timeLeftMs = Math.max(0, timeLeftMs - deltaMs);
    }

    /** เปิดร้าน (เดินเข้าร้านก่อนหมดเวลาเช้า ก็เปิดได้เลย) */
    public void openShop() {
        if (!open) {
            open = true;
            justOpened = true;
        }
    }

    public boolean consumeJustOpened() {
        boolean result = justOpened;
        justOpened = false;
        return result;
    }

    public boolean isOpen() {
        return open;
    }

    public int getPrepSecondsLeft() {
        return (prepLeftMs + 999) / 1000;
    }

    public boolean isDayOver() {
        return open && timeLeftMs == 0;
    }

    /** ค่าเช่าร้านที่ต้องจ่ายตอนจบวัน: เพิ่มวันละ $75 */
    public int getRent() {
        return 100 + (day - 1) * 75;
    }

    /** ลูกค้ามาถี่ขึ้นทุกวัน (ลดวันละ 0.9 วินาที) แต่ไม่ถี่กว่า 2.5 วินาที */
    public int getSpawnIntervalMs() {
        return Math.max(2500, 7000 - (day - 1) * 900);
    }

    /**
     * วันแรก ๆ ลูกค้ารอได้นานกว่าปกติ (160%) เผื่อเวลาวิ่งไปซื้อวัตถุดิบเพิ่มที่ตลาด
     * แล้วอดทนน้อยลงวันละ 15% (วันที่ 5 = 100%) แต่ไม่ต่ำกว่า 50%
     */
    public double getPatienceFactor() {
        return Math.max(0.5, 1.6 - (day - 1) * 0.15);
    }

    /** อาหารสุกแล้วรอได้กี่มิลลิวินาทีก่อนไหม้: 7 วินาที ลดวันละ 0.7 วินาที (ต่ำสุด 3.5) */
    public int getBurnDelayMs() {
        return Math.max(3500, 7000 - (day - 1) * 700);
    }

    public int getDay() {
        return day;
    }

    public int getSecondsLeft() {
        return (timeLeftMs + 999) / 1000;
    }
}
