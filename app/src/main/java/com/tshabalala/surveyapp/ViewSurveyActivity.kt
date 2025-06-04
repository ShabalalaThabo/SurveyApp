package com.tshabalala.surveyapp

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tshabalala.surveyapp.models.Survey

import java.time.LocalDate
import java.time.Period
import kotlin.math.round

class ViewSurveyActivity : AppCompatActivity() {

    private lateinit var navFillSurvey: TextView
    private var surveyArrayList: ArrayList<Survey> = arrayListOf()

    private var totalNumberSurvey :Int = 0
    private var youngestPersonInSurvey :Int = 0
    private var oldestPersonInSurvey :Int = 0
    private var averageAge :Int = 0
    private var pizzaPercentage :Float = 0F
    private var papPercentage :Float = 0F
    private var pastaPercentage :Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_survey)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navFillSurvey = findViewById(R.id.tvNavFillSurvey)

        navFillSurvey.setOnClickListener {
            val intent = Intent(this, FillOutSurveyActivity::class.java)
            startActivity(intent)
        }

        getAllSurvey()

    }

    private fun getAllSurvey() {

        val myRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("survey")
        myRef.addValueEventListener(object : ValueEventListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                surveyArrayList.clear()

                for (childSnapshot in dataSnapshot.children) {

                    val survey = childSnapshot.getValue(Survey::class.java)

                    if (survey != null) {
                        surveyArrayList.add(survey)
                    }
                }

                if(surveyArrayList.isNotEmpty()){

                    totalNumberSurvey = surveyArrayList.count()
                    youngestPersonInSurvey = calYoungest(surveyArrayList)
                    oldestPersonInSurvey = calOldest(surveyArrayList)
                    averageAge = calAverageAge(surveyArrayList)


                    pizzaPercentage = calPizzaPercentage(surveyArrayList)
                    pastaPercentage = calPastaPercentage(surveyArrayList)
                    papPercentage  =  calPapWorsPercentage(surveyArrayList)


                    findViewById<TextView>(R.id.tvTotalNumbetOfSurvey).text = totalNumberSurvey.toString()
                    findViewById<TextView>(R.id.tvAverageAge).text = averageAge.toString()
                    findViewById<TextView>(R.id.tvOldestAge).text = oldestPersonInSurvey.toString()
                    findViewById<TextView>(R.id.tvYoungestAge).text = youngestPersonInSurvey.toString()

                    findViewById<TextView>(R.id.tvPizzaPer).text = pizzaPercentage.toString()
                    findViewById<TextView>(R.id.tvPastaPer).text = pastaPercentage.toString()
                    findViewById<TextView>(R.id.tvPapWorsPer).text = papPercentage.toString()

                    findViewById<TextView>(R.id.tvMoviesPer).text = String.format("%.1f",calAverageMoviesRatings(surveyArrayList))
                    findViewById<TextView>(R.id.tvRadioPer).text = String.format("%.1f",calAverageRadioRatings(surveyArrayList))
                    findViewById<TextView>(R.id.tvEatOutPer).text = String.format("%.1f",calAverageEatOutRatings(surveyArrayList))
                    findViewById<TextView>(R.id.tvTVPer).text = String.format("%.1f",calAverageTVRatings(surveyArrayList))
                }else{
                    findViewById<TextView>(R.id.tvNoSurveysAvailable).visibility = View.VISIBLE
                    findViewById<TextView>(R.id.llBody).visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showMsg(error.toString())
            }
        })
    }

    private fun calAverageTVRatings(surveyArrayList: ArrayList<Survey>):Float{
        var peopleLikeTV = 0

        val totalNumberOfPeople = surveyArrayList.count()

        for (survey in surveyArrayList){
            peopleLikeTV +=  survey.TVRating
        }

        val result = peopleLikeTV.toFloat() / totalNumberOfPeople
        return result

    }

    private fun calAverageEatOutRatings(surveyArrayList: ArrayList<Survey>):Float{
        var peopleLikeEatOut = 0

        val totalNumberOfPeople = surveyArrayList.count()

        for (survey in surveyArrayList){
            peopleLikeEatOut +=  survey.eatingOutRating
        }

        val result = peopleLikeEatOut.toFloat() / totalNumberOfPeople
        return result

    }

    private fun calAverageRadioRatings(surveyArrayList: ArrayList<Survey>):Float{
        var peopleLikeRadio = 0

        val totalNumberOfPeople = surveyArrayList.count()

        for (survey in surveyArrayList){
            peopleLikeRadio +=  survey.RadioRating
        }

        val result = peopleLikeRadio.toFloat() / totalNumberOfPeople
        return result

    }

    private fun calAverageMoviesRatings(surveyArrayList: ArrayList<Survey>):Float{
        var peopleLikeMovies = 0

        val totalNumberOfPeople = surveyArrayList.count()

        for (survey in surveyArrayList){
            peopleLikeMovies +=  survey.movieRating
        }

        val result = peopleLikeMovies.toFloat() / totalNumberOfPeople
        return result

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun calAverageAge(surveyArrayList: ArrayList<Survey>):Int{

        var sumOfAges = 0
        val numberOfPeople = surveyArrayList.count()

        for (survey in surveyArrayList){
            sumOfAges +=  getAge(survey.dobYear,survey.dobMonth,survey.dobDay)
        }

        val average =  round(sumOfAges.toFloat() / numberOfPeople)
        return average.toInt()

    }

    private fun calPapWorsPercentage(surveyArrayList: ArrayList<Survey>):Float{
        var pplLikePapWors = 0
        for (survey in surveyArrayList){
            if(survey.likePapWors){
                pplLikePapWors += 1
            }
        }

        val result = pplLikePapWors.toFloat() / surveyArrayList.count()
        return result * 100

    }

    private fun calPastaPercentage(surveyArrayList: ArrayList<Survey>):Float{
        var pplLikePasta = 0
        for (survey in surveyArrayList){
            if(survey.likePasta){
                pplLikePasta += 1
            }
        }

        val result = pplLikePasta.toFloat() / surveyArrayList.count()
        return result * 100

    }

    private fun calPizzaPercentage(surveyArrayList: ArrayList<Survey>):Float{
         var pplLikePizza  = 0

        for (survey in surveyArrayList){
            if(survey.likePizza){
                pplLikePizza += 1
            }
        }

        val result = pplLikePizza.toFloat() / surveyArrayList.count()
        return result * 100

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calYoungest(surveyArrayList: ArrayList<Survey>):Int{
       var youngest  = getAge(surveyArrayList[0].dobYear,surveyArrayList[0].dobMonth,surveyArrayList[0].dobDay)

        for (survey in surveyArrayList){
            val age :Int = getAge(survey.dobYear,survey.dobMonth,survey.dobDay)
          if(age < youngest){
              youngest = age
          }
        }
        return youngest
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calOldest(surveyArrayList: ArrayList<Survey>):Int{
        var oldes  = getAge(surveyArrayList[0].dobYear,surveyArrayList[0].dobMonth,surveyArrayList[0].dobDay)

        for (survey in surveyArrayList){
            val age :Int = getAge(survey.dobYear,survey.dobMonth,survey.dobDay)
            if(age >= oldes){
                oldes = age
            }
        }
        return oldes
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getAge(year: Int, month: Int, dayOfMonth: Int): Int {
        return Period.between(LocalDate.of(year, month, dayOfMonth),LocalDate.now()).years
    }

    private fun showMsg(msg: String) {
        val toast = Toast.makeText(this@ViewSurveyActivity, msg, Toast.LENGTH_LONG)
        toast.show()
    }


}