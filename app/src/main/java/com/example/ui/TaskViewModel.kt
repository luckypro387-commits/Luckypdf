package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.Task
import com.example.data.TaskPriority
import com.example.data.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TaskFilter {
    ALL,
    ACTIVE,
    COMPLETED
}

enum class TaskSortBy {
    PRIORITY,
    CREATION_DATE,
    ALPHABETICAL
}

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedFilter = MutableStateFlow(TaskFilter.ALL)
    val selectedFilter: StateFlow<TaskFilter> = _selectedFilter

    private val _selectedSortBy = MutableStateFlow(TaskSortBy.PRIORITY)
    val selectedSortBy: StateFlow<TaskSortBy> = _selectedSortBy

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // Dynamically filtered and sorted list of tasks
    val uiState: StateFlow<List<Task>> = combine(
        repository.allTasks,
        _searchQuery,
        _selectedFilter,
        _selectedSortBy,
        _selectedCategory
    ) { tasks, query, filter, sortBy, category ->
        var filteredList = tasks

        // Search query filter
        if (query.isNotEmpty()) {
            filteredList = filteredList.filter {
                it.title.contains(query, ignoreCase = true) ||
                it.description.contains(query, ignoreCase = true)
            }
        }

        // Category filter
        if (category != "All") {
            filteredList = filteredList.filter { it.category == category }
        }

        // Status filter
        filteredList = when (filter) {
            TaskFilter.ALL -> filteredList
            TaskFilter.ACTIVE -> filteredList.filter { !it.isCompleted }
            TaskFilter.COMPLETED -> filteredList.filter { it.isCompleted }
        }

        // Sorting
        when (sortBy) {
            TaskSortBy.PRIORITY -> {
                filteredList.sortedWith(
                    compareBy<Task> { it.isCompleted }
                        .thenByDescending { it.priority.ordinal }
                        .thenByDescending { it.createdAt }
                )
            }
            TaskSortBy.CREATION_DATE -> {
                filteredList.sortedWith(
                    compareBy<Task> { it.isCompleted }
                        .thenByDescending { it.createdAt }
                )
            }
            TaskSortBy.ALPHABETICAL -> {
                filteredList.sortedWith(
                    compareBy<Task> { it.isCompleted }
                        .thenBy { it.title.lowercase() }
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Categories list based on available tasks + defaults
    val categoriesState: StateFlow<List<String>> = repository.allTasks
        .combine(MutableStateFlow(listOf("Personal", "Work", "Shopping", "Health"))) { tasks, defaults ->
            val taskCategories = tasks.map { it.category }.distinct()
            (listOf("All") + (defaults + taskCategories).distinct()).filter { it.isNotEmpty() }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf("All", "Personal", "Work", "Shopping", "Health")
        )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }

    fun setSortBy(sortBy: TaskSortBy) {
        _selectedSortBy.value = sortBy
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun addTask(title: String, description: String, priority: TaskPriority, category: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insert(
                Task(
                    title = title.trim(),
                    description = description.trim(),
                    priority = priority,
                    category = category.trim().ifEmpty { "Personal" }
                )
            )
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            repository.update(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun clearCompleted() {
        viewModelScope.launch {
            repository.clearCompleted()
        }
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
