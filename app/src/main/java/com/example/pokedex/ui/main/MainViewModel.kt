package com.example.pokedex.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.local.entity.CartItemEntity
import com.example.pokedex.data.remote.model.PokemonResult
import com.example.pokedex.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {
    private val _pokemonList = MutableStateFlow<List<PokemonResult>>(emptyList())
    val pokemonList: StateFlow<List<PokemonResult>> = _pokemonList.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var offset = 0
    private val limit = 20

    fun loadPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getPokemonList(offset, limit)
                _pokemonList.value = _pokemonList.value + response.results
                offset += limit
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar Pokémon: " + (e.message ?: "Desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToCart(name: String, imageUrl: String) {
        viewModelScope.launch {
            repository.addToCart(CartItemEntity(name, imageUrl))
        }
    }
}
