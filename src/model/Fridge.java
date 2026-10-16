package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.IntSupplier;
import model.food.Ingredient;
import model.food.Menu;

/**
 * ตู้เย็น: เก็บวัตถุดิบที่ซื้อมา ยืนใกล้แล้วกด 1-3 เพื่อหยิบวัตถุดิบไปใส่เตา
 */
public class Fridge extends GameObject implements Interactable {

    private static final int OPEN_TICKS = 24;

    private final Inventory inventory;   // ตู้เย็นใช้สต็อกเดียวกับที่ตลาดเติมให้
    private final IntSupplier suggestion; // ถามร้านว่า "ตอนนี้ควรหยิบเมนูไหน" (0 = ไม่มี)
    private int openTimer;

    public Fridge(int x, int y, Inventory inventory, IntSupplier suggestion) {
        super(x, y, 44, 72);
        this.inventory = inventory;
        this.suggestion = suggestion;
    }

    /** กด Space: หยิบวัตถุดิบที่ลูกค้าที่ใกล้หมดความอดทนที่สุดสั่งไว้ให้อัตโนมัติ */
    @Override
    public String interact(Chef chef, GameStats stats) {
        if (chef.hasFood()) {
            return "Your hands are full!";
        }
        int choice = suggestion.getAsInt();
        if (choice == 0) {
            if (inventory.getCount("Salad") + inventory.getCount("Burger") + inventory.getCount("Pizza") == 0) {
                return "Fridge is empty - buy ingredients at the market!";
            }
            return "No order needs these yet (press 1-3 to take one anyway)";
        }
        return takeIngredient(choice, chef);
    }

    /** กด 1-3: หยิบวัตถุดิบดิบของเมนูนั้น 1 ที่ */
    public String takeIngredient(int choice, Chef chef) {
        String name = Menu.getName(choice);
        if (name == null) {
            return null;
        }
        if (chef.hasFood()) {
            return "Your hands are full!";
        }
        if (!inventory.take(name)) {
            return "No " + name + " ingredients - buy more at the market!";
        }
        chef.pickUp(new Ingredient(choice));
        openTimer = OPEN_TICKS;
        return "Took raw " + name + " - put it on a stove";
    }

    @Override
    public void update() {
        if (openTimer > 0) {
            openTimer--;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage image = Sprites.get(openTimer > 0 ? "fridge_open" : "fridge");
        if (image == null) {
            image = Sprites.get("fridge");
        }
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
            return;
        }
        g.setColor(new Color(225, 235, 240));
        g.fillRoundRect(x, y, width, height, 8, 8);
        g.setColor(new Color(120, 130, 140));
        g.drawRoundRect(x, y, width, height, 8, 8);
        g.drawLine(x, y + 26, x + width, y + 26);
        g.fillRect(x + width - 8, y + 8, 3, 12);
        g.fillRect(x + width - 8, y + 34, 3, 18);
    }
}
