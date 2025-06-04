package com.tshabalala.surveyapp

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.FirebaseDatabase
import com.tshabalala.surveyapp.models.Survey
import java.util.Calendar

class FillOutSurveyActivity : AppCompatActivity() {

    private lateinit var editTextPhone :EditText
    private lateinit var editTextEmail :EditText
    private lateinit var editTextDOB   :EditText
    private lateinit var editTextName  :EditText
    private lateinit var buttonSubmit  : Button

    private lateinit var checkBoxPizza : CheckBox
    private lateinit var checkBoxPasta : CheckBox
    private lateinit var checkBoxPapWors : CheckBox
    private lateinit var checkBoxOther : CheckBox

    private var pizzaIsChecked: Boolean = false
    private var pastaIsChecked: Boolean = false
    private var papWorsIsChecked: Boolean = false
    private var otherIsChecked: Boolean = false

    private lateinit var navViewSurvey: TextView

    private  var dobYear: Int = 0
    private  var dobMonth: Int = 0
    private  var dobDay: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_fill_out_survey)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        intiUI()

        navViewSurvey.setOnClickListener {
            val intent = Intent(this, ViewSurveyActivity::class.java)
            startActivity(intent)
        }

        // on below line we are adding
        // click listener for our edit text.
        editTextDOB.setOnClickListener {

            // on below line we are getting
            // the instance of our calendar.
            val c = Calendar.getInstance()

            // on below line we are getting
            // our day, month and year.
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

            // on below line we are creating a
            // variable for date picker dialog.
            val datePickerDialog = DatePickerDialog(
                // on below line we are passing context.
                this,
                { view, year, monthOfYear, dayOfMonth ->
                    // on below line we are setting
                    // date to our edit text.
                    val dat = (dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                    editTextDOB.setText(dat)
                    dobYear = year
                    dobDay = dayOfMonth
                    dobMonth = monthOfYear +1


                },
                // on below line we are passing year, month
                // and day for the selected date in our date picker.
                year,
                month,
                day
            )
            // at last we are calling show
            // to display our date picker dialog.
            datePickerDialog.show()
        }

        checkBoxPizza.setOnCheckedChangeListener { buttonView, isChecked ->
                //Log.d("CHECKBOXES", "Pizza is checked: $isChecked")
                pizzaIsChecked = isChecked
            }

        checkBoxPasta.setOnCheckedChangeListener { buttonView, isChecked ->
            //Log.d("CHECKBOXES", "Pasta is checked: $isChecked")
            pastaIsChecked =isChecked
        }

        checkBoxPapWors.setOnCheckedChangeListener { buttonView, isChecked ->
            //Log.d("CHECKBOXES", "Pap & Wors is checked: $isChecked")
            papWorsIsChecked = isChecked
        }

        checkBoxOther.setOnCheckedChangeListener { buttonView, isChecked ->
            //Log.d("CHECKBOXES", "Other is checked: $isChecked")
            otherIsChecked = isChecked
        }


        var moviesRate = 0 // Default value

        findViewById<RadioGroup>(R.id.radGroupMoves).setOnCheckedChangeListener { _, checkedId ->
            moviesRate = when (checkedId) {
                R.id.rbMovesStronglyAgree -> 1
                R.id.rbMovesAgree -> 2
                R.id.rbMovesNeutral -> 3
                R.id.rbMovesDisagree -> 4
                R.id.rbMovesStrongDisagree -> 5
                else -> 0 // Default value if none of the radio buttons are selected
            }
        }

        var radioRate = 0 // Default value

        findViewById<RadioGroup>(R.id.radGroupRadio).setOnCheckedChangeListener { _, checkedId ->
            radioRate = when (checkedId) {
                R.id.rbRadioStrongAgree -> 1
                R.id.rbRadioAgree -> 2
                R.id.rbRadioNeutral -> 3
                R.id.rbRadioDisagree -> 4
                R.id.rbRadioStrongDisagree -> 5
                else -> 0 // Default value if none of the radio buttons are selected
            }
        }

        var eatOutRate = 0 // Default value

        findViewById<RadioGroup>(R.id.radGroupEatOut).setOnCheckedChangeListener { _, checkedId ->
            eatOutRate = when (checkedId) {
                R.id.rbEatOutStrongAgree -> 1
                R.id.rbEatOutAgree -> 2
                R.id.rbEatOutNeutral -> 3
                R.id.rbEatOutDisagree -> 4
                R.id.rbEatOutStrongDisagree -> 5
                else -> 0 // Default value if none of the radio buttons are selected
            }
        }

        var tvRate = 0 // Default value

        findViewById<RadioGroup>(R.id.radGroupTV).setOnCheckedChangeListener { _, checkedId ->
            tvRate = when (checkedId) {
                R.id.rbTVStrongAgree -> 1
                R.id.rbTVAgree -> 2
                R.id.rbTVNeutral -> 3
                R.id.rbTVDisagree -> 4
                R.id.rbTVStrongDisagree -> 5
                else -> 0 // Default value if none of the radio buttons are selected
            }
        }



        buttonSubmit.setOnClickListener {
            // Code to execute when the button is clicked
            if(editTextPhone.text.isNullOrBlank()){
                editTextPhone.error = "Contact number required"
            }else if (editTextEmail.text.isNullOrBlank()){
                editTextEmail.error = "Email required"
            }else if (editTextDOB.text.isNullOrBlank()){
                editTextDOB.error = "DOB required"
            }else if (editTextName.text.isNullOrBlank()){
                editTextName.error = "Names required"
            } else if(moviesRate == 0){
                Toast.makeText(this@FillOutSurveyActivity, "Movies rating is required", Toast.LENGTH_LONG).show()

            }else if(radioRate == 0){
                Toast.makeText(this@FillOutSurveyActivity, "Radio rating is required", Toast.LENGTH_LONG).show()

            }else if(eatOutRate == 0){
                Toast.makeText(this@FillOutSurveyActivity, "Eat out rating is required", Toast.LENGTH_LONG).show()

            }else if(tvRate == 0){
                Toast.makeText(this@FillOutSurveyActivity, "TV rating is required", Toast.LENGTH_LONG).show()

            } else {

                val survey = Survey("",editTextName.text.toString(),editTextDOB.text.toString(),editTextPhone.text.toString(),editTextEmail.text.toString(),
                    pizzaIsChecked,pastaIsChecked,papWorsIsChecked,otherIsChecked,moviesRate,radioRate,eatOutRate,tvRate,dobYear,dobDay,dobMonth)

                val database = FirebaseDatabase.getInstance().reference.child("survey").push().setValue(survey)
                Toast.makeText(this@FillOutSurveyActivity, "survey saved", Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun intiUI(){

        editTextName = findViewById(R.id.edtFullNames)
        editTextEmail = findViewById(R.id.edtEmail)
        editTextPhone = findViewById(R.id.edtPhone)
        editTextDOB = findViewById(R.id.edtDOB)
        buttonSubmit = findViewById(R.id.btnSubmit)

        checkBoxPizza = findViewById(R.id.ckPizza)
        checkBoxPasta = findViewById(R.id.ckPasta)
        checkBoxPapWors = findViewById(R.id.ckPap)
        checkBoxOther = findViewById(R.id.ckOther)

        navViewSurvey = findViewById(R.id.tvNavViewSurvey)


    }
}