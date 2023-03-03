package com.example.ahbakken_oblig2.data

import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.model.Party
import com.example.ahbakken_oblig2.model.Vote

data class AlpacaUiState(
    val alpacaParties: List<AlpacaParty>,
    var district : String
)

data class VoteUiState(
    //JSON votes
    val votes1: List<Vote>,
    val votes2 : List<Vote>,
    //xml vote
    val votes3 : List<Party>
)

