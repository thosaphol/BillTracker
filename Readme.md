# Bill Tracker (รายการค้างจ่าย)

แอปจัดการรายการค้างจ่ายส่วนตัว พัฒนาด้วย Jetpack Compose + Kotlin ตามหลัก MVVM และ Clean Architecture โปรเจกต์นี้ทำขึ้นเป็น portfolio สำหรับสมัครงานสาย Android Developer

## ทำไมถึงทำโปรเจกต์นี้

เดิมมีพื้นฐาน Android แบบ Java/XML/SQLite และกำลังฝึกเปลี่ยนมาใช้ Jetpack Compose + Kotlin เต็มรูปแบบ จึงเลือกทำแอปที่ใช้งานได้จริง ครอบคลุมทักษะที่ต้องใช้ในการทำงานจริง ตั้งแต่ UI, state management, local database, background job, การเข้ารหัสข้อมูล ไปจนถึงการเชื่อมต่อ API ภายนอก

## ฟีเจอร์หลัก

- **CRUD รายการค้างจ่าย** — เพิ่ม แก้ไข ลบ แสดงรายการได้ครบ
- **จัดหมวดหมู่รายจ่าย** — มีหมวดหมู่เริ่มต้น (เช่าบ้าน, ค่าไฟ, ค่าน้ำ, อินเทอร์เน็ต, อื่นๆ) ที่ลบไม่ได้ และเพิ่มหมวดหมู่ของตัวเองได้
- **สถานะอัตโนมัติ** — จ่ายแล้ว / ยังไม่จ่าย / เกินกำหนด คำนวณจากวันที่จริง ไม่ใช่ค่าที่เก็บตรงๆ (กันข้อมูลไม่ตรงกัน)
- **แจ้งเตือนผ่าน WorkManager** — เลือกได้ว่าจะเตือนทุกวันหรือทุกเดือน พร้อม logic เฉพาะสำหรับรอบเดือน (เตือนถี่ช่วงแรกของรอบ แล้วลดความถี่ลงถ้ายังไม่จ่าย)
- **ตรวจสอบวันหยุดธนาคาร** — เชื่อมต่อ BOT API (ธนาคารแห่งประเทศไทย) ผ่าน Retrofit เตือนล่วงหน้า 3 วันถ้าวันครบกำหนดใกล้วันหยุดธนาคาร
- **ส่งออก/นำเข้าข้อมูลแบบเข้ารหัส** — เข้ารหัสไฟล์ backup ด้วย AES-256-GCM + PBKDF2 (ตั้งรหัสผ่านเอง) ก่อนบันทึกลงเครื่อง ป้องกันคนอื่นเปิดอ่านไฟล์ backup ได้
- **Local-only, ไม่มี cloud sync** — ข้อมูลทั้งหมดเก็บในเครื่องเท่านั้น ออกแบบมาเพื่อลดความเสี่ยงด้าน PDPA ตั้งแต่ต้น

## Tech Stack

| ส่วน | เทคโนโลยีที่ใช้ |
|---|---|
| ภาษา | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | MVVM + Clean Architecture (domain / data / ui) |
| Dependency Injection | Hilt |
| Local Database | Room |
| Background Task | WorkManager |
| Network | Retrofit + Gson |
| State | LiveData |
| Encryption | AES-256-GCM + PBKDF2 (javax.crypto) |
| Testing | JUnit — unit test เฉพาะ business logic ที่ไม่ผูกกับ Android framework |

## สถาปัตยกรรม

```
domain/     ← business logic ล้วนๆ ไม่รู้จัก Room, Retrofit, หรือ Android framework เลย
            (model, repository interface, use case)

data/       ← implementation จริงที่ผูกกับ Android/แพลตฟอร์ม
            (Room DAO, Retrofit API, SharedPreferences, WorkManager)

ui/         ← Jetpack Compose (screens, viewmodel, navigation)
            แต่ละ Composable เป็น "dumb component" รับ state ผ่าน parameter
            ไม่เรียก Repository/ViewModel ตรงๆ ในไฟล์ screen
```


## หน้าจอ

1. **รายการบิล** (`BillListScreen`) — หน้าหลัก แสดงยอดค้างชำระรวม + รายการทั้งหมด
2. **เพิ่ม/แก้ไขบิล** (`AddEditBillScreen`) — ใช้หน้าเดียวกันทั้งสองโหมด
3. **รายละเอียดบิล** (`BillDetailScreen`) — ดูรายละเอียด, แจ้งเตือนวันหยุดธนาคาร, ทำเครื่องหมายจ่ายแล้ว
4. **จัดการหมวดหมู่** (`CategoryManageScreen`)
5. **ตั้งค่า** (`SettingsScreen`) — ส่งออก/นำเข้าข้อมูล, ลบข้อมูลทั้งหมด

## ความเป็นส่วนตัว (PDPA)

- ไม่มีระบบ login หรือเก็บข้อมูลระบุตัวตนใดๆ
- ไม่มี analytics หรือ crash reporting SDK ที่ส่งข้อมูลออกจากเครื่อง
- ข้อมูลทั้งหมดเก็บใน Room (local) เท่านั้น
- ไฟล์ backup เข้ารหัสด้วยรหัสผ่านที่ผู้ใช้ตั้งเอง ก่อนบันทึกลงเครื่อง
- ขอ permission เท่าที่จำเป็นจริง (แจ้งเตือน, เขียนไฟล์บน Android เก่ากว่า 10)

## การติดตั้งเพื่อ build เอง

โปรเจกต์นี้เชื่อมต่อ [BOT API](https://portal.api.bot.or.th/) (ธนาคารแห่งประเทศไทย) เพื่อดึงข้อมูลวันหยุดธนาคาร ต้องมี API token ของตัวเอง:

1. สมัครและขอ token ที่ `https://portal.api.bot.or.th/` (product: Financial Institutions' Holidays)
2. สร้างไฟล์ `local.properties` ที่ root ของโปรเจกต์ (ไฟล์นี้ไม่ควร commit ขึ้น git)
3. เพิ่มบรรทัด:
   ```
   BOT_API_TOKEN=<token ของคุณ>
   ```
4. Sync Gradle แล้ว build ได้ตามปกติ

หากไม่มี token ฟีเจอร์อื่นทั้งหมดยังใช้งานได้ปกติ มีแค่การแจ้งเตือนวันหยุดธนาคารที่จะไม่แสดงผล (แอปจัดการ error กรณีไม่มีเน็ต/token ผิดพลาดไว้แล้ว ไม่ทำให้แอปพัง)

## ข้อจำกัดที่ทราบอยู่แล้ว (Known Limitations)

- Reminder ผ่าน WorkManager มีความคลาดเคลื่อนได้ ±15 นาที (ข้อจำกัดของ `PeriodicWorkRequest`)
- ยังไม่มี dark mode toggle ในแอป (follow system theme อัตโนมัติเท่านั้น)

## License

โปรเจกต์นี้ทำเพื่อวัตถุประสงค์ portfolio ส่วนตัว
