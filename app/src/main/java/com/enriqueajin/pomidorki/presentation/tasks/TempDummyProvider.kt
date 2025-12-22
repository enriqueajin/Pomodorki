package com.enriqueajin.pomidorki.presentation.tasks

import androidx.compose.ui.graphics.Color
import com.enriqueajin.pomidorki.domain.model.Category
import com.enriqueajin.pomidorki.domain.model.Priority
import com.enriqueajin.pomidorki.domain.model.Status
import com.enriqueajin.pomidorki.domain.model.Task
import kotlin.random.Random

fun getRandomDarkColor(): Color {
    val red = Random.nextFloat() * 0.5f
    val myColor = Random.nextFloat() * 0.5f
    val blue = Random.nextFloat() * 0.5f
    return Color(red, myColor, blue, 1f)
}

val myColor = Color.Green

fun getTasks(): List<Task> {
    return listOf(
        Task(
            title = "Going to the gym",
            description = "Make 50 squats, 10 reps each",
            targetPomodoros = 5,
            status = Status.IN_PROGRESS,
            priority = Priority.HIGH,
            category = Category("Gym", myColor),
            dueDate = "01/05/2025"
        ),
        Task(
            title = "Finish the report",
            description = "Complete the sales report for Q1",
            targetPomodoros = 4,
            status = Status.TODO,
            priority = Priority.VERY_HIGH,
            category = Category("Work", myColor),
            dueDate = "30/04/2025"
        ),
        Task(
            title = "Study Kotlin",
            description = "Complete the Kotlin advanced course",
            targetPomodoros = 3,
            status = Status.IN_PROGRESS,
            priority = Priority.HIGH,
            category = Category("Education", myColor),
            dueDate = "10/05/2025"
        ),
        Task(
            title = "Buy groceries",
            description = "Buy milk, eggs, and bread",
            targetPomodoros = 1,
            status = Status.TODO,
            priority = Priority.LOW,
            category = Category("Personal", myColor),
            dueDate = "28/04/2025"
        ),
        Task(
            title = "Finish reading book",
            description = "Finish reading the last chapter of 'The Kotlin Handbook'",
            targetPomodoros = 2,
            status = Status.IN_PROGRESS,
            priority = Priority.MEDIUM,
            category = Category("Reading", myColor),
            dueDate = "05/05/2025"
        ),
        Task(
            title = "Prepare for meeting",
            description = "Prepare a presentation for the Monday meeting",
            targetPomodoros = 3,
            status = Status.TODO,
            priority = Priority.HIGH,
            category = Category("Work", myColor),
            dueDate = "03/05/2025"
        ),
        Task(
            title = "Clean the house",
            description = "Vacuum and clean all rooms",
            targetPomodoros = 2,
            status = Status.TODO,
            priority = Priority.LOW,
            category = Category("Personal", myColor),
            dueDate = "27/04/2025"
        ),
        Task(
            title = "Complete workout challenge",
            description = "Complete 30 minutes of HIIT",
            targetPomodoros = 2,
            status = Status.IN_PROGRESS,
            priority = Priority.MEDIUM,
            category = Category("Gym", myColor),
            dueDate = "02/05/2025"
        ),
        Task(
            title = "Fix bug in app",
            description = "Fix the login bug in the app",
            targetPomodoros = 3,
            status = Status.IN_PROGRESS,
            priority = Priority.HIGH,
            category = Category("Development", myColor),
            dueDate = "01/05/2025"
        ),
        Task(
            title = "Take out the trash",
            description = "Take out all the trash from the house",
            targetPomodoros = 1,
            status = Status.TODO,
            priority = Priority.LOW,
            category = Category("Personal", myColor),
            dueDate = "26/04/2025"
        ),
        Task(
            title = "Learn new song on guitar",
            description = "Learn and practice 'Let It Be' by The Beatles",
            targetPomodoros = 4,
            status = Status.TODO,
            priority = Priority.MEDIUM,
            category = Category("Hobby", myColor),
            dueDate = "15/05/2025"
        ),
        Task(
            title = "Attend seminar",
            description = "Attend the online Kotlin seminar on coroutines",
            targetPomodoros = 2,
            status = Status.TODO,
            priority = Priority.MEDIUM,
            category = Category("Education", myColor),
            dueDate = "20/05/2025"
        ),
        Task(
            title = "Plan weekend trip",
            description = "Research and book a place for the weekend getaway",
            targetPomodoros = 3,
            status = Status.DONE,
            priority = Priority.MEDIUM,
            category = Category("Travel", myColor),
            dueDate = "05/05/2025"
        ),
        Task(
            title = "Meditation session",
            description = "20 minutes of guided meditation",
            targetPomodoros = 1,
            status = Status.TODO,
            priority = Priority.LOW,
            category = Category("Wellness", myColor),
            dueDate = "28/04/2025"
        ),
        Task(
            title = "Update LinkedIn profile",
            description = "Refresh work experience and add recent projects",
            targetPomodoros = 2,
            status = Status.IN_PROGRESS,
            priority = Priority.HIGH,
            category = Category("Career", myColor),
            dueDate = "02/05/2025"
        ),
        Task(
            title = "Organize workspace",
            description = "Clean desk, organize cables, and declutter shelves",
            targetPomodoros = 1,
            status = Status.DONE,
            priority = Priority.LOW,
            category = Category("Personal", myColor),
            dueDate = "29/04/2025"
        ),
        Task(
            title = "Practice public speaking",
            description = "Rehearse speech for upcoming conference",
            targetPomodoros = 3,
            status = Status.IN_PROGRESS,
            priority = Priority.HIGH,
            category = Category("Career", myColor),
            dueDate = "04/05/2025"
        ),
        Task(
            title = "Update app documentation",
            description = "Add latest API changes to the documentation",
            targetPomodoros = 2,
            status = Status.TODO,
            priority = Priority.HIGH,
            category = Category("Development", myColor),
            dueDate = "07/05/2025"
        ),
        Task(
            title = "Visit parents",
            description = "Weekend visit and family dinner",
            targetPomodoros = 2,
            status = Status.DONE,
            priority = Priority.MEDIUM,
            category = Category("Family", myColor),
            dueDate = "03/05/2025"
        ),
        Task(
            title = "Watch KotlinConf recordings",
            description = "Catch up on talks missed from the conference",
            targetPomodoros = 3,
            status = Status.TODO,
            priority = Priority.MEDIUM,
            category = Category("Education", myColor),
            dueDate = "18/05/2025"
        ),
        Task(
            title = "Run 5k",
            description = "Complete a 5-kilometer run",
            targetPomodoros = 2,
            status = Status.TODO,
            priority = Priority.HIGH,
            category = Category("Fitness", myColor),
            dueDate = "02/05/2025"
        ),
        Task(
            title = "Design new logo",
            description = "Create initial drafts for the new brand logo",
            targetPomodoros = 4,
            status = Status.IN_PROGRESS,
            priority = Priority.VERY_HIGH,
            category = Category("Design", myColor),
            dueDate = "08/05/2025"
        )
    )
}