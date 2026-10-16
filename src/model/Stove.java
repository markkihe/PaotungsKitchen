package model;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.food.BurntFood;
import model.food.Food;

/**
 * เตาทำอาหาร มี 4 สถานะ: ว่าง -> กำลังทำ -> สุกพร้อมหยิบ -> ไหม้
 * ถ้าสุกแล้วไม่รีบมาหยิบภายในเวลาที่กำหนด อาหารจะไหม้
 */
public class Stove extends GameObject implements Interactable {

    private enum Phase { EMPTY, COOKING, READY, BURNT }

    private static final Font LABEL_FONT = new Font("SansSerif", Font.BOLD, 14);

    private Phase phase = Phase.EMPTY;
    private Food dish;          // อาหารบนเตา
    private int cookTimeMs;     // เวลาทำของจานนี้ (หลังคิดอัปเกรดแล้ว)
    private int burnDelayMs = 7000;      // สุกแล้วรอได้นานเท่าไรก่อนไหม้
    private double cookMultiplier = 1.0; // ตัวคูณเวลาทำ (จากอัปเกรด Fast Stove)
    private int timerMs;        // เวลาที่ผ่านไปในสถานะปัจจุบัน
    private boolean locked;     // ยังไม่ได้ซื้อ
    private int animTick;
    private boolean justReady;  // เพิ่งสุกในรอบนี้ (ให้ GamePanel เล่นเสียง)
    private boolean justBurnt;  // เพิ่งไหม้ในรอบนี้

    public Stove(int x, int y) {
        super(x, y, 64, 72);
    }

    public void lock() {
        locked = true;
    }

    public boolean isLocked() {
        return locked;
    }

    public boolean isBusy() {
        return phase != Phase.EMPTY;
    }

    public boolean isReady() {
        return phase == Phase.READY;
    }

    public boolean isBurnt() {
        return phase == Phase.BURNT;
    }

    /** ชื่ออาหารที่กำลังทำหรือสุกรออยู่ (null ถ้าเตาว่างหรือไหม้) */
    public String getDishInProgress() {
        return (phase == Phase.COOKING || phase == Phase.READY) ? dish.getName() : null;
    }

    /**
     * ตั้งค่าเตาประจำวัน
     * @param cookMultiplier ตัวคูณเวลาทำ (จากอัปเกรด Fast Stove)
     * @param burnDelayMs เวลาก่อนไหม้ (จาก DayManager)
     */
    public void configure(double cookMultiplier, int burnDelayMs) {
        this.cookMultiplier = cookMultiplier;
        this.burnDelayMs = burnDelayMs;
    }

    /** เริ่มทำอาหาร ได้เฉพาะเตาที่ปลดล็อกแล้วและว่างอยู่ */
    public boolean startCooking(Food food) {
        if (locked || isBusy() || food == null) {
            return false;
        }
        dish = food;
        cookTimeMs = Math.max(500, (int) (food.getCookTime() * cookMultiplier));
        timerMs = 0;
        phase = Phase.COOKING;
        return true;
    }

    /** ความคืบหน้าการทำ 0.0 - 1.0 */
    public double getCookProgress() {
        return phase == Phase.COOKING ? Math.min(1.0, (double) timerMs / cookTimeMs) : 0;
    }

    /** เวลาที่เหลือก่อนไหม้ 1.0 (เพิ่งสุก) -> 0.0 (กำลังจะไหม้) */
    public double getFreshness() {
        return phase == Phase.READY ? 1.0 - (double) timerMs / burnDelayMs : 0;
    }

    /** คืนค่า true ครั้งเดียวตอนอาหารเพิ่งสุก */
    public boolean consumeJustReady() {
        boolean result = justReady;
        justReady = false;
        return result;
    }

    /** คืนค่า true ครั้งเดียวตอนอาหารเพิ่งไหม้ */
    public boolean consumeJustBurnt() {
        boolean result = justBurnt;
        justBurnt = false;
        return result;
    }

    public void clear() {
        phase = Phase.EMPTY;
        dish = null;
        timerMs = 0;
    }

    @Override
    public void update() {
        animTick++;
        switch (phase) {
            case COOKING -> {
                timerMs += TICK_MS;
                if (timerMs >= cookTimeMs) {
                    phase = Phase.READY;
                    timerMs = 0;
                    justReady = true;
                }
            }
            case READY -> {
                timerMs += TICK_MS;
                if (timerMs >= burnDelayMs) {
                    dish = new BurntFood(dish);  // อาหารกลายเป็นของไหม้
                    phase = Phase.BURNT;
                    justBurnt = true;
                }
            }
            default -> { }
        }
    }

    @Override
    public String interact(Chef chef, GameStats stats) {
        if (locked) {
            return "Locked - buy Second Stove in the shop";
        }
        switch (phase) {
            case EMPTY:
                return loadIngredient(chef);
            case COOKING:
                return dish.getName() + " is not ready yet";
            default:
                break;
        }
        // READY หรือ BURNT: หยิบขึ้นมา
        if (chef.hasFood()) {
            return "Your hands are full!";
        }
        boolean burnt = isBurnt();
        String name = dish.getName();
        chef.pickUp(dish);
        clear();
        return burnt ? "Burnt! Throw it in the trash" : "Picked up " + name;
    }

    /** รับวัตถุดิบจากมือเชฟมาทำ: cook() บอกว่าของในมือทำเป็นเมนูอะไรได้ */
    private String loadIngredient(Chef chef) {
        Food held = chef.getHeldFood();
        Food dish = (held == null) ? null : held.cook();
        if (dish == null) {
            return "Bring raw ingredients from the fridge";
        }
        chef.handOver();
        startCooking(dish);
        return "Cooking " + dish.getName() + "...";
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage image = Sprites.get(getSpriteName());
        if (image == null) {
            image = Sprites.get("stove"); // ไม่มีรูปของสถานะนี้ ใช้รูปเตาปกติแทน
        }
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            drawShape(g);
        }

        if (locked) {
            g.setColor(new Color(200, 200, 205, 190));
            g.fillRoundRect(x, y, width, height, 10, 10);
            g.setColor(new Color(90, 90, 95));
            g.setFont(LABEL_FONT);
            drawCenteredText(g, "LOCKED", y + height / 2 + 5);
            return;
        }

        if (dish == null) {
            return;
        }
        dish.drawIcon(g, x + 20, y + 27); // อาหารอยู่ในช่องหน้าต่างเตา
        drawStatus(g);
    }

    /** เลือกรูปตามสถานะ: เตาว่าง / กำลังอบ (2 เฟรมสลับให้ไฟกระพริบ) / สุก / ไหม้ */
    private String getSpriteName() {
        return switch (phase) {
            case COOKING -> "stove_cooking_" + ((animTick / 12) % 2);
            case READY -> "stove_ready";
            case BURNT -> "stove_burnt";
            default -> "stove";
        };
    }

    private void drawCenteredText(Graphics2D g, String text, int baselineY) {
        int textWidth = g.getFontMetrics().stringWidth(text);
        g.drawString(text, x + (width - textWidth) / 2, baselineY);
    }

    /** วาดเตาด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    private void drawShape(Graphics2D g) {
        g.setColor(new Color(90, 90, 95));
        g.fillRoundRect(x, y, width, height, 10, 10);

        // หัวเตา: แดงตอนทำ, ส้มกะพริบตอนสุก, ดำตอนว่างหรือไหม้
        Color burner = new Color(40, 40, 40);
        if (phase == Phase.COOKING) {
            burner = new Color(210, 70, 40);
        } else if (phase == Phase.READY) {
            burner = (animTick / 10) % 2 == 0 ? new Color(240, 140, 40) : new Color(210, 70, 40);
        }
        g.setColor(burner);
        g.fillOval(x + 6, y + 10, 24, 24);
        g.fillOval(x + 34, y + 10, 24, 24);

        // ปุ่มหมุน
        g.setColor(new Color(200, 200, 200));
        for (int i = 0; i < 4; i++) {
            g.fillOval(x + 8 + i * 13, y + 58, 8, 8);
        }
    }

    /** แถบเวลาและข้อความสถานะใต้เตา */
    private void drawStatus(Graphics2D g) {
        g.setFont(LABEL_FONT);
        int barY = y + height + 6;
        switch (phase) {
            case COOKING -> {
                g.setColor(new Color(60, 60, 60));
                g.fillRect(x, barY, width, 8);
                g.setColor(new Color(240, 170, 40));
                g.fillRect(x, barY, (int) (width * getCookProgress()), 8);
            }
            case READY -> {
                double fresh = getFreshness();
                // แถบเวลาก่อนไหม้: เขียว -> แดง
                g.setColor(new Color(60, 60, 60));
                g.fillRect(x, barY, width, 8);
                g.setColor(fresh > 0.35 ? new Color(70, 180, 70) : new Color(220, 60, 50));
                g.fillRect(x, barY, (int) (width * fresh), 8);

                boolean hurry = fresh <= 0.35;
                if (!hurry || (animTick / 8) % 2 == 0) {
                    g.setColor(hurry ? new Color(220, 50, 40) : new Color(40, 160, 60));
                    drawCenteredText(g, hurry ? "HURRY!" : "READY!", barY + 24);
                }
            }
            case BURNT -> {
                // ควันลอยขึ้น
                g.setColor(new Color(120, 120, 120, 150));
                for (int i = 0; i < 3; i++) {
                    int rise = (animTick + i * 20) % 60;
                    g.fillOval(x + 14 + i * 13, y - rise / 2, 12, 12);
                }
                g.setColor(new Color(60, 40, 30));
                drawCenteredText(g, "BURNT!", barY + 12);
            }
            default -> { }
        }
    }
}
