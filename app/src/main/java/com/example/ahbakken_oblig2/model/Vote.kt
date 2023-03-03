package com.example.ahbakken_oblig2.model

//JSON file
data class Vote(
    val id : String
    )

//XML file, nullable
data class Party(
    val id : Int?,
    val votes : Int?
    )
