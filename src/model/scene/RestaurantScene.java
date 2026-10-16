package model.scene;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Chef;
import model.Door;
import model.Fridge;
import model.GameModifiers;
import model.Inventory;
import model.Stove;
import model.Table;
import model.TrashBin;
import model.customer.Customer;
import model.food.Food;
import model.food.Menu;

/**
 * ฉากร้านอาหาร: เตา ตู้เย็น ถังขยะ โต๊ะลูกค้า และประตูไปตลาด
 */
public class RestaurantScene extends Scene {

    private static final int WALL_BOTTOM = 254;   // พื้นไม้เริ่มตรงนี้
    private static final int SIDE_WALL = 38;

    private final List<Stove> stoves = new ArrayList<>();
    private final List<Table> tables = new ArrayList<>();
    private final Fridge fridge;
    private final Door marketDoor;
    private final Inventory inventory;
    private final Chef chef;   // ใช้ดูว่าเชฟถืออะไรอยู่ ตอนคิดว่าควรหยิบวัตถุดิบไหน

    public RestaurantScene(GameModifiers modifiers, Inventory inventory, Chef chef) {
        super(new Rectangle(SIDE_WALL, 208, SCREEN_WIDTH - SIDE_WALL * 2, 568 - 208));
        this.inventory = inventory;
        this.chef = chef;

        // ครัวเรียงชิดผนังหลังแถวเดียว: ตู้เย็น -> เตา 1 -> เตา 2  เดินด้านหน้าได้โล่ง ๆ
        fridge = new Fridge(48, 250, inventory, this::suggestIngredient);
        Stove firstStove = new Stove(112, 255);
        Stove secondStove = new Stove(196, 255);
        if (!modifiers.hasSecondStove()) {
            secondStove.lock();
        }
        stoves.add(firstStove);
        stoves.add(secondStove);

        TrashBin trashBin = new TrashBin(60, 470);
        marketDoor = new Door(374, 182, "MARKET", "door");

        tables.add(new Table(470, 255));
        tables.add(new Table(640, 255));
        tables.add(new Table(470, 450));
        tables.add(new Table(640, 450));

        // ประตูวาดก่อน (อยู่บนผนัง) ของอื่นจะได้ทับประตูได้
        addObject(marketDoor, false);
        addInteractable(marketDoor);
        for (Stove stove : stoves) {
            addObject(stove, true);
            addInteractable(stove);
        }
        addObject(fridge, true);
        addInteractable(fridge);
        addObject(trashBin, true);
        addInteractable(trashBin);
        for (Table table : tables) {
            addObject(table, true);
            addInteractable(table);
        }
    }

    /** ตั้งค่าเตาทุกตัวประจำวัน */
    public void configureStoves(double cookMultiplier, int burnDelayMs) {
        for (Stove stove : stoves) {
            stove.configure(cookMultiplier, burnDelayMs);
        }
    }

    /**
     * ควรหยิบวัตถุดิบเมนูไหนจากตู้เย็น: ไล่ลูกค้าจากคนที่ใกล้หมดความอดทนที่สุด
     * ข้ามออเดอร์ที่มีคนทำให้อยู่แล้ว (อยู่บนเตา หรือเชฟถืออยู่) และต้องมีของในตู้
     * @return เลขเมนู 1-3 หรือ 0 ถ้าไม่มีอะไรต้องหยิบ
     */
    public int suggestIngredient() {
        Map<String, Integer> covered = new HashMap<>();   // จานที่กำลังมา นับแยกตามเมนู
        for (Stove stove : stoves) {
            String dish = stove.getDishInProgress();
            if (dish != null) {
                covered.merge(dish, 1, Integer::sum);
            }
        }
        Food held = chef.getHeldFood();
        if (held != null) {
            Food result = held.cook();          // ถือของดิบ = เมนูที่จะได้
            String dish = (result != null) ? result.getName() : held.getName();
            covered.merge(dish, 1, Integer::sum);
        }

        List<Customer> waiting = new ArrayList<>();
        for (Table table : tables) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.isWaiting()) {
                waiting.add(customer);
            }
        }
        waiting.sort(Comparator.comparingInt(Customer::getPatienceLeftMs));   // ใกล้หมดเวลาก่อน

        for (Customer customer : waiting) {
            String dish = customer.getOrder().getName();
            if (covered.getOrDefault(dish, 0) > 0) {
                covered.merge(dish, -1, Integer::sum);   // ออเดอร์นี้มีจานกำลังมาแล้ว
            } else if (inventory.getCount(dish) > 0) {
                for (int choice = 1; choice <= 3; choice++) {
                    if (dish.equals(Menu.getName(choice))) {
                        return choice;
                    }
                }
            }
        }
        return 0;
    }

    /** จำนวนลูกค้าที่นั่งรออาหารอยู่ (ใช้เตือนตอนอยู่ตลาด) */
    public int countWaitingCustomers() {
        int count = 0;
        for (Table table : tables) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.isWaiting()) {
                count++;
            }
        }
        return count;
    }

    public List<Stove> getStoves() {
        return Collections.unmodifiableList(stoves);
    }

    public List<Table> getTables() {
        return Collections.unmodifiableList(tables);
    }

    public Fridge getFridge() {
        return fridge;
    }

    @Override
    protected String getBackgroundSprite() {
        return "floor";
    }

    @Override
    public Point getEntryPoint() {
        return new Point(380, 262);   // ยืนหน้าประตู
    }

    @Override
    public Door getExitDoor() {
        return marketDoor;
    }

    @Override
    protected void drawFallbackBackground(Graphics2D g) {
        Color light = new Color(245, 235, 215);
        Color dark = new Color(230, 215, 190);
        for (int row = 0; row * 40 < PLAY_HEIGHT; row++) {
            for (int col = 0; col * 40 < SCREEN_WIDTH; col++) {
                g.setColor((row + col) % 2 == 0 ? light : dark);
                g.fillRect(col * 40, HUD_HEIGHT + row * 40, 40, 40);
            }
        }
        g.setColor(new Color(236, 195, 150));
        g.fillRect(0, HUD_HEIGHT, SCREEN_WIDTH, WALL_BOTTOM - HUD_HEIGHT);
        g.setColor(new Color(176, 112, 80));
        g.fillRect(0, WALL_BOTTOM - 40, SCREEN_WIDTH, 40);
        g.setColor(new Color(110, 62, 40));
        g.fillRect(0, HUD_HEIGHT, SIDE_WALL, PLAY_HEIGHT);
        g.fillRect(SCREEN_WIDTH - SIDE_WALL, HUD_HEIGHT, SIDE_WALL, PLAY_HEIGHT);
    }
}
