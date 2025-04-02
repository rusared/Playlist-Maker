package com.example.playlistmaker

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: MaterialToolbar
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderMessageImage: ImageView
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderMessageButton: Button
    private lateinit var tracksList: RecyclerView
    private lateinit var currentRequestStatus: RequestStatus
    private lateinit var tracksHistoryList: RecyclerView
    private lateinit var historyView: LinearLayout
    private lateinit var clearHistoryButton: Button

    private var valueEditText: String? = null
    private val tracks = ArrayList<Track>()
    private val adapter = TracksAdapter(tracks)
    private val tracksHistory = ArrayList<Track>()
    private val historyAdapter = TracksHistoryAdapter(tracksHistory)

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

        placeholderMessageButton.setOnClickListener {
            responseHandler()
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
                    responseHandler()
                }
                true
            }
            false
        }

    }

    private fun responseHandler() {
        RetrofitClient.iTunesService.search(queryInput.text.toString()).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        adapter.notifyDataSetChanged()
                    }
                    if (tracks.isEmpty()) {
                        currentRequestStatus = RequestStatus.NOTHING_FOUND
                        showMessage(currentRequestStatus)
                    }else {
                        currentRequestStatus = RequestStatus.SUCCESS
                        showMessage(currentRequestStatus)
                    }
                } else {
                    currentRequestStatus = RequestStatus.CONNECTION_PROBLEM
                    showMessage(currentRequestStatus)
                }
            }
            override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                currentRequestStatus = RequestStatus.CONNECTION_PROBLEM
                showMessage(currentRequestStatus)
            }
        })
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VALUE_EDIT_TEXT, valueEditText)
        outState.putSerializable(TRACKS, tracks)
        outState.putString(CURRENT_REQUEST_STATUS, currentRequestStatus.name)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        valueEditText = savedInstanceState.getString(VALUE_EDIT_TEXT)
        queryInput.setText(valueEditText)
        tracks.clear()
        val savedTracks = savedInstanceState.getSerializable(TRACKS) as? ArrayList<Track>
        if (savedTracks != null) {
            tracks.addAll(savedTracks)
        }
        currentRequestStatus = RequestStatus.valueOf(savedInstanceState.getString(CURRENT_REQUEST_STATUS).toString())
        showMessage(currentRequestStatus)
    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
        private const val TRACKS = "tracks"
        private const val CURRENT_REQUEST_STATUS = "current_request_status"
    }

    enum class RequestStatus {
        SUCCESS,
        NOTHING_FOUND,
        CONNECTION_PROBLEM
    }

    private fun showMessage(requestStatus: RequestStatus) {
        when (requestStatus) {

            RequestStatus.SUCCESS -> {
                placeholderMessage.visibility = View.GONE
            }

            RequestStatus.NOTHING_FOUND -> {
                placeholderMessage.visibility = View.VISIBLE
                tracks.clear()
                adapter.notifyDataSetChanged()
                placeholderMessageText.text = getString(R.string.nothing_found)
                placeholderMessageImage.setImageDrawable(getDrawable(R.drawable.nothing_found_placeholder))
                placeholderMessageButton.visibility = View.GONE
            }

            RequestStatus.CONNECTION_PROBLEM -> {
                placeholderMessage.visibility = View.VISIBLE
                tracks.clear()
                adapter.notifyDataSetChanged()
                placeholderMessageText.text = getString(R.string.connection_problem)
                placeholderMessageImage.setImageDrawable(getDrawable(R.drawable.connection_problem_placeholder))
                placeholderMessageButton.visibility = View.VISIBLE
            }
        }
    }
}