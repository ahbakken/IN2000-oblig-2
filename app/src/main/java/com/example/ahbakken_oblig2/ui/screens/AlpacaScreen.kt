package com.example.ahbakken_oblig2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ahbakken_oblig2.R
import com.example.ahbakken_oblig2.model.AlpacaParty
import com.example.ahbakken_oblig2.ui.theme.Ahbakken_oblig2Theme


@Composable
fun AlpacaScreen(
    alpacaUiState: AlpacaUiState,
    modifier: Modifier = Modifier
) {
    when (alpacaUiState) {
        is AlpacaUiState.Success -> AlpacaPartyScreen(alpacaUiState.alpacaList, modifier)
        is AlpacaUiState.Error -> ErrorScreen(modifier)
        else -> LoadingScreen(modifier)
    }
}


@Composable
fun AlpacaPartyScreen(parties: List<AlpacaParty>, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(items = parties, key = { party -> party.id }) { party ->
            AlpacaPartyCard(party)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlpacaPartyCard(party: AlpacaParty, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(4.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Spacer(
            modifier = modifier
                .background(color = Color(party.color.toColorInt()))
        )
        Text(text = party.name)
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(party.img)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.loading_img),
            contentDescription = stringResource(R.string.alpacaLeader),
            contentScale = ContentScale.Crop,
            alignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
        )
        Text(text = "Leader: ${party.leader}")
        Text(text = "Votes: [PLACEHOLDER]")
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.loading),
            fontSize = 24.sp
        )
    }
}
@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(stringResource(R.string.loading_failed))
    }
}
@Preview(showBackground = true)
@Composable
fun PhotosGridScreenPreview() {
    Ahbakken_oblig2Theme {
        val mockData = List(10) {
            AlpacaParty("1", "AlpacaNorth","Chewpaca","https://cdn.pixabay.com/photo/2019/06/24/10/42/alpaca-4295702_960_720.jpg", "#edb879")
        }
        AlpacaPartyScreen(mockData)
    }
}