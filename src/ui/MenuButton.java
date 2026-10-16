package ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 * ปุ่มสไตล์เกม: สีสด มีเงาด้านล่าง สว่างขึ้นตอนเอาเมาส์ชี้ และยุบลงตอนกด
 * สืบทอดจาก JButton แล้ว override paintComponent() เพื่อวาดหน้าตาเอง
 * (การทำงานของปุ่ม เช่น addActionListener ได้มาจาก JButton ทั้งหมด)
 */
public class MenuButton extends JButton {

    private static final int SHADOW = 5;   // ความหนาของเงาด้านล่าง
    private final Color baseColor;

    public MenuButton(String text, Color baseColor) {
        this(text, baseColor, 22);
    }

    /** constructor แบบกำหนดขนาดตัวอักษรเอง (ใช้กับปุ่มเล็กบน HUD) */
    public MenuButton(String text, Color baseColor, int fontSize) {
        super(text);
        this.baseColor = baseColor;
        setFont(new Font("SansSerif", Font.BOLD, fontSize));
        setContentAreaFilled(false);   // ไม่ให้ Swing วาดพื้นปุ่มแบบเดิม
        setBorderPainted(false);
        setFocusPainted(false);
        setFocusable(false);           // ไม่แย่ง focus คีย์บอร์ดจากหน้าเกม
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        boolean pressed = getModel().isPressed();
        boolean hover = getModel().isRollover() && isEnabled();
        Color base = isEnabled() ? baseColor : new Color(140, 130, 125); // กดไม่ได้ = สีเทา
        int w = getWidth();
        int h = getHeight() - SHADOW;
        int offset = pressed ? SHADOW - 1 : 0;   // กดแล้วปุ่มยุบลงไปทับเงา

        // เงาด้านล่าง
        g2.setColor(base.darker().darker());
        g2.fillRoundRect(0, SHADOW, w, h, 18, 18);

        // ตัวปุ่ม
        Color face = hover ? brighten(base) : base;
        g2.setColor(face);
        g2.fillRoundRect(0, offset, w, h, 18, 18);
        g2.setColor(new Color(255, 255, 255, 70));          // แสงสะท้อนครึ่งบน
        g2.fillRoundRect(4, offset + 3, w - 8, h / 2 - 2, 14, 14);
        g2.setColor(base.darker().darker());
        g2.drawRoundRect(0, offset, w - 1, h - 1, 18, 18);

        // ตัวหนังสือ: ขอบเข้ม + ตัวขาว
        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int tx = (w - fm.stringWidth(getText())) / 2;
        int ty = offset + (h + fm.getAscent() - fm.getDescent()) / 2;
        g2.setColor(new Color(60, 30, 20));
        int outline = getFont().getSize() >= 18 ? 2 : 1;
        for (int dx = -outline; dx <= outline; dx++) {
            for (int dy = -outline; dy <= outline; dy++) {
                g2.drawString(getText(), tx + dx, ty + dy);
            }
        }
        g2.setColor(isEnabled() ? Color.WHITE : new Color(210, 205, 200));
        g2.drawString(getText(), tx, ty);

        g2.dispose();
    }

    private static Color brighten(Color c) {
        return new Color(Math.min(255, c.getRed() + 30), Math.min(255, c.getGreen() + 30),
                Math.min(255, c.getBlue() + 30));
    }
}
