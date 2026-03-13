package edu.nd.pmcburne.hwapp.one

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import edu.nd.pmcburne.hwapp.one.ui.screens.ScoreboardScreen
import edu.nd.pmcburne.hwapp.one.ui.state.ScoreboardViewModel
import edu.nd.pmcburne.hwapp.one.ui.theme.CollegeBasketballTheme
import edu.nd.pmcburne.hwapp.one.ui.theme.HWStarterRepoTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    private val container by lazy { AppContainer(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CollegeBasketballTheme {
                val viewModel: ScoreboardViewModel = viewModel(
                    factory = ScoreboardViewModel.Factory(container.repository)
                )
                ScoreboardScreen(viewModel)
            }
        }
    }
}