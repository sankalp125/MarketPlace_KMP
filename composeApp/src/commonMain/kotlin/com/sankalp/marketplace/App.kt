package com.sankalp.marketplace


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sankalp.marketplace.di.appModule
import com.sankalp.marketplace.di.platformModule
import com.sankalp.marketplace.ui.theme.AppTheme
import com.sankalp.marketplace.utils.AuthEvent
import com.sankalp.marketplace.utils.AuthEventBus
import org.koin.compose.KoinApplication
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

@OptIn(KoinExperimentalAPI::class)
@Composable
fun App() {
    KoinApplication(configuration = koinConfiguration {
        modules(appModule, platformModule)
    }, content = {
        AppTheme{
            Surface(
                modifier = Modifier.fillMaxSize(),
                color    = MaterialTheme.colorScheme.background
            ) {
                val navController = rememberNavController()
                LaunchedEffect(Unit) {
                    AuthEventBus.events.collect { event ->
                        when (event) {
                            is AuthEvent.SessionExpired,
                            is AuthEvent.LoggedOut -> {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                    }
                }
                AppNavGraph(navController = navController)
            }
        }
    })
}