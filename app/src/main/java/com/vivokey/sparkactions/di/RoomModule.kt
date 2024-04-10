package com.vivokey.sparkactions.di

import android.content.Context
import androidx.room.Room
import com.vivokey.sparkactions.data.ActionDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideActionDatabase(@ApplicationContext context: Context): ActionDatabase {
        return Room.databaseBuilder(
            context,
            ActionDatabase::class.java, "action-database"
        ).fallbackToDestructiveMigration().build()
    }
}