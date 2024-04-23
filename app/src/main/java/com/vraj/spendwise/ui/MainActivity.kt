package com.vraj.spendwise.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vraj.spendwise.ui.base.BaseComposeActivity
import com.vraj.spendwise.ui.inputexpense.InputExpenseScreen
import com.vraj.spendwise.ui.theme.SpendWiseTheme
import com.vraj.spendwise.util.MainScreen
import com.vraj.spendwise.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpendWiseTheme {
                val navHostController = rememberNavController()
                val viewModel = hiltViewModel<MainViewModel>()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    MainScreens(navHostController, viewModel)
                }
            }
        }
        hideStatusBar()
    }
}

@Composable
fun MainScreens(
    navHostController: NavHostController,
    viewModel: MainViewModel
) {
    NavHost(
        navController = navHostController,
        startDestination = MainScreen.InputExpenseScreen.route
    ) {
        composable(MainScreen.InputExpenseScreen.route) {
            InputExpenseScreen(navHostController, viewModel)
        }
        composable(MainScreen.GraphicalDataScreen.route) {

        }
    }
}