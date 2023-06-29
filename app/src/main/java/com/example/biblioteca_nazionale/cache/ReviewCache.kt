package com.example.biblioteca_nazionale.cache

import android.util.LruCache
import com.example.biblioteca_nazionale.model.RequestCodeLocation
import com.example.biblioteca_nazionale.model.Review

object ReviewCache {
    private const val CACHE_SIZE = 50 // Dimensione massima della cache
    private val cache: LruCache<String, List<Review>> = LruCache(CACHE_SIZE)

    fun getCacheKey(query: String): String {
        return query
    }

    fun putResult(query: String, result: List<Review>) {
        val key = getCacheKey(query)
        cache.put(key, result)
    }

    fun getResult(query: String): List<Review>? {
        val key = getCacheKey(query)
        return cache.get(key)
    }
}