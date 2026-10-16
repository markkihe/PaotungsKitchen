package model.customer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.GameObject;
import model.Sprites;
import model.food.Food;

/**
 * class แม่ของลูกค้าทุกชนิด
 * เดินเข้าร้าน -> รอ (ความอดทนลดลง) -> เดินออก (ได้กินแล้ว หรือรอไม่ไหว)
 */
public abstract class Customer extends GameObject {

    private enum Phase { ARRIVING, WAITING, LEAVING }

    private static final int WALK_SPEED = 2;
    private static final int EXIT_X = 820;
    private static final Color SKIN = new Color(240, 200, 160);

    private final Food order;
    private final int maxPatienceMs;
    private final int seatX;
    private int patienceMs;
    private Phase phase;
    private boolean happy;
    private boolean walkedOut;   // รอไม่ไหวแล้ว แต่ GamePanel ยังไม่ได้หักดาว
    private boolean gone;        // เดินพ้นจอแล้ว
    private int animTick;

    public Customer(int startX, int seatX, int seatY, Food order, int maxPatienceMs) {
        super(startX, seatY, 34, 46);
        this.seatX = seatX;
        this.order = order;
        this.maxPatienceMs = maxPatienceMs;
        this.patienceMs = maxPatienceMs;
        this.phase = Phase.ARRIVING;
    }

    // ---------- ส่วนที่ลูกแต่ละชนิดต้องเขียนเอง (Polymorphism) ----------

    /** ลูกค้าจ่ายเงินเท่าไรเมื่อได้อาหารถูก */
    public abstract int calculatePayment();

    /** สีเสื้อ ใช้แยกชนิดลูกค้าด้วยตา */
    protected abstract Color getColor();

    /** ชื่อไฟล์รูปของลูกค้าชนิดนี้ใน assets/images/ */
    protected abstract String getSpriteName();

    /** วาดของประดับเพิ่ม (ค่าเริ่มต้น: ไม่มี) ลูกที่อยากมีให้ override */
    protected void drawAccessory(Graphics2D g) {
    }

    // ---------- ส่วนที่ใช้ร่วมกัน ----------

    public Food getOrder() {
        return order;
    }

    /** เวลาที่ยังรอได้ (มิลลิวินาที) */
    public int getPatienceLeftMs() {
        return patienceMs;
    }

    public double getPatienceRatio() {
        return (double) patienceMs / maxPatienceMs;
    }

    public boolean isWaiting() {
        return phase == Phase.WAITING;
    }

    /** กำลังเดินออกเพราะรอไม่ไหว */
    protected boolean isLeavingAngry() {
        return phase == Phase.LEAVING && !happy;
    }

    public boolean wants(Food food) {
        return isWaiting() && order.isSameDish(food);
    }

    /** ได้อาหารแล้ว เดินออกอย่างมีความสุข */
    public void serve() {
        if (isWaiting()) {
            phase = Phase.LEAVING;
            happy = true;
        }
    }

    /** คืนค่า true ครั้งเดียวตอนลูกค้ารอไม่ไหว (เพื่อหักดาวครั้งเดียว) */
    public boolean consumeWalkout() {
        boolean result = walkedOut;
        walkedOut = false;
        return result;
    }

    public boolean hasLeft() {
        return gone;
    }

    @Override
    public void update() {
        animTick++;
        switch (phase) {
            case ARRIVING -> {
                x -= WALK_SPEED;
                if (x <= seatX) {
                    x = seatX;
                    phase = Phase.WAITING;
                }
            }
            case WAITING -> {
                patienceMs -= TICK_MS;
                if (patienceMs <= 0) {
                    patienceMs = 0;
                    phase = Phase.LEAVING;
                    happy = false;
                    walkedOut = true;
                }
            }
            case LEAVING -> {
                x += WALK_SPEED * 2;
                if (x >= EXIT_X) {
                    gone = true;
                }
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // ตัวเด้งขึ้นลงตอนเดิน
        int bob = (phase != Phase.WAITING) ? ((animTick / 6) % 2) * 2 : 0;
        boolean angry = phase == Phase.LEAVING && !happy;

        BufferedImage image = Sprites.getAnimated(getSpriteName(), 250);
        if (image != null) {
            g.drawImage(image, x, y + bob, width, height, null);
            if (angry) {
                g.setColor(new Color(220, 40, 40));
                g.setFont(new Font("SansSerif", Font.BOLD, 20));
                g.drawString("!", x + width - 6, y + 6);
            }
        } else {
            drawShape(g, bob, angry);
        }

        if (phase == Phase.WAITING) {
            drawOrderBubble(g);
        }
    }

    /** วาดตัวลูกค้าด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    private void drawShape(Graphics2D g, int bob, boolean angry) {
        // ขา
        g.setColor(new Color(60, 60, 80));
        g.fillRect(x + 9, y + 40, 6, 6);
        g.fillRect(x + 19, y + 40, 6, 6);

        // ตัว (สีตามชนิดลูกค้า)
        g.setColor(getColor());
        g.fillRoundRect(x + 5, y + 20 + bob, 24, 22, 8, 8);

        // หัว: หน้าแดงตอนโกรธ
        g.setColor(angry ? new Color(235, 120, 110) : SKIN);
        g.fillOval(x + 7, y + 2 + bob, 20, 20);

        // ตาและปาก
        g.setColor(Color.BLACK);
        g.fillOval(x + 11, y + 9 + bob, 3, 3);
        g.fillOval(x + 20, y + 9 + bob, 3, 3);
        if (phase == Phase.LEAVING) {
            if (happy) {
                g.drawArc(x + 12, y + 11 + bob, 10, 6, 180, 180); // ยิ้ม
            } else {
                g.drawArc(x + 12, y + 15 + bob, 10, 6, 0, 180);   // บึ้ง
            }
        }

        drawAccessory(g);
    }

    /** ฟองคำพูดบอกเมนู + แถบความอดทน */
    private void drawOrderBubble(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.fillRoundRect(x - 2, y - 42, 38, 30, 10, 10);
        g.setColor(new Color(150, 150, 150));
        g.drawRoundRect(x - 2, y - 42, 38, 30, 10, 10);
        order.drawIcon(g, x + 5, y - 39);

        double ratio = getPatienceRatio();
        g.setColor(new Color(80, 80, 80));
        g.fillRect(x, y - 10, 34, 5);
        if (ratio > 0.5) {
            g.setColor(new Color(70, 180, 70));
        } else if (ratio > 0.25) {
            g.setColor(new Color(240, 170, 40));
        } else {
            g.setColor(new Color(220, 60, 50));
        }
        g.fillRect(x, y - 10, (int) (34 * ratio), 5);
    }
}
