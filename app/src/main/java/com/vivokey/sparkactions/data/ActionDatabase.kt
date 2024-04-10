package com.vivokey.sparkactions.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vivokey.sparkactions.domain.models.Action
import com.vivokey.sparkactions.domain.models.ActionDao
import com.vivokey.sparkactions.domain.models.ActionTargetTypeConverter

@Database(entities = [Action::class], version = 3, exportSchema = false)
@TypeConverters(ActionTargetTypeConverter::class)
abstract class ActionDatabase : RoomDatabase() {
    abstract fun actionDao(): ActionDao
}