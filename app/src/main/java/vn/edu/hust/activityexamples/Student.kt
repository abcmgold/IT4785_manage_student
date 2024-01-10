package vn.edu.hust.activityexamples

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Student(
    @PrimaryKey val studentId: Int,
    val fullName: String,
    val email: String,
    val dateOfBirth: String,
    val hometown: String
)

