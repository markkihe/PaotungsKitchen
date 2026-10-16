package model.food;

import java.awt.Color;
import java.awt.Graphics2D;

public class Salad extends Food {

    public Salad() {
        super("Salad", 2000, 30);
    }

    @Override
    protected void drawShape(Graphics2D g, int x, int y) {
        g.setColor(new Color(90, 180, 70));
        g.fillOval(x + 3, y + 6, 8, 8);      // ใบผัก
        g.fillOval(x + 9, y + 3, 8, 8);
        g.fillOval(x + 14, y + 6, 8, 8);
        g.setColor(new Color(220, 60, 50));
        g.fillOval(x + 10, y + 8, 4, 4);     // มะเขือเทศ
        g.setColor(new Color(245, 245, 245));
        g.fillArc(x + 1, y + 4, 22, 20, 180, 180);  // ชาม
        g.setColor(new Color(150, 150, 150));
        g.drawArc(x + 1, y + 4, 22, 20, 180, 180);
    }
}
