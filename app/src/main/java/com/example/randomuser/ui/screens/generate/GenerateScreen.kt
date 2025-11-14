package com.example.randomuser.ui.screens.generate

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.view.WindowInsetsController
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.randomuser.R
import com.example.randomuser.ui.filter.Gender
import com.example.randomuser.ui.filter.Nationality
import com.example.randomuser.ui.theme.NoneTransparent
import com.example.randomuser.ui.theme.RandomUserTheme

@Composable
fun GenerateScreen(
    modifier: Modifier = Modifier,
    onGenerate: (gender: String?, nat: String?) -> Unit,
    onBack: () -> Unit,
) {
    var gender by rememberSaveable { mutableStateOf("") }
    var nat by rememberSaveable { mutableStateOf("") }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) SystemBarWhiteIcons()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column {
                GenerateTopBar(
                    title = stringResource(R.string.generate_user),
                    onBack = onBack,
                )
                Column(
                    modifier = Modifier
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Top
                ) {
                    GenderDropdown(selectedGender = gender, onGenderSelected = { gender = it })
                    Spacer(Modifier.height(24.dp))
                    NationalityDropdown(selectedNat = nat, onNatSelected = { nat = it })
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = { onGenerate(gender.ifBlank { null }, nat.ifBlank { null }) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        Text(
                            text = stringResource(R.string.generate).uppercase(),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenerateTopBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Filled.ArrowBackIosNew,
                contentDescription = stringResource(R.string.back),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GenderDropdown(selectedGender: String, onGenderSelected: (String) -> Unit) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    val genderLabel = Gender.options.firstOrNull { it.second == selectedGender }?.let {
        stringResource(id = it.first)
    } ?: stringResource(R.string.gender_any)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = genderLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.select_gender)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(PrimaryNotEditable),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = NoneTransparent,
                unfocusedContainerColor = NoneTransparent,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Gender.options.forEachIndexed { index, (labelRes, value) ->
                val isSelected = value == selectedGender
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(id = labelRes),
                            color = if (isSelected) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onGenderSelected(value)
                        expanded = false
                    }
                )
                if (index < Gender.options.lastIndex) HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NationalityDropdown(selectedNat: String, onNatSelected: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }

    val natLabel = Nationality.options.firstOrNull { it.second == selectedNat }?.let {
        stringResource(id = it.first)
    } ?: ""

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = natLabel,
            onValueChange = {},
            readOnly = true,
            label = { Text(stringResource(R.string.select_nationality)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Nationality.options.forEachIndexed { index, (labelRes, value) ->
                val isSelected = value == selectedNat
                DropdownMenuItem(
                    text = {
                        Text(
                            stringResource(id = labelRes),
                            color = if (isSelected) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {
                        onNatSelected(value)
                        expanded = false
                    }
                )
                if (index < Nationality.options.lastIndex) HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
private fun SystemBarWhiteIcons() {
    val context = LocalContext.current
    val window = (context as? Activity)?.window
    val controller = window?.insetsController

    DisposableEffect(window) {
        controller?.setSystemBarsAppearance(
            0,
            WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
        onDispose {
            controller?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or
                        WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, locale = "ru")
@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun GenerateScreenPreview() {
    RandomUserTheme {
        GenerateScreen(
            onGenerate = { _, _ -> },
            onBack = {},
        )
    }
}
