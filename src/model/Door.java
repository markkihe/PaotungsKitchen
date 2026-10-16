package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * ประตูไปอีกฉาก: เดินไปกด Space แล้ว GamePanel จะพาเชฟไปฉากปลายทาง
 */
public class Door extends GameObject implements Interactable {

    private final String label;        // ป้ายบนประตู เช่น MARKET
    private final String spriteName;
    private boolean used;              // เพิ่งถูกกด (ให้ GamePanel มาอ่านแล้วล้าง)

    public Door(int x, int y, String label, String spriteName) {
        super(x, y, 52, 72);
        this.label = label;
        this.spriteName = spriteName;
    }

    @Override
    public String interact(Chef chef, GameStats stats) {
        used = true;
        return "Going to the " + label.toLowerCase() + "...";
    }

    /** คืนค่า true ครั้งเดียวหลังถูกกด */
    public boolean consumeUsed() {
        boolean result = used;
        used = false;
        return result;
    }

    /** ประตูไม่มีอะไรเปลี่ยนตามเวลา (แต่ต้องเขียน เพราะเป็น abstract method) */
    @Override
    public void update() {
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage image = Sprites.get(spriteName);
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(new Color(120, 75, 45));
            g.fillRoundRect(x, y, width, height, 10, 10);
            g.setColor(new Color(80, 45, 25));
            g.drawRoundRect(x, y, width, height, 10, 10);
            g.setColor(new Color(240, 200, 60));
            g.fillOval(x + width - 14, y + height / 2, 6, 6);
        }
        // ป้ายชื่อเหนือประตู
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        int textWidth = g.getFontMetrics().stringWidth(label);
        g.setColor(new Color(60, 35, 20, 210));
        g.fillRoundRect(x + (width - textWidth) / 2 - 5, y - 16, textWidth + 10, 15, 6, 6);
        g.setColor(new Color(255, 230, 160));
        g.drawString(label, x + (width - textWidth) / 2, y - 5);
    }
}
