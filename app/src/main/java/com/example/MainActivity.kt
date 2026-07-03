package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.TodoDatabase
import com.example.data.TaskRepository
import com.example.ui.TodoScreen
import com.example.ui.TaskViewModel
import com.example.ui.TaskViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    val database = TodoDatabase.getDatabase(this)
    val repository = TaskRepository(database.taskDao())
    val factory = TaskViewModelFactory(repository)
    val viewModel: TaskViewModel by viewModels { factory }

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = androidx.compose.material3.MaterialTheme.colorScheme.background
        ) {
          TodoScreen(viewModel = viewModel)
        }
      }
    }
  }
}
