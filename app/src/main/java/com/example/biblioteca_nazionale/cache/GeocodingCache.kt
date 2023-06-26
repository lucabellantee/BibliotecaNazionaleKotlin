package com.example.biblioteca_nazionale.cache

import android.util.LruCache
import com.google.maps.model.GeocodingResult

object GeocodingCache {
    private const val CACHE_SIZE = 100 // Dimensione massima della cache
    private val cache: LruCache<String, GeocodingResult> = LruCache(CACHE_SIZE)

    fun getCacheKey(query: String): String {
        // Crea una chiave univoca per la cache basata sulla query di geocoding
        return query
    }

    fun putResult(query: String, result: GeocodingResult) {
        val key = getCacheKey(query)
        cache.put(key, result)
    }

    fun getResult(query: String): GeocodingResult? {
        val key = getCacheKey(query)
        return cache.get(key)
    }
}
