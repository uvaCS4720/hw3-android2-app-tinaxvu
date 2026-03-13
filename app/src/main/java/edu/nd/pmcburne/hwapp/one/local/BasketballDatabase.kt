package edu.nd.pmcburne.hwapp.one.local

import androidx.room.Database
import androidx.room.RoomDatabase
import edu.nd.pmcburne.hwapp.one.local.GameEntity

@Database(
    entities = [GameEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BasketballDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}