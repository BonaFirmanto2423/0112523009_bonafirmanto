package com.example.kulinernav.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kulinernav.data.Place

/**
 * Entity Room = satu baris pada tabel "places".
 * Memiliki 6 field dengan primary key id yang dibuat otomatis oleh Room.
 */
@Entity(tableName = "places")
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "rating") val rating: Double,
    @ColumnInfo(name = "description") val description: String
)

/** Konversi Entity (data layer) -> model yang dipakai UI. */
fun PlaceEntity.toPlace(): Place = Place(id, name, category, location, rating, description)

/** Konversi model UI -> Entity untuk disimpan ke Room. */
fun Place.toEntity(): PlaceEntity = PlaceEntity(id, name, category, location, rating, description)
