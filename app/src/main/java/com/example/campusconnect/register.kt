package com.example.campusconnect

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.campusconnect.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class Register : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.main.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupDatePicker()
        setupRegisterButton()
    }

    private fun setupDatePicker() {
        binding.etDob.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val dateString = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                    binding.etDob.setText(dateString)
                },
                year, month, day
            )
            datePickerDialog.show()
        }
    }

    private fun setupRegisterButton() {
        binding.btnRegister.setOnClickListener {
            // 1. Get all data
            val studentNum = binding.etStudentNumber.text.toString()
            val fname = binding.etFname.text.toString()
            val mname = binding.etMname.text.toString()
            val lname = binding.etLname.text.toString()
            val email = binding.etEmail.text.toString()
            val phone = binding.etPhone.text.toString()
            val college = binding.etCollege.text.toString()
            val program = binding.etProgram.text.toString()
            val password = binding.etPassword.text.toString()
            val dob = binding.etDob.text.toString()

            // Get Gender
            val selectedGenderId = binding.rgGender.checkedRadioButtonId
            var gender = ""
            if (selectedGenderId != -1) {
                val radioButton = findViewById<RadioButton>(selectedGenderId)
                gender = radioButton.text.toString()
            }

            // 2. Validation
            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. Create Request Object
            val request = RegisterRequest(
                student_number = studentNum,
                fname = fname,
                mname = mname,
                lname = lname,
                gender = gender,
                dob = dob,
                email = email,
                phone = phone,
                college = college,
                program = program,
                password = password
            )

            // 4. SEND TO DATABASE (The Network Call)
            ApiClient.instance.registerUser(request).enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val result = response.body()!!
                        if (result.status == "success") {
                            showSuccessDialog() // Only show if PHP says success
                        } else {
                            Toast.makeText(this@Register, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this@Register, "Server Error", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@Register, "Network Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Successful")
        builder.setMessage("Your account has been created successfully! You can now log in.")
        builder.setPositiveButton("Go to Login") { dialog, which ->
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        builder.setCancelable(false)
        val dialog = builder.create()
        dialog.show()
    }
}