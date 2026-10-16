package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;

import logic.CustomerSpawner;
import logic.DayManager;
import logic.HighScoreManager;
import model.Chef;
import model.GameModifiers;
import model.GameObject;
import model.GameState;
import model.GameStats;
import model.Fridge;
import model.Interactable;
import model.Inventory;
import model.Sprites;
import model.Stove;
import model.Table;
import model.scene.MarketScene;
import model.scene.RestaurantScene;
import model.scene.Scene;
import model.customer.Customer;
import model.food.Food;
import model.food.Menu;
import model.upgrade.FastStoveUpgrade;
import model.upgrade.PatienceUpgrade;
import model.upgrade.SecondStoveUpgrade;
import model.upgrade.ShoesUpgrade;
import model.upgrade.Upgrade;
import ui.SoundManager.Sound;

/**
 * หน้าเล่นเกม: เป็นเจ้าของ game loop รับคีย์บอร์ด อัปเดตทุก object แล้ววาดลงจอ
 */
public class GamePanel extends JPanel implements ActionListener {

    public static final int SCREEN_WIDTH = 800;
    public static final int SCREEN_HEIGHT = 600;
    public static final int HUD_HEIGHT = 60;
    private static final int INTERACT_RANGE = 20;    // ระยะที่กด Space ได้
    private static final int CHEF_BASE_SPEED = 3;
    private static final int MESSAGE_TICKS = 120;    // ข้อความค้างบนจอ ~2 วินาที

    // Game loop แบบ fixed time step: Timer เช็คถี่ ๆ แต่ update ทุก 16 ms ตามเวลาจริง
    private static final int LOOP_CHECK_MS = 4;
    private static final long TICK_NANOS = GameObject.TICK_MS * 1_000_000L;
    private static final long MAX_LAG_NANOS = TICK_NANOS * 5;  // กันกระโดดไกลหลังเครื่องค้าง

    // สร้างฟอนต์ครั้งเดียว ไม่ต้องสร้างใหม่ทุกเฟรม
    private static final Font HUD_FONT = new Font("SansSerif", Font.BOLD, 16);
    private static final Font HINT_FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font MESSAGE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 44);
    private static final Font BODY_FONT = new Font("SansSerif", Font.PLAIN, 18);
    private static final Font FOOTER_FONT = new Font("SansSerif", Font.BOLD, 19);
    private static final Font SHOP_NAME_FONT = new Font("SansSerif", Font.BOLD, 17);
    private static final Font SHOP_DESC_FONT = new Font("SansSerif", Font.PLAIN, 14);

    private final GameFrame frame;
    private final KeyHandler keys;
    private final SoundManager sound;
    private final HighScoreManager highScores;
    private final Timer gameLoop;
    private final Rectangle playArea;   // พื้นที่ใต้ HUD ทั้งหมด (ใช้วาดหน้าจอซ้อน)
    private final Inventory inventory;  // วัตถุดิบในตู้เย็น (ค้างข้ามวันได้)

    private final GameStats stats;
    private final DayManager dayManager;
    private final CustomerSpawner spawner;
    private final List<Upgrade> upgrades;      // อัปเกรดทั้งหมดในร้านค้า (อยู่ตลอดเกม)

    private final JButton pauseButton;
    private final JButton resumeButton;
    private final JButton restartButton;
    private final JButton menuButton;

    private GameState state;
    private GameModifiers modifiers;           // ผลรวมของอัปเกรดที่ซื้อแล้ว
    private Chef chef;
    private RestaurantScene restaurant;
    private MarketScene market;
    private Scene currentScene;                 // ฉากที่เชฟอยู่ตอนนี้ (Polymorphism)

    private Upgrade boughtToday;               // ซื้อได้วันละ 1 ชิ้น (null = ยังไม่ซื้อ)
    private String message;
    private int messageTicks;
    private String gameOverReason;
    private boolean newHighScore;

    private long lastLoopNanos;
    private long lagNanos;

    public GamePanel(GameFrame frame, SoundManager sound, HighScoreManager highScores) {
        this.frame = frame;
        this.sound = sound;
        this.highScores = highScores;
        Sprites.preloadAll();   // โหลดรูปทั้งหมดครั้งเดียวตอนเริ่ม ไม่ให้สะดุดกลางเกม

        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setLayout(null);
        setFocusable(true);

        keys = new KeyHandler();
        addKeyListener(keys);

        // Timer เรียก actionPerformed() ทุก ๆ 4 ms เพื่อเช็คว่าถึงเวลา update หรือยัง
        gameLoop = new Timer(LOOP_CHECK_MS, this);
        playArea = new Rectangle(0, HUD_HEIGHT, SCREEN_WIDTH, SCREEN_HEIGHT - HUD_HEIGHT);
        // เท้าเชฟ (ขอบล่าง) ต้องอยู่บนพื้นไม้: ให้หัวล้ำขึ้นไปหน้าผนังได้เล็กน้อยเหมือนยืนชิดผนัง
        inventory = new Inventory("Salad", "Burger", "Pizza");

        stats = new GameStats();
        dayManager = new DayManager();
        spawner = new CustomerSpawner();

        // Polymorphism: list เดียวเก็บอัปเกรดได้ทุกชนิด
        upgrades = new ArrayList<>();
        upgrades.add(new SecondStoveUpgrade());
        upgrades.add(new FastStoveUpgrade());
        upgrades.add(new ShoesUpgrade());
        upgrades.add(new PatienceUpgrade());

        pauseButton = createHudButton("Pause", 440, new Color(235, 150, 50));
        resumeButton = createHudButton("Resume", 530, new Color(90, 175, 70));
        restartButton = createHudButton("Restart", 620, new Color(70, 130, 200));
        menuButton = createHudButton("Menu", 710, new Color(205, 70, 60));

        pauseButton.addActionListener(e -> pauseGame());
        resumeButton.addActionListener(e -> resumeGame());
        restartButton.addActionListener(e -> restartGame());
        menuButton.addActionListener(e -> this.frame.showMenu());

        resetWorld();
    }

    private JButton createHudButton(String text, int x, Color color) {
        JButton button = new MenuButton(text, color, 14);  // ปุ่มสไตล์เดียวกับหน้าเมนู
        button.setBounds(x, 12, 84, 38);
        add(button);
        return button;
    }

    // =============== สร้างโลกของเกม ===============

    /** เริ่มเกมใหม่ทั้งหมด: วันที่ 1 ดาวเต็ม เงินทุนเริ่มต้น อัปเกรดเป็น 0 */
    private void resetWorld() {
        stats.reset();
        dayManager.reset();
        inventory.clear();
        for (Upgrade upgrade : upgrades) {
            upgrade.reset();
        }
        setupDay();
    }

    /** รวมผลของอัปเกรดทุกตัว: แต่ละตัว apply() ผลของตัวเองลงไป */
    private GameModifiers buildModifiers() {
        GameModifiers result = new GameModifiers();
        for (Upgrade upgrade : upgrades) {
            upgrade.apply(result);
        }
        return result;
    }

    /** จัดร้านใหม่สำหรับวันใหม่ (ใช้ทั้งตอนเริ่มเกมและตอนขึ้นวันถัดไป) */
    private void setupDay() {
        modifiers = buildModifiers();
        chef = new Chef(0, 0, CHEF_BASE_SPEED + modifiers.getChefSpeedBonus());

        // สร้างฉากใหม่ทุกวัน (อัปเกรดที่ซื้อเมื่อวานจะมีผล) แต่ใช้ตู้เย็นชุดเดิม
        restaurant = new RestaurantScene(modifiers, inventory, chef);
        restaurant.configureStoves(modifiers.getCookTimeMultiplier(), dayManager.getBurnDelayMs());
        market = new MarketScene(inventory);
        // เตรียมพื้นหลังทั้งสองฉากไว้ก่อน จะได้ไม่สะดุดตอนเดินผ่านประตูครั้งแรก
        restaurant.prepare();
        market.prepare();

        // ทุกเช้าเริ่มที่ตลาด ซื้อของก่อนเปิดร้าน
        enterScene(market);

        spawner.reset();
        boughtToday = null;
        message = null;
        messageTicks = 0;
        state = GameState.PLAYING;
        keys.reset();
    }

    // =============== ควบคุมเกม ===============

    public void startNewGame() {
        resetWorld();
        updateButtons();
        lastLoopNanos = System.nanoTime();
        lagNanos = 0;
        gameLoop.restart();
        requestFocusInWindow();
        showMessage("Morning! Buy ingredients, then go to the RESTAURANT door");
        repaint();
    }

    public void stopGame() {
        gameLoop.stop();
    }

    public void pauseGame() {
        if (state != GameState.PLAYING) return;
        state = GameState.PAUSED;
        keys.reset();
        updateButtons();
        repaint();
    }

    public void resumeGame() {
        if (state != GameState.PAUSED) return;
        state = GameState.PLAYING;
        updateButtons();
        requestFocusInWindow();
    }

    public void restartGame() {
        startNewGame();
    }

    private void startNextDay() {
        dayManager.nextDay();
        stats.startNewDay();
        setupDay();
        updateButtons();
        requestFocusInWindow();
        showMessage("Day " + dayManager.getDay() + " - shop at the market first (rent $" + dayManager.getRent() + ")");
    }

    private void endGame(String reason) {
        state = GameState.GAME_OVER;
        gameOverReason = reason;
        newHighScore = highScores.submit(stats.getScore(), dayManager.getDay());
        keys.reset();
        updateButtons();
        sound.play(Sound.GAME_OVER);
    }

    /** เปิด/ปิดปุ่มให้ตรงกับสถานะ กดปุ่มที่ไม่มีความหมายไม่ได้ */
    private void updateButtons() {
        pauseButton.setEnabled(state == GameState.PLAYING);
        resumeButton.setEnabled(state == GameState.PAUSED);
    }

    private void showMessage(String text) {
        message = text;
        messageTicks = MESSAGE_TICKS;
    }

    // =============== Game Loop ===============

    /**
     * Timer เรียก method นี้ทุก 4 ms
     * นับเวลาจริงที่ผ่านไป แล้ว update ทีละ 16 ms ให้ครบ (fixed time step)
     * ทำให้เกมเดินความเร็วเท่าเดิมเสมอ แม้ Timer จะมาช้าหรือเร็วไม่สม่ำเสมอ
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        lagNanos += now - lastLoopNanos;
        lastLoopNanos = now;
        if (lagNanos > MAX_LAG_NANOS) {
            lagNanos = MAX_LAG_NANOS;
        }

        boolean updated = false;
        while (lagNanos >= TICK_NANOS) {
            update();
            lagNanos -= TICK_NANOS;
            updated = true;
        }
        if (updated) {
            repaint(); // วาดใหม่เฉพาะตอนที่มีอะไรเปลี่ยน
        }
    }

    private void update() {
        if (messageTicks > 0) {
            messageTicks--;
        }
        if (keys.consumeMute()) {
            sound.toggleMute();
            showMessage(sound.isMuted() ? "Sound off" : "Sound on");
        }
        if (keys.consumePauseToggle()) {
            if (state == GameState.PLAYING) {
                pauseGame();
            } else if (state == GameState.PAUSED) {
                resumeGame();
            }
        }

        if (state == GameState.DAY_END) {
            handleShop();
            return;
        }
        if (state == GameState.GAME_OVER) {
            if (keys.consumeConfirm()) restartGame();
            return;
        }
        if (state != GameState.PLAYING) {
            return; // PAUSED: ไม่ต้องคำนวณอะไร
        }

        Sprites.advanceClock(GameObject.TICK_MS);   // อาหาร/ลูกค้าขยับเฉพาะตอนเล่น (Pause แล้วหยุด)
        updateChef();
        handleNumberKeys();
        handleInteraction();
        updateWorld();
        checkEndConditions();
    }

    private void updateChef() {
        int dx = 0;
        int dy = 0;
        if (keys.isLeft()) dx -= 1;
        if (keys.isRight()) dx += 1;
        if (keys.isUp()) dy -= 1;
        if (keys.isDown()) dy += 1;

        chef.move(dx, dy, keys.isSprint(), currentScene.getWalkArea(), currentScene.getSolids());
        chef.update();
    }

    /** กด 1-3 ใกล้ตู้เย็น = หยิบวัตถุดิบดิบของเมนูนั้น */
    private void handleNumberKeys() {
        int choice = keys.consumeMenuChoice();
        if (Menu.getName(choice) == null) return; // ไม่ได้กด หรือกดเลขที่ไม่มีเมนู

        Fridge fridge = restaurant.getFridge();
        if (currentScene != restaurant || !chef.isNear(fridge.getBounds(), INTERACT_RANGE)) {
            showMessage("Stand next to the fridge and press 1-3");
            return;
        }
        boolean hadFood = chef.hasFood();
        showMessage(fridge.takeIngredient(choice, chef));
        sound.play(!hadFood && chef.hasFood() ? Sound.PICKUP : Sound.ERROR);
    }

    /** เดินเข้าฉากใหม่: ย้ายเชฟไปยืนหน้าประตูของฉากนั้น */
    private void enterScene(Scene scene) {
        currentScene = scene;
        Point entry = scene.getEntryPoint();
        chef.moveTo(entry.x, entry.y);
        keys.clearPresses();   // กันกด Space ซ้ำในฉากใหม่ แต่ยังเดินต่อได้ไม่สะดุด
    }

    /** ประตูถูกกด: สลับไปอีกฉาก ถ้าเข้าร้านตอนเช้า = เปิดร้านทันที */
    private void travelThroughDoor() {
        if (currentScene == market) {
            enterScene(restaurant);
            dayManager.openShop();
        } else {
            enterScene(market);
            showMessage("At the market - customers are still waiting inside!");
        }
        sound.play(Sound.PICKUP);
    }

    /** กด Space = ใช้งานของที่อยู่ใกล้ (เตา โต๊ะ หรือถังขยะ) */
    private void handleInteraction() {
        if (!keys.consumeInteract()) return;

        Interactable obj = findClosestInteractable();
        if (obj == null) {
            showMessage("Nothing to use here");
            return;
        }

        // จำสถานะก่อน เพื่อดูว่า interact() ทำให้อะไรเปลี่ยน แล้วเลือกเสียงให้ตรง
        boolean hadFood = chef.hasFood();
        int servedBefore = stats.getTotalServed();
        int starsBefore = stats.getReputation();
        int cashBefore = stats.getCash();
        int busyBefore = countBusyStoves();

        // Polymorphism: ไม่ต้องรู้ว่าเป็นเตา โต๊ะ ถังขยะ ตู้เย็น แผงขาย หรือประตู แค่เรียก interact()
        showMessage(obj.interact(chef, stats));

        if (currentScene.getExitDoor().consumeUsed()) {
            travelThroughDoor();
            return;
        }
        playInteractionSound(hadFood, servedBefore, starsBefore, cashBefore, busyBefore);
    }

    /** ของที่อยู่ในระยะกด Space และใกล้เชฟที่สุด (กันกดโดนชิ้นที่ไม่ได้ตั้งใจ) */
    private Interactable findClosestInteractable() {
        Interactable closest = null;
        double bestDistance = Double.MAX_VALUE;
        double chefX = chef.getBounds().getCenterX();
        double chefY = chef.getBounds().getCenterY();
        for (Interactable obj : currentScene.getInteractables()) {
            Rectangle box = obj.getBounds();
            if (!chef.isNear(box, INTERACT_RANGE)) {
                continue;
            }
            // ระยะจากกลางตัวเชฟถึงจุดที่ใกล้ที่สุดของกรอบ object
            double nearestX = Math.max(box.getMinX(), Math.min(chefX, box.getMaxX()));
            double nearestY = Math.max(box.getMinY(), Math.min(chefY, box.getMaxY()));
            double distance = Math.hypot(chefX - nearestX, chefY - nearestY);
            if (distance < bestDistance) {
                bestDistance = distance;
                closest = obj;
            }
        }
        return closest;
    }

    private int countBusyStoves() {
        int count = 0;
        for (Stove stove : restaurant.getStoves()) {
            if (stove.isBusy()) count++;
        }
        return count;
    }

    private void playInteractionSound(boolean hadFood, int servedBefore, int starsBefore,
                                      int cashBefore, int busyBefore) {
        if (stats.getTotalServed() > servedBefore) {
            sound.play(Sound.SERVE);
            if (stats.getReputation() > starsBefore) {
                sound.play(Sound.STAR);   // เสิร์ฟครบ streak ได้ดาวคืน
            }
        } else if (stats.getReputation() < starsBefore) {
            sound.play(Sound.FAIL);
        } else if (stats.getCash() < cashBefore) {
            sound.play(Sound.BUY);      // ซื้อวัตถุดิบที่ตลาด
        } else if (countBusyStoves() > busyBefore) {
            sound.play(Sound.COOK);     // ใส่วัตถุดิบลงเตา
        } else if (!hadFood && chef.hasFood()) {
            sound.play(Sound.PICKUP);
        } else if (hadFood && !chef.hasFood()) {
            sound.play(Sound.TRASH);
        }
    }

    private void updateWorld() {
        // ร้านทำงานตลอด แม้เชฟจะอยู่ตลาด: ลูกค้ายังรอ และอาหารยังไหม้ได้
        restaurant.update();
        if (currentScene == market) {
            market.update();
        }

        for (Stove stove : restaurant.getStoves()) {
            if (stove.consumeJustReady()) {
                sound.play(Sound.READY);
            }
            if (stove.consumeJustBurnt()) {
                sound.play(Sound.BURNT);
                showMessage("Food burnt! Clean the stove");
            }
        }
        for (Table table : restaurant.getTables()) {
            Customer customer = table.getCustomer();
            if (customer != null && customer.consumeWalkout()) {
                stats.loseReputation();
                showMessage("A customer left angry! -1 star");
                sound.play(Sound.FAIL);
            }
        }

        if (dayManager.isOpen()) {
            double patience = dayManager.getPatienceFactor() * modifiers.getPatienceMultiplier();
            boolean newCustomer = spawner.update(GameObject.TICK_MS, restaurant.getTables(),
                    dayManager.getSpawnIntervalMs(), patience, dayManager.getDay());
            if (newCustomer) {
                sound.play(Sound.DOORBELL);
            }
        }
        dayManager.update(GameObject.TICK_MS);
        if (dayManager.consumeJustOpened()) {
            showMessage(currentScene == restaurant ? "The restaurant is OPEN!"
                    : "Opening time! Hurry back to the restaurant!");
            sound.play(Sound.DOORBELL);
        }
    }

    /** ดาวหมด = แพ้ทันที / หมดเวลา = จ่ายค่าเช่า จ่ายไม่ไหว = ล้มละลาย */
    private void checkEndConditions() {
        if (stats.isGameOver()) {
            endGame("Your restaurant lost all its stars.");
        } else if (dayManager.isDayOver()) {
            int rent = dayManager.getRent();
            if (stats.payRent(rent)) {
                state = GameState.DAY_END;
                keys.reset();
                updateButtons();
                sound.play(Sound.DAY_END);
            } else {
                endGame("Bankrupt! You could not pay $" + rent + " rent.");
            }
        }
    }

    // =============== ร้านค้าหลังจบวัน ===============

    /** กด 1-4 ซื้อ (วันละ 1 ชิ้น), Enter ไปวันถัดไป */
    private void handleShop() {
        keys.consumeInteract(); // ไม่ใช้ Space ในหน้านี้
        int choice = keys.consumeMenuChoice();
        if (choice >= 1 && choice <= upgrades.size()) {
            buyUpgrade(upgrades.get(choice - 1));
        }
        if (keys.consumeConfirm()) {
            startNextDay();
        }
    }

    private void buyUpgrade(Upgrade upgrade) {
        if (boughtToday != null) {
            showMessage("Only 1 upgrade per day - come back tomorrow!");
            sound.play(Sound.ERROR);
            return;
        }
        if (upgrade.isMaxed()) {
            showMessage(upgrade.getName() + " is already max level");
            sound.play(Sound.ERROR);
            return;
        }
        int price = upgrade.getPrice();
        if (upgrade.tryBuy(stats)) {
            boughtToday = upgrade;
            showMessage("Bought " + upgrade.getName() + "! (-$" + price + ")");
            sound.play(Sound.BUY);
        } else {
            showMessage("Not enough cash - need $" + price);
            sound.play(Sound.ERROR);
        }
    }

    // =============== วาดจอ (วาดอย่างเดียว ไม่เปลี่ยนค่าใด ๆ) ===============

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        currentScene.draw(g2);   // ฉากวาดพื้นหลังและของทั้งหมดของตัวเอง
        chef.draw(g2);
        if (currentScene == market) {
            drawRestaurantStatus(g2);
        }

        drawControlsHint(g2);
        drawHud(g2);

        switch (state) {
            case PAUSED -> drawOverlay(g2, "PAUSED", Color.WHITE, new String[0], "Press P or click Resume");
            case DAY_END -> drawShop(g2);
            case GAME_OVER -> drawOverlay(g2, newHighScore ? "NEW HIGH SCORE!" : "GAME OVER",
                    newHighScore ? new Color(255, 205, 80) : Color.WHITE, new String[] {
                    gameOverReason,
                    "You reached day " + dayManager.getDay() + "   |   Customers served: " + stats.getTotalServed(),
                    "Final score: " + stats.getScore(),
                    "Best: " + highScores.getBestScore() + " pts   |   Best day: " + highScores.getBestDay()
            }, "Press ENTER or click Restart");
            default -> { }
        }

        drawMessage(g2); // วาดท้ายสุด ให้อยู่บนทุกอย่าง

        Toolkit.getDefaultToolkit().sync(); // ส่งภาพขึ้นจอทันที ลดอาการกระตุก
    }

    /** ตอนอยู่ตลาด: บอกว่าในร้านมีลูกค้ารออยู่กี่คน */
    private void drawRestaurantStatus(Graphics2D g2) {
        String text;
        Color color;
        if (!dayManager.isOpen()) {
            text = "Restaurant opens in " + dayManager.getPrepSecondsLeft() + "s";
            color = new Color(60, 120, 60, 210);
        } else {
            int waiting = restaurant.countWaitingCustomers();
            text = waiting == 0 ? "Restaurant: no one waiting" : "Restaurant: " + waiting + " customer(s) waiting!";
            color = waiting == 0 ? new Color(60, 120, 60, 210) : new Color(190, 50, 40, 220);
        }
        g2.setFont(MESSAGE_FONT);
        int width = g2.getFontMetrics().stringWidth(text) + 24;
        g2.setColor(color);
        g2.fillRoundRect(SCREEN_WIDTH - width - 12, HUD_HEIGHT + 52, width, 30, 14, 14);
        g2.setColor(Color.WHITE);
        g2.drawString(text, SCREEN_WIDTH - width, HUD_HEIGHT + 73);
    }

    private void drawControlsHint(Graphics2D g2) {
        g2.setColor(new Color(255, 255, 255, 170));
        g2.fillRect(0, SCREEN_HEIGHT - 26, SCREEN_WIDTH, 26);
        g2.setColor(new Color(80, 60, 40));
        g2.setFont(HINT_FONT);
        drawCentered(g2, "WASD move   |   SPACE do everything   |   SHIFT sprint   |   P pause   |   M sound",
                SCREEN_HEIGHT - 8);
    }

    private void drawMessage(Graphics2D g2) {
        if (messageTicks <= 0 || message == null) return;
        g2.setFont(MESSAGE_FONT);
        int textWidth = g2.getFontMetrics().stringWidth(message);
        int boxWidth = textWidth + 30;
        int boxX = (SCREEN_WIDTH - boxWidth) / 2;
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRoundRect(boxX, HUD_HEIGHT + 10, boxWidth, 34, 16, 16);
        g2.setColor(Color.WHITE);
        g2.drawString(message, boxX + 15, HUD_HEIGHT + 33);
    }

    private void drawHud(Graphics2D g2) {
        g2.setColor(new Color(60, 40, 30));
        g2.fillRect(0, 0, SCREEN_WIDTH, HUD_HEIGHT);

        g2.setFont(HUD_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("Day " + dayManager.getDay(), 15, 25);
        if (dayManager.isOpen()) {
            g2.drawString("Time " + dayManager.getSecondsLeft(), 90, 25);
        } else {
            g2.setColor(new Color(255, 180, 80));
            g2.drawString("Prep " + dayManager.getPrepSecondsLeft(), 90, 25);
            g2.setColor(Color.WHITE);
        }

        // เงินในกระเป๋า: เขียวเมื่อพอจ่ายค่าเช่า
        boolean canPayRent = stats.getCash() >= dayManager.getRent();
        g2.setColor(canPayRent ? new Color(120, 230, 120) : new Color(255, 210, 90));
        g2.drawString("Cash $" + stats.getCash(), 195, 25);
        g2.setColor(new Color(230, 200, 190));
        g2.drawString("Rent $" + dayManager.getRent(), 310, 25);

        g2.setColor(Color.WHITE);
        g2.drawString("Score " + stats.getScore(), 15, 50);

        // ดาวชื่อเสียง (HP)
        for (int i = 0; i < GameStats.MAX_REPUTATION; i++) {
            Color color = i < stats.getReputation() ? new Color(250, 200, 40) : new Color(110, 90, 80);
            drawStar(g2, 120 + i * 22, 44, 9, color);
        }
        // แถบสะสม streak ใต้ดาว: เต็มแถบเมื่อไหร่ได้ดาวคืน 1 ดวง (ซ่อนเมื่อดาวเต็ม)
        if (stats.getReputation() < GameStats.MAX_REPUTATION) {
            int barWidth = GameStats.MAX_REPUTATION * 22 - 4;
            g2.setColor(new Color(110, 90, 80));
            g2.fillRect(111, 54, barWidth, 3);
            g2.setColor(new Color(250, 200, 40));
            g2.fillRect(111, 54, barWidth * stats.getServeStreak() / GameStats.STREAK_FOR_STAR, 3);
        }

        // วัตถุดิบในตู้เย็น
        int stockX = 240;
        for (int choice = 1; choice <= 3; choice++) {
            String name = Menu.getName(choice);
            Menu.createFood(choice).drawIcon(g2, stockX, 30);
            int count = inventory.getCount(name);
            g2.setColor(count == 0 ? new Color(255, 120, 100) : Color.WHITE);
            g2.drawString("" + count, stockX + 25, 49);
            g2.setColor(Color.WHITE);
            stockX += 52;
        }
    }

    private void drawStar(Graphics2D g2, int cx, int cy, int radius, Color color) {
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 2 + i * Math.PI / 5;
            int r = (i % 2 == 0) ? radius : radius / 2;
            star.addPoint(cx + (int) (r * Math.cos(angle)), cy - (int) (r * Math.sin(angle)));
        }
        g2.setColor(color);
        g2.fillPolygon(star);
    }

    /** หน้าสรุปวัน + ร้านค้า */
    private void drawShop(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 185));
        g2.fillRect(playArea.x, playArea.y, playArea.width, playArea.height);

        g2.setColor(Color.WHITE);
        g2.setFont(TITLE_FONT);
        drawCentered(g2, "Day " + dayManager.getDay() + " complete!", 140);

        g2.setFont(BODY_FONT);
        drawCentered(g2, "Sales +$" + stats.getDayMoney()
                + "   Ingredients -$" + stats.getIngredientsToday()
                + "   Rent -$" + stats.getRentToday()
                + "   Profit $" + stats.getProfitToday(), 172);
        drawCentered(g2, "Served " + stats.getServedToday() + "   Lost " + stats.getLostToday(), 196);

        g2.setColor(new Color(255, 210, 90));
        g2.setFont(FOOTER_FONT);
        drawCentered(g2, "Cash: $" + stats.getCash(), 224);

        int rowY = 238;
        for (int i = 0; i < upgrades.size(); i++) {
            Upgrade upgrade = upgrades.get(i);
            boolean affordable = boughtToday == null && !upgrade.isMaxed()
                    && stats.getCash() >= upgrade.getPrice();

            g2.setColor(new Color(255, 255, 255, affordable ? 60 : 22));
            g2.fillRoundRect(110, rowY, 580, 52, 12, 12);

            g2.setColor(affordable ? Color.WHITE : new Color(180, 180, 180));
            g2.setFont(SHOP_NAME_FONT);
            g2.drawString("[" + (i + 1) + "]  " + upgrade.getName()
                    + "   Lv " + upgrade.getLevel() + "/" + upgrade.getMaxLevel(), 130, rowY + 22);
            g2.setColor(new Color(210, 210, 210));
            g2.setFont(SHOP_DESC_FONT);
            g2.drawString(upgrade.getDescription(), 130, rowY + 42);

            String priceText;
            if (upgrade == boughtToday) {
                priceText = "BOUGHT";
            } else if (upgrade.isMaxed()) {
                priceText = "MAX";
            } else {
                priceText = "$" + upgrade.getPrice();
            }
            g2.setFont(SHOP_NAME_FONT);
            g2.setColor(affordable || upgrade == boughtToday ? new Color(120, 230, 120) : new Color(150, 150, 150));
            g2.drawString(priceText, 600, rowY + 32);

            rowY += 58;
        }

        g2.setColor(new Color(255, 220, 120));
        g2.setFont(FOOTER_FONT);
        String buyHint = boughtToday == null ? "Press 1-4 to buy ONE upgrade" : "Upgrade bought for today";
        drawCentered(g2, buyHint + "   |   ENTER: start Day " + (dayManager.getDay() + 1), rowY + 26);
    }

    private void drawOverlay(Graphics2D g2, String title, Color titleColor, String[] lines, String footer) {
        g2.setColor(new Color(0, 0, 0, 160)); // ดำโปร่งแสง
        g2.fillRect(playArea.x, playArea.y, playArea.width, playArea.height);

        int y = 220;
        g2.setColor(titleColor);
        g2.setFont(TITLE_FONT);
        drawCentered(g2, title, y);

        g2.setColor(Color.WHITE);
        g2.setFont(BODY_FONT);
        y += 50;
        for (String line : lines) {
            drawCentered(g2, line, y);
            y += 32;
        }

        g2.setColor(new Color(255, 220, 120));
        g2.setFont(FOOTER_FONT);
        drawCentered(g2, footer, y + 20);
    }

    private void drawCentered(Graphics2D g2, String text, int y) {
        int textWidth = g2.getFontMetrics().stringWidth(text);
        g2.drawString(text, (SCREEN_WIDTH - textWidth) / 2, y);
    }
}
