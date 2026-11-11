package com.example.kulinernav.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO = kumpulan query ke tabel "places".
 * - Read memakai Flow: setiap ada perubahan data, Room otomatis mengirim list terbaru.
 * - Operasi tulis memakai suspend agar tidak berjalan di main thread.
 */
@Dao
interface PlaceDao {

    // ---------- Read (Flow) dengan tiga pilihan urutan ----------

    @Query("SELECT * FROM places ORDER BY name COLLATE NOCASE ASC")
    fun observeAllByName(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places ORDER BY rating DESC, name COLLATE NOCASE ASC")
    fun observeAllByRating(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places ORDER BY id DESC")
    fun observeAllNewest(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE id = :id")
    fun observeById(id: Int): Flow<PlaceEntity?>

    // ---------- getById, insert, update, delete ----------

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getById(id: Int): PlaceEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(place: PlaceEntity): Long

    @Update
    suspend fun update(place: PlaceEntity)

    @Query("DELETE FROM places WHERE id = :id")
    suspend fun deleteById(id: Int)
}
