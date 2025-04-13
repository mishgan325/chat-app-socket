//package ru.mishgan325.chatappsocket.presentation.screens
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextFieldDefaults
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import ru.mishgan325.chatappsocket.R
//import ru.mishgan325.chatappsocket.api.MainApi
//import ru.mishgan325.chatappsocket.viewmodels.LoginViewModel
//
//@Composable
//fun RegisterScreen(
//    modifier: Modifier = Modifier,
//    mainApi: MainApi,
//    navHostController: NavHostController,
//    viewModel: LoginViewModel
//) {
//    var email by remember { mutableStateOf("") }
//    var username by remember { mutableStateOf("") }
//    var password by remember { mutableStateOf("") }
//
//    Column(
//        modifier = modifier
//            .fillMaxSize()
//            .padding(32.dp)
//            .verticalScroll(rememberScrollState()),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // Email Field
//        OutlinedTextField(
//            value = email,
//            onValueChange = { email = it },
//            label = { Text(stringResource(R.string.email)) },
//            leadingIcon = { Icon(painterResource(R.drawable.ic_email), null) },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
//            colors = TextFieldDefaults.colors(),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        // Username Field
//        OutlinedTextField(
//            value = username,
//            onValueChange = { username = it },
//            label = { Text(stringResource(R.string.username)) },
//            leadingIcon = { Icon(painterResource(R.drawable.ic_person), null) },
//            colors = TextFieldDefaults.colors(
//                // аналогичные цвета как в email field
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(Modifier.height(16.dp))
//
//        // Password Field
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text(stringResource(R.string.password)) },
//            leadingIcon = { Icon(painterResource(R.drawable.ic_login), null) },
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
//            visualTransformation = PasswordVisualTransformation(),
//            colors = TextFieldDefaults.colors(
//                // аналогичные цвета
//            ),
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(Modifier.height(32.dp))
//
//        // Register Button
//        Button(
//            onClick = {
//                viewModel
//            },
//            modifier = Modifier.fillMaxWidth(),
//            shape = RoundedCornerShape(20.dp),
//            colors = ButtonDefaults.buttonColors(
//                containerColor = MaterialTheme.colorScheme.primary,
//                contentColor = MaterialTheme.colorScheme.onPrimary
//            )
//        ) {
//            Text(stringResource(R.string.register))
//        }
//    }
//
//}
//
//
////@Composable
////@Preview
////fun RegisterScreenPreview() {
////    RegisterScreen({}, apiInterface = apiService)
////}