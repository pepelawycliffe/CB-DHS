//package com.example.cb_dhs.screens.common.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.cb_dhs.HomeFragment
//import com.example.cb_dhs.MainActivity
//import com.example.cb_dhs.R
//import com.example.cb_dhs.screens.common.auth.LoginScreen
//import com.example.cb_dhs.screens.common.auth.SignUpScreen
//import com.example.cb_dhs.screens.common.auth.model.AuthViewModel
//
//@Composable
//fun AuthNavigation(modifier: Modifier, authViewModel: AuthViewModel) {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "login", builder = {
//        composable("login") { LoginScreen(navController, authViewModel) }
//        composable("signUp") { SignUpScreen(navController, authViewModel) }
//        composable("login") { LoginScreen(navController, authViewModel) }
//        composable("home") {MainActivity::class.java}
//    })
//}