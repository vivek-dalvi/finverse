package com.finverse.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import com.finverse.data.model.FinancialGoal
import com.finverse.data.model.FinancialProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface FinancialDao {
    @Query("SELECT * FROM financial_profile WHERE id = 1")
    fun getProfile(): Flow<FinancialProfile?>

    @Query("SELECT * FROM financial_profile WHERE id = 1")
    suspend fun getProfileDirect(): FinancialProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: FinancialProfile)

    @Query("SELECT * FROM financial_goals")
    fun getGoals(): Flow<List<FinancialGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGoal(goal: FinancialGoal)

    @Query("DELETE FROM financial_goals WHERE id = :id")
    suspend fun deleteGoal(id: Int)
}

@Database(
    entities = [FinancialProfile::class, FinancialGoal::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun financialDao(): FinancialDao
}
