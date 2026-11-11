package com.example.kulinernav.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database Room aplikasi. Instance dibuat satu kali (singleton) lewat getInstance(),
 * dipanggil dari AppContainer, bukan dari Composable.
 */
@Database(entities = [PlaceEntity::class], version = 1, exportSchema = false)
abstract class KulinerDatabase : RoomDatabase() {

    abstract fun placeDao(): PlaceDao

    companion object {
        @Volatile
        private var INSTANCE: KulinerDatabase? = null

        fun getInstance(context: Context): KulinerDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    KulinerDatabase::class.java,
                    "kuliner.db"
                )
                    .addCallback(SeedCallback)
                    .build()
                    .also { INSTANCE = it }
            }
    }

    /**
     * Mengisi data contoh HANYA saat database pertama kali dibuat (install pertama).
     * Setelah itu semua data dibaca dari Room, bukan dari list hard-coded.
     */
    private object SeedCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            val seed: List<Array<Any>> = listOf(
                arrayOf<Any>("Sate Khas Senayan", "Sate", "Jakarta Pusat", 4.6,
                    "Sate ayam dan kambing dengan bumbu kacang kental, cocok untuk makan malam keluarga."),
                arrayOf<Any>("Soto Betawi H. Ma'ruf", "Soto", "Jakarta Pusat", 4.5,
                    "Soto Betawi legendaris berkuah santan dengan daging dan jeroan sapi."),
                arrayOf<Any>("Gudeg Yu Djum", "Gudeg", "Yogyakarta", 4.7,
                    "Gudeg kering khas Jogja dengan krecek, telur, dan ayam kampung."),
                arrayOf<Any>("Bakmi GM", "Mie", "Jakarta Selatan", 4.4,
                    "Bakmi ayam klasik dengan pangsit goreng yang renyah."),
                arrayOf<Any>("Nasi Goreng Kambing Kebon Sirih", "Nasi Goreng", "Jakarta Pusat", 4.5,
                    "Nasi goreng berbumbu rempah dengan potongan daging kambing empuk."),
                arrayOf<Any>("Kerak Telor Bang Udin", "Jajanan", "Jakarta Pusat", 4.2,
                    "Jajanan khas Betawi dari beras ketan, telur bebek, dan serundeng."),
                arrayOf<Any>("Rawon Setan Mbak Endang", "Rawon", "Surabaya", 4.6,
                    "Rawon berkuah hitam pekat dari kluwek, buka sampai larut malam."),
                arrayOf<Any>("Ayam Taliwang Moro Seneng", "Ayam Bakar", "Mataram", 4.5,
                    "Ayam kampung bakar pedas khas Lombok disajikan dengan plecing kangkung."),
                arrayOf<Any>("Pempek Candy", "Pempek", "Palembang", 4.6,
                    "Pempek kapal selam dan lenjer dengan cuko yang asam pedas manis."),
                arrayOf<Any>("Es Teler 77", "Minuman", "Jakarta Barat", 4.3,
                    "Es teler berisi alpukat, kelapa muda, dan nangka dengan susu kental manis."),
                arrayOf<Any>("Mie Aceh Seulawah", "Mie", "Jakarta Pusat", 4.5,
                    "Mie kuning tebal berbumbu kari khas Aceh, tersedia versi goreng dan kuah."),
                arrayOf<Any>("Coto Makassar Daeng", "Coto", "Makassar", 4.6,
                    "Coto berkuah kental dari kacang dan rempah, disantap dengan ketupat.")
            )
            seed.forEach { row ->
                db.execSQL(
                    "INSERT INTO places (name, category, location, rating, description) VALUES (?, ?, ?, ?, ?)",
                    row
                )
            }
        }
    }
}
