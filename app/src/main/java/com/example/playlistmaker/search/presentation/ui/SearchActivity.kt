package com.example.playlistmaker.search.presentation.ui

import android.content.Intent
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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.presentation.ui.PlayerActivity
import com.example.playlistmaker.search.data.debounce.ClickDebouncerImpl
import com.example.playlistmaker.search.domain.interactor.ClickDebouncer
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel.SearchState
import com.google.android.material.appbar.MaterialToolbar

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
    private lateinit var historyView: LinearLayout
    private lateinit var clearHistoryButton: Button

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var tracksHistoryAdapter: TracksAdapter

    private lateinit var clickDebouncer: ClickDebouncer

    private val viewModel: SearchViewModel by viewModels {
        Creator.provideSearchViewModelFactory(this)
    }

    private var valueEditText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        clickDebouncer = provideClickDebouncer()

        initViews()
        setupAdapters()
        setupObservers()
        setupClickListeners()
        setupTextWatcher()

        queryInput.requestFocus()
    }


    private fun initViews() {
        backButton = findViewById(R.id.mt_search_back_button)
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

        setSupportActionBar(backButton)
    }

    private fun setupAdapters() {
        val trackClickListener: (Track) -> Unit = { track ->
            val intent = Intent(this, PlayerActivity::class.java).apply {
                putExtra(TracksAdapter.TRACK, track)
            }
            startActivity(intent)
            viewModel.addTrackToHistory(track)
        }

        tracksAdapter = TracksAdapter(emptyList(), trackClickListener, clickDebouncer)
        tracksHistoryAdapter = TracksAdapter(emptyList(), trackClickListener, clickDebouncer)

        tracksList.layoutManager = LinearLayoutManager(this)
        tracksHistoryList.layoutManager = LinearLayoutManager(this)
        tracksList.adapter = tracksAdapter
        tracksHistoryList.adapter = tracksHistoryAdapter
    }

    private fun provideClickDebouncer(): ClickDebouncer {
        return ClickDebouncerImpl()
    }

    override fun onDestroy() {
        super.onDestroy()
        clickDebouncer.reset()
        tracksAdapter.onDestroy()
        tracksHistoryAdapter.onDestroy()
    }

    private fun setupObservers() {
        viewModel.observeState.observe(this) { state ->
            handleSearchState(state)
        }

        viewModel.observeHistory.observe(this) { history ->
            handleHistory(history)
        }
    }

    private fun handleSearchState(state: SearchState) {
        progressBar.visibility = if (state is SearchState.Loading) View.VISIBLE else View.GONE
        tracksList.visibility = if (state is SearchState.Content) View.VISIBLE else View.GONE
        placeholderMessage.visibility = if (state is SearchState.Empty || state is SearchState.Error) View.VISIBLE else View.GONE

        when (state) {
            is SearchState.Default -> {
                showSearchHistory()
            }
            is SearchState.Loading -> {
                hideSearchHistory()
            }
            is SearchState.Content -> {
                hideSearchHistory()
                tracksAdapter.updateTracks(state.tracks)
            }
            is SearchState.Empty -> {
                hideSearchHistory()
                showEmptyState()
            }
            is SearchState.Error -> {
                hideSearchHistory()
                showErrorState()
            }
        }
    }

    private fun handleHistory(history: List<Track>) {
        tracksHistoryAdapter.updateTracks(history)
        val shouldShowHistory = history.isNotEmpty() &&
                queryInput.text.isEmpty() &&
                queryInput.hasFocus()
        historyView.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        clearButton.setOnClickListener {
            queryInput.setText("")
            viewModel.clearSearch()
            hideKeyboard()
        }

        placeholderMessageButton.setOnClickListener {
            performSearch()
        }

        clearHistoryButton.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        backButton.setNavigationOnClickListener {
            finish()
        }
    }


    private fun setupTextWatcher() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                valueEditText = s?.toString()

                if (s.isNullOrEmpty()) {
                    viewModel.cancelSearch()
                    viewModel.clearSearch()
                    viewModel.loadSearchHistory()
                    showSearchHistory()
                } else {
                    viewModel.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        queryInput.addTextChangedListener(simpleTextWatcher)

        queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (queryInput.text.isNotEmpty()) {
                    viewModel.cancelSearch()
                    performSearch()
                }
                true
            } else {
                false
            }
        }

        queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && queryInput.text.isEmpty()) {
                viewModel.loadSearchHistory()
            }
        }
    }

    private fun performSearch() {
        val query = queryInput.text.toString()
        if (query.isNotEmpty()) {
            viewModel.searchTracksImmediately(query)
        }
    }

    private fun showSearchHistory() {
        val hasHistory = !viewModel.observeHistory.value.isNullOrEmpty()
        historyView.visibility = if (hasHistory && queryInput.text.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun hideSearchHistory() {
        historyView.visibility = View.GONE
    }

    private fun showEmptyState() {
        placeholderMessageText.text = getString(R.string.nothing_found)
        placeholderMessageImage.setImageResource(R.drawable.nothing_found_placeholder)
        placeholderMessageButton.visibility = View.GONE
    }

    private fun showErrorState() {
        placeholderMessageText.text = getString(R.string.connection_problem)
        placeholderMessageImage.setImageResource(R.drawable.connection_problem_placeholder)
        placeholderMessageButton.visibility = View.VISIBLE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(queryInput.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VALUE_EDIT_TEXT, valueEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        valueEditText = savedInstanceState.getString(VALUE_EDIT_TEXT)
        queryInput.setText(valueEditText ?: "")
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSearchHistory()
    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
    }
}