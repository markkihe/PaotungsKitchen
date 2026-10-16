package model.food;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * วัตถุดิบดิบ 1 ที่ หยิบจากตู้เย็นแล้วเอาไปใส่เตา
 * เป็น Food ชนิดหนึ่ง เชฟจึงถือได้เหมือนอาหาร แต่กินไม่ได้ และ cook() แล้วได้อาหารจริง
 */
public class Ingredient extends Food {

    private final int menuChoice;   // ทำเป็นเมนูไหน (1 Salad, 2 Burger, 3 Pizza)

    public Ingredient(int menuChoice) {
        super("Raw " + Menu.getName(menuChoice), 0, 0);
        this.menuChoice = menuChoice;
    }

    @Override
    public Food cook() {
        return Menu.createFood(menuChoice);
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    @Override
    protected String getSpriteName() {
        return "ingredient_" + Menu.getName(menuChoice).toLowerCase();
    }

    @Override
    protected void drawShape(Graphics2D g, int x, int y) {
        // ลังไม้ใส่วัตถุดิบ สีด้านบนบอกเมนู
        g.setColor(new Color(170, 120, 70));
        g.fillRect(x + 2, y + 8, 20, 14);
        g.setColor(switch (menuChoice) {
            case 1 -> new Color(100, 190, 80);
            case 2 -> new Color(200, 80, 70);
            default -> new Color(245, 210, 90);
        });
        g.fillOval(x + 4, y + 2, 16, 10);
    }
}
