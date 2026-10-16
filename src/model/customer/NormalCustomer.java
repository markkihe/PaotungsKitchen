package model.customer;

import java.awt.Color;
import model.food.Food;

/** ลูกค้าทั่วไป: รอได้นาน ให้ทิป 10 ถ้าเสิร์ฟเร็ว */
public class NormalCustomer extends Customer {

    private static final int BASE_PATIENCE_MS = 20000;

    public NormalCustomer(int startX, int seatX, int seatY, Food order, double patienceFactor) {
        super(startX, seatX, seatY, order, (int) (BASE_PATIENCE_MS * patienceFactor));
    }

    @Override
    public int calculatePayment() {
        int tip = getPatienceRatio() > 0.5 ? 10 : 0;
        return getOrder().getPrice() + tip;
    }

    @Override
    protected String getSpriteName() {
        return "customer_normal";
    }

    @Override
    protected Color getColor() {
        return new Color(70, 120, 200);
    }
}
