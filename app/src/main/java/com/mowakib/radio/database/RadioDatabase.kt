package com.mowakib.radio.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface BaseDao<T> {

    @Insert
    fun insert(data: T)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg data: T)

    @Update
    fun update(data: T)

    @Delete
    fun delete(data: T)

}


@Dao
interface RadioDao : BaseDao<DatabaseRadio> {
    @Query("SELECT * FROM databaseradio")
    fun getRadios(): LiveData<List<DatabaseRadio>>
}

@Dao
interface FavRadioDao : BaseDao<FavDatabaseRadio> {
    @Query("SELECT * FROM favdatabaseradio")
    fun getFavRadios(): LiveData<List<FavDatabaseRadio>>

    @Query("SELECT EXISTS (SELECT 1 FROM favdatabaseradio WHERE logo = :logo)")
    fun isFav(logo: String): Boolean
}

@Dao
interface AppDao : BaseDao<DatabaseApps> {
    @Query("SELECT * FROM databaseapps")
    fun getPubApps(): LiveData<List<DatabaseApps>>
}

@Database(
    entities = [DatabaseRadio::class, FavDatabaseRadio::class, DatabaseApps::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract val radioDao: RadioDao
    abstract val favRadioDao: FavRadioDao
    abstract val appDao: AppDao
}

private lateinit var INSTANCE: AppDatabase

fun database(context: Context): AppDatabase {
    synchronized(AppDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "radios"
            ).build()
        }
    }
    return INSTANCE
}