package com.segnities007.local.dao

import androidx.room.*
import com.segnities007.local.entity.ItemCheckStateEntity

@Dao
interface ItemCheckStateDao {
    @Query("SELECT * FROM item_check_states")
    suspend fun getAll(): List<ItemCheckStateEntity>

    @Query("SELECT * FROM item_check_states WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ItemCheckStateEntity?

    @Query("SELECT * FROM item_check_states WHERE itemId = :itemId LIMIT 1")
    suspend fun getByItemId(itemId: Int): ItemCheckStateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(state: ItemCheckStateEntity)

    @Update
    suspend fun update(state: ItemCheckStateEntity)

    @Delete
    suspend fun delete(state: ItemCheckStateEntity)

    @Query("DELETE FROM item_check_states WHERE id = :id")
    suspend fun deleteById(id: Int)
}
