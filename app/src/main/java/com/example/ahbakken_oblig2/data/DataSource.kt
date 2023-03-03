package com.example.ahbakken_oblig2.data

import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.model.AlpacaPartyList
import com.example.ahbakken_oblig2.model.Party
import com.example.ahbakken_oblig2.model.Vote
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.xml.*

//returns list of parties
class DataSource(private val path: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    //Gets the page with different parties with suspend
    suspend fun fetchAlpacaParties(): List<AlpacaParty> {
        val alpacaPartyList: AlpacaPartyList = client.get(path).body()
        return alpacaPartyList.parties
    }
}
//for the JSON files
class VoteDataSource(private val path: String) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }
    suspend fun fetchAlpacaVote(): List<Vote> {
        return client.get(path).body() //string with ids
    }

}
//for the XML file
class VoteDataSourceXML(private val path : String){
    private val client = HttpClient {
        install(ContentNegotiation) {
            xml()
        }
    }
    suspend fun fetchXMLVote() : List<Party>{
        return XMLparser().parse(client.get(path).body())
    }
}

