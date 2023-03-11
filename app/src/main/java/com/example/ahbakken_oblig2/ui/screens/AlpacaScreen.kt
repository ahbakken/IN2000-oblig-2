package com.example.ahbakken_oblig2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
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
import com.example.ahbakken_oblig2.model.Vote
import androidx.compose.material.Text


@Composable
fun AlpacaScreen(
    alpacaViewModel : AlpacaViewModel = viewModel(),
) {
    val alpacaUiState by alpacaViewModel.alpacaUiState.collectAsState()
    val districts = listOf("All districts", "District 1", "District 2", "District 3")
    val defaultDistrict = districts[0]

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //drop down menu, set district, all districts is default.
        ChooseDistrict(alpacaViewModel, defaultDistrict, districts)
        LazyColumn {
            items(alpacaUiState.alpacaParties){
                AlpacaPartyCard(it, alpacaViewModel, districts)
            }
        }
    }


}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseDistrict(
    alpacaViewModel: AlpacaViewModel,
    defaultDistrict: String,
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
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlpacaPartyCard(
    alpacaParty: AlpacaParty,
    alpacaViewModel: AlpacaViewModel,
    districts: List<String>
) {

    val voteUiState by alpacaViewModel.voteUiState.collectAsState()
    //JSON, district 1 and 2
    val voteListDistrict1 : List<Vote> = voteUiState.votes1
    val voteMapDistrict1 = alpacaViewModel.sumAlpacaVotes(voteListDistrict1)
    val voteListDistrict2 : List<Vote> = voteUiState.votes2
    val voteMapDistrict2 = alpacaViewModel.sumAlpacaVotes(voteListDistrict2)
    //XML, district 3
    val voteListDistrict3: List<Party> = voteUiState.votes3
    val voteMapDistrict3 = alpacaViewModel.sumAlpacaVotesXML(voteListDistrict3)

    val alpacaUiState by alpacaViewModel.alpacaUiState.collectAsState()

    var currentDistrict by remember{ mutableStateOf(alpacaUiState.district) }

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

            if (voteListDistrict1.isNotEmpty() && voteListDistrict2.isNotEmpty() && voteListDistrict3.isNotEmpty() && alpacaUiState.district == "All districts") {
                val totalVotes = alpacaViewModel.sumAlpacaPartyVotes(voteMapDistrict1, voteMapDistrict2, voteMapDistrict3)
                Text(text = "Votes: $totalVotes --- ")
            }
            if (voteListDistrict1.isNotEmpty() && alpacaUiState.district == "District 1"){
                val totalVotes = voteMapDistrict1[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict1[alpacaParty.id]!!/voteListDistrict1.size.toFloat()*100))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }
            if (voteListDistrict2.isNotEmpty() && alpacaUiState.district == "District 2"){
                val totalVotes = voteMapDistrict2[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict1[alpacaParty.id]!!/voteListDistrict2.size.toFloat()*100))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }
            if (voteListDistrict2.isNotEmpty() && alpacaUiState.district == "District 3"){
                val totalVotes = voteMapDistrict3[alpacaParty.id]
                val percentVotes = String.format("%.1f", (voteMapDistrict1[alpacaParty.id]!!/voteListDistrict3.size.toFloat()*100))
                Text(text = "Votes: $totalVotes --- $percentVotes%")
            }
            Text(text = "My problem is here --- ${alpacaUiState.district} --- ${voteMapDistrict1[alpacaParty.id]}")

        }

    }
}

