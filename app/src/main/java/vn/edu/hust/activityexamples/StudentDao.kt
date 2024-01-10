package vn.edu.hust.activityexamples

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface StudentDao {
    @Query("SELECT * FROM student")
    fun getAllStudents(): List<Student>

    @Query("SELECT * FROM student WHERE studentId=:studentId")
    fun getStudentById(studentId: Int): Student

    @Insert
    fun insertStudent(student: Student)

    @Update
    fun updateStudent(student: Student)

    @Query("DELETE FROM student WHERE studentId=:studentId")
    fun deleteStudent(studentId: Int): Int

}
