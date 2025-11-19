package com.example.campusconnect

import android.app.DatePickerDialog
import android.content.Intent // Import Intent
import android.os.Bundle
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog // Import AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.campusconnect.databinding.ActivityRegisterBinding
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
            // 1. Get text from inputs
            val fname = binding.etFname.text.toString()
            val lname = binding.etLname.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val dob = binding.etDob.text.toString()

            // (I omitted the other variables for brevity, but they are still there in your code)

            // 2. Simple Validation
            if (fname.isEmpty() || lname.isEmpty() || email.isEmpty() || password.isEmpty() || dob.isEmpty()) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 3. SHOW THE SUCCESS POPUP
            // Later, you will put the PHP connection code here.
            // For now, we assume it succeeded:
            showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Registration Successful")
        builder.setMessage("Your account has been created successfully! You can now log in.")

        // "Positive" button is the main action button
        builder.setPositiveButton("Go to Login") { dialog, which ->
            // Navigate back to Login (MainActivity)
            val intent = Intent(this, MainActivity::class.java)

            // This flag clears the "back stack" so the user can't press "Back"
            // and return to the registration form after registering.
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK

            startActivity(intent)
            finish() // Destroys this Register activity
        }

        // Prevent the user from clicking outside the box to close it
        builder.setCancelable(false)

        val dialog = builder.create()
        dialog.show()
    }
}