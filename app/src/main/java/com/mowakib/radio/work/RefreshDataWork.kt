package com.mowakib.radio.work

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import retrofit2.HttpException
import com.mowakib.radio.database.database
import com.mowakib.radio.repo.AppRepository

class RefreshDataWorker(appContext: Context, params: WorkerParameters) :
        CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }

    override suspend fun doWork(): Result {

        val database = database(applicationContext)
        val repository = AppRepository(database)
        return try {
            repository.refreshData()
            Log.d(WORK_NAME, "doWork: -----------------------------------------------------")
            Result.success()
        } catch (e: HttpException) {
            Log.e(WORK_NAME, "doWork: -------------------------ERROR-----------------------")
            Result.retry()
        }
    }
}