package com.example.randomuser.ui.screens.list

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.randomuser.R
import com.example.randomuser.domain.error.UserListError
import com.example.randomuser.domain.model.User
import com.example.randomuser.ui.theme.RandomUserTheme

@Composable
fun UserListScreen(
    modifier: Modifier = Modifier,
    onGenerate: () -> Unit,
    onOpenDetail: (String) -> Unit,
    viewModel: UserListViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val errorMessage by viewModel.error.collectAsState()
    val context = LocalContext.current

    val message = when (errorMessage) {
        UserListError.NoInternet -> stringResource(R.string.no_internet)
        UserListError.FetchFailed -> stringResource(R.string.error_fetch_users)
        UserListError.LoadFailed -> stringResource(R.string.error_load_users)
        UserListError.DeleteFailed -> stringResource(R.string.error_delete_user)
        null -> null
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.refreshUsersFromDb()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onGenerate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        UserListScreenContent(
            users = users,
            isLoadingMore = isLoadingMore,
            onOpenDetail = onOpenDetail,
            onDeleteUser = { viewModel.deleteUser(it) },
            modifier = modifier.padding(paddingValues)
        )
    }
}

@Composable
private fun UserListScreenContent(
    users: List<User>,
    isLoadingMore: Boolean,
    onOpenDetail: (String) -> Unit,
    onDeleteUser: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (users.isEmpty() && !isLoadingMore) {
            Text(
                text = stringResource(R.string.error_no_data),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                items(users) { user ->
                    UserCard(
                        user = user,
                        onClick = { onOpenDetail(user.uid) },
                        onDelete = { onDeleteUser(user.uid) }
                    )
                }

                if (isLoadingMore) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun UserCard(
    user: User,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(80.dp)
                    .clip(shape = MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.onPrimary)
            ) {
                if (user.picture != null) {
                    SubcomposeAsyncImage(
                        model = user.picture,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    ) {
                        when (painter.state) {
                            is AsyncImagePainter.State.Loading -> {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }

                            is AsyncImagePainter.State.Error -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_no_image),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    modifier = Modifier.padding(24.dp)
                                )
                            }

                            else -> {
                                SubcomposeAsyncImageContent()
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

            UserInfoColumn(
                user = user,
                modifier = Modifier.weight(1f)
            )

            UserCardMenu(
                onOpenProfile = onClick,
                onDelete = onDelete,
            )
        }
    }
}

@Composable
private fun UserInfoColumn(
    user: User,
    modifier: Modifier = Modifier,
) {
    val flagRes = user.nat?.let {
        when (it.uppercase()) {
            "US" -> R.drawable.ic_us
            "AU" -> R.drawable.ic_au
            else -> R.drawable.ic_no_image
        }
    }

    Column(
        modifier = modifier
            .padding(start = 16.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {

        Text(
            text = user.fullName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = user.phone.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.tertiary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(verticalAlignment = Alignment.CenterVertically) {

            AsyncImage(
                model = flagRes,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = user.nat.orEmpty(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun UserCardMenu(
    onOpenProfile: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {

        IconButton(
            onClick = { expanded = true },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {

            DropdownMenuItem(
                text = { Text(stringResource(R.string.open_profile)) },
                onClick = {
                    expanded = false
                    onOpenProfile()
                }
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.delete)) },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun UserListScreenPreview() {
    RandomUserTheme {
        UserListScreenContent(
            users =
                listOf(
                    User(
                        uid = "1",
                        fullName = "Maksimets Vadim",
                        phone = "+7 978 7612216",
                        nat = "RU",
                        email = "",
                        thumbnail = "",
                        gender = "",
                        dob = "",
                        age = "",
                        picture = "",
                        location = ""
                    ),
                    User(
                        uid = "2",
                        fullName = "Maksimets Vadim",
                        phone = "+7 978 7612216",
                        nat = "RU",
                        email = "",
                        thumbnail = "",
                        gender = "",
                        dob = "",
                        age = "",
                        picture = "",
                        location = ""
                    )
                ),
            onOpenDetail = {},
            onDeleteUser = {},
            isLoadingMore = false,
        )
    }
}
