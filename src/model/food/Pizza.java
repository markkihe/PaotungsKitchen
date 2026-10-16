package model.food;

import java.awt.Color;
import java.awt.Graphics2D;

public class Pizza extends Food {

    public Pizza() {
        super("Pizza", 5000, 80);
    }

    @Override
    protected void drawShape(Graphics2D g, int x, int y) {
        g.setColor(new Color(220, 160, 80));
        g.fillOval(x + 1, y + 1, 22, 22);    // ขอบแป้ง
        g.setColor(new Color(245, 210, 90));
        g.fillOval(x + 4, y + 4, 16, 16);    // ชีส
        g.setColor(new Color(200, 50, 40));
        g.fillOval(x + 7, y + 7, 4, 4);      // เปปเปอโรนี
        g.fillOval(x + 13, y + 9, 4, 4);
        g.fillOval(x + 9, y + 14, 4, 4);
    }
}
