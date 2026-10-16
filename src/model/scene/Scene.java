package model.scene;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.Door;
import model.GameObject;
import model.Interactable;
import model.Sprites;

/**
 * class แม่ของทุกฉากในเกม (ร้านอาหาร ตลาด ...)
 * แต่ละฉากมีพื้นหลัง พื้นที่เดิน ของในฉาก และประตูออกของตัวเอง
 * GamePanel ไม่ต้องรู้ว่าฉากปัจจุบันคืออะไร แค่สั่ง update() / draw()
 */
public abstract class Scene {

    protected static final int SCREEN_WIDTH = 800;
    protected static final int HUD_HEIGHT = 60;
    protected static final int PLAY_HEIGHT = 540;

    private final List<GameObject> objects = new ArrayList<>();          // ทุกอย่างในฉาก (ยกเว้นเชฟ)
    private final List<GameObject> solids = new ArrayList<>();           // เดินทะลุไม่ได้
    private final List<Interactable> interactables = new ArrayList<>();  // กด Space ใส่ได้
    private final Rectangle walkArea;
    private BufferedImage background;   // สร้างครั้งแรกที่วาด แล้วใช้ซ้ำ

    protected Scene(Rectangle walkArea) {
        this.walkArea = walkArea;
    }

    // ---------- ส่วนที่ฉากลูกต้องกำหนดเอง ----------

    /** ชื่อไฟล์รูปพื้นหลังใน assets/images/ */
    protected abstract String getBackgroundSprite();

    /** วาดพื้นหลังด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    protected abstract void drawFallbackBackground(Graphics2D g);

    /** ตำแหน่งที่เชฟยืนตอนเดินเข้าฉากนี้ */
    public abstract Point getEntryPoint();

    /** ประตูสำหรับออกจากฉากนี้ */
    public abstract Door getExitDoor();

    // ---------- ส่วนที่ใช้ร่วมกันทุกฉาก ----------

    /** เพิ่มของเข้าฉาก: solid = เดินชนได้ */
    protected void addObject(GameObject object, boolean solid) {
        objects.add(object);
        if (solid) {
            solids.add(object);
        }
    }

    protected void addInteractable(Interactable interactable) {
        interactables.add(interactable);
    }

    public void update() {
        for (GameObject object : objects) {
            object.update();
        }
    }

    public void draw(Graphics2D g) {
        g.drawImage(getBackground(), 0, 0, null);
        for (GameObject object : objects) {
            object.draw(g);
        }
    }

    /** สร้างพื้นหลังล่วงหน้า (เรียกตอนจัดฉาก ไม่ต้องรอวาดครั้งแรก) */
    public void prepare() {
        getBackground();
    }

    private BufferedImage getBackground() {
        if (background == null) {
            background = new BufferedImage(SCREEN_WIDTH, HUD_HEIGHT + PLAY_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = background.createGraphics();
            BufferedImage sprite = Sprites.get(getBackgroundSprite());
            if (sprite != null) {
                g.drawImage(sprite, 0, HUD_HEIGHT, SCREEN_WIDTH, PLAY_HEIGHT, null);
            } else {
                drawFallbackBackground(g);
            }
            g.dispose();
        }
        return background;
    }

    public Rectangle getWalkArea() {
        return walkArea;
    }

    /** ให้คนอื่นอ่านได้อย่างเดียว แก้ไม่ได้ (Encapsulation) */
    public List<GameObject> getSolids() {
        return Collections.unmodifiableList(solids);
    }

    public List<Interactable> getInteractables() {
        return Collections.unmodifiableList(interactables);
    }
}
