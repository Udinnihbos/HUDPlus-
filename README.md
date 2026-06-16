# HUD Plus 🗺️
**Fabric 1.21.11 (Mounts of Mayhem)** | by Hikaru

---

## 🚀 Cara Dapat File .jar (Tanpa Install Apapun!)

### Step 1 — Buat akun GitHub
Pergi ke https://github.com dan daftar gratis

### Step 2 — Buat repo baru
Klik tombol **+** → **New repository** → kasih nama `hudplus-mod` → klik **Create repository**

### Step 3 — Upload semua file
Di halaman repo yang baru dibuat, klik **uploading an existing file** lalu drag & drop **semua isi folder HudPlus** (bukan folder-nya, tapi isi dalamnya)

> ⚠️ Pastikan struktur file-nya bener:
> ```
> .github/workflows/build.yml   ← WAJIB ADA
> build.gradle
> gradle.properties
> settings.gradle
> src/...
> ```

### Step 4 — Tunggu build otomatis
Setelah upload selesai, GitHub otomatis mulai build!
Pergi ke tab **Actions** → lihat job **Build HUD Plus Mod** → tunggu ~5 menit sampai ✅ hijau

### Step 5 — Download .jar
Klik job yang sudah selesai → scroll ke bawah ke bagian **Artifacts** → klik **hudplus-1.0.0** → file .jar terdownload!

### Step 6 — Install ke Minecraft
1. Install **Fabric Loader 0.18.1** untuk **1.21.11** → https://fabricmc.net/use/installer/
2. Download **Fabric API 0.141.1+1.21.11** → https://modrinth.com/mod/fabric-api
3. Copy `hudplus-1.0.0.jar` + `fabric-api.jar` ke folder `.minecraft/mods/`
4. Launch Minecraft profile Fabric 1.21.11

---

## 🎮 Keybind

| Tombol | Fungsi |
|--------|--------|
| `H` | Toggle seluruh HUD |
| `N` | Tambah waypoint di posisi sekarang |
| `M` | Buka daftar waypoint |
| `F6` | Toggle koordinat |
| `F7` | Toggle FPS |
