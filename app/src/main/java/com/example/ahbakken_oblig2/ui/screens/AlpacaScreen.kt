package com.example.ahbakken_oblig2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import coil.compose.AsyncImage
import com.example.ahbakken_oblig2.R
import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.model.Party
import com.example.ahbakken_oblig2.model.Vote
import coil.request.ImageRequest
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue



@Composable
fun AlpacaScreen(
    alpacaViewModel : AlpacaViewModel = viewModel(),
) {
    val alpacaUiState by alpacaViewModel.alpacaUiState.collectAsState()
    val districts = listOf("All districts", "District 1", "District 2", "District 3")
    val defaultDistrict = districts[0]

    Column {
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
    districts: List<String>
) {

    var expanded by remember{ mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(defaultDistrict) }

        ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = selectedOptionText,
            onValueChange = { },
            label = { Text("Choose district") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = !expanded },
            ) {
                districts.forEach {selectionOption ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOptionText = selectionOption
                            alpacaViewModel.updateDistrict(selectionOption)
                            expanded = !expanded
                        },
                        text = { selectedOptionText }
                    )

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

    var districtVotes by remember{ mutableStateOf("${alpacaViewModel.sumAlpacaPartyVotes(voteMapDistrict1, voteMapDistrict2, voteMapDistrict3)[alpacaParty.id]}") }
    var currentDistrict by remember{ mutableStateOf(alpacaUiState.district) }

    districtVotes = when (currentDistrict) {
        districts[0] -> if (voteListDistrict1.isNotEmpty() && voteListDistrict2.isNotEmpty() && voteListDistrict3.isNotEmpty() ) {
            "Votes:  ${alpacaViewModel.sumAlpacaPartyVotes(voteMapDistrict1, voteMapDistrict2, voteMapDistrict3)[alpacaParty.id]} %"
        } else {"Could not calculate"}

        districts[1] -> if (voteListDistrict1.isNotEmpty()) {
            "Votes: ${voteMapDistrict1[alpacaParty.id]} --- ${calcPercentVotes(alpacaParty, voteMapDistrict1, (voteListDistrict1.size.toFloat()) * 100)}"
        } else {"Could not calculate"}

        districts[2] -> if (voteListDistrict2.isNotEmpty()) {
            "Votes: ${voteMapDistrict2[alpacaParty.id]} --- ${calcPercentVotes(alpacaParty, voteMapDistrict2, (voteListDistrict2.size.toFloat()) * 100)}"
        }  else {"Could not calculate" }

        districts[3] -> if (voteListDistrict2.isNotEmpty()) {
            "Votes: ${voteMapDistrict3[alpacaParty.id]} --- ${calcPercentVotes(alpacaParty, voteMapDistrict3, (voteListDistrict3.size.toFloat()) * 100)}"
        } else {"Could not calculate" }

        else -> { "Could not calculate" }
    }

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
            
            Text(text = "Votes: $districtVotes")

        }

    }
}

fun calcPercentVotes(
    alpacaParty: AlpacaParty,
    mapDistrict: Map<String, Int?>,
    listDistrictSize: Float
): String {
    val votes: Float = (mapDistrict[alpacaParty.id]!! / listDistrictSize)
    return String.format("%.1f", votes)
}