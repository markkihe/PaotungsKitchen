package model.customer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import model.food.Food;

/** ลูกค้า VIP: จ่าย 2 เท่า และสวมมงกุฎ */
public class VipCustomer extends Customer {

    private static final int BASE_PATIENCE_MS = 16000;

    public VipCustomer(int startX, int seatX, int seatY, Food order, double patienceFactor) {
        super(startX, seatX, seatY, order, (int) (BASE_PATIENCE_MS * patienceFactor));
    }

    @Override
    public int calculatePayment() {
        return getOrder().getPrice() * 2;
    }

    @Override
    protected String getSpriteName() {
        return "customer_vip";
    }

    @Override
    protected Color getColor() {
        return new Color(130, 70, 170);
    }

    @Override
    protected void drawAccessory(Graphics2D g) {
        Polygon crown = new Polygon();
        crown.addPoint(x + 9, y + 4);
        crown.addPoint(x + 9, y - 3);
        crown.addPoint(x + 13, y + 1);
        crown.addPoint(x + 17, y - 5);
        crown.addPoint(x + 21, y + 1);
        crown.addPoint(x + 25, y - 3);
        crown.addPoint(x + 25, y + 4);
        g.setColor(new Color(240, 200, 40));
        g.fillPolygon(crown);
    }
}
