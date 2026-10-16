package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import model.food.Food;

/**
 * ตัวละครที่ผู้เล่นบังคับ เดินได้ ชนของได้ และถืออาหารได้ทีละ 1 จาน
 */
public class Chef extends GameObject {

    private static final Color SKIN = new Color(240, 200, 160);
    private static final Color OUTLINE = new Color(120, 120, 120);
    private static final Color PANTS = new Color(60, 60, 80);

    private static final int SPRINT_BONUS = 2;
    // ขนาดรูปที่วาด (ใหญ่กว่ากรอบชนได้ เพื่อให้เห็นหน้าชัด) วางให้เท้าตรงกับขอบล่างของกรอบชน
    private static final int SPRITE_WIDTH = 56;
    private static final int SPRITE_HEIGHT = 64;
    // ชนของด้วย "เท้า" เท่านั้น (ส่วนล่างของตัว) หัวจึงซ้อนหน้าเตา/ตู้เย็นได้แบบมุมมองจากด้านบน
    private static final int FEET_HEIGHT = 20;
    private static final int FEET_INSET = 4;
    // ชนมุมของไม่เกินกี่พิกเซล ให้ไถลอ้อมมุมเอง ไม่ต้องถอยมาเล็งใหม่
    private static final int CORNER_SLIDE = 10;

    private final int baseSpeed;
    private final Stamina stamina;   // Composition: เชฟเป็นเจ้าของเกจพลังงานของตัวเอง
    private boolean moving;
    private boolean sprinting;
    private boolean facingLeft;
    private int animFrame;   // เฟรมการเดิน 0-3
    private int animTick;    // นับรอบ เพื่อเปลี่ยนเฟรมทุก ๆ 8 รอบ
    private Food heldFood;   // อาหารที่ถืออยู่ (null = มือว่าง)

    public Chef(int x, int y, int baseSpeed) {
        super(x, y, 40, 48); // ส่งตำแหน่งและขนาดให้ constructor ของ GameObject
        this.baseSpeed = baseSpeed;
        this.stamina = new Stamina();
    }

    /**
     * ขยับเชฟ dx, dy มีค่า -1, 0 หรือ 1 (ทิศทาง)
     * ขยับทีละ 1 พิกเซล แยกแกน x กับ y เพื่อให้เดินชิดของได้พอดี และชนด้านหนึ่งแล้วยังไถลไปอีกแกนได้
     * @param wantsSprint ผู้เล่นกด Shift อยู่หรือไม่
     */
    public void move(int dx, int dy, boolean wantsSprint, Rectangle playArea, List<GameObject> solids) {
        moving = (dx != 0 || dy != 0);
        sprinting = stamina.update(wantsSprint && moving);
        int speed = sprinting ? baseSpeed + SPRINT_BONUS : baseSpeed;
        if (dx != 0) {
            facingLeft = dx < 0;
        }

        for (int step = 0; step < speed; step++) {
            if (dx != 0) {
                stepAxis(dx, 0, dy == 0, playArea, solids);
            }
            if (dy != 0) {
                stepAxis(0, dy, dx == 0, playArea, solids);
            }
        }
    }

    /**
     * ขยับ 1 พิกเซล ถ้าชน และเดินแกนเดียวอยู่ ให้ลองหาช่องข้าง ๆ ภายใน CORNER_SLIDE พิกเซล
     * เจอแล้วเบี่ยงเข้าหาช่องนั้นทีละ 1 พิกเซล (เดินเฉียดมุมเตา/ตู้เย็นแล้วไม่ติด)
     */
    private void stepAxis(int dx, int dy, boolean canSlide, Rectangle playArea, List<GameObject> solids) {
        if (tryMove(dx, dy, playArea, solids) || !canSlide) {
            return;
        }
        for (int offset = 1; offset <= CORNER_SLIDE; offset++) {
            for (int side = -1; side <= 1; side += 2) {
                int slideX = (dy != 0) ? side * offset : 0;   // เดินแนวตั้ง -> เบี่ยงซ้าย/ขวา
                int slideY = (dx != 0) ? side * offset : 0;   // เดินแนวนอน -> เบี่ยงขึ้น/ลง
                if (!isBlocked(x + dx + slideX, y + dy + slideY, playArea, solids)) {
                    tryMove(Integer.signum(slideX), Integer.signum(slideY), playArea, solids);
                    return;
                }
            }
        }
    }

    private boolean tryMove(int dx, int dy, Rectangle playArea, List<GameObject> solids) {
        if (isBlocked(x + dx, y + dy, playArea, solids)) {
            return false;
        }
        x += dx;
        y += dy;
        return true;
    }

    /** ถ้าเชฟยืนที่ (newX, newY) จะออกนอกพื้นที่เล่น หรือเท้าทับของแข็งชิ้นใดหรือไม่ */
    private boolean isBlocked(int newX, int newY, Rectangle playArea, List<GameObject> solids) {
        if (!playArea.contains(new Rectangle(newX, newY, width, height))) {
            return true;
        }
        Rectangle feet = new Rectangle(newX + FEET_INSET, newY + height - FEET_HEIGHT,
                width - FEET_INSET * 2, FEET_HEIGHT);
        for (GameObject solid : solids) {
            if (feet.intersects(solid.getBounds())) {
                return true;
            }
        }
        return false;
    }

    // ---------------- ถืออาหาร ----------------

    /** หยิบอาหาร ได้เฉพาะตอนมือว่าง */
    public boolean pickUp(Food food) {
        if (heldFood != null || food == null) {
            return false;
        }
        heldFood = food;
        return true;
    }

    /** ส่งอาหารที่ถืออยู่ออกไป แล้วมือว่าง */
    public Food handOver() {
        Food food = heldFood;
        heldFood = null;
        return food;
    }

    public boolean hasFood() {
        return heldFood != null;
    }

    public Food getHeldFood() {
        return heldFood;
    }

    // ---------------- update / draw ----------------

    @Override
    public void update() {
        if (moving) {
            animTick++;
            int frameEvery = sprinting ? 5 : 8; // วิ่งแล้วขาสลับเร็วขึ้น
            if (animTick % frameEvery == 0) {
                animFrame = (animFrame + 1) % 4;
            }
        } else {
            animFrame = 0;
            animTick = 0;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // เส้นความเร็วตอนวิ่ง
        if (sprinting) {
            g.setColor(new Color(150, 150, 150));
            int backX = facingLeft ? x + width + 2 : x - 14;
            for (int i = 0; i < 3; i++) {
                g.drawLine(backX, y + 20 + i * 8, backX + 10, y + 20 + i * 8);
            }
        }

        // ใช้รูป chef_0..chef_3 ถ้ามี (รูปหันขวา หันซ้ายจะกลับด้านให้) ไม่มีก็วาดรูปทรง
        BufferedImage image = Sprites.get("chef_" + animFrame);
        if (image == null) {
            image = Sprites.get("chef_0");
        }
        int topY = y; // ขอบบนของตัวละครที่มองเห็น (ใช้วางอาหารเหนือหัว)
        if (image != null) {
            int drawX = x + (width - SPRITE_WIDTH) / 2;
            int drawY = y + height - SPRITE_HEIGHT;
            topY = drawY;
            if (facingLeft) {
                g.drawImage(image, drawX + SPRITE_WIDTH, drawY, -SPRITE_WIDTH, SPRITE_HEIGHT, null);
            } else {
                g.drawImage(image, drawX, drawY, SPRITE_WIDTH, SPRITE_HEIGHT, null);
            }
        } else {
            drawShape(g);
        }

        // เกจพลังงานใต้เท้า (แสดงเฉพาะตอนไม่เต็ม)
        if (!stamina.isFull()) {
            g.setColor(new Color(60, 60, 60));
            g.fillRect(x, y + height + 3, width, 4);
            g.setColor(stamina.isExhausted() ? new Color(150, 150, 150) : new Color(60, 150, 230));
            g.fillRect(x, y + height + 3, (int) (width * stamina.getRatio()), 4);
        }

        // อาหารที่ถืออยู่ ลอยเหนือหัว
        if (heldFood != null) {
            g.setColor(Color.WHITE);
            g.fillOval(x + 4, topY - 10, 32, 10);
            g.setColor(OUTLINE);
            g.drawOval(x + 4, topY - 10, 32, 10);
            heldFood.drawIcon(g, x + 8, topY - 30);
        }
    }

    /** วาดเชฟด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    private void drawShape(Graphics2D g) {
        // ขาแกว่งตามเฟรม: 0 = ยืนตรง, 1 = ก้าวหนึ่ง, 2 = ตรง, 3 = ก้าวอีกข้าง
        int step = 0;
        if (animFrame == 1) step = 3;
        if (animFrame == 3) step = -3;

        // ขา
        g.setColor(PANTS);
        g.fillRect(x + 12 + step, y + 41, 6, 7);
        g.fillRect(x + 22 - step, y + 41, 6, 7);

        // ตัว (ชุดเชฟสีขาว)
        g.setColor(Color.WHITE);
        g.fillRoundRect(x + 8, y + 27, 24, 15, 6, 6);
        g.setColor(OUTLINE);
        g.drawRoundRect(x + 8, y + 27, 24, 15, 6, 6);

        // หัว
        g.setColor(SKIN);
        g.fillOval(x + 10, y + 11, 20, 18);

        // ตา หันตามทิศที่เดิน
        g.setColor(Color.BLACK);
        int eyeX = facingLeft ? x + 13 : x + 23;
        g.fillOval(eyeX, y + 18, 4, 4);

        // หมวกเชฟ
        g.setColor(Color.WHITE);
        g.fillOval(x + 8, y, 24, 14);
        g.fillRect(x + 11, y + 8, 18, 6);
        g.setColor(OUTLINE);
        g.drawOval(x + 8, y, 24, 14);
    }
}
