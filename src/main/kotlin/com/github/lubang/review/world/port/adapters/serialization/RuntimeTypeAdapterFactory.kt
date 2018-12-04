package com.github.lubang.review.world.port.adapters.serialization

import com.google.gson.*
import com.google.gson.internal.Streams
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.io.IOException
import java.util.*

class RuntimeTypeAdapterFactory<T> private constructor(private val baseType: Class<*>,
                                                       private val typeFieldName: String,
                                                       private val maintainType: Boolean)
    : TypeAdapterFactory {

    private val labelToSubtype = LinkedHashMap<String, Class<*>>()
    private val subtypeToLabel = LinkedHashMap<Class<*>, String>()

    @JvmOverloads
    fun registerSubtype(type: Class<out T>?, label: String? = type!!.name): RuntimeTypeAdapterFactory<T> {
        if (type == null || label == null) {
            throw NullPointerException()
        }
        if (subtypeToLabel.containsKey(type) || labelToSubtype.containsKey(label)) {
            throw IllegalArgumentException("types and labels must be unique")
        }
        labelToSubtype[label] = type
        subtypeToLabel[type] = label
        return this
    }

    override fun <R : Any> create(gson: Gson, type: TypeToken<R>): TypeAdapter<R>? {
        if (type.rawType != baseType) {
            return null
        }

        val labelToDelegate = LinkedHashMap<String, TypeAdapter<*>>()
        val subtypeToDelegate = LinkedHashMap<Class<*>, TypeAdapter<*>>()
        for ((key, value) in labelToSubtype) {
            val delegate = gson.getDelegateAdapter(this, TypeToken.get(value))
            labelToDelegate[key] = delegate
            subtypeToDelegate[value] = delegate
        }

        return object : TypeAdapter<R>() {
            @Throws(IOException::class)
            override fun read(`in`: JsonReader): R {
                val jsonElement = Streams.parse(`in`)
                val labelJsonElement = (if (maintainType) {
                    jsonElement.asJsonObject.get(typeFieldName)
                } else {
                    jsonElement.asJsonObject.remove(typeFieldName)
                }) ?: throw JsonParseException("cannot deserialize " + baseType
                        + " because it does not define a field named " + typeFieldName)

                val label = labelJsonElement.asString
                val delegate = labelToDelegate[label] as TypeAdapter<R>
                return delegate.fromJsonTree(jsonElement)
            }

            @Throws(IOException::class)
            override fun write(out: JsonWriter, value: R) {
                val srcType = value::class.java
                val label = subtypeToLabel[srcType]
                val delegate = subtypeToDelegate[srcType] as TypeAdapter<R>
                val jsonObject = delegate.toJsonTree(value).asJsonObject

                if (maintainType) {
                    Streams.write(jsonObject, out)
                    return
                }

                val clone = JsonObject()

                if (jsonObject.has(typeFieldName)) {
                    throw JsonParseException("cannot serialize ${srcType.name}" +
                            " because it already defines a field named $typeFieldName")
                }
                clone.add(typeFieldName, JsonPrimitive(label))

                for ((k, v) in jsonObject.entrySet()) {
                    clone.add(k, v)
                }
                Streams.write(clone, out)
            }
        }.nullSafe()
    }

    companion object {
        private const val TYPE_FIELD_NAME = "@type"

        fun <T> of(baseType: Class<T>, typeFieldName: String, maintainType: Boolean): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, maintainType)
        }

        fun <T> of(baseType: Class<T>, typeFieldName: String): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, typeFieldName, false)
        }

        fun <T> of(baseType: Class<T>): RuntimeTypeAdapterFactory<T> {
            return RuntimeTypeAdapterFactory(baseType, TYPE_FIELD_NAME, false)
        }
    }
}