package ui;

import java.awt.CardLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import logic.HighScoreManager;

/**
 * หน้าต่างหลักของเกม ทำหน้าที่ถือหน้าจอทั้งหมดและสลับระหว่างหน้าเมนูกับหน้าเล่นเกม
 * final = ห้ามมี class ลูก (ไม่ได้ออกแบบมาให้สืบทอด)
 */
public final class GameFrame extends JFrame {

    // ชื่อของแต่ละหน้า ใช้เป็น "กุญแจ" ตอนสั่ง CardLayout ให้สลับหน้า
    private static final String MENU = "menu";
    private static final String GAME = "game";

    private final CardLayout cardLayout;
    private final JPanel container;
    private final MenuPanel menuPanel;
    private final GamePanel gamePanel;
    private final SoundManager sound;            // ใช้ร่วมกันทุกหน้า: เพลงเล่นต่อเนื่องตอนสลับหน้า
    private final HighScoreManager highScores;   // ใช้ร่วมกัน: เมนูแสดง เกมบันทึก

    public GameFrame() {
        super("Paotung's Kitchen - เป๋าตุงพุงกาง"); // เรียก constructor ของ JFrame เพื่อตั้งชื่อหน้าต่าง

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        sound = new SoundManager();
        highScores = new HighScoreManager();

        menuPanel = new MenuPanel(this, highScores);
        gamePanel = new GamePanel(this, sound, highScores);

        container.add(menuPanel, MENU);
        container.add(gamePanel, GAME);
        add(container);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        pack();                       // ปรับขนาดหน้าต่างให้พอดีกับ preferredSize ของ panel (800x600)
        setLocationRelativeTo(null);  // แสดงกลางจอ

        showMenu();
        sound.startMusic();
    }

    /** กลับไปหน้าเมนู และหยุดเกมที่เล่นค้างอยู่ */
    public void showMenu() {
        gamePanel.stopGame();
        cardLayout.show(container, MENU);
        menuPanel.startAnimation();
    }

    /** ไปหน้าเล่นเกม และเริ่มเกมใหม่ */
    public void showGame() {
        menuPanel.stopAnimation();
        cardLayout.show(container, GAME);
        gamePanel.startNewGame();
    }
}
