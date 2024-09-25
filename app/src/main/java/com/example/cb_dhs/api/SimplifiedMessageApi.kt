package com.example.cb_dhs.api

import org.json.JSONException
import org.json.JSONObject

object SimplifiedMessageApi {
    fun get(responseMessage: String): HashMap<String, String> {
        val messages = HashMap<String, String>()
        val jsonObject = JSONObject(responseMessage)

        try {
            val jsonMessage = jsonObject.getJSONObject("message")
            jsonMessage.keys().forEach { messages[it] = jsonMessage.getString(it) }
        } catch (e: JSONException) {
            messages["message"] = jsonObject.getString("message")
        }

        return messages
    }
}