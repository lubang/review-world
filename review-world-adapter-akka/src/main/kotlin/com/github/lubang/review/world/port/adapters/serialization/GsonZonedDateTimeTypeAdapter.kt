package com.github.lubang.review.world.port.adapters.serialization

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class GsonZonedDateTimeTypeAdapter : TypeAdapter<ZonedDateTime>() {
    override fun write(writer: JsonWriter, value: ZonedDateTime?) {
        if (value == null) {
            writer.nullValue()
        } else {
            writer.value(value.format(DateTimeFormatter.ISO_INSTANT))
        }
    }

    override fun read(reader: JsonReader): ZonedDateTime {
        return ZonedDateTime.parse(reader.nextString(), DateTimeFormatter.ISO_INSTANT)
    }
}