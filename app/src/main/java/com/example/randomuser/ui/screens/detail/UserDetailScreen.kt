package com.example.randomuser.ui.screens.detail

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.randomuser.R
import com.example.randomuser.domain.error.UserDetailError
import com.example.randomuser.domain.model.User
import com.example.randomuser.ui.common.UiState
import com.example.randomuser.ui.theme.CriticalRed
import com.example.randomuser.ui.theme.RandomUserTheme

@Composable
fun UserDetailScreen(
    onBack: () -> Unit,
    viewModel: UserDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val errorMessage = when (uiState) {
        is UiState.Error -> when ((uiState as UiState.Error).error) {
            UserDetailError.IdNotProvided -> stringResource(R.string.error_id_not_provided)
            UserDetailError.LoadFailed -> stringResource(R.string.error_load_failed)
        }

        else -> null
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (uiState) {
            is UiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is UiState.Empty -> Text(
                text = stringResource(R.string.error_user_not_found),
                modifier = Modifier.align(Alignment.Center)
            )

            is UiState.Error -> {
                val message = when ((uiState as UiState.Error).error) {
                    UserDetailError.IdNotProvided -> stringResource(R.string.error_id_not_provided)
                    UserDetailError.LoadFailed -> stringResource(R.string.error_load_failed)
                }
                Text(
                    text = message,
                    color = CriticalRed,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }

            is UiState.Success -> {
                UserDetailContent(
                    user = (uiState as UiState.Success).data,
                    onBack = onBack
                )
            }
        }
    }

}

@Composable
private fun UserDetailContent(
    user: User,
    onBack: () -> Unit,
    avatarSize: Dp = 120.dp,
) {
    val gradientHeight = avatarSize + 24.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .height(gradientHeight)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(start = 8.dp, end = 8.dp, top = 80.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        AvatarWithDialog(
            pictureUrl = user.picture,
            avatarSize = avatarSize
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Hi how are you today?\nI'm",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fillMaxSize()) {
            UserDetailTabs(user)
        }
    }

    Box(
        modifier = Modifier
            .padding(12.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .size(32.dp)
            .background(MaterialTheme.colorScheme.onPrimary, CircleShape)
            .clickable(onClick = onBack),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun AvatarWithDialog(
    pictureUrl: String?,
    avatarSize: Dp,
) {
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(avatarSize)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        if (pictureUrl != null) {
            SubcomposeAsyncImage(
                model = pictureUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showDialog = true },
                contentScale = ContentScale.Crop
            ) {
                when (painter.state) {
                    is AsyncImagePainter.State.Loading -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_no_image),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.padding(24.dp)
                        )
                    }

                    is AsyncImagePainter.State.Error -> {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_error_picture),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            modifier = Modifier.padding(24.dp)
                        )
                    }

                    else -> SubcomposeAsyncImageContent()
                }

                if (showDialog) {
                    Dialog(onDismissRequest = { showDialog = false }) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { showDialog = false },
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = pictureUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        } else {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            )
        }
    }
}

@Composable
private fun UserDetailTabs(user: User) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    val tabs = listOf(
        "Info" to Icons.Default.AccountCircle,
        "Phone" to Icons.Default.Phone,
        "Email" to Icons.Default.Email,
        "Location" to Icons.Default.Place
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondary,
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    ) {
        Column {
            UserDetailTabBar(
                tabs = tabs,
                selectedTab = selectedTab,
                onSelect = { selectedTab = it }
            )

            UserDetailTabContent(
                user = user,
                selectedTab = selectedTab
            )
        }
    }
}

@Composable
private fun UserDetailTabBar(
    tabs: List<Pair<String, ImageVector>>,
    selectedTab: Int,
    onSelect: (Int) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        tabs.forEachIndexed { index, pair ->
            val isSelected = selectedTab == index

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 0.dp
                        )
                    )
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else Color.Transparent
                    )
                    .clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null
                    ) { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    pair.second,
                    contentDescription = pair.first,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun UserDetailTabContent(
    user: User,
    selectedTab: Int,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onPrimary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            when (selectedTab) {
                0 -> UserDetailInfoTab(user)
                1 -> UserDetailPhoneTab(user)
                2 -> UserDetailEmailTab(user)
                3 -> UserDetailLocationTab(user)
            }
        }
    }
}

@Composable
private fun UserDetailInfoTab(user: User) {
    val nameParts = user.fullName.split(" ")
    val firstName = nameParts.getOrNull(0) ?: "-"
    val lastName = nameParts.drop(1).joinToString(" ").ifEmpty { "-" }

    InfoRow(label = stringResource(R.string.first_name), value = firstName)
    InfoRow(label = stringResource(R.string.last_name), value = lastName)
    InfoRow(label = stringResource(R.string.gender), value = user.gender.orEmpty())
    InfoRow(label = stringResource(R.string.age), value = user.age.orEmpty())
    InfoRow(label = stringResource(R.string.date_of_birth), value = user.dob.orEmpty())
}

@Composable
private fun UserDetailPhoneTab(user: User) {
    InfoRow(
        label = stringResource(R.string.phone),
        value = user.phone.orEmpty()
    )
}

@Composable
private fun UserDetailEmailTab(user: User) {
    InfoRow(
        label = stringResource(R.string.email),
        value = user.email.orEmpty()
    )
}

@Composable
private fun UserDetailLocationTab(user: User) {
    InfoRow(
        label = stringResource(R.string.location),
        value = user.location ?: "-"
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun UserDetailScreenPreview() {
    val sampleUser = User(
        uid = "1",
        fullName = "Vadim Maksimets",
        email = "vadim@example.com",
        phone = "+7 978 7612216",
        gender = "male",
        location = "Sevastopol",
        nat = "RU",
        picture = "https://randomuser.me/api/portraits/men/1.jpg",
        thumbnail = "https://randomuser.me/api/portraits/thumb/men/1.jpg",
        dob = "13.11.2025",
        age = "28",
    )

    RandomUserTheme {
        UserDetailContent(
            user = sampleUser,
            onBack = {}
        )
    }
}
