package model.scene;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import model.Door;
import model.Inventory;
import model.MarketStall;

/**
 * ฉากตลาดนอกร้าน: แผงขายวัตถุดิบ 3 แผง และประตูกลับร้าน
 * ราคาซื้อเป็นชุด (3 ที่) ถูกกว่าซื้อทีละที่
 */
public class MarketScene extends Scene {

    private static final int GROUND_Y = 300;   // พื้นตลาดเริ่มตรงนี้
    private final Door restaurantDoor;

    public MarketScene(Inventory inventory) {
        super(new Rectangle(20, 262, SCREEN_WIDTH - 40, 568 - 262));

        restaurantDoor = new Door(52, 230, "RESTAURANT", "door_market");
        addObject(restaurantDoor, false);
        addInteractable(restaurantDoor);

        MarketStall veggie = new MarketStall(210, 200, 1, 25, 3, inventory, new Color(90, 170, 80));
        MarketStall meat = new MarketStall(395, 200, 2, 40, 3, inventory, new Color(200, 70, 60));
        MarketStall bakery = new MarketStall(580, 200, 3, 65, 3, inventory, new Color(240, 190, 60));
        for (MarketStall stall : new MarketStall[] {veggie, meat, bakery}) {
            addObject(stall, true);
            addInteractable(stall);
        }
    }

    /** วาดฉากตามปกติ แล้วเขียนชื่อร้านบนป้ายหน้าร้าน (Override แล้วเรียก super เพิ่มของเอง) */
    @Override
    public void draw(Graphics2D g) {
        super.draw(g);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        String sign = "PAOTUNG'S";
        int textWidth = g.getFontMetrics().stringWidth(sign);
        g.setColor(new Color(150, 50, 35));
        g.drawString(sign, 58 - textWidth / 2, 157);
    }

    @Override
    protected String getBackgroundSprite() {
        return "market_bg";
    }

    @Override
    public Point getEntryPoint() {
        return new Point(58, 330);   // ยืนหน้าประตูร้าน
    }

    @Override
    public Door getExitDoor() {
        return restaurantDoor;
    }

    @Override
    protected void drawFallbackBackground(Graphics2D g) {
        g.setColor(new Color(150, 200, 235));
        g.fillRect(0, HUD_HEIGHT, SCREEN_WIDTH, GROUND_Y - HUD_HEIGHT);
        g.setColor(new Color(190, 170, 140));
        g.fillRect(0, GROUND_Y, SCREEN_WIDTH, HUD_HEIGHT + PLAY_HEIGHT - GROUND_Y);
    }
}
