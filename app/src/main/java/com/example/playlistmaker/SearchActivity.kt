package com.example.playlistmaker

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private val iTunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(ITunesSearchAPI::class.java)

    private lateinit var backButton: MaterialToolbar
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderMessageImage: ImageView
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderMessageButton: Button
    private lateinit var tracksList: RecyclerView


    private val tracks = ArrayList<Track>()
    private val adapter = TracksAdapter(tracks)
    private var valueEditText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        backButton = findViewById(R.id.mt_search_back_button)
        setSupportActionBar(backButton)
        queryInput = findViewById(R.id.et_query_input)
        clearButton = findViewById(R.id.iv_clear_icon)
        placeholderMessage = findViewById(R.id.ll_placeholder_message)
        placeholderMessageImage = findViewById(R.id.iv_placeholder_message)
        placeholderMessageText = findViewById(R.id.tv_placeholder_message)
        placeholderMessageButton = findViewById(R.id.b_placeholder_message)
        tracksList = findViewById(R.id.rv_tracks_list)

        clearButton.setOnClickListener {
            queryInput.setText("")
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessage.visibility = View.GONE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }

        backButton.setNavigationOnClickListener {
            finish()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                valueEditText = s?.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        queryInput.addTextChangedListener(simpleTextWatcher)


        tracksList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = adapter

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    iTunesService.search(queryInput.text.toString()).enqueue(object : Callback<TracksResponse> {
                        override fun onResponse(call: Call<TracksResponse>,
                                                response: Response<TracksResponse>) {
                            if (response.code() == 200) {
                                tracks.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    adapter.notifyDataSetChanged()
                                }
                                if (tracks.isEmpty()) {
                                    showMessage(getString(R.string.nothing_found), getDrawable(R.drawable.nothing_found_placeholder), false)
                                } else {
                                    showMessage("", null, false)
                                }
                            } else {
                                showMessage(getString(R.string.connection_problem), getDrawable(R.drawable.connection_problem_placeholder), true)
//                                showMessage(getString(R.string.connection_problem), response.code().toString())
                            }
                        }

                        override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                            showMessage(getString(R.string.connection_problem), getDrawable(R.drawable.connection_problem_placeholder), true)
//                            showMessage(getString(R.string.connection_problem), t.message.toString())
                        }

                    })
                }
                true
            }
            false
        }

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VALUE_EDIT_TEXT, valueEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        valueEditText = savedInstanceState.getString(VALUE_EDIT_TEXT)
        queryInput.setText(valueEditText)
    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
    }

    private fun showMessage(text: String, image: Drawable?, isUpdater: Boolean) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            placeholderMessageImage.setImageDrawable(image)
            placeholderMessageText.text = text
            if (isUpdater) placeholderMessageButton.visibility = View.VISIBLE
        } else {
            placeholderMessage.visibility = View.GONE
            if (!isUpdater) placeholderMessageButton.visibility = View.GONE
        }
    }
}