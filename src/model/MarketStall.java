package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.food.Menu;

/**
 * แผงขายวัตถุดิบในตลาด 1 แผงขาย 1 เมนู
 * กด Space = ซื้อ 1 ชุด (หลายที่) ของจะเข้าตู้เย็นในร้านทันที
 * แผงทุกแผงเป็น class เดียวกัน ต่างกันแค่ข้อมูลที่ส่งเข้า constructor
 */
public class MarketStall extends GameObject implements Interactable {

    private final int menuChoice;
    private final int packPrice;
    private final int packSize;
    private final Inventory inventory;
    private final Color awningColor;   // สีหลังคาผ้าใบ (ใช้ตอนไม่มีรูป)

    public MarketStall(int x, int y, int menuChoice, int packPrice, int packSize,
                       Inventory inventory, Color awningColor) {
        super(x, y, 120, 110);
        this.menuChoice = menuChoice;
        this.packPrice = packPrice;
        this.packSize = packSize;
        this.inventory = inventory;
        this.awningColor = awningColor;
    }

    @Override
    public String interact(Chef chef, GameStats stats) {
        String name = Menu.getName(menuChoice);
        if (!stats.payIngredients(packPrice)) {
            return "Not enough cash - " + name + " pack costs $" + packPrice;
        }
        inventory.add(name, packSize);
        return "Bought " + packSize + " " + name + " (-$" + packPrice + ")  Fridge: "
                + inventory.getCount(name);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
        String name = Menu.getName(menuChoice);
        BufferedImage image = Sprites.get("stall_" + name.toLowerCase());
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(new Color(120, 80, 50));
            g.fillRect(x + 6, y + 20, 8, height - 20);
            g.fillRect(x + width - 14, y + 20, 8, height - 20);
            g.setColor(awningColor);
            g.fillRect(x, y, width, 28);
            g.setColor(new Color(170, 120, 75));
            g.fillRect(x, y + 64, width, 40);
        }
        // ป้ายราคา
        String tag = name + "  x" + packSize + "  $" + packPrice;
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        int textWidth = g.getFontMetrics().stringWidth(tag);
        g.setColor(new Color(255, 250, 235));
        g.fillRoundRect(x + (width - textWidth) / 2 - 6, y + height + 4, textWidth + 12, 18, 8, 8);
        g.setColor(new Color(90, 55, 30));
        g.drawRoundRect(x + (width - textWidth) / 2 - 6, y + height + 4, textWidth + 12, 18, 8, 8);
        g.drawString(tag, x + (width - textWidth) / 2, y + height + 17);
    }
}
