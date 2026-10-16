package ui;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

/**
 * โหลดและเล่นเสียงเอฟเฟกต์จากโฟลเดอร์ assets/sounds/ (ไฟล์ .wav)
 * ใช้ Clip ของ Java ล้วน ไม่ต้องลง library เพิ่ม
 * ถ้าโหลดไม่ได้ (ไม่มีไฟล์ หรือเครื่องไม่มีลำโพง) เกมยังเล่นได้ตามปกติ แค่ไม่มีเสียง
 */
public class SoundManager {

    /**
     * เสียงทั้งหมดในเกม แต่ละตัวผูกกับชื่อไฟล์และความดัง
     * ความดัง: 1.0 = ดังเท่าไฟล์จริง, 0.5 = เบาลงประมาณครึ่ง
     */
    public enum Sound {
        SERVE("serve", 0.7),
        STAR("star", 0.7),       // ได้ดาวคืนจาก service streak
        READY("ready", 0.6),
        BURNT("burnt", 0.4),
        PICKUP("pickup", 0.5),
        TRASH("trash", 0.4),
        COOK("cook", 0.3),
        FAIL("fail", 0.4),
        BUY("buy", 0.6),
        ERROR("error", 0.25),
        DOORBELL("doorbell", 0.4),
        DAY_END("dayend", 0.7),
        GAME_OVER("gameover", 0.7);

        private final String fileName;
        private final double volume;

        Sound(String fileName, double volume) {
            this.fileName = fileName;
            this.volume = volume;
        }
    }

    private static final String FOLDER = "assets/sounds/";

    /** ความดังรวมของทั้งเกม: ทุกเสียงถูกคูณด้วยค่านี้ (แก้ที่เดียว เบา/ดังทั้งเกม) */
    private static final double MASTER_VOLUME = 0.5;

    /** เพลงพื้นหลัง (เล่นวนตลอด) และความดัง: ตั้งให้เบากว่าเสียงเอฟเฟกต์ */
    private static final String MUSIC_FILE = "music";
    private static final double MUSIC_VOLUME = 0.25;

    private final Map<Sound, Clip> clips = new EnumMap<>(Sound.class);
    private boolean muted;
    private boolean warned;   // แจ้งเตือนปัญหาเสียงแค่ครั้งเดียว
    private final Clip music;

    public SoundManager() {
        for (Sound sound : Sound.values()) {
            Clip clip = loadClip(sound.fileName, sound.volume * MASTER_VOLUME);
            if (clip != null) {
                clips.put(sound, clip);
            }
        }
        music = loadClip(MUSIC_FILE, MUSIC_VOLUME * MASTER_VOLUME);
    }

    /** โหลดไฟล์ .wav เป็น Clip พร้อมตั้งความดัง คืน null ถ้าโหลดไม่ได้ */
    private Clip loadClip(String fileName, double volume) {
        File file = new File(FOLDER + fileName + ".wav");
        if (!file.exists()) {
            return null; // ไม่มีไฟล์ = ไม่มีเสียงนี้ (ลบไฟล์ทิ้งเพื่อปิดเสียงที่ไม่ชอบได้)
        }
        try (AudioInputStream input = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(input);
            setVolume(clip, volume);
            return clip;
        } catch (Exception e) {
            if (!warned) {
                System.err.println("Sound disabled (" + file + "): " + e.getMessage());
                warned = true;
            }
            return null;
        }
    }

    /** เริ่มเพลงพื้นหลัง วนไม่มีที่สิ้นสุด */
    public void startMusic() {
        if (music == null || muted || music.isRunning()) {
            return;
        }
        music.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void stopMusic() {
        if (music != null) {
            music.stop();
        }
    }

    /** ตั้งความดัง: 1.0 = เท่าเดิม, 0.5 = ครึ่งหนึ่ง (แปลงเป็นเดซิเบลให้ Java) */
    private void setVolume(Clip clip, double volume) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return; // บางเครื่องปรับเสียงไม่ได้ ก็เล่นที่ความดังเดิม
        }
        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float decibels = (float) (20.0 * Math.log10(Math.max(volume, 0.0001)));
        decibels = Math.max(gain.getMinimum(), Math.min(decibels, gain.getMaximum()));
        gain.setValue(decibels);
    }

    /** เล่นเสียงตั้งแต่ต้น (ถ้ากำลังเล่นอยู่จะเริ่มใหม่) */
    public void play(Sound sound) {
        if (muted) {
            return;
        }
        Clip clip = clips.get(sound);
        if (clip == null) {
            return;
        }
        if (clip.isRunning()) {
            clip.stop();
        }
        clip.setFramePosition(0);
        clip.start();
    }

    /** ปิด/เปิดเสียงทั้งหมด รวมถึงเพลงพื้นหลัง */
    public void toggleMute() {
        muted = !muted;
        if (muted) {
            stopMusic();
        } else {
            startMusic();
        }
    }

    public boolean isMuted() {
        return muted;
    }
}
