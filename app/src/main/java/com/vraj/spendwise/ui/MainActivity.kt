package com.vraj.spendwise.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.vraj.spendwise.ui.base.BaseComposeActivity
import com.vraj.spendwise.ui.inputexpense.InputExpenseScreen
import com.vraj.spendwise.ui.theme.SpendWiseTheme
import com.vraj.spendwise.ui.totalexpenses.TotalExpensesScreen
import com.vraj.spendwise.util.MainScreen
import com.vraj.spendwise.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : BaseComposeActivity() {

    private var adView: AdView? = null
    private val backgroundScope by lazy { CoroutineScope(Dispatchers.IO) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backgroundScope.launch {
            MobileAds.initialize(this@MainActivity) { }
        }
        loadBannerAd()

        setContent {
            SpendWiseTheme {
                val navHostController = rememberNavController()
                val viewModel = hiltViewModel<MainViewModel>()
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    MainScreens(
                        navHostController = navHostController,
                        viewModel = viewModel,
                        modifier = Modifier.padding(bottom = 40.dp)
                    )

                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.fillMaxSize(),
                        content = { BannerAd() }
                    )
                }
            }
        }
        hideStatusBar()
    }

    private fun loadBannerAd() {
        adView?.destroy()
        val displayMetrics = this.resources.displayMetrics
        val screenWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, screenWidth)
        adView = AdView(this).apply {
            adUnitId = AD_UNIT_ID
            setAdSize(adSize)
            loadAd(AdRequest.Builder().build())
        }
    }

    @Composable
    private fun BannerAd(adUnitId: String = AD_UNIT_ID) {
        AndroidView(
            factory = { context ->
                adView ?: AdView(context).apply {
                    setAdSize(AdSize.BANNER)
                    setAdUnitId(adUnitId)
                    loadAd(AdRequest.Builder().build())
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }

    @Composable
    private fun MainScreens(
        navHostController: NavHostController,
        viewModel: MainViewModel,
        modifier: Modifier = Modifier
    ) {
        NavHost(
            navController = navHostController,
            startDestination = MainScreen.InputExpenseScreen.route,
            modifier = modifier
        ) {
            composable(MainScreen.InputExpenseScreen.route) {
                InputExpenseScreen(navHostController, viewModel)
            }
            composable(MainScreen.TotalExpensesScreen.route) {
                TotalExpensesScreen(navHostController, viewModel)
            }
        }
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-8992410147925175/5469790461"
    }
}