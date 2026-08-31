package com.example.testing1.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Addchart
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.example.testing1.pages.AddPage
import com.example.testing1.pages.GoalsPage
import com.example.testing1.pages.HistoryPage
import com.example.testing1.pages.HomePage
import com.example.testing1.pages.TotalsPage


@Composable
fun HomeScreen(modifier: Modifier = Modifier, navController: NavController) {

    val navItemList = listOf(
        NavItem("Home",Icons.Default.Home),
        NavItem("Add",Icons.Default.Add),
        NavItem("Totals",Icons.Default.Addchart),
        NavItem("Goals",Icons.Default.AttachMoney),
        NavItem("History",Icons.Default.History)
    )

    var selectedIndex by remember {
        mutableStateOf(0)
    }

  Scaffold(
      bottomBar = {
          NavigationBar {
              navItemList.forEachIndexed{ index, navItem ->
                  NavigationBarItem(
                      selected = index == selectedIndex,
                      onClick ={
                          selectedIndex = index
                      },
                      icon = {
                          Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                      },
                      label = {
                          Text(text = navItem.label)
                      })
              }
          }
      }
  ) {
      ContentScreen(modifier = modifier.padding(it),selectedIndex)
  }
}

@Composable
fun ContentScreen(modifier: Modifier = Modifier,selectedIndex: Int) {
    when(selectedIndex){
        0-> HomePage(modifier)
        1-> AddPage(modifier)
        2-> TotalsPage(modifier)
        3-> GoalsPage(modifier)
        4-> HistoryPage(modifier)
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)