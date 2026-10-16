package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GraphicsEnvironment;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import javax.swing.Timer;
import logic.HighScoreManager;
import model.Sprites;

/**
 * หน้าเมนูหลัก: ชื่อเกม ตัวละครยืนเด้ง ๆ และปุ่ม Start / How to Play / Exit
 */
public class MenuPanel extends JPanel {

    private static final int GROUND_Y = 380;   // ระดับเท้าของตัวละครที่ยืนโชว์
    private static final String GAME_TITLE = "Paotung's Kitchen";
    private static final String THAI_TITLE = "เป๋าตุงพุงกาง";
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 64);
    private static final Font THAI_TITLE_FONT = findThaiFont(Font.BOLD, 30);
    private static final Font SUBTITLE_FONT = new Font("SansSerif", Font.BOLD, 20);
    private static final Font HELP_TITLE_FONT = new Font("SansSerif", Font.BOLD, 24);
    private static final Font HELP_FONT = new Font("SansSerif", Font.PLAIN, 16);

    private static final String[] HELP_LINES = {
        "Morning: buy ingredient packs at the MARKET, then enter the RESTAURANT",
        "Fridge: SPACE grabs what the most impatient customer ordered",
        "SPACE does everything: cook, pick up, serve, trash, doors, buy",
        "Pick up food before it burns!  Out of stock? Run back to the market",
        "(customers keep waiting while you're away)",
        "Pay rent at the end of each day, then buy ONE upgrade.",
        "Lose all 5 stars or go bankrupt = game over.  5 good serves in a row = +1 star"
    };

    private final HighScoreManager highScores;
    private final MenuButton startButton;
    private final MenuButton helpButton;
    private final MenuButton exitButton;
    private final Timer animationTimer;     // ให้ตัวละครในเมนูขยับ
    private boolean showingHelp;
    private int tick;

    public MenuPanel(GameFrame frame, HighScoreManager highScores) {
        this.highScores = highScores;
        setPreferredSize(new Dimension(GamePanel.SCREEN_WIDTH, GamePanel.SCREEN_HEIGHT));
        setLayout(null); // จัดตำแหน่งปุ่มเองด้วย setBounds
        setBackground(new Color(70, 40, 30));

        startButton = new MenuButton("START", new Color(90, 175, 70));
        helpButton = new MenuButton("HOW TO PLAY", new Color(235, 150, 50));
        exitButton = new MenuButton("EXIT", new Color(205, 70, 60));
        startButton.setBounds(290, 410, 220, 54);
        helpButton.setBounds(290, 472, 220, 50);
        exitButton.setBounds(290, 530, 220, 50);

        // lambda = การ implement interface ActionListener แบบย่อ
        startButton.addActionListener(e -> frame.showGame());
        helpButton.addActionListener(e -> toggleHelp());
        exitButton.addActionListener(e -> System.exit(0));

        add(startButton);
        add(helpButton);
        add(exitButton);

        animationTimer = new Timer(40, e -> {
            tick++;
            Sprites.advanceClock(40);
            repaint();
        });
    }

    /** เริ่ม/หยุดแอนิเมชันตอนเปิด/ปิดหน้าเมนู (ไม่เปลือง CPU ตอนเล่นเกม) */
    public void startAnimation() {
        animationTimer.start();
    }

    public void stopAnimation() {
        animationTimer.stop();
        showingHelp = false;
        helpButton.setText("HOW TO PLAY");
    }

    private void toggleHelp() {
        showingHelp = !showingHelp;
        helpButton.setText(showingHelp ? "BACK" : "HOW TO PLAY");
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // ล้างหน้าจอเก่าก่อนวาดใหม่
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawBackground(g2);
        drawTitle(g2);
        if (showingHelp) {
            drawHelp(g2);
        } else {
            drawCharacters(g2);
        }
    }

    /** ใช้ menu_bg.png ถ้ามี ไม่มีก็ใช้ฉากร้าน floor.png แล้วทำให้มืดลงเล็กน้อย */
    private void drawBackground(Graphics2D g2) {
        BufferedImage background = Sprites.get("menu_bg");
        if (background == null) {
            background = Sprites.get("floor");
        }
        if (background != null) {
            g2.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        }
        g2.setColor(new Color(30, 15, 10, 110));
        g2.fillRect(0, 0, getWidth(), getHeight());
    }

    private void drawTitle(Graphics2D g2) {
        int bob = (int) Math.round(Math.sin(tick * 0.08) * 4); // ชื่อเกมลอยขึ้นลงเบา ๆ
        g2.setFont(TITLE_FONT);
        String title = GAME_TITLE;
        int x = centerX(g2, title);
        int y = 104 + bob;

        g2.setColor(new Color(0, 0, 0, 110));                 // เงา
        g2.drawString(title, x + 5, y + 6);
        g2.setColor(new Color(80, 35, 20));                   // ขอบหนา
        for (int dx = -4; dx <= 4; dx += 2) {
            for (int dy = -4; dy <= 4; dy += 2) {
                g2.drawString(title, x + dx, y + dy);
            }
        }
        g2.setColor(new Color(255, 205, 80));                 // ตัวอักษร
        g2.drawString(title, x, y);

        // ชื่อภาษาไทยใต้ชื่ออังกฤษ
        g2.setFont(THAI_TITLE_FONT);
        int thaiX = centerX(g2, THAI_TITLE);
        int thaiY = 146 + bob;
        g2.setColor(new Color(80, 35, 20));
        for (int dx = -2; dx <= 2; dx += 2) {
            for (int dy = -2; dy <= 2; dy += 2) {
                g2.drawString(THAI_TITLE, thaiX + dx, thaiY + dy);
            }
        }
        g2.setColor(new Color(255, 240, 215));
        g2.drawString(THAI_TITLE, thaiX, thaiY);

        g2.setFont(SUBTITLE_FONT);
        String subtitle = "Cook fast. Serve faster. Don't let anything burn!";
        g2.setColor(new Color(60, 30, 20));
        g2.drawString(subtitle, centerX(g2, subtitle) + 2, 182);
        g2.setColor(new Color(255, 240, 215));
        g2.drawString(subtitle, centerX(g2, subtitle), 180);

        if (highScores != null && highScores.getBestScore() > 0) {
            String best = "BEST  " + highScores.getBestScore() + " pts   |   Day " + highScores.getBestDay();
            int w = g2.getFontMetrics().stringWidth(best) + 30;
            g2.setColor(new Color(40, 20, 10, 170));
            g2.fillRoundRect((getWidth() - w) / 2, 192, w, 28, 14, 14);
            g2.setColor(new Color(255, 215, 90));
            g2.drawString(best, centerX(g2, best), 212);
        }
    }

    /** ตัวละครยืนเรียงแถว เด้งขึ้นลงคนละจังหวะ และอาหารลอยสองข้าง */
    private void drawCharacters(Graphics2D g2) {
        drawSprite(g2, Sprites.getAnimated("customer_impatient", 250), 225, 51, 69, 0);
        drawSprite(g2, Sprites.get("customer_normal"), 312, 51, 69, 1);
        drawSprite(g2, Sprites.get("chef_0"), 400, 84, 96, 2);
        drawSprite(g2, Sprites.get("customer_vip"), 488, 51, 69, 3);

        drawFloating(g2, Sprites.getAnimated("food_burger", 120), 120, 290, 0);
        drawFloating(g2, Sprites.getAnimated("food_pizza", 120), 632, 290, 2);
        drawFloating(g2, Sprites.get("food_salad"), 600, 220, 4);
    }

    /** วาดตัวละครให้เท้าแตะพื้นที่ GROUND_Y และเด้งตามจังหวะของตัวเอง */
    private void drawSprite(Graphics2D g2, BufferedImage image, int centerX, int w, int h, int phase) {
        if (image == null) {
            return;
        }
        int hop = (int) Math.round(Math.abs(Math.sin(tick * 0.12 + phase * 0.9)) * 8);
        g2.setColor(new Color(0, 0, 0, 60));   // เงาที่พื้น หดลงตอนลอยสูง
        int shadowW = w - hop * 2;
        g2.fillOval(centerX - shadowW / 2, GROUND_Y - 5, shadowW, 10);
        g2.drawImage(image, centerX - w / 2, GROUND_Y - h - hop, w, h, null);
    }

    private void drawFloating(Graphics2D g2, BufferedImage image, int x, int y, int phase) {
        if (image == null) {
            return;
        }
        int bob = (int) Math.round(Math.sin(tick * 0.07 + phase) * 6);
        g2.drawImage(image, x, y + bob, 48, 48, null);
    }

    private void drawHelp(Graphics2D g2) {
        int boxX = 70;
        int boxY = 190;
        int boxW = 660;
        int boxH = 205;
        g2.setColor(new Color(255, 245, 225, 235));
        g2.fillRoundRect(boxX, boxY, boxW, boxH, 20, 20);
        g2.setColor(new Color(120, 70, 40));
        g2.drawRoundRect(boxX, boxY, boxW, boxH, 20, 20);

        g2.setFont(HELP_TITLE_FONT);
        g2.drawString("How to play", centerX(g2, "How to play"), boxY + 32);

        g2.setFont(HELP_FONT);
        g2.setColor(new Color(70, 45, 30));
        int y = boxY + 60;
        for (String line : HELP_LINES) {
            g2.drawString(line, centerX(g2, line), y);
            y += 21;
        }
    }

    /**
     * หาฟอนต์ในเครื่องที่แสดงภาษาไทยได้ (Windows มี Leelawadee UI / Tahoma)
     * ถ้าไม่เจอเลย ใช้ SansSerif ซึ่ง Java จะพยายามหาฟอนต์ไทยมาแทนเอง
     */
    private static Font findThaiFont(int style, int size) {
        String[] candidates = {"Leelawadee UI", "Leelawadee", "Tahoma", "Sarabun", "Loma", "Garuda"};
        java.util.Set<String> installed = new java.util.HashSet<>(java.util.Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
        for (String name : candidates) {
            if (installed.contains(name)) {
                Font font = new Font(name, style, size);
                if (font.canDisplayUpTo(THAI_TITLE) == -1) {
                    return font;
                }
            }
        }
        return new Font("SansSerif", style, size);
    }

    private int centerX(Graphics2D g2, String text) {
        FontMetrics fm = g2.getFontMetrics();
        return (getWidth() - fm.stringWidth(text)) / 2;
    }
}
