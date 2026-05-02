package com.inspection.utils

import android.util.Xml
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.*
import kotlin.reflect.*
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import org.jsoup.nodes.Element
import kotlin.reflect.full.memberProperties


import com.google.gson.JsonObject
import org.xmlpull.v1.XmlPullParser
import java.io.StringReader

object XmlUtils {

    fun extractResponseXmlBlock(response: String): String {
        val start = response.indexOf("<responseXml")
        val end = response.indexOf("<returnCode")
        return response.substring(start, end)
    }


    fun normalizeJsonArrays(element: JsonElement): JsonElement {

        if (element.isJsonObject) {
            val obj = element.asJsonObject
            val keys = obj.keySet().toList()

            for (key in keys) {
                val value = obj.get(key)

                when {
                    value.isJsonObject -> {
                        // Wrap single object inside array
                        val array = JsonArray()
                        array.add(normalizeJsonArrays(value))
                        obj.add(key, array)
                    }

                    value.isJsonArray -> {
                        val newArray = JsonArray()
                        value.asJsonArray.forEach {
                            newArray.add(normalizeJsonArrays(it))
                        }
                        obj.add(key, newArray)
                    }

                    else -> {
                        // primitives → do nothing
                    }
                }
            }
            return obj
        }

        if (element.isJsonArray) {
            val newArray = JsonArray()
            element.asJsonArray.forEach {
                newArray.add(normalizeJsonArrays(it))
            }
            return newArray
        }

        return element
    }

    fun normalizeForModel(
        root: JsonObject,
        modelClass: Class<*>
    ): JsonObject {

        val listFields = modelClass.declaredFields
            .filter { List::class.java.isAssignableFrom(it.type) }
            .map { it.name }
            .toSet()

        val fixed = JsonObject()

        for ((key, value) in root.entrySet()) {

            if (listFields.contains(key)) {
                if (value.isJsonArray) {
                    fixed.add(key, value)
                } else {
                    val arr = JsonArray()
                    arr.add(value)
                    fixed.add(key, arr)
                }
            } else {
                fixed.add(key, value)
            }
        }

        return fixed
    }

    fun xmlToJsonObject(xml: String): JsonObject {
        val document = Jsoup.parse(xml, "", Parser.xmlParser())
        val root = document.children().first() ?: return JsonObject()
        return elementToJson(root).asJsonObject
    }

    private fun parseXml(parser: XmlPullParser): MutableMap<String, Any?> {
        val stack = mutableListOf<MutableMap<String, Any?>>()
        var current: MutableMap<String, Any?> = mutableMapOf()

        var eventType = parser.eventType
        var tagName: String? = null

        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.START_TAG -> {
                    tagName = parser.name
                    val newMap = mutableMapOf<String, Any?>()
                    stack.add(current)
                    current = newMap
                }

                XmlPullParser.TEXT -> {
                    val text = parser.text.trim()
                    if (text.isNotEmpty() && tagName != null) {
                        current[tagName] = text
                    }
                }

                XmlPullParser.END_TAG -> {
                    val finished = current
                    current = stack.removeLastOrNull() ?: mutableMapOf()
                    current[parser.name] = finished
                }
            }
            eventType = parser.next()
        }

        return current
    }

    private fun elementToJson(element: Element): JsonElement {

        val children = element.children()

        // No child elements → return text value
        if (children.isEmpty()) {
            return JsonPrimitive(element.text().trim())
        }

        val jsonObject = JsonObject()

        val grouped = children.groupBy { it.tagName() }

        for ((tag, elements) in grouped) {

            if (elements.size == 1) {
                jsonObject.add(tag, elementToJson(elements.first()))
            } else {
                val jsonArray = JsonArray()
                elements.forEach { jsonArray.add(elementToJson(it)) }
                jsonObject.add(tag, jsonArray)
            }
        }

        return jsonObject
    }
}