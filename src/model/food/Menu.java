package model.food;

import java.util.Random;

/**
 * จุดเดียวที่ตัดสินว่าเลขเมนูไหนคืออาหารอะไร
 * ถ้าจะเพิ่มเมนูใหม่ แก้แค่ที่นี่ + สร้าง class อาหารใหม่
 */
public final class Menu {

    public static final int MENU_COUNT = 3;

    private Menu() {
        // ห้ามสร้าง object (ใช้แค่ static method)
    }

    /** 1 = Salad, 2 = Burger, 3 = Pizza, อื่น ๆ = null */
    public static Food createFood(int choice) {
        return switch (choice) {
            case 1 -> new Salad();
            case 2 -> new Burger();
            case 3 -> new Pizza();
            default -> null;
        };
    }

    /** ชื่อเมนูจากเลข 1-3 (null ถ้าไม่มี) */
    public static String getName(int choice) {
        Food food = createFood(choice);
        return food == null ? null : food.getName();
    }

    public static Food randomFood(Random random) {
        return createFood(random.nextInt(MENU_COUNT) + 1);
    }
}
