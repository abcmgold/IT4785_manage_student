package vn.edu.hust.activityexamples

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var studentAdapter: StudentAdapter
    private lateinit var studentDao: StudentDao
    val listStudent = arrayListOf<Student>()
    private val ADD_STUDENT_REQUEST = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        studentDao = AppDatabase.getInstance(application).studentDao()
        Log.d("studentDao", "$studentDao")


        loadStudents()

    }

    private fun loadStudents() {

        lifecycleScope.launch(Dispatchers.IO) {
            val students = studentDao.getAllStudents()
            listStudent.clear()
            listStudent.addAll(students)
            Log.d("studentDao", "get all $students")
        }

        recyclerView.adapter = StudentAdapter(listStudent) {student ->
            val intent = Intent(this, DetailStudentActivity::class.java)
            intent.putExtra("studentId", student.studentId)
            Log.d("studentDao", "get all $student")
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadStudents()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_new_student -> {
                // Log để kiểm tra xem đã click vào menu item hay chưa
                Log.v("TAG", "Permission Granted")

                // Tạo Intent để chuyển sang một hoạt động mới (NewStudentActivity)
                val intent = Intent(this, AddStudentActivity::class.java)

                // Bắt đầu hoạt động mới
                startActivityForResult(intent, ADD_STUDENT_REQUEST)
//                startActivity(intent)
                loadStudents()

                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

}