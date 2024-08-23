package com.mangbaam.remoteconfigservice.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mangbaam.remoteconfigservice.domain.model.User

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.loadUserData()
    }

    when {
        uiState.loading -> LoadingScreen(modifier = modifier)
        uiState.error != null -> ErrorScreen(
            onClickRetry = viewModel::loadUserData,
            errorMessage = uiState.error ?: "",
            modifier = modifier,
        )

        else -> HomeContent(modifier = modifier, user = uiState.user)
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(
    onClickRetry: () -> Unit,
    errorMessage: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(errorMessage, style = MaterialTheme.typography.labelLarge)
            Button(
                modifier = Modifier.padding(top = 4.dp),
                onClick = onClickRetry,
            ) {
                Text("retry".uppercase())
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeContent(
    user: User,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(20.dp),
    ) {
        Text("User Info", style = MaterialTheme.typography.titleLarge)

        // nickname
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = "nickname",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            text = user.nickname,
            style = MaterialTheme.typography.bodyMedium,
        )

        // age
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "age",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            text = user.age.toString(),
            style = MaterialTheme.typography.bodyMedium,
        )

        // married
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "married",
            style = MaterialTheme.typography.titleMedium,
        )
        Row(
            modifier = Modifier.padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = user.married, onCheckedChange = {}, enabled = false)
            Text("Y", style = MaterialTheme.typography.labelSmall)

            Checkbox(checked = !user.married, onCheckedChange = {}, enabled = false)
            Text("N", style = MaterialTheme.typography.labelSmall)
        }

        // skills
        Text(
            modifier = Modifier.padding(top = 8.dp, start = 4.dp),
            text = "skills",
            style = MaterialTheme.typography.titleMedium,
        )
        FlowRow(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            user.skills.sortedByDescending { it.level.level }.forEach { (skillName, level) ->
                val levelColor = when (level.level) {
                    0 -> Color.LightGray
                    1 -> Color.DarkGray
                    2 -> Color.Yellow
                    3 -> Color.Blue
                    4 -> Color.Magenta
                    5 -> Color.Red
                    else -> Color.White
                }
                OutlinedCard(
                    shape = RoundedCornerShape(6.dp),
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        text = buildAnnotatedString {
                            append(skillName)
                            append(" | ")
                            withStyle(SpanStyle(color = levelColor, fontWeight = FontWeight.SemiBold)) {
                                append(level.toString())
                            }
                        },
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
