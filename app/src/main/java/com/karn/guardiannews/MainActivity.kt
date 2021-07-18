package com.karn.guardiannews

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private var pageNumber = 1
    private var list = mutableListOf<Data>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        search_button.setOnClickListener {
            sendRequest()
        }
        load_more_button.setOnClickListener {
            list = mutableListOf()
            pageNumber += 1
            sendRequest()
        }

    }

    private fun getUrl(): String {

        val word = search_edit_text.text
        val apiKey = "002a5fd6-e84f-474f-8d44-ad9892280fe9"
        val pageSize = 12
        return "https://content.guardianapis.com/$word?page=$pageNumber&page-size=$pageSize&api-key=$apiKey"

    }

    private fun extractJSON(response: String) {

        val jsonObject = JSONObject(response)
        val jsonResponseBody = jsonObject.getJSONObject("response")
        val results = jsonResponseBody.getJSONArray("results")

        for (i in 0..11) {

            val item = results.getJSONObject(i)
            val webTitle = item.getString("webTitle")
            val webUrl = item.getString("webUrl")
            val data = Data(webTitle, webUrl)
            list.add(data)

        }
        val adapter = NewsAdapter(list)
        list_view.adapter = adapter

    }

    private fun sendRequest() {
        val url = getUrl()

        val queue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                try {
                    extractJSON(response)
                } catch (exception: Exception) {
                    exception.printStackTrace()
                }
            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            })

        queue.add(stringRequest)
    }
}