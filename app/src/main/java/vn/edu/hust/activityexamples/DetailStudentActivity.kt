package vn.edu.hust.activityexamples

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class DetailStudentActivity : AppCompatActivity() {

    private lateinit var editTextBirthday: EditText
    private val calendar = Calendar.getInstance()
    private var isEditing = false
    private lateinit var editTextMSSV: EditText
    private lateinit var editTextHoTen: EditText
    private lateinit var autoCompleteProvince: AutoCompleteTextView
    private lateinit var studentDao: StudentDao
    private var studentId: Int = -1
    private var countClick: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_student)

        editTextMSSV = findViewById(R.id.editTextMSSV)
        editTextHoTen = findViewById(R.id.editTextHoTen)
        autoCompleteProvince = findViewById(R.id.autoCompleteProvince)
        editTextBirthday = findViewById(R.id.textViewNgaySinh)
        editTextBirthday.setOnClickListener {
            if (isEditing) {
                showDatePickerDialog()
            }
        }
        val buttonUpdateStudent = findViewById<Button>(R.id.buttonUpdateStudent)
        val buttonDeleteStudent = findViewById<Button>(R.id.buttonDeleteStudent)
        val buttonCancelStudent = findViewById<Button>(R.id.buttonCancelStudent)

        buttonCancelStudent.visibility = View.GONE

        studentId = intent.getIntExtra("studentId", -1)
        Log.d("studentDao", "studentId $studentId")

//        val studentDao = AppDatabase.getInstance(application).studentDao()
        studentDao = AppDatabase.getInstance(application).studentDao()
        lifecycleScope.launch(Dispatchers.IO) {
            val student:Student
            if(studentId != -1) {
                student = studentDao.getStudentById(studentId)
                Log.d("studentDao", "getStudentById student $student")
                editTextMSSV.setText(student.studentId.toString())
                editTextHoTen.setText(student.fullName)
                editTextBirthday.setText(student.dateOfBirth)
                autoCompleteProvince.setText(student.hometown)
            }

        }


        editTextMSSV.isEnabled = false
        editTextHoTen.isEnabled = false
        autoCompleteProvince.isEnabled = false
        editTextBirthday.isEnabled = false

        val inputStream = resources.openRawResource(R.raw.provinces)
        val provinceList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                provinceList.add(it.trim())
            }
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provinceList)
        autoCompleteProvince.setAdapter(adapter)



        buttonUpdateStudent.setOnClickListener {
            enableEditing(true)
            buttonCancelStudent.visibility = View.VISIBLE
            buttonUpdateStudent.text = "Xác nhận"
            countClick += 1

            val mssv = editTextMSSV.text.toString().toInt()
            val name = editTextHoTen.text.toString()
            val email = getEmailFromStudent(name, mssv)
            val dateOfBirth = editTextBirthday.text.toString()
            val province = autoCompleteProvince.text.toString()
            val student = Student(mssv, name, email, dateOfBirth, province)

            if (countClick == 2) {
                lifecycleScope.launch(Dispatchers.IO) {
                    studentDao.updateStudent(student)
                    }
                enableEditing(false)
                buttonCancelStudent.visibility = View.GONE
                buttonUpdateStudent.text = "Cập nhật"
                Log.d("studentDao", "countClick $countClick")
                countClick = 0
            }

        }

        buttonCancelStudent.setOnClickListener {
            enableEditing(false)
            buttonCancelStudent.visibility = View.GONE
            buttonUpdateStudent.text = "Cập nhật"
            countClick = 0
        }

        buttonDeleteStudent.setOnClickListener {
            enableEditing(false)
            showDeleteConfirmationDialog()
        }
    }

    private fun showDeleteConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Xác Nhận Xóa")
        builder.setMessage("Bạn có chắc chắn muốn xóa sinh viên này?")

//        builder.setPositiveButton("Xác Nhận") { _, _ ->
//            lifecycleScope.launch(Dispatchers.IO) {
//                val ret = studentDao.deleteStudent(studentId)
//                Log.d("studentDao", "deleteStudent $ret")
//
//            }
//            finish()
//        }

        builder.setPositiveButton("Xác Nhận") { _, _ ->
            lifecycleScope.launch(Dispatchers.IO) {
                val ret = studentDao.deleteStudent(studentId)
                Log.d("studentDao", "studentId $studentId")
                Log.d("studentDao", "deleteStudent $ret")
                withContext(Dispatchers.Main) {
                    finish()
                }
            }
        }


        builder.setNegativeButton("Hủy") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun enableEditing(isEnabled: Boolean) {
        editTextMSSV.isEnabled = isEnabled
        editTextHoTen.isEnabled = isEnabled
        autoCompleteProvince.isEnabled = isEnabled
        editTextBirthday.isEnabled = isEnabled
    }

    private fun getInitialsFromFullName(fullName: String): String {
        val nameParts = fullName.trim().split(" ")
        val initialsBuilder = StringBuilder()

        for (i in 0 until nameParts.size - 1) {
            initialsBuilder.append(nameParts[i].get(0))
        }

        return initialsBuilder.toString().lowercase()
    }

    private fun getEmailFromStudent(fullName: String, studentId: Int): String {
        val initials = getInitialsFromFullName(fullName)
        val lastName = fullName.substringAfterLast(" ").lowercase()
        val studentIdSubstring = studentId.toString().substring(2, 8)
        val emailPrefix = "$lastName.$initials$studentIdSubstring"

        return "$emailPrefix@sis.hust.edu.vn"
    }

    private fun showDatePickerDialog() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                onDateSet(year, month, dayOfMonth)
            },
            year, month, day
        )

        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun onDateSet(year: Int, month: Int, day: Int) {
        val selectedDate = "$day/${month + 1}/$year"
        editTextBirthday.setText(selectedDate)
    }
}
