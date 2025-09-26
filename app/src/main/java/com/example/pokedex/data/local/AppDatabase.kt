package com.example.pokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pokedex.data.local.dao.CartDao
import com.example.pokedex.data.local.entity.CartItemEntity

@Database(entities = [CartItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao
}
