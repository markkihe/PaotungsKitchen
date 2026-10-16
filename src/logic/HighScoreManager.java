package logic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * บันทึกสถิติสูงสุดลงไฟล์ highscore.txt (อยู่ในโฟลเดอร์โปรเจกต์)
 * ปิดเกมแล้วเปิดใหม่ สถิติยังอยู่
 * รูปแบบไฟล์: บรรทัดที่ 1 = คะแนนสูงสุด, บรรทัดที่ 2 = วันที่ไปได้ไกลสุด
 */
public class HighScoreManager {

    private static final Path FILE = Path.of("highscore.txt");

    private int bestScore;
    private int bestDay;

    public HighScoreManager() {
        load();
    }

    /** อ่านไฟล์ ถ้าไม่มีไฟล์หรือไฟล์เสีย ให้เริ่มที่ 0 (เกมไม่พัง) */
    private void load() {
        if (!Files.exists(FILE)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(FILE, StandardCharsets.UTF_8);
            bestScore = Integer.parseInt(lines.get(0).trim());
            bestDay = Integer.parseInt(lines.get(1).trim());
        } catch (IOException | RuntimeException e) {
            System.err.println("Cannot read high score: " + e.getMessage());
            bestScore = 0;
            bestDay = 0;
        }
    }

    private void save() {
        try {
            Files.writeString(FILE, bestScore + System.lineSeparator() + bestDay + System.lineSeparator(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Cannot save high score: " + e.getMessage());
        }
    }

    /**
     * ส่งผลเกมที่เพิ่งจบ: ถ้าทำลายสถิติจะบันทึกลงไฟล์
     * @return true ถ้าได้คะแนนสูงสุดใหม่
     */
    public boolean submit(int score, int day) {
        boolean newScore = score > bestScore;
        boolean newDay = day > bestDay;
        if (newScore) {
            bestScore = score;
        }
        if (newDay) {
            bestDay = day;
        }
        if (newScore || newDay) {
            save();
        }
        return newScore;
    }

    public int getBestScore() { return bestScore; }
    public int getBestDay() { return bestDay; }
}
