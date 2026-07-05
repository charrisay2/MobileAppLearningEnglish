package com.example

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.news.worker.NewsSyncWorker
import com.example.data.worker.DailyGoalReminderWorker
import com.example.di.AppContainer
import com.example.di.DefaultAppContainer
import java.util.concurrent.TimeUnit

class LinguaMasterApplication : Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        instance = this
        container = DefaultAppContainer(this)
        
        setupBackgroundWorkers()
    }

    companion object {
        lateinit var instance: LinguaMasterApplication
            private set
    }

    private fun setupBackgroundWorkers() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
            
        val newsSyncRequest = PeriodicWorkRequestBuilder<NewsSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "NewsSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            newsSyncRequest
        )

        val dailyGoalRequest = PeriodicWorkRequestBuilder<DailyGoalReminderWorker>(24, TimeUnit.HOURS)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyGoalReminderWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyGoalRequest
        )
    }
}
