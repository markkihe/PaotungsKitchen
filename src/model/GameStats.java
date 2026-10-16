package model;

/**
 * เก็บตัวเลขทั้งหมดของผู้เล่น: เงิน คะแนน ดาวชื่อเสียง (HP) และจำนวนลูกค้า
 * ทุก field เป็น private และเปลี่ยนค่าได้ผ่าน method ที่ตรวจความถูกต้องเท่านั้น
 */
public class GameStats {

    public static final int MAX_REPUTATION = 5;
    public static final int STARTING_CASH = 100;   // ทุนเริ่มต้นสำหรับซื้อวัตถุดิบ
    public static final int STREAK_FOR_STAR = 5;   // เสิร์ฟถูกติดกันกี่ครั้งถึงได้ดาวคืน 1 ดวง

    private int score;
    private int cash;         // เงินในกระเป๋า ใช้ซื้ออัปเกรดได้
    private int dayMoney;
    private int reputation;   // ดาวชื่อเสียงร้าน ทำหน้าที่เป็น HP
    private int servedToday;
    private int lostToday;
    private int ingredientsToday;   // ค่าวัตถุดิบที่จ่ายไปวันนี้
    private int rentToday;          // ค่าเช่าที่จ่ายตอนจบวัน
    private int totalServed;
    private int serveStreak;        // เสิร์ฟถูกติดกันโดยไม่เสียดาว (นับข้ามวันได้)

    public GameStats() {
        reset();
    }

    /** เริ่มเกมใหม่ทั้งหมด */
    public void reset() {
        score = 0;
        cash = STARTING_CASH;
        totalServed = 0;
        serveStreak = 0;
        reputation = MAX_REPUTATION;
        startNewDay();
    }

    /** ล้างตัวเลขรายวัน (คะแนนรวมและดาวยังอยู่) */
    public void startNewDay() {
        dayMoney = 0;
        servedToday = 0;
        lostToday = 0;
        ingredientsToday = 0;
        rentToday = 0;
    }

    public void earn(int amount) {
        if (amount <= 0) return; // กันค่าติดลบหลุดเข้ามา
        dayMoney += amount;
        cash += amount;
    }

    /** จ่ายเงิน: สำเร็จเมื่อเงินพอเท่านั้น */
    public boolean spend(int amount) {
        if (amount <= 0 || amount > cash) {
            return false;
        }
        cash -= amount;
        return true;
    }

    /** จ่ายค่าวัตถุดิบตอนเริ่มทำอาหาร */
    public boolean payIngredients(int cost) {
        if (cost <= 0) {
            return true;
        }
        if (!spend(cost)) {
            return false;
        }
        ingredientsToday += cost;
        return true;
    }

    /** จ่ายค่าเช่าร้านตอนจบวัน: เงินไม่พอ = ล้มละลาย */
    public boolean payRent(int rent) {
        if (!spend(rent)) {
            return false;
        }
        rentToday = rent;
        return true;
    }

    public void addScore(int points) {
        if (points <= 0) return;
        score += points;
    }

    /**
     * นับลูกค้าที่เสิร์ฟสำเร็จ และนับ streak: ครบ STREAK_FOR_STAR ครั้งได้ดาวคืน 1 ดวง (ไม่เกิน MAX)
     * @return true ถ้าการเสิร์ฟครั้งนี้ทำให้ได้ดาวเพิ่ม
     */
    public boolean addServed() {
        servedToday++;
        totalServed++;
        if (reputation >= MAX_REPUTATION) {
            serveStreak = 0; // ดาวเต็มแล้ว ไม่ต้องสะสม
            return false;
        }
        serveStreak++;
        if (serveStreak < STREAK_FOR_STAR) {
            return false;
        }
        serveStreak = 0;
        reputation++;
        return true;
    }

    /** เสีย 1 ดาว (ลูกค้าเดินออก หรือเสิร์ฟผิดเมนู) ไม่มีทางติดลบ */
    public void loseReputation() {
        if (reputation > 0) {
            reputation--;
        }
        serveStreak = 0; // พลาดแล้วต้องเริ่มนับใหม่
        lostToday++;
    }

    public boolean isGameOver() {
        return reputation <= 0;
    }

    public int getScore() { return score; }
    public int getDayMoney() { return dayMoney; }
    public int getCash() { return cash; }
    public int getIngredientsToday() { return ingredientsToday; }
    public int getRentToday() { return rentToday; }
    /** กำไรวันนี้ = รายได้ - วัตถุดิบ - ค่าเช่า */
    public int getProfitToday() { return dayMoney - ingredientsToday - rentToday; }
    public int getReputation() { return reputation; }
    public int getServedToday() { return servedToday; }
    public int getLostToday() { return lostToday; }
    public int getTotalServed() { return totalServed; }
    public int getServeStreak() { return serveStreak; }
}
