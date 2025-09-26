package com.example.pokedex.ui.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import com.example.pokedex.R

@Composable
fun CartScreen(viewModel: CartViewModel, onBack: () -> Unit) {
    val cartItems by viewModel.cartItems.collectAsState()
    val totalPrice by viewModel.totalPrice.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_back), contentDescription = "Volver")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Carrito de compras", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(cartItems) { cartItemWithPrice ->
                CartItemRow(
                    name = cartItemWithPrice.item.name,
                    imageUrl = cartItemWithPrice.item.imageUrl,
                    price = cartItemWithPrice.price,
                    onRemove = { viewModel.removeItem(cartItemWithPrice.item.name) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Total: $${"%.2f".format(totalPrice)}", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun CartItemRow(name: String, imageUrl: String, price: Double, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = rememberAsyncImagePainter(imageUrl),
                contentDescription = name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(name.replaceFirstChar { it.uppercase() }, modifier = Modifier.weight(1f))
            Text("$${"%.2f".format(price)}", modifier = Modifier.padding(end = 8.dp))
            Button(onClick = onRemove) {
                Text("Eliminar")
            }
        }
    }
}
