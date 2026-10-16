package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import model.Table;
import model.customer.Customer;
import model.customer.ImpatientCustomer;
import model.customer.NormalCustomer;
import model.customer.VipCustomer;
import model.food.Food;
import model.food.Menu;

/**
 * สุ่มลูกค้าชนิดต่าง ๆ ให้เข้ามานั่งโต๊ะที่ว่างตามช่วงเวลา
 */
public class CustomerSpawner {

    private static final int START_X = 820;            // ลูกค้าเดินเข้ามาจากขอบขวา
    private static final int FIRST_SPAWN_DELAY_MS = 1500;

    private final Random random = new Random();
    private int elapsedMs;
    private boolean firstSpawned;

    public CustomerSpawner() {
        reset();
    }

    public void reset() {
        elapsedMs = 0;
        firstSpawned = false;
    }

    /**
     * @return true ถ้ารอบนี้มีลูกค้าใหม่เข้าร้าน (ใช้เล่นเสียงกริ่ง)
     */
    public boolean update(int deltaMs, List<Table> tables, int spawnIntervalMs, double patienceFactor, int day) {
        elapsedMs += deltaMs;
        int waitMs = firstSpawned ? spawnIntervalMs : FIRST_SPAWN_DELAY_MS;
        if (elapsedMs < waitMs) {
            return false;
        }

        List<Table> freeTables = new ArrayList<>();
        for (Table table : tables) {
            if (table.isFree()) {
                freeTables.add(table);
            }
        }
        if (freeTables.isEmpty()) {
            return false; // โต๊ะเต็ม รอให้มีโต๊ะว่างก่อน
        }

        Table table = freeTables.get(random.nextInt(freeTables.size()));
        table.seat(createRandomCustomer(table, patienceFactor, day));
        elapsedMs = 0;
        firstSpawned = true;
        return true;
    }

    /** คืนค่าเป็น Customer แต่ object จริงเป็นลูกชนิดใดชนิดหนึ่ง (Polymorphism) */
    private Customer createRandomCustomer(Table table, double patienceFactor, int day) {
        Food order = Menu.randomFood(random);
        int seatX = table.getSeatX();
        int seatY = table.getSeatY();
        int roll = random.nextInt(100);

        // ยิ่งหลายวัน ลูกค้าใจร้อนยิ่งเยอะ: วันแรก 25% เพิ่มวันละ 5% (สูงสุด 45%)
        int impatientChance = Math.min(45, 25 + (day - 1) * 5);
        int vipChance = 15;

        if (roll < vipChance) {
            return new VipCustomer(START_X, seatX, seatY, order, patienceFactor);
        } else if (roll < vipChance + impatientChance) {
            return new ImpatientCustomer(START_X, seatX, seatY, order, patienceFactor);
        } else {
            return new NormalCustomer(START_X, seatX, seatY, order, patienceFactor);
        }
    }
}
