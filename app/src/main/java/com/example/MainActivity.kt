package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppTab
import com.example.ui.MahadiViewModel
import com.example.ui.components.CyberBottomNavBar
import com.example.ui.screens.ChatAiScreen
import com.example.ui.screens.HudDashboardScreen
import com.example.ui.screens.PersonaStudioScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MahadiAiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MahadiAiTheme {
                val viewModel: MahadiViewModel = viewModel()
                MahadiAppRoot(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MahadiAppRoot(
    viewModel: MahadiViewModel
) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()

    // BackHandler to return to HUD Home from sub-screens
    BackHandler(enabled = currentTab != AppTab.HUD_HOME) {
        viewModel.selectTab(AppTab.HUD_HOME)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground),
        bottomBar = {
            CyberBottomNavBar(
                selectedTab = currentTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
        },
        containerColor = CyberBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AppTab.HUD_HOME -> HudDashboardScreen(viewModel = viewModel)
                AppTab.CHAT -> ChatAiScreen(viewModel = viewModel)
                AppTab.TOOLS -> ToolsScreen(viewModel = viewModel)
                AppTab.PERSONA -> PersonaStudioScreen(viewModel = viewModel)
            }
        }
    }
}
