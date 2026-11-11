package com.example.kulinernav.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kulinernav.data.Place
import com.example.kulinernav.data.settings.SortOrder
import com.example.kulinernav.ui.theme.KulinerNavTheme

/**
 * Stateless screen: hanya merender HomeUiState dan mengirim event lewat callback.
 * Tidak menerima NavController maupun ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onQueryChange: (String) -> Unit,
    onCategorySelected: (String?) -> Unit,
    onResetFilter: () -> Unit,
    onSortOrderChange: (SortOrder) -> Unit,
    onRetry: () -> Unit,
    onPlaceClick: (Int) -> Unit,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onTestInvalidId: () -> Unit,
    modifier: Modifier = Modifier
) {
    // UI-element state (hanya untuk tampilan menu), bukan screen state
    var menuExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Kuliner Pilihan", fontWeight = FontWeight.Bold)
                        Text(
                            if (uiState.isLoading) "Memuat..."
                            else "${uiState.places.size} dari ${uiState.totalCount} tempat • ${uiState.sortOrder.label}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Settings, contentDescription = "Pengaturan")
                    }
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Tes buka ID invalid (999)") },
                            onClick = { menuExpanded = false; onTestInvalidId() }
                        )
                        SortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        (if (order == uiState.sortOrder) "✓ " else "    ") +
                                            "Urutkan: ${order.label}"
                                    )
                                },
                                onClick = { menuExpanded = false; onSortOrderChange(order) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Tambah") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onQueryChange,
                placeholder = { Text("Cari nama atau lokasi...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { onQueryChange("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Hapus pencarian")
                        }
                    }
                },
                singleLine = true,
                enabled = !uiState.isLoading && uiState.errorMessage == null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            )

            if (uiState.categories.isNotEmpty() && uiState.errorMessage == null) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { onCategorySelected(null) },
                            label = { Text("Semua") }
                        )
                    }
                    items(uiState.categories) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category,
                            onClick = { onCategorySelected(category) },
                            label = { Text(category) }
                        )
                    }
                }
            }

            when {
                uiState.isLoading -> LoadingContent()
                uiState.errorMessage != null -> ErrorContent(uiState.errorMessage, onRetry)
                uiState.isEmpty -> EmptyContent(uiState.hasActiveFilter, onResetFilter)
                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.places, key = { it.id }) { place ->
                        PlaceCard(place = place, onClick = { onPlaceClick(place.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(Modifier.height(12.dp))
        Text("Memuat daftar kuliner...")
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Filled.Warning, contentDescription = null,
            tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(message, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRetry) { Text("Coba Lagi") }
    }
}

@Composable
private fun EmptyContent(hasActiveFilter: Boolean, onResetFilter: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Filled.Search, contentDescription = null, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(12.dp))
        Text(
            if (hasActiveFilter) "Tidak ada tempat yang cocok dengan pencarian."
            else "Belum ada tempat kuliner.",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )
        if (hasActiveFilter) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onResetFilter) { Text("Reset Filter") }
        }
    }
}

@Composable
private fun PlaceCard(place: Place, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.Star, contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(place.rating.toString(), style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.size(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                AssistChip(onClick = onClick, label = { Text(place.category) })
                Spacer(Modifier.width(8.dp))
                Icon(Icons.Filled.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                Text(
                    place.location,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ---------------- Preview (tanpa ViewModel) ----------------

private val previewPlaces = listOf(
    Place(1, "Sate Khas Senayan", "Sate", "Jakarta Pusat", 4.6, ""),
    Place(2, "Soto Betawi H. Ma'ruf", "Soto", "Jakarta Pusat", 4.5, ""),
    Place(3, "Gudeg Yu Djum", "Gudeg", "Yogyakarta", 4.7, "")
)

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    KulinerNavTheme {
        HomeScreen(
            uiState = HomeUiState(
                isLoading = false, places = previewPlaces, totalCount = 3,
                categories = listOf("Gudeg", "Sate", "Soto")
            ),
            onQueryChange = {}, onCategorySelected = {}, onResetFilter = {},
            onSortOrderChange = {}, onRetry = {}, onPlaceClick = {}, onAddClick = {},
            onSettingsClick = {}, onTestInvalidId = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeEmptyPreview() {
    KulinerNavTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = false, query = "pizza", totalCount = 12,
                categories = listOf("Sate", "Soto")),
            onQueryChange = {}, onCategorySelected = {}, onResetFilter = {},
            onSortOrderChange = {}, onRetry = {}, onPlaceClick = {}, onAddClick = {},
            onSettingsClick = {}, onTestInvalidId = {}
        )
    }
}
