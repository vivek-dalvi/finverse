package com.finverse

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.finverse.ui.navigation.FinverseNavGraph
import com.finverse.ui.theme.MyApplicationTheme
import com.finverse.worker.DailyNotificationWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher = registerForActivityResult(
    ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
    if (isGranted) {
      scheduleDailyNotification()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    } else {
      scheduleDailyNotification()
    }

    setContent {
      MyApplicationTheme {
        FinverseNavGraph()
      }
    }
  }

  private fun scheduleDailyNotification() {
    val currentDate = Calendar.getInstance()
    val dueDate = Calendar.getInstance().apply {
      set(Calendar.HOUR_OF_DAY, 18) // 6:00 PM
      set(Calendar.MINUTE, 0)
      set(Calendar.SECOND, 0)
    }

    if (dueDate.before(currentDate)) {
      dueDate.add(Calendar.HOUR_OF_DAY, 24)
    }

    val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis

    val dailyWorkRequest = PeriodicWorkRequestBuilder<DailyNotificationWorker>(24, TimeUnit.HOURS)
      .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
      .build()

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
      "DailyFinverseReminder",
      ExistingPeriodicWorkPolicy.UPDATE,
      dailyWorkRequest
    )
  }
}
