package com.example.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private lateinit var backButton: MaterialToolbar
    private lateinit var queryInput: EditText
    private lateinit var clearButton: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var placeholderMessage: LinearLayout
    private lateinit var placeholderMessageImage: ImageView
    private lateinit var placeholderMessageText: TextView
    private lateinit var placeholderMessageButton: Button
    private lateinit var tracksList: RecyclerView
    private lateinit var tracksHistoryList: RecyclerView
    private lateinit var currentRequestStatus: RequestStatus
    private lateinit var historyView: LinearLayout
    private lateinit var clearHistoryButton: Button
    private lateinit var searchHistory: SearchHistory
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var tracksHistoryAdapter: TracksAdapter
    private lateinit var preferencesManager: PreferencesManager
    private val sharedPreferencesChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == PreferencesManager.SEARCH_HISTORY_LIST_KEY) {
            tracksHistory.clear()
            if (tracksHistory.addAll(searchHistory.getHistory()))
            tracksHistoryAdapter.notifyDataSetChanged()
        }
    }
    private var valueEditText: String? = null
    private val tracks = ArrayList<Track>()
    private var tracksHistory = ArrayList<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        preferencesManager = PreferencesManager(this)
        searchHistory = SearchHistory().apply {
            this.preferencesManager = this@SearchActivity.preferencesManager
        }
        currentRequestStatus = RequestStatus.SUCCESS

        preferencesManager.sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)

        tracksHistory = searchHistory.getHistory()
        tracksAdapter = TracksAdapter(tracks, searchHistory)
        tracksHistoryAdapter = TracksAdapter(tracksHistory, searchHistory)

        backButton = findViewById(R.id.mt_search_back_button)
        setSupportActionBar(backButton)
        queryInput = findViewById(R.id.et_query_input)
        clearButton = findViewById(R.id.iv_clear_icon)
        progressBar = findViewById(R.id.progressBar)
        placeholderMessage = findViewById(R.id.ll_placeholder_message)
        placeholderMessageImage = findViewById(R.id.iv_placeholder_message)
        placeholderMessageText = findViewById(R.id.tv_placeholder_message)
        placeholderMessageButton = findViewById(R.id.b_placeholder_message)
        tracksList = findViewById(R.id.rv_tracks_list)
        tracksHistoryList = findViewById(R.id.rv_track_history_list)
        historyView = findViewById(R.id.ll_track_history)
        clearHistoryButton = findViewById(R.id.b_track_history_clear)

        clearButton.setOnClickListener {
            queryInput.setText("")
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderMessage.visibility = View.GONE
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
        }

        placeholderMessageButton.setOnClickListener {
            responseHandler()
        }

        clearHistoryButton.setOnClickListener {
            searchHistory.clearHistory()
            tracksHistoryAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
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
                historyView.visibility = historyViewVisibility(s)
                if (s.isNullOrEmpty()==false) searchDebounce()
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        queryInput.addTextChangedListener(simpleTextWatcher)

        tracksList.layoutManager = LinearLayoutManager(this)
        tracksHistoryList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = tracksAdapter
        tracksHistoryList.adapter = tracksHistoryAdapter

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    responseHandler()
                }
                true
            }
            false
        }
        queryInput.setOnFocusChangeListener { view, hasFocus ->
            historyView.visibility = if (hasFocus && queryInput.text.isEmpty() && tracksHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }
        queryInput.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        preferencesManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferencesChangeListener)
    }

    private fun responseHandler() {
        tracksList.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        RetrofitClient.iTunesService.search(queryInput.text.toString()).enqueue(object : Callback<TracksResponse> {
            override fun onResponse(call: Call<TracksResponse>, response: Response<TracksResponse>) {
                progressBar.visibility = View.GONE
                tracksList.visibility = View.VISIBLE
                if (response.code() == 200) {
                    tracks.clear()
                    if (response.body()?.results?.isNotEmpty() == true) {
                        tracks.addAll(response.body()?.results!!)
                        tracksAdapter.notifyDataSetChanged()
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
                progressBar.visibility = View.GONE
                currentRequestStatus = RequestStatus.CONNECTION_PROBLEM
                showMessage(currentRequestStatus)
            }
        })
    }

    private fun historyViewVisibility(s: CharSequence?): Int {
        if (queryInput.hasFocus() && tracksHistory.isNotEmpty() && s?.isEmpty() == true) {
            tracks.clear()
            tracksAdapter.notifyDataSetChanged()
            placeholderMessage.visibility = View.GONE
            return View.VISIBLE
        } else {
            return View.GONE
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VALUE_EDIT_TEXT, valueEditText)
        outState.putSerializable(TRACKS, tracks)
        outState.putSerializable(TRACKS_HISTORY, tracksHistory)
        outState.putString(CURRENT_REQUEST_STATUS, currentRequestStatus.name)
        outState.putInt(PLACEHOLDER_VISIBILITY, placeholderMessage.visibility)
        outState.putInt(HISTORY_VIEW_VISIBILITY, historyView.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        valueEditText = savedInstanceState.getString(VALUE_EDIT_TEXT)
        queryInput.setText(valueEditText ?: "")

        val savedTracks = savedInstanceState.getSerializable(TRACKS) as? ArrayList<Track> ?: ArrayList()
        tracks.clear()
        tracks.addAll(savedTracks)

        val savedTracksHistory = savedInstanceState.getSerializable(TRACKS_HISTORY) as? ArrayList<Track> ?: ArrayList()
        tracksHistory.clear()
        tracksHistory.addAll(savedTracksHistory)

        currentRequestStatus = RequestStatus.valueOf(
            savedInstanceState.getString(CURRENT_REQUEST_STATUS, RequestStatus.SUCCESS.name)
        )

        tracksAdapter.notifyDataSetChanged()
        tracksHistoryAdapter.notifyDataSetChanged()

        showMessage(currentRequestStatus)
        placeholderMessage.visibility = savedInstanceState.getInt(PLACEHOLDER_VISIBILITY)
        historyView.visibility = savedInstanceState.getInt(HISTORY_VIEW_VISIBILITY)

    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
        private const val TRACKS = "tracks"
        private const val TRACKS_HISTORY = "tracks_history"
        private const val CURRENT_REQUEST_STATUS = "current_request_status"
        private const val PLACEHOLDER_VISIBILITY = "placeholder_visibility"
        private const val HISTORY_VIEW_VISIBILITY = "history_view_visibility"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { responseHandler() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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
                tracksAdapter.notifyDataSetChanged()
                placeholderMessageText.text = getString(R.string.nothing_found)
                placeholderMessageImage.setImageDrawable(getDrawable(R.drawable.nothing_found_placeholder))
                placeholderMessageButton.visibility = View.GONE
            }

            RequestStatus.CONNECTION_PROBLEM -> {
                placeholderMessage.visibility = View.VISIBLE
                tracks.clear()
                tracksAdapter.notifyDataSetChanged()
                placeholderMessageText.text = getString(R.string.connection_problem)
                placeholderMessageImage.setImageDrawable(getDrawable(R.drawable.connection_problem_placeholder))
                placeholderMessageButton.visibility = View.VISIBLE
            }
        }
    }
}