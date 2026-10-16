package model;

/**
 * ผลรวมของอัปเกรดทั้งหมดที่ซื้อแล้ว
 * อัปเกรดแต่ละตัวเข้ามาแก้ค่าในนี้ แล้วเกมอ่านค่าจากที่นี่ที่เดียว
 */
public class GameModifiers {

    private double cookTimeMultiplier = 1.0;  // 0.8 = ทำอาหารเร็วขึ้น 20%
    private int chefSpeedBonus = 0;
    private double patienceMultiplier = 1.0;
    private boolean secondStove = false;

    public double getCookTimeMultiplier() { return cookTimeMultiplier; }
    public int getChefSpeedBonus() { return chefSpeedBonus; }
    public double getPatienceMultiplier() { return patienceMultiplier; }
    public boolean hasSecondStove() { return secondStove; }

    public void setCookTimeMultiplier(double value) {
        cookTimeMultiplier = Math.max(0.3, value); // กันไม่ให้เร็วเกินจริง
    }

    public void setChefSpeedBonus(int value) {
        chefSpeedBonus = Math.max(0, value);
    }

    public void setPatienceMultiplier(double value) {
        patienceMultiplier = Math.max(1.0, value);
    }

    public void setSecondStove(boolean value) {
        secondStove = value;
    }
}
