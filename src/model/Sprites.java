package model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * โหลดรูป PNG จากโฟลเดอร์ assets/images/ และจำไว้ (โหลดแต่ละรูปครั้งเดียว)
 * ถ้าไม่มีไฟล์ จะคืน null แล้ว object จะวาดด้วยรูปทรงแทน เกมจึงรันได้เสมอ
 */
public final class Sprites {

    private static final String FOLDER = "assets/images/";
    private static final Map<String, BufferedImage> CACHE = new HashMap<>();
    private static final Map<String, Integer> FRAME_COUNTS = new HashMap<>();
    private static long animationClockMs;   // นาฬิกาของแอนิเมชัน เดินเฉพาะตอนเกมเดิน (Pause แล้วหยุด)

    private Sprites() {
        // ห้ามสร้าง object (ใช้แค่ static method)
    }

    /**
     * โหลดรูปทุกไฟล์ในโฟลเดอร์ไว้ตั้งแต่เริ่มเกม
     * กันเกมสะดุดตอนเห็นรูปครั้งแรกกลางเกม (เช่น เข้าตลาดครั้งแรก)
     */
    public static void preloadAll() {
        File[] files = new File(FOLDER).listFiles((dir, fileName) -> fileName.endsWith(".png"));
        if (files == null) {
            return;
        }
        for (File file : files) {
            get(file.getName().substring(0, file.getName().length() - ".png".length()));
        }
    }

    public static BufferedImage get(String name) {
        if (CACHE.containsKey(name)) {
            return CACHE.get(name);
        }
        BufferedImage image = null;
        File file = new File(FOLDER + name + ".png");
        if (file.exists()) {
            try {
                image = ImageIO.read(file);
            } catch (IOException e) {
                System.err.println("Cannot load image " + file + ": " + e.getMessage());
            }
        }
        CACHE.put(name, image); // จำแม้เป็น null จะได้ไม่ต้องหาไฟล์ซ้ำทุกเฟรม
        return image;
    }

    /** เดินนาฬิกาแอนิเมชัน: GamePanel เรียกทุกรอบที่เกมกำลังเล่น, MenuPanel เรียกตอนเปิดเมนู */
    public static void advanceClock(int ms) {
        animationClockMs += ms;
    }

    /**
     * รูปเคลื่อนไหวจากไฟล์ name_0.png, name_1.png, ... สลับเฟรมตามเวลาจริง
     * ถ้าไม่มีไฟล์แบบมีเลขเฟรม จะใช้ name.png รูปเดียว (หรือ null ถ้าไม่มีเลย)
     */
    public static BufferedImage getAnimated(String name, int frameMs) {
        int count = FRAME_COUNTS.computeIfAbsent(name, Sprites::countFrames);
        if (count == 0) {
            return get(name);
        }
        int frame = (int) ((animationClockMs / frameMs) % count);
        return get(name + "_" + frame);
    }

    /** นับว่ามีไฟล์ name_0, name_1, ... ติดกันกี่เฟรม */
    private static int countFrames(String name) {
        int count = 0;
        while (get(name + "_" + count) != null) {
            count++;
        }
        return count;
    }
}
