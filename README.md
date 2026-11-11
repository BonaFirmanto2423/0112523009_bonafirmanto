# KulinerNav — Daftar Tempat Kuliner

**Nama:** Bona Firmanto  
**NIM:** 0112523009  
**Kelas:** IF23H

Lanjutan Pertemuan 5 (Multi-Screen Navigation) dan Pertemuan 6 (ViewModel + StateFlow), sekarang **Tugas Pertemuan 7 — Local Persistence dengan Room + DataStore**. Tema: Daftar Tempat / Kuliner (digit NIM 9).

## Fitur
- **Home**: daftar tempat kuliner dari **Room** (LazyColumn), pencarian nama/lokasi, filter kategori, reset filter, menu urutan (Nama A–Z / Rating tertinggi / Terbaru).
- **Detail**: data sesuai `id` (Room Flow), Edit, Hapus dengan dialog konfirmasi, halaman "tidak ditemukan" untuk ID invalid.
- **Form Add/Edit**: insert/update ke Room dengan validasi (nama min. 3 karakter, field wajib, rating 1.0–5.0, deskripsi maks. 200 karakter).
- **Pengaturan**: 2 preference **DataStore**, yaitu *Mode Gelap* dan *Urutan Daftar*.
- **Persistence**: data tambah/edit/hapus dan pengaturan tetap ada setelah aplikasi ditutup lalu dibuka kembali.

## Arsitektur Data Layer
```
UI (Screen) ──event──▶ ViewModel ──▶ PlaceRepository ──▶ PlaceDao ──▶ Room (kuliner.db)
     ▲                    │       └─▶ SettingsRepository ──▶ Preferences DataStore (settings)
     └──── UiState ◀── StateFlow
```
| Komponen | File | Keterangan |
|---|---|---|
| Entity | `data/local/PlaceEntity.kt` | Tabel `places`: id (PK auto), name, category, location, rating, description |
| DAO | `data/local/PlaceDao.kt` | Read `Flow` (3 urutan + byId), `getById`, `insert`, `update`, `deleteById` |
| Database | `data/local/KulinerDatabase.kt` | Singleton `getInstance()`, data contoh hanya diisi saat database pertama kali dibuat |
| Repository | `data/PlaceRepository.kt` | Satu-satunya akses ke DAO; mapping Entity ↔ Place |
| DataStore | `data/settings/SettingsRepository.kt` | `dark_mode` (Boolean), `sort_order` (String) |
| DI manual | `di/AppContainer.kt`, `KulinerApp.kt` | Database & Repository dibuat sekali di Application, bukan di Composable |
| ViewModel factory | `ui/AppViewModelProvider.kt` | Menyuntikkan Repository ke ViewModel |

Aturan yang dipatuhi: ViewModel tidak memanggil DAO langsung, Composable tidak membuat instance database, dan DataStore hanya untuk preference (bukan pengganti tabel utama).

## Route / Destination (type-safe)
| Route | Argument | Keterangan |
|---|---|---|
| `Home` | – | start destination |
| `Detail(id: Int)` | id wajib | dibaca ViewModel lewat `SavedStateHandle.toRoute<Detail>()` |
| `PlaceForm(id: Int = -1)` | id opsional | `-1` = tambah, selain itu edit |
| `Settings` | – | pengaturan DataStore |

## Struktur
```
app/src/main/java/com/example/kulinernav/
├── KulinerApp.kt, MainActivity.kt
├── di/              AppContainer.kt
├── data/            Place.kt, PlaceRepository.kt
│   ├── local/       PlaceEntity.kt, PlaceDao.kt, KulinerDatabase.kt
│   └── settings/    SettingsRepository.kt (DataStore)
├── navigation/      Routes.kt, KulinerNavHost.kt
└── ui/
    ├── AppViewModelProvider.kt
    ├── home/  detail/  form/  settings/   (UiState, ViewModel, Route, Screen)
    └── theme/       Theme.kt
```

## Cara Build / Run
1. Clone repo, buka folder project di **Android Studio** (Ladybug 2024.2 atau lebih baru).
2. Tunggu Gradle Sync selesai.
3. Pilih emulator (API 24+), klik **Run ▶**.
4. **Persistence test**: tambah/edit/hapus data dan ubah Pengaturan → tutup aplikasi dari Recent Apps (swipe) → buka lagi → data dan pengaturan masih sama.
5. Untuk kembali ke data awal: Settings Android → Apps → KulinerNav → Storage → Clear storage.

## Versi Tool / Library Utama
| Komponen | Versi |
|---|---|
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 2.0.21 |
| Compose BOM | 2024.12.01 |
| Navigation Compose | 2.8.5 |
| Lifecycle (ViewModel, runtime-compose) | 2.8.7 |
| kotlinx-serialization-json | 1.7.3 |
| Room (runtime, ktx, compiler via KSP) | 2.6.1 |
| KSP | 2.0.21-1.0.28 |
| DataStore Preferences | 1.1.1 |
| compileSdk / minSdk | 35 / 24 |

## Known Limitation
- Belum ada migrasi database (versi 1); jika struktur Entity diubah, uninstall/clear storage terlebih dahulu.
- Belum ada sinkronisasi ke server; data hanya tersimpan di perangkat.

## Deklarasi Penggunaan AI
- **Penggunaan AI:** Ya
- **Tool yang digunakan:** Claude (Anthropic)
- **Bagian yang dibantu:** struktur awal project, navigation, refactor ke UiState + ViewModel + StateFlow, Room (Entity/DAO/Database), Repository, DataStore, README.
- **Contoh prompt/tujuan penggunaan:** _(isi sendiri)_
- **Apa yang saya ubah/pahami dari hasil AI:** _(isi sendiri)_
