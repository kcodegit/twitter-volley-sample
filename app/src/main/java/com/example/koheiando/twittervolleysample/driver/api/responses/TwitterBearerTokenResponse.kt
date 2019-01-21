package com.example.koheiando.twittervolleysample.driver.api.responses

import com.example.koheiando.twittervolleysample.driver.api.HttpResponse
import com.example.koheiando.twittervolleysample.util.PreferenceUtil
import org.json.JSONObject

class TwitterBearerTokenResponse : HttpResponse() {
    companion object {
        private const val ACCESS_TOKEN_KEY = "access_token"
    }

    lateinit var accessToken: String

    override fun parseJson(json: JSONObject): HttpResponse {
        accessToken = json.getString(ACCESS_TOKEN_KEY)
        PreferenceUtil.TwitterApiInfo.twitterBearerToken = accessToken
        return this
    }
}