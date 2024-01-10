package vn.edu.hust.activityexamples

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class AddStudentActivity : AppCompatActivity() {

    private lateinit var editTextBirthday: EditText
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.create_student)

        val studentDao = AppDatabase.getInstance(application).studentDao()

        // Lấy các đối tượng từ giao diện
        val editTextMSSV = findViewById<EditText>(R.id.editTextMSSV)
        val editTextHoTen = findViewById<EditText>(R.id.editTextHoTen)
//        val datePicker = findViewById<DatePicker>(R.id.datePicker)
        val autoCompleteProvince = findViewById<AutoCompleteTextView>(R.id.autoCompleteProvince)
        val buttonAddStudent = findViewById<Button>(R.id.buttonAddStudent)


        // Đọc dữ liệu từ file provinces.txt trong res/raw
        val inputStream = resources.openRawResource(R.raw.provinces)
        val provinceList = mutableListOf<String>()
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach {
                provinceList.add(it.trim())
            }
        }

        Log.d("FileReading", "Danh sách tỉnh thành: $provinceList")

        // Tạo ArrayAdapter và gán cho AutoCompleteTextView
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, provinceList)
        autoCompleteProvince.setAdapter(adapter)

        buttonAddStudent.setOnClickListener {
            val mssv = editTextMSSV.text.toString().toInt()
            val name = editTextHoTen.text.toString()
            val email = getEmailFromStudent(name, mssv)
            val dateOfBirth = editTextBirthday.text.toString()
            val province = autoCompleteProvince.text.toString()
            val student = Student(mssv, name, email, dateOfBirth, province)

            lifecycleScope.launch(Dispatchers.IO) {
                val ret = studentDao.insertStudent(student)
                Log.d("studentDao", "insert student $ret")
            }

            finish()
        }

        editTextBirthday = findViewById(R.id.textViewNgaySinh)

        editTextBirthday.setOnClickListener {
            showDatePickerDialog()
        }
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

        // Kiểm tra chiều dài của chuỗi studentId.toString()
        val studentIdString = studentId.toString()
        val studentIdSubstring = if (studentIdString.length >= 8) {
            studentIdString.substring(2, 8)
        } else {
            // Xử lý nếu chiều dài không đạt đến 8
            studentIdString
        }

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