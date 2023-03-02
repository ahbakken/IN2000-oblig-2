package com.example.ahbakken_oblig2

import com.example.ahbakken_oblig2.model.AlpacaParty
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//
object AlpacaPartyNetwork {
    val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://in2000-proxy.ifi.uio.no/alpacaapi/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AlpacaPartyAPI::class.java)
    }
}
//Gets the page asunchronized with suspend
interface AlpacaPartyAPI {
    @GET("alpacaparties")
    suspend fun getAlpacaParties(): List<AlpacaParty>
}