package model.food;

import java.awt.Color;
import java.awt.Graphics2D;

public class Burger extends Food {

    public Burger() {
        super("Burger", 3000, 50);
    }

    @Override
    protected void drawShape(Graphics2D g, int x, int y) {
        g.setColor(new Color(210, 150, 70));
        g.fillArc(x + 1, y + 2, 22, 14, 0, 180);      // ขนมปังบน
        g.setColor(new Color(80, 170, 60));
        g.fillRect(x + 2, y + 9, 20, 3);              // ผักกาด
        g.setColor(new Color(110, 60, 30));
        g.fillRoundRect(x + 2, y + 12, 20, 5, 4, 4);  // เนื้อ
        g.setColor(new Color(210, 150, 70));
        g.fillRoundRect(x + 2, y + 17, 20, 5, 4, 4);  // ขนมปังล่าง
    }
}
