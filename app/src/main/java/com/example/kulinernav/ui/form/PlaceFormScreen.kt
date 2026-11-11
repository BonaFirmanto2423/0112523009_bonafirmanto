package com.example.kulinernav.ui.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kulinernav.ui.theme.KulinerNavTheme

/**
 * Stateless screen Form: semua isi input dan pesan error berasal dari PlaceFormUiState.
 * Setiap perubahan dikirim sebagai event ke ViewModel lewat callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceFormScreen(
    uiState: PlaceFormUiState,
    onNameChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onRatingChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditMode) "Edit Tempat" else "Tambah Tempat") },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = "Batal")
                    }
                },
                actions = {
                    if (!uiState.isNotFound) {
                        TextButton(onClick = onReset) { Text("Reset") }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        if (uiState.isNotFound) {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text("Data yang ingin diedit tidak ditemukan.", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                OutlinedButton(onClick = onCancel) { Text("Kembali") }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormField(uiState.name, onNameChange, "Nama Tempat *", uiState.nameError)
            FormField(uiState.category, onCategoryChange, "Kategori * (mis. Sate, Soto)", uiState.categoryError)
            FormField(uiState.location, onLocationChange, "Lokasi / Kota *", uiState.locationError)
            FormField(
                uiState.ratingText, onRatingChange, "Rating (1.0 - 5.0) *", uiState.ratingError,
                keyboardType = KeyboardType.Decimal
            )
            OutlinedTextField(
                value = uiState.description,
                onValueChange = onDescriptionChange,
                label = { Text("Deskripsi (opsional)") },
                minLines = 3,
                isError = uiState.descriptionError != null,
                supportingText = { Text(uiState.descriptionError ?: "${uiState.description.length}/200") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Batal") }
                Button(
                    onClick = onSave,
                    enabled = !uiState.isSaving,
                    modifier = Modifier.weight(1f)
                ) { Text(if (uiState.isSaving) "Menyimpan..." else "Simpan") }
            }
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
private fun PlaceFormPreview() {
    KulinerNavTheme {
        PlaceFormScreen(
            uiState = PlaceFormUiState(name = "Sa", showErrors = true, nameError = "Nama minimal 3 karakter"),
            onNameChange = {}, onCategoryChange = {}, onLocationChange = {}, onRatingChange = {},
            onDescriptionChange = {}, onSave = {}, onReset = {}, onCancel = {}
        )
    }
}
