package com.github.connorwyatt.common.mongodb

import kotlin.reflect.full.findAnnotation

annotation class CollectionName(val name: String)

inline fun <reified T> collectionName() =
    T::class.findAnnotation<CollectionName>()?.name
        ?: throw Exception("${T::class.simpleName} is missing CollectionName annotation.")
