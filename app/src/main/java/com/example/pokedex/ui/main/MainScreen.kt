package com.example.pokedex.ui.main

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.pokedex.PokemonCatalogViewModel
import com.example.pokedex.R
import com.example.pokedex.data.remote.model.PokemonResult
import com.example.pokedex.ui.cart.CartScreen
import com.example.pokedex.ui.cart.CartViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

fun showCartNotification(context: Context, title: String, message: String, iconRes: Int, notificationId: Int = 1) {
    val channelId = "cart_channel"
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Carrito",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
    val hasPermission = if (Build.VERSION.SDK_INT >= 33) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
    if (hasPermission) {
        try {
            NotificationManagerCompat.from(context).notify(notificationId, NotificationCompat.Builder(context, channelId)
                .setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build())
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Aquí podrías mostrar un mensaje al usuario si lo deseas
        }
    } else {
        // Aquí podrías mostrar un mensaje al usuario indicando que no tiene permiso para notificaciones
    }
}

fun authenticateWithBiometrics(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Autenticación biométrica")
        .setSubtitle("Selecciona una huella registrada para acceder al carrito")
        .setNegativeButtonText("Cancelar")
        .build()
    val biometricPrompt = BiometricPrompt(activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                onCancel()
            }
            override fun onAuthenticationFailed() {
                // No hacer nada, solo mostrar el error si se desea
            }
        })
    biometricPrompt.authenticate(promptInfo)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokemonDetailScreen(
    pokemon: PokemonResult,
    price: Double,
    onAddToCart: () -> Unit,
    onBack: () -> Unit,
    cartCount: Int,
    cartMessage: String?,
    clearCartMessage: () -> Unit,
    snackbarHostState: SnackbarHostState,
    cartViewModel: CartViewModel
) {
    var notificationPermissionGranted by remember { mutableStateOf(false) }
    var showCart by remember { mutableStateOf(false) }
    RequestNotificationPermission { granted ->
        notificationPermissionGranted = granted
    }
    val id = pokemon.url.trimEnd('/').split("/").lastOrNull() ?: "1"
    val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    if (showCart) {
        CartScreen(viewModel = cartViewModel, onBack = { showCart = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(pokemon.name.replaceFirstChar { it.uppercase() }) },
                    actions = {
                        Box {
                            IconButton(onClick = {
                                activity?.let {
                                    authenticateWithBiometrics(
                                        it,
                                        onSuccess = { showCart = true },
                                        onCancel = { /* Puedes mostrar un mensaje si lo deseas */ }
                                    )
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cart),
                                    contentDescription = "Ver carrito"
                                )
                            }
                            if (cartCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                ) {
                                    Text(cartCount.toString())
                                }
                            }
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            model = imageUrl,
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground)
                        ),
                        contentDescription = pokemon.name,
                        modifier = Modifier.size(128.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "Precio: $${"%.2f".format(price)}", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            onAddToCart()
                            if (notificationPermissionGranted) {
                                showCartNotification(
                                    context,
                                    "Pokémon agregado",
                                    "${pokemon.name.replaceFirstChar { it.uppercase() }} se agregó al carrito.",
                                    R.drawable.ic_cart
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Agregar al carrito")
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
        LaunchedEffect(cartMessage) {
            if (cartMessage != null) {
                snackbarHostState.showSnackbar(cartMessage)
                clearCartMessage()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: PokemonCatalogViewModel = hiltViewModel(),
    cartViewModel: CartViewModel = hiltViewModel()
) {
    var showCart by remember { mutableStateOf(false) }
    var showBiometricDialog by remember { mutableStateOf(false) }
    var showBiometricScreen by remember { mutableStateOf(false) }
    val pokemonList by viewModel.pokemonList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val cartCount by cartViewModel.cartCount.collectAsState()
    val cartMessage by cartViewModel.cartMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var selectedPokemon by remember { mutableStateOf<PokemonResult?>(null) }

    if (selectedPokemon != null) {
        val price = cartViewModel.simulatePrice(selectedPokemon!!.name)
        PokemonDetailScreen(
            pokemon = selectedPokemon!!,
            price = price,
            onAddToCart = {
                val id = selectedPokemon!!.url.trimEnd('/').split("/").lastOrNull() ?: "1"
                val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                cartViewModel.addToCart(selectedPokemon!!.name, imageUrl)
            },
            onBack = { selectedPokemon = null },
            cartCount = cartCount,
            cartMessage = cartMessage,
            clearCartMessage = { cartViewModel.clearCartMessage() },
            snackbarHostState = snackbarHostState,
            cartViewModel = cartViewModel
        )
    } else if (showBiometricScreen) {
        BiometricScreen(
            onSuccess = {
                showBiometricScreen = false
                showCart = true
            },
            onCancel = {
                showBiometricScreen = false
            }
        )
    } else if (showCart) {
        CartScreen(viewModel = cartViewModel, onBack = { showCart = false })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                TopAppBar(
                    title = { Text("Catálogo Pokémon") },
                    actions = {
                        Box {
                            IconButton(onClick = {
                                showBiometricDialog = true
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_cart),
                                    contentDescription = "Carrito"
                                )
                            }
                            if (cartCount > 0) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = (-4).dp, y = 4.dp)
                                ) {
                                    Text(cartCount.toString())
                                }
                            }
                        }
                    }
                )
                if (showBiometricDialog) {
                    AlertDialog(
                        onDismissRequest = { showBiometricDialog = false },
                        title = { Text("Acceso protegido") },
                        text = { Text("Para ver el carrito, debes autenticarte con tu huella.") },
                        confirmButton = {
                            Button(onClick = {
                                showBiometricDialog = false
                                showBiometricScreen = true
                            }) {
                                Text("Autenticar")
                            }
                        },
                        dismissButton = {
                            Button(onClick = { showBiometricDialog = false }) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
                SnackbarHost(hostState = snackbarHostState)
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (isLoading && pokemonList.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        items(pokemonList) { pokemon ->
                            val id = pokemon.url.trimEnd('/').split("/").lastOrNull() ?: "1"
                            val imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"
                            val price = cartViewModel.simulatePrice(pokemon.name)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { selectedPokemon = pokemon },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = imageUrl),
                                    contentDescription = pokemon.name,
                                    modifier = Modifier.size(64.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(
                                    modifier = Modifier.width(120.dp),
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(
                                        text = pokemon.name.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "$${"%.2f".format(price)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                Button(
                                    onClick = {
                                        cartViewModel.addToCart(pokemon.name, imageUrl)
                                    },
                                    modifier = Modifier
                                        .width(140.dp)
                                        .height(40.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_cart),
                                        contentDescription = "Agregar al carrito",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "Agregar",
                                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                                    )
                                }
                            }
                        }
                        item {
                            if (!isLoading) {
                                Button(
                                    onClick = { viewModel.loadPokemon() },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                                ) {
                                    Text("Cargar más")
                                }
                            } else {
                                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            }
                        }
                    }
                }
            }
        }
    }
    if (showSnackbar) {
        LaunchedEffect(snackbarMessage) {
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }
    if (cartMessage != null) {
        LaunchedEffect(cartMessage) {
            snackbarHostState.showSnackbar(cartMessage!!)
            cartViewModel.clearCartMessage()
        }
    }
}

@Composable
fun RequestNotificationPermission(onPermissionResult: (Boolean) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        onPermissionResult(isGranted)
    }
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                onPermissionResult(true)
            }
        } else {
            onPermissionResult(true)
        }
    }
}

@Composable
fun BiometricScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    DisposableEffect(Unit) {
        Log.d("BiometricScreen", "DisposableEffect lanzado")
        val activity = context as? FragmentActivity
        if (activity == null) {
            Log.e("BiometricScreen", "El contexto no es FragmentActivity")
            errorMessage = "No se puede mostrar la autenticación biométrica en este dispositivo/emulador."
            onCancel()
        } else {
            Log.d("BiometricScreen", "FragmentActivity detectada, creando BiometricPrompt")
            val executor = ContextCompat.getMainExecutor(activity)
            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Selecciona una huella registrada para acceder al carrito")
                .setNegativeButtonText("Cancelar")
                .build()
            val biometricPrompt = BiometricPrompt(activity, executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        Log.d("BiometricScreen", "Autenticación biométrica exitosa")
                        onSuccess()
                    }
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        Log.e("BiometricScreen", "Error de autenticación: $errString")
                        errorMessage = errString.toString()
                        onCancel()
                    }
                    override fun onAuthenticationFailed() {
                        Log.w("BiometricScreen", "Autenticación biométrica fallida")
                        errorMessage = "La autenticación falló. Intenta nuevamente."
                    }
                })
            Log.d("BiometricScreen", "Lanzando biometricPrompt.authenticate")
            biometricPrompt.authenticate(promptInfo)
        }
        onDispose { Log.d("BiometricScreen", "DisposableEffect onDispose") }
    }
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Coloca tu huella para acceder al carrito", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
