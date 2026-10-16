วางรูป PNG ไว้ในโฟลเดอร์นี้ โดยตั้งชื่อไฟล์ให้ตรงตามรายการด้านล่าง
ถ้าไม่มีไฟล์ไหน เกมจะวาดด้วยรูปทรงแทนโดยอัตโนมัติ (ใส่ทีละรูปได้)
รูปจะถูกย่อ/ขยายให้พอดีกับขนาดที่ระบุ แนะนำให้ใช้รูปพื้นหลังโปร่งใส (ยกเว้น floor และ menu_bg)

ชื่อไฟล์                 ขนาดที่แสดงบนจอ (กว้าง x สูง)   หมายเหตุ
-----------------------------------------------------------------------
floor.png               800 x 540     พื้นร้านทั้งหมด (ใต้แถบ HUD)
menu_bg.png             800 x 600     พื้นหลังหน้าเมนู
chef_0.png              40 x 48       เชฟยืน หันขวา
chef_1.png - chef_3.png 40 x 48       เฟรมเดิน หันขวา (ถ้าไม่มี จะใช้ chef_0)
customer_normal.png     34 x 46       ลูกค้าทั่วไป (แมวส้มอ้วน)
customer_vip.png        34 x 46       ลูกค้า VIP (หมูเด้ง ฮิปโปแคระใส่มงกุฎ)
customer_impatient_0/1.png          34 x 46   คาปิบาราใจเย็น (เคี้ยว 2 เฟรม)
customer_impatient_annoyed_0/1.png  34 x 46   คาปิบาราหงุดหงิด (ความอดทน <= 60%)
customer_impatient_angry_0/1.png    34 x 46   คาปิบาราโกรธหน้าแดง (ความอดทน <= 30% หรือเดินออก)
(ทุกชื่อใส่เลขเฟรม _0, _1, ... เพื่อให้เคลื่อนไหวได้ หรือใช้รูปเดียวไม่มีเลขก็ได้)
stove.png               64 x 72       เตาว่าง
stove_cooking_0.png     64 x 72       เตากำลังอบ เฟรม 1 (สลับกับเฟรม 2 ให้ไฟกระพริบ)
stove_cooking_1.png     64 x 72       เตากำลังอบ เฟรม 2
stove_ready.png         64 x 72       อาหารสุกแล้ว
stove_burnt.png         64 x 72       อาหารไหม้ (ถ้าไม่มีรูปสถานะไหน จะใช้ stove.png แทน)
table.png               71 x 44       โต๊ะ
trash.png               50 x 65       ถังขยะปิดฝา (ด้านบนเว้นที่ไว้ให้ฝายก)
trash_open_0.png        50 x 65       ฝาแง้ม
trash_open_1.png        50 x 65       ฝาเปิดสุด
food_salad.png          24 x 24
food_burger.png         24 x 24       หรือ food_burger_0.png, food_burger_1.png, ... (เคลื่อนไหว)
food_pizza.png          24 x 24       หรือ food_pizza_0.png ... food_pizza_6.png (เคลื่อนไหว)
food_burnt_pizza.png    24 x 24       พิซซ่าไหม้ (ไหม้เมนูไหนใช้รูปเมนูนั้น)
food_burnt_burger.png   24 x 24       เบอร์เกอร์ไหม้
food_burnt_salad.png    24 x 24       สลัดไหม้
food_burnt.png          24 x 24       (สำรอง) ใช้เมื่อไม่มีรูปไหม้ของเมนูนั้น

หมายเหตุ: ต้องรันเกมจากโฟลเดอร์โปรเจกต์ (CrazyRestaurant) ซึ่ง VS Code ทำให้อยู่แล้ว

---- ฉากตลาดและวัตถุดิบ ----
market_bg.png           800 x 540     พื้นหลังตลาด
stall_salad.png         120 x 110     แผงผัก
stall_burger.png        120 x 110     แผงเนื้อ
stall_pizza.png         120 x 110     แผงชีส/แป้ง
fridge.png / fridge_open.png  44 x 72 ตู้เย็นปิด / เปิด (ตอนหยิบของ)
door.png                52 x 72       ประตูในร้าน (ไปตลาด)
door_market.png         52 x 72       ประตูที่ตลาด (กลับร้าน)
ingredient_salad.png    24 x 24       วัตถุดิบดิบที่ถือไปใส่เตา
ingredient_burger.png   24 x 24
ingredient_pizza.png    24 x 24
