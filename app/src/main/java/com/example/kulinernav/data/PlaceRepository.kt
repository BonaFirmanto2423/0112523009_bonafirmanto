package com.example.kulinernav.data

import com.example.kulinernav.data.local.PlaceDao
import com.example.kulinernav.data.local.toEntity
import com.example.kulinernav.data.local.toPlace
import com.example.kulinernav.data.settings.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Repository = satu-satunya pintu masuk ke data tempat kuliner.
 * ViewModel memanggil Repository, Repository yang memanggil DAO.
 * Di sini juga terjadi konversi PlaceEntity <-> Place.
 */
class PlaceRepository(private val dao: PlaceDao) {

    fun observePlaces(sortOrder: SortOrder): Flow<List<Place>> {
        val source = when (sortOrder) {
            SortOrder.NAME_ASC -> dao.observeAllByName()
            SortOrder.RATING_DESC -> dao.observeAllByRating()
            SortOrder.NEWEST -> dao.observeAllNewest()
        }
        return source.map { list -> list.map { it.toPlace() } }
    }

    fun observePlace(id: Int): Flow<Place?> =
        dao.observeById(id).map { it?.toPlace() }

    suspend fun getPlace(id: Int): Place? = dao.getById(id)?.toPlace()

    /** Menyimpan data baru dan mengembalikan id yang dibuat Room. */
    suspend fun addPlace(
        name: String, category: String, location: String,
        rating: Double, description: String
    ): Int {
        val entity = Place(0, name, category, location, rating, description).toEntity()
        return dao.insert(entity).toInt()
    }

    suspend fun updatePlace(place: Place) = dao.update(place.toEntity())

    suspend fun deletePlace(id: Int) = dao.deleteById(id)
}
