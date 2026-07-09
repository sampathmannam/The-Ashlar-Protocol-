package com.ashlarprotocol

import android.os.Bundle
import android.os.Build
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.ExistingPeriodicWorkPolicy
import java.util.concurrent.TimeUnit
import java.util.Calendar
import com.ashlarprotocol.ui.TracingBoardApp
import com.ashlarprotocol.ui.theme.MyApplicationTheme
import com.ashlarprotocol.worker.ReminderWorker

class MainActivity : ComponentActivity() {

  private val requestPermissionLauncher = registerForActivityResult(
      ActivityResultContracts.RequestPermission()
  ) { isGranted: Boolean ->
      if (isGranted) {
          scheduleDailyReminder()
      }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    askNotificationPermission()
    scheduleDailyReminder()
    
    setContent {
      MyApplicationTheme {
        TracingBoardApp()
      }
    }
  }
  
  private fun askNotificationPermission() {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
              PackageManager.PERMISSION_GRANTED
          ) {
              // Permission already granted
          } else {
              requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
          }
      }
  }

  private fun scheduleDailyReminder() {
      // Calculate delay to 8:00 PM for the daily reminder
      val currentDate = Calendar.getInstance()
      val dueDate = Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 20)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
      }
      
      if (dueDate.before(currentDate)) {
          dueDate.add(Calendar.HOUR_OF_DAY, 24)
      }
      
      val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
      
      val dailyWorkRequest = PeriodicWorkRequestBuilder<ReminderWorker>(24, TimeUnit.HOURS)
          .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
          .build()
          
      WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
          "DailyReminderWork",
          ExistingPeriodicWorkPolicy.UPDATE,
          dailyWorkRequest
      )
  }
}
