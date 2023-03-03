package com.example.ahbakken_oblig2.model

import kotlinx.serialization.Serializable //parse to kotlin object

@Serializable
data class AlpacaParty (
    val id: String, //party id
    val name: String, //party name
    val leader: String, //name of leader of party
    val img: String, //URl image if leader
    val color: String, //hex color
)