package com.mowakib.radio.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RadioDao {

    @Query("SELECT * FROM databaseradio")
    fun getRadios(): LiveData<List<DatabaseRadio>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg radios: DatabaseRadio)

    @Query("SELECT * FROM favdatabaseradio")
    fun getFavRadios(): LiveData<List<FavDatabaseRadio>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(radio: FavDatabaseRadio)

    @Delete
    fun delete(radio: FavDatabaseRadio)

    @Query("SELECT EXISTS (SELECT 1 FROM favdatabaseradio WHERE logo = :logo)")
    fun isFav(logo: String): Boolean

    @Update
    fun update(radio: FavDatabaseRadio)
}

@Database(entities = [DatabaseRadio::class, FavDatabaseRadio::class], version = 1)
abstract class RadiosDatabase : RoomDatabase() {
    abstract val radioDao: RadioDao
}

private lateinit var INSTANCE: RadiosDatabase

fun getRadioDatabase(context: Context): RadiosDatabase {
    synchronized(RadiosDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                RadiosDatabase::class.java,
                "radios"
            ).build()
        }
    }
    return INSTANCE
}