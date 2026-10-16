package model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * สต็อกวัตถุดิบในตู้เย็น (นับเป็นจำนวนที่ ต่อเมนู)
 * แก้จำนวนได้ผ่าน add() / take() เท่านั้น จำนวนจึงไม่มีทางติดลบ
 */
public class Inventory {

    private final Map<String, Integer> stock = new LinkedHashMap<>();

    public Inventory(String... dishNames) {
        for (String name : dishNames) {
            stock.put(name, 0);
        }
    }

    public void add(String dishName, int amount) {
        if (amount <= 0 || !stock.containsKey(dishName)) {
            return;
        }
        stock.put(dishName, stock.get(dishName) + amount);
    }

    /** หยิบออก 1 ที่ สำเร็จเมื่อยังมีของเหลือ */
    public boolean take(String dishName) {
        int count = getCount(dishName);
        if (count <= 0) {
            return false;
        }
        stock.put(dishName, count - 1);
        return true;
    }

    public int getCount(String dishName) {
        return stock.getOrDefault(dishName, 0);
    }

    public void clear() {
        stock.replaceAll((name, count) -> 0);
    }
}
