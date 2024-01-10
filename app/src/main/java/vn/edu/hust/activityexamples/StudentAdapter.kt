package vn.edu.hust.activityexamples

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudentAdapter(private val students: List<Student>, private val onItemClick: (Student) -> Unit) :
    RecyclerView.Adapter<StudentAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name)
        val mssvTextView: TextView = itemView.findViewById(R.id.mssv)
        val emailTextView: TextView = itemView.findViewById(R.id.email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_student, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val student = students[position]
        holder.nameTextView.text = student.fullName
        holder.mssvTextView.text = student.studentId.toString()
        holder.emailTextView.text = student.email
//        holder.emailTextView.text = getEmailFromStudent(student)

        holder.itemView.setOnClickListener { onItemClick(student) }
    }

    override fun getItemCount(): Int = students.size

    fun getItem(position: Int): Student {
        return students[position]
    }
}
