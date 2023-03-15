package com.example.ahbakken_oblig2.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ahbakken_oblig2.data.DataSource
import com.example.ahbakken_oblig2.data.VoteDataSource
import com.example.ahbakken_oblig2.data.VoteDataSourceXML
import com.example.ahbakken_oblig2.data.AlpacaUiState
import com.example.ahbakken_oblig2.data.VoteUiState
import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.model.Party
import com.example.ahbakken_oblig2.model.Vote
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlpacaViewModel: ViewModel() {
    //the parties
    private val alpacaPartyDataSource = DataSource("https://in2000-proxy.ifi.uio.no/alpacaapi/alpacaparties")
    private var district by mutableStateOf(" ")

    private val _alpacaUiState = MutableStateFlow(AlpacaUiState(listOf(), district))
    val alpacaUiState : StateFlow<AlpacaUiState> = _alpacaUiState.asStateFlow()

    //the votes
    private val dataSourceDistrict1 = VoteDataSource("https://in2000-proxy.ifi.uio.no/alpacaapi/district1")
    private val dataSourceDistrict2 = VoteDataSource("https://in2000-proxy.ifi.uio.no/alpacaapi/district2")
    private val dataSourceDistrict3 = VoteDataSourceXML("https://in2000-proxy.ifi.uio.no/alpacaapi/district3")

    private val _voteUiState = MutableStateFlow(VoteUiState(mutableListOf(), mutableListOf(),mutableListOf()))
    val voteUiState : StateFlow<VoteUiState> = _voteUiState.asStateFlow()

    init {
        getAlpacaParties()
        getPartyVotes(
            dataSourceDistrict1,
            dataSourceDistrict2,
            dataSourceDistrict3,
        )
    }

    private fun getAlpacaParties() {
            viewModelScope.launch {
                val alpacaPartyList = alpacaPartyDataSource.fetchAlpacaParties()
                _alpacaUiState.value = AlpacaUiState(alpacaPartyList, "")
            }
    }
    private fun getPartyVotes(dataSource1: VoteDataSource, dataSource2: VoteDataSource, dataSource3: VoteDataSourceXML){
        viewModelScope.launch {
            val votes1 = dataSource1.fetchAlpacaVote()
            val votes2 = dataSource2.fetchAlpacaVote()
            val votes3 = dataSource3.fetchXMLVote()

            _voteUiState.value = VoteUiState(votes1,votes2,votes3)

        }
    }
    //sum votes for parties in 1 district
    fun sumAlpacaVotes(districtList : List<Vote>) : Map<String,Int>{
        val alpacaPartyVotes = mutableMapOf<String,Int>()
        districtList.forEach {
            if(alpacaPartyVotes.containsKey(it.id)){
                alpacaPartyVotes[it.id] = alpacaPartyVotes[it.id]!! +1
            }else{
                alpacaPartyVotes[it.id] = 1
            }
        }
        return alpacaPartyVotes
    }
    fun sumAlpacaVotesXML(districtListXML : List<Party>) : Map<String,Int?>{
        val alpacaPartyVotes = mutableMapOf<String,Int?>()
        districtListXML.forEach {
            alpacaPartyVotes[it.id.toString()] = it.votes
        }
        return alpacaPartyVotes
    }
    //sum based on id in the different districts by combining the maps.
    fun sumAlpacaPartyVotes(district1 : Map<String,Int>, district2 : Map<String,Int>, district3 : Map<String,Int?>): Map<String,Int> {
        val sumAlpacaPartyVotes = mutableMapOf<String,Int>()
        for (key in district1.keys) {
            if (district2.containsKey(key) && district3.containsKey(key)) {
                val sumPartyVotes = district1.getValue(key) + district2.getValue(key) + district3.getValue(key)!!
                sumAlpacaPartyVotes[key] = sumPartyVotes
            }
        }
        return sumAlpacaPartyVotes
    }
    fun updateDistrict(district : String){
        _alpacaUiState.value = alpacaUiState.value.copy(
            alpacaParties = _alpacaUiState.value.alpacaParties,
            district = district
        )
    }

    fun totalVotesMap(alpacaParty: AlpacaParty): String {
        //JSON, district 1 and 2
        val voteMapDistrict1 = sumAlpacaVotes(voteUiState.value.votes1)
        val voteMapDistrict2 = sumAlpacaVotes(voteUiState.value.votes2)
        //XML, district 3
        val voteMapDistrict3 = sumAlpacaVotesXML(voteUiState.value.votes3)

        if (alpacaUiState.value.district == "All districts") {
            val totalVotesMap = sumAlpacaPartyVotes(voteMapDistrict1, voteMapDistrict2, voteMapDistrict3)
            val percentVotes = String.format("%.1f", (totalVotesMap[alpacaParty.id]?.div(( totalVotesMap.values.sum().toFloat() )))?.times(100 ))
            return "${ totalVotesMap[alpacaParty.id] } --- $percentVotes"
        }
        if (alpacaUiState.value.district == "District 1"){
            val totalVotes = voteMapDistrict1[alpacaParty.id]
            val percentVotes = String.format("%.1f", (voteMapDistrict1[alpacaParty.id]!!/voteUiState.value.votes1.size.toFloat()*100))
            return "$totalVotes --- $percentVotes"
        }
        if (alpacaUiState.value.district == "District 2"){
            val totalVotes = voteMapDistrict2[alpacaParty.id]
            val percentVotes = String.format("%.1f", (voteMapDistrict2[alpacaParty.id]!!/voteUiState.value.votes2.size.toFloat()*100))
            return "$totalVotes --- $percentVotes"
        }
        return if (alpacaUiState.value.district == "District 3"){
            val totalVotes = voteMapDistrict3[alpacaParty.id]
            val percentVotes = String.format("%.1f", (voteMapDistrict3[alpacaParty.id]?.div(( voteUiState.value.votes3.sumOf { it.votes!! }.toFloat() )))?.times(100 ))
            "$totalVotes --- $percentVotes"
        } else {
            " --- "
        }
    }



}