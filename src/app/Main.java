package app;

import javax.swing.SwingUtilities;
import ui.GameFrame;

/**
 * จุดเริ่มต้นของโปรแกรม มีหน้าที่เดียวคือสร้างหน้าต่างเกมแล้วแสดงผล
 */
public class Main {

    public static void main(String[] args) {
        // สร้าง GUI บน Event Dispatch Thread (EDT) ซึ่งเป็น thread ที่ Swing กำหนดให้ใช้
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
