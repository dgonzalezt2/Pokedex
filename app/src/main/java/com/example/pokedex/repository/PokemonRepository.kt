package com.example.pokedex.repository

import com.example.pokedex.data.local.dao.CartDao
import com.example.pokedex.data.local.entity.CartItemEntity
import com.example.pokedex.data.remote.PokeApiService
import com.example.pokedex.data.remote.model.PokemonResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PokemonRepository @Inject constructor(
    private val api: PokeApiService,
    private val cartDao: CartDao
) {
    suspend fun getPokemonList(offset: Int, limit: Int): PokemonResponse =
        api.getPokemonList(offset, limit)

    suspend fun addToCart(item: CartItemEntity) = cartDao.insertCartItem(item)

    fun getCartItems(): Flow<List<CartItemEntity>> = cartDao.getAllCartItems()
}
