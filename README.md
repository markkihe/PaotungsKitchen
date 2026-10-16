# Paotung's Kitchen (เป๋าตุงพุงกาง)

เกมทำอาหาร 2D แบบบริหารเวลา พัฒนาด้วย Java + Swing สำหรับวิชา Object-Oriented Programming

## วิธีรัน

**ต้องมี:** JDK 21 ขึ้นไป (ทดสอบกับ Eclipse Temurin 21)

**VS Code:** ติดตั้ง Extension Pack for Java แล้วเปิดโฟลเดอร์ `CrazyRestaurant` → เปิด `src/app/Main.java` → กด **Run**

**Terminal** (รันที่โฟลเดอร์ `CrazyRestaurant`):

```
javac -encoding UTF-8 -d bin -sourcepath src src/app/Main.java
java -cp bin app.Main
```

ต้องรันจากโฟลเดอร์ `CrazyRestaurant` เพราะเกมโหลดรูปและเสียงจาก `assets/` และบันทึกสถิติลง `highscore.txt` ในโฟลเดอร์นี้
ถ้าไม่มี `assets/` เกมยังเล่นได้ครบ แต่จะวาดทุกอย่างด้วยรูปทรงแทนรูปภาพ และไม่มีเสียง

## วิธีเล่น

| ปุ่ม | การทำงาน |
|---|---|
| WASD / ลูกศร | เดิน |
| SPACE | ทำทุกอย่าง: ซื้อของ, หยิบวัตถุดิบ, ใส่เตา, หยิบอาหาร, เสิร์ฟ, ทิ้งขยะ, เข้าประตู |
| SHIFT | วิ่ง (ใช้เกจพลังงาน) |
| 1 / 2 / 3 | (ทางลัด) เลือกวัตถุดิบเองที่ตู้เย็น / ซื้ออัปเกรดตอนจบวัน |
| P | หยุด / เล่นต่อ |
| M | ปิด / เปิดเสียง |
| ENTER | ไปวันถัดไป / เริ่มใหม่หลังแพ้ |

1. **ช่วงเช้า (30 วินาที):** เริ่มที่ตลาด ซื้อวัตถุดิบเป็นชุด แล้วเข้าประตู RESTAURANT เพื่อเปิดร้าน
2. **เปิดร้าน (90 วินาที):** ตู้เย็น → เตา → หยิบอาหารที่สุก (ก่อนไหม้!) → เสิร์ฟลูกค้าให้ตรงเมนู
3. ของหมดกลางวัน ออกไปตลาดได้ แต่ลูกค้าในร้านยังรออยู่
4. **จบวัน:** จ่ายค่าเช่า แล้วซื้ออัปเกรดได้ 1 ชิ้น
5. ลูกค้ารอไม่ไหวหรือเสิร์ฟผิด = เสีย 1 ดาว, เสิร์ฟถูกติดกัน 5 ครั้ง = ได้ดาวคืน
6. **แพ้เมื่อ:** ดาวหมด หรือเงินไม่พอจ่ายค่าเช่า (เกมไม่มีวันจบ แข่งทำ High Score)

## โครงสร้างโปรเจกต์

```
src/
├── app/          Main (จุดเริ่มโปรแกรม)
├── ui/           หน้าจอ Swing: GameFrame, MenuPanel, GamePanel, MenuButton, KeyHandler, SoundManager
├── logic/        กฎของเกม: DayManager, CustomerSpawner, HighScoreManager
└── model/        ข้อมูลและวัตถุในเกม
    ├── scene/    Scene (abstract), RestaurantScene, MarketScene
    ├── food/     Food (abstract), Salad, Burger, Pizza, BurntFood, Ingredient, Menu
    ├── customer/ Customer (abstract), NormalCustomer, ImpatientCustomer, VipCustomer
    └── upgrade/  Upgrade (abstract), SecondStove, FastStove, Shoes, Patience
assets/
├── images/       รูป PNG (รายชื่อไฟล์และขนาดอยู่ใน images/README.txt)
└── sounds/       เสียง .wav และเพลงพื้นหลัง
```
