package com.example.ahbakken_oblig2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.Text
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ahbakken_oblig2.R
import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.model.Party


@Composable
fun AlpacaScreen(
    alpacaViewModel : AlpacaViewModel = viewModel(),
) {
    val alpacaUiState by alpacaViewModel.alpacaUiState.collectAsState()
    val districts = listOf("All districts", "District 1", "District 2", "District 3")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //drop down menu, set district, all districts is default.
        ChooseDistrict(alpacaViewModel, districts)
        LazyColumn {
            items(alpacaUiState.alpacaParties){
                AlpacaPartyCard(it, alpacaViewModel)
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseDistrict(
    alpacaViewModel: AlpacaViewModel,

    districts: List<String>,
) {

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    // We want to react on tap/press on TextField to show menu
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        TextField(
            readOnly = true,
            value = selectedItem,
            onValueChange = {  },
            label = { Text("Choose district") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            districts.forEachIndexed { index, district ->
                DropdownMenuItem(
                    onClick = {
                        selectedItem = districts[index]
                        expanded = false
                        alpacaViewModel.updateDistrict(selectedItem)
                    }
                ) {
                    Text(text = district)
                }
            }
        }
    }

}


@Composable
fun AlpacaPartyCard(
    alpacaParty: AlpacaParty,
    alpacaViewModel: AlpacaViewModel,
) {

    val alpacaUiState by alpacaViewModel.alpacaUiState.collectAsState()
    val voteUiState by alpacaViewModel.voteUiState.collectAsState()

    //JSON, district 1 and 2
    val voteMapDistrict1 = alpacaViewModel.sumAlpacaVotes(voteUiState.votes1)
    val voteMapDistrict2 = alpacaViewModel.sumAlpacaVotes(voteUiState.votes2)
    //XML, district 3
    val voteMapDistrict3 = alpacaViewModel.sumAlpacaVotesXML(voteUiState.votes3)



    ElevatedCard(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(
                Modifier
                    .height(24.dp)
                    .fillMaxWidth()
                    .background(Color(alpacaParty.color.toColorInt()))
            )
            Text(
                text = alpacaParty.name,
                fontSize = 24.sp,
            )
            AsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(alpacaParty.img)
                    .crossfade(true)
                    .build(),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape),
                contentDescription = stringResource(R.string.leader_photo),
            )
            Text(
                text = alpacaParty.leader,
            )

            if (alpacaUiState.district == "All districts") {
                val totalVotesMap = alpacaViewModel.sumAlpacaPartyVotes(voteMapDistrict1, voteMapDistrict2, voteMapDistrict3)
                val percentVotes = String.format("%.1f", (totalVotesMap[alpacaParty.id]?.div(( totalVotesMap.values.sum().toFloat() )))?.times(100 ))
                Text(text = "Votes: ${ totalVotesMap[alpacaParty.id] } --- ${percentVotes}%")
            }
            if (alpacaUiState.district == "District 1"){
                val totalVotes = voteMapDistrict1[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict1[alpacaParty.id]!!/voteUiState.votes1.size.toFloat()*100))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }
            if (alpacaUiState.district == "District 2"){
                val totalVotes = voteMapDistrict2[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict2[alpacaParty.id]!!/voteUiState.votes2.size.toFloat()*100))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }
            if (alpacaUiState.district == "District 3"){
                val totalVotes = voteMapDistrict3[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict3[alpacaParty.id]?.div(( voteUiState.votes3.sumOf<Party>{ it.votes!! }.toFloat() )))?.times(100 ))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }

        }

    }
}

