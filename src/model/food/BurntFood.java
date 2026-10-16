package model.food;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * อาหารที่ไหม้แล้ว: กินไม่ได้ เสิร์ฟไปจะเสียดาว ต้องเอาไปทิ้งถังขยะ
 */
public class BurntFood extends Food {

    private final String spriteName;   // จำว่าไหม้มาจากเมนูไหน เช่น food_burnt_pizza

    public BurntFood(Food original) {
        super("Burnt " + original.getName(), 0, 0);
        this.spriteName = "food_burnt_" + original.getName().toLowerCase();
    }

    @Override
    public boolean isEdible() {
        return false;
    }

    /** รูปเมนูเดิมแบบไหม้ เช่น พิซซ่าไหม้ยังเป็นทรงพิซซ่า */
    @Override
    protected String getSpriteName() {
        return spriteName;
    }

    /** ไม่มีรูปไหม้ของเมนูนั้น ใช้รูปไหม้แบบรวม food_burnt.png */
    @Override
    protected String getFallbackSpriteName() {
        return "food_burnt";
    }

    @Override
    protected void drawShape(Graphics2D g, int x, int y) {
        g.setColor(new Color(50, 35, 25));
        g.fillOval(x + 2, y + 8, 20, 14);
        g.setColor(new Color(25, 20, 15));
        g.fillOval(x + 6, y + 11, 6, 5);
        g.fillOval(x + 13, y + 13, 5, 4);
        g.setColor(new Color(150, 150, 150, 180));   // ควัน
        g.fillOval(x + 6, y, 7, 7);
        g.fillOval(x + 12, y - 4, 6, 6);
    }
}
