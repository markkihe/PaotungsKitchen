package model.customer;

import java.awt.Color;
import model.food.Food;

/** ลูกค้าใจร้อน: รอได้สั้น แต่จ่ายโบนัส 15 ถ้าเสิร์ฟทัน */
public class ImpatientCustomer extends Customer {

    private static final int BASE_PATIENCE_MS = 12000;

    public ImpatientCustomer(int startX, int seatX, int seatY, Food order, double patienceFactor) {
        super(startX, seatX, seatY, order, (int) (BASE_PATIENCE_MS * patienceFactor));
    }

    @Override
    public int calculatePayment() {
        return getOrder().getPrice() + 15;
    }

    /**
     * คาปิบาราใจร้อน: หน้าเปลี่ยนตามความอดทนที่เหลือ
     * ใจเย็น (เคี้ยวหญ้า) -> หงุดหงิด (คิ้วขมวด) -> โกรธจนหน้าแดง ควันออกหัว ตัวสั่น
     */
    @Override
    protected String getSpriteName() {
        double patience = getPatienceRatio();
        if (isLeavingAngry() || (isWaiting() && patience <= 0.3)) {
            return "customer_impatient_angry";
        }
        if (isWaiting() && patience <= 0.6) {
            return "customer_impatient_annoyed";
        }
        return "customer_impatient";
    }

    @Override
    protected Color getColor() {
        return new Color(220, 90, 50);
    }
}
