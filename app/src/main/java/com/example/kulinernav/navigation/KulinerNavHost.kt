package com.example.kulinernav.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.kulinernav.ui.detail.DetailRoute
import com.example.kulinernav.ui.form.PlaceFormRoute
import com.example.kulinernav.ui.home.HomeRoute
import com.example.kulinernav.ui.settings.SettingsRoute

/**
 * Navigation graph: Home -> Detail(id) -> PlaceForm(id)
 *                   Home -> PlaceForm()  (tambah baru)
 *                   Home -> Settings     (preference DataStore)
 *
 * NavController hanya ada di sini. ViewModel tidak pernah menyimpan NavController;
 * Route cukup memanggil callback, lalu NavHost yang menentukan tujuan navigasi.
 * Setiap destination mendapat ViewModel sendiri yang terikat pada back stack entry-nya.
 */
@Composable
fun KulinerNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Home) {

        composable<Home> {
            HomeRoute(
                onPlaceClick = { id -> navController.navigate(Detail(id)) },
                onAddClick = { navController.navigate(PlaceForm()) },
                onSettingsClick = { navController.navigate(Settings) },
                onTestInvalidId = { navController.navigate(Detail(id = 999)) }
            )
        }

        composable<Detail> {
            DetailRoute(
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(PlaceForm(id)) },
                // Kembali ke Home yang sudah ada, tanpa membuat Home duplikat
                onDeleted = { navController.popBackStack(Home, inclusive = false) }
            )
        }

        composable<PlaceForm> {
            PlaceFormRoute(
                onCancel = { navController.popBackStack() },
                onSaved = { id, isEditMode ->
                    if (isEditMode) {
                        // Kembali ke Detail yang sama
                        navController.popBackStack()
                    } else {
                        // Buka detail item baru, Form dihapus dari back stack
                        navController.navigate(Detail(id)) {
                            popUpTo(Home) { inclusive = false }
                        }
                    }
                }
            )
        }

        composable<Settings> {
            SettingsRoute(onBack = { navController.popBackStack() })
        }
    }
}
