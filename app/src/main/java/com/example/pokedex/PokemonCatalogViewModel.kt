package com.example.pokedex

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonCatalogViewModel @Inject constructor(
    private val apiService: PokeApiService
) : ViewModel() {
    private val _pokemonList = MutableStateFlow<List<PokemonResult>>(emptyList())
    val pokemonList: StateFlow<List<PokemonResult>> = _pokemonList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private var offset = 0
    private val limit = 20

    init {
        loadPokemon()
    }

    fun loadPokemon() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getPokemonList(offset, limit)
                _pokemonList.value = response.results
                offset += limit
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar Pokémon: " + (e.message ?: "Desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
