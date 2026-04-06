package com.sankalp.marketplace.ui.on_board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.absoluteValue

@Composable
fun OnBoardRoot(
    onNavigateToLogin : () -> Unit
){
    val viewModel = koinViewModel<OnBoardVm>()
    val state by viewModel.state.collectAsState()
    val onEvent = viewModel::onEvent
    LaunchedEffect(Unit){
        viewModel.effect.collect { effect ->
            when(effect){
                is OnBoardEffect.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }
    OnBoardScreen(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeContent)
            .verticalScroll(rememberScrollState()),
        state = state,
        onEvent = onEvent
    )
}

@Composable
private fun OnBoardScreen(
    modifier: Modifier = Modifier,
    state: OnBoardState,
    onEvent : (OnBoardEvent) -> Unit
){
    val pagerState = rememberPagerState(pageCount = { state.pages.size })
    val scope = rememberCoroutineScope()
    Column(
        modifier = modifier
    ){
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "${pagerState.currentPage + 1}",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "/${pagerState.pageCount}",
                color = Color(0xFF7B7B7B),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Skip",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable{
                    onEvent(OnBoardEvent.NavigateToLogin)
                }
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        HorizontalPager( state = pagerState ) { page ->
            Pager(
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer {
                        val pageOffset = ((pagerState.currentPage-page) + pagerState.currentPageOffsetFraction).absoluteValue
                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    },
                page = state.pages[page]
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(10.dp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp)
        ) {
            if (pagerState.currentPage == pagerState.pageCount - 1){
                Button(
                    text = "Previous",
                    click = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    text = "Next",
                    click = {
                        onEvent(OnBoardEvent.NavigateToLogin)
                    }
                )
            }else if (pagerState.currentPage > 0){
                Button(
                    text = "Previous",
                    click = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage - 1)
                        }
                    }
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    text = "Next",
                    click = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            }else {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    text = "Get Started",
                    click = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                )
            }
        }
    }
}
@Composable
private fun Pager(
    modifier: Modifier = Modifier,
    page : OnboardingPage
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(page.imgRes),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
private fun Button(
    text : String,
    click : () -> Unit
){
    TextButton(onClick = {click()}) {
        Text(
            text = text,
            color = Color(0xFFF83737),
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview(){
    MaterialTheme {
        OnBoardScreen(
            modifier = Modifier.fillMaxSize(),
            OnBoardState(),
            onEvent = {}
        )
    }
}