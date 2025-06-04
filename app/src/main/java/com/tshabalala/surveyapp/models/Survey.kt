package com.tshabalala.surveyapp.models

class Survey (val personID:String = "",
              val fullName:String= "",
              val dateOfBirth:String= "",
              val phone:String= "",
              val email:String= "",
              val likePizza:Boolean = false,
              val likePasta:Boolean= false,
              val likePapWors:Boolean= false,
              val likeOther:Boolean= false,
              val movieRating:Int = 0,
              val RadioRating:Int= 0,
              val eatingOutRating:Int = 0,
              val TVRating:Int = 0,
              val dobYear :Int = 0,
              val dobDay:Int = 0,
              val dobMonth:Int = 0)