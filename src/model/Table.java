package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import model.customer.Customer;
import model.food.Food;

/**
 * โต๊ะลูกค้า: มีลูกค้าได้ทีละ 1 คน เชฟเสิร์ฟอาหารที่นี่
 */
public class Table extends GameObject implements Interactable {

    private Customer customer; // null = โต๊ะว่าง

    public Table(int x, int y) {
        super(x, y, 71, 44);
    }

    /** ตำแหน่งที่ลูกค้ายืน (เหนือโต๊ะ ตรงกลาง) */
    public int getSeatX() { return x + 18; }
    public int getSeatY() { return y - 48; }

    public boolean isFree() {
        return customer == null;
    }

    public void seat(Customer newCustomer) {
        if (isFree()) {
            customer = newCustomer;
        }
    }

    public Customer getCustomer() {
        return customer;
    }

    public void clear() {
        customer = null;
    }

    @Override
    public void update() {
        if (customer != null) {
            customer.update();
            if (customer.hasLeft()) {
                customer = null; // เดินออกจากจอแล้ว โต๊ะว่าง
            }
        }
    }

    @Override
    public String interact(Chef chef, GameStats stats) {
        if (customer == null || !customer.isWaiting()) {
            return "No one is waiting here";
        }
        if (!chef.hasFood()) {
            return "Customer wants " + customer.getOrder().getName();
        }

        Food served = chef.handOver();
        if (!served.isEdible()) {   // ของไหม้ หรือวัตถุดิบดิบที่ยังไม่ได้ทำ
            stats.loseReputation();
            return "You can't serve " + served.getName() + "! -1 star";
        }
        if (customer.wants(served)) {
            // Polymorphism: ลูกค้าแต่ละชนิดคำนวณเงินต่างกัน โต๊ะไม่ต้องรู้ว่าเป็นชนิดไหน
            int payment = customer.calculatePayment();
            int speedBonus = (int) (customer.getPatienceRatio() * 50);
            stats.earn(payment);
            stats.addScore(payment + speedBonus);
            boolean gainedStar = stats.addServed();
            customer.serve();
            if (gainedStar) {
                return "Served! +$" + payment + "  Great service streak: +1 star!";
            }
            return "Served! +$" + payment;
        }

        stats.loseReputation();
        return "Wrong dish! -1 star";
    }

    @Override
    public void draw(Graphics2D g) {
        BufferedImage image = Sprites.get("table");
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            drawShape(g);
        }
        if (customer != null) {
            customer.draw(g);
        }
    }

    /** วาดโต๊ะด้วยรูปทรง (ใช้เมื่อไม่มีไฟล์รูป) */
    private void drawShape(Graphics2D g) {
        // ขาโต๊ะ
        g.setColor(new Color(110, 70, 40));
        g.fillRect(x + 6, y + 28, 8, height - 28);
        g.fillRect(x + width - 14, y + 28, 8, height - 28);
        // หน้าโต๊ะ
        g.setColor(new Color(160, 105, 60));
        g.fillRoundRect(x, y, width, 30, 8, 8);
        g.setColor(new Color(120, 80, 45));
        g.drawRoundRect(x, y, width, 30, 8, 8);
    }
}
