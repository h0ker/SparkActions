package com.vivokey.sparkactions.domain.models

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ActionDao {
    @Query("SELECT * FROM actions ORDER BY id DESC LIMIT 1")
    suspend fun getLastInsertedAction(): Action

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: Action)

    @Query("SELECT * FROM actions")
    fun getAllActions(): Flow<List<Action>>

    @Query("SELECT * FROM actions WHERE id == -1 LIMIT 1")
    fun getSelectedAction(): Flow<Action?>

    @Query("DELETE FROM actions WHERE id = -1")
    fun clearActionSelection()

    @Query("DELETE FROM actions WHERE id = :id")
    suspend fun deleteById(id: String)
}