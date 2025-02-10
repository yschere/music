package com.example.music.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.music.ui.Screen
import kotlinx.coroutines.launch
import java.time.format.TextStyle
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavDrawer(
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
    onNavigationIconClick: () -> Unit,
    gesturesEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
//    val navController = rememberNavController()
//    val currentBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = currentBackStackEntry?.destination?.route ?: Screen.Home
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val coroutineScope = rememberCoroutineScope()

    val screens = listOf<MenuItem>(
        MenuItem(0, "Home", "Home Screen", Screen.Home, true),
        MenuItem(1, "Library", "Library Screen", Screen.Library, true),
        MenuItem(2, "Player", "Now Playing Screen", Screen.Player, true)
//        Screen.Home,
//        Screen.Library,
//        Screen.Player,
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                NavDrawerHeader()
                Divider(thickness = 1.dp, modifier = Modifier.padding(bottom = 20.dp))
                NavDrawerBody(
                    screens,
                    onItemClick = {
                    }
                )
//                screens.forEach { screen ->
//                    NavigationDrawerItem(
//                        label = { Text(text = screen.route) },
//                        selected = currentRoute == screen.route,
//                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                launchSingleTop = true
//                            }
//                            coroutineScope.launch {
//                                drawerState.close()
//                            }
//                        }
//                    )
//                }

            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = currentRoute.toString().replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu icon")
                        }
                    }
                )
            }
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding())
            ) {
                //DrawerNavigation(navController)
            }
        }
    }
}

@Composable
fun NavDrawerHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp)
    ) {
        Text(text = "Musicality", style = MaterialTheme.typography.displaySmall)
    }
}

@Composable
fun NavDrawerBody(
    items: List<MenuItem>,
    modifier: Modifier = Modifier,
    onItemClick: (MenuItem) -> Unit,
) {
    LazyColumn(modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{
                        onItemClick(item)
                    }.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = item.title,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

data class MenuItem(
    val id: Long,
    val title: String,
    val contentDescription: String,
    val dest: Screen,
    val isClickable: Boolean
)
*/