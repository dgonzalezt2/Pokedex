package com.example.pokedex.ui.cart

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.local.dao.CartDao
import com.example.pokedex.data.local.entity.CartItemEntity
import com.example.pokedex.ui.util.showCartNotification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartDao: CartDao,
    private val app: Application
) : ViewModel() {
    val cartItems: StateFlow<List<CartItemWithPrice>> = cartDao.getAllCartItems()
        .map { items -> items.map { CartItemWithPrice(it, simulatePrice(it.name)) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalPrice: StateFlow<Double> = cartItems
        .map { items -> items.sumOf { it.price } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartCount: StateFlow<Int> = cartItems
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _cartMessage = MutableStateFlow<String?>(null)
    val cartMessage: StateFlow<String?> = _cartMessage

    fun removeItem(name: String) {
        viewModelScope.launch {
            cartDao.deleteCartItem(name)
            // Notificación usando la función centralizada
            showCartNotification(
                app,
                "Pokémon eliminado",
                "Se eliminó $name del carrito",
                android.R.drawable.ic_delete,
                name.hashCode()
            )
        }
    }

    fun addToCart(name: String, imageUrl: String) {
        viewModelScope.launch {
            val item = CartItemEntity(name = name, imageUrl = imageUrl)
            try {
                cartDao.insertCartItem(item)
                _cartMessage.value = "Pokémon agregado al carrito"
            } catch (e: Exception) {
                if (e.message?.contains("UNIQUE constraint failed") == true) {
                    _cartMessage.value = "Ya fue agregado al carrito"
                } else {
                    _cartMessage.value = "Error al agregar al carrito"
                }
            }
        }
    }

    fun clearCartMessage() {
        _cartMessage.value = null
    }

    fun simulatePrice(name: String): Double {
        // Simula un precio entre 10 y 100 para cada Pokémon
        return (10 + (name.hashCode().absoluteValue % 91)).toDouble()
    }
}

data class CartItemWithPrice(
    val item: CartItemEntity,
    val price: Double
)
