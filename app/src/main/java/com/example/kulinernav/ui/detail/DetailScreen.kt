package com.example.kulinernav.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kulinernav.data.Place
import com.example.kulinernav.ui.theme.KulinerNavTheme

/**
 * Stateless screen Detail: hanya merender DetailUiState dan mengirim event lewat callback.
 * Tidak menerima NavController maupun ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onConfirmDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val place = uiState.place

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNotFound) "Tidak Ditemukan" else "Detail Kuliner") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (place != null) {
                        IconButton(onClick = { onEdit(place.id) }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Filled.Delete, contentDescription = "Hapus")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading || uiState.isDeleted) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
        } else if (place == null) {
            NotFoundContent(
                requestedId = uiState.placeId,
                onBack = onBack,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            PlaceDetailContent(
                place = place,
                onEdit = { onEdit(place.id) },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }

    if (uiState.showDeleteDialog && place != null) {
        AlertDialog(
            onDismissRequest = onDismissDeleteDialog,
            title = { Text("Hapus tempat?") },
            text = { Text("\"${place.name}\" akan dihapus dari daftar.") },
            confirmButton = {
                TextButton(onClick = onConfirmDelete) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = onDismissDeleteDialog) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun PlaceDetailContent(place: Place, onEdit: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    place.name,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(place.category, style = MaterialTheme.typography.titleSmall)
            }
        }

        InfoRow(label = "ID", value = place.id.toString())
        HorizontalDivider()
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(place.location, style = MaterialTheme.typography.bodyLarge)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary
            )
            Spacer(Modifier.width(8.dp))
            Text("${place.rating} / 5.0", style = MaterialTheme.typography.bodyLarge)
        }
        HorizontalDivider()
        Text("Deskripsi", style = MaterialTheme.typography.titleMedium)
        Text(
            place.description.ifBlank { "Belum ada deskripsi." },
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = onEdit, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Edit, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Edit Tempat")
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text("$label: ", fontWeight = FontWeight.SemiBold)
        Text(value)
    }
}

/** Ditampilkan bila ID tidak ada di data (misal Detail(999)). */
@Composable
private fun NotFoundContent(requestedId: Int, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "Tempat dengan ID $requestedId tidak ditemukan",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Data mungkin sudah dihapus atau ID tidak valid.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onBack) { Text("Kembali ke Daftar") }
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailScreenPreview() {
    KulinerNavTheme {
        DetailScreen(
            uiState = DetailUiState(placeId = 1, isLoading = false, place = Place(1, "Sate Khas Senayan", "Sate", "Jakarta Pusat", 4.6, "Sate ayam dan kambing.")),
            onBack = {}, onEdit = {}, onDeleteClick = {}, onDismissDeleteDialog = {}, onConfirmDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DetailNotFoundPreview() {
    KulinerNavTheme {
        DetailScreen(
            uiState = DetailUiState(placeId = 999, isLoading = false),
            onBack = {}, onEdit = {}, onDeleteClick = {}, onDismissDeleteDialog = {}, onConfirmDelete = {}
        )
    }
}
