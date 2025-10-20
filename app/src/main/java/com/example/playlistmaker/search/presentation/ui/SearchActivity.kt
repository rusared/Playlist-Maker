package com.example.playlistmaker.search.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.presentation.ui.PlayerActivity
import com.example.playlistmaker.search.presentation.debounce.ClickDebouncerImpl
import com.example.playlistmaker.search.presentation.debounce.ClickDebouncer
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel.SearchState

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var tracksHistoryAdapter: TracksAdapter

    private lateinit var clickDebouncer: ClickDebouncer

    private val viewModel: SearchViewModel by viewModels {
        Creator.provideSearchViewModelFactory(this)
    }

    private var valueEditText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchBackButton)

        clickDebouncer = provideClickDebouncer()

        setupAdapters()
        setupObservers()
        setupClickListeners()
        setupTextWatcher()

        binding.queryInput.requestFocus()
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

        binding.tracksList.layoutManager = LinearLayoutManager(this)
        binding.trackHistoryList.layoutManager = LinearLayoutManager(this)
        binding.tracksList.adapter = tracksAdapter
        binding.trackHistoryList.adapter = tracksHistoryAdapter
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
        binding.progressBar.visibility = if (state is SearchState.Loading) View.VISIBLE else View.GONE
        binding.tracksList.visibility = if (state is SearchState.Content) View.VISIBLE else View.GONE
        binding.placeholderMessage.visibility = if (state is SearchState.Empty || state is SearchState.Error) View.VISIBLE else View.GONE

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
                binding.queryInput.text.isEmpty() &&
                binding.queryInput.hasFocus()
        binding.trackHistory.visibility = if (shouldShowHistory) View.VISIBLE else View.GONE
    }

    private fun setupClickListeners() {
        binding.clearIcon.setOnClickListener {
            binding.queryInput.setText("")
            viewModel.clearSearch()
            hideKeyboard()
        }

        binding.placeholderMessageButton.setOnClickListener {
            performSearch()
        }

        binding.trackHistoryClear.setOnClickListener {
            viewModel.clearSearchHistory()
        }

        binding.searchBackButton.setNavigationOnClickListener {
            finish()
        }
    }


    private fun setupTextWatcher() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.visibility = clearButtonVisibility(s)
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

        binding.queryInput.addTextChangedListener(simpleTextWatcher)

        binding.queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.queryInput.text.isNotEmpty()) {
                    viewModel.cancelSearch()
                    performSearch()
                }
                true
            } else {
                false
            }
        }

        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.queryInput.text.isEmpty()) {
                viewModel.loadSearchHistory()
            }
        }
    }

    private fun performSearch() {
        val query = binding.queryInput.text.toString()
        if (query.isNotEmpty()) {
            viewModel.searchTracksImmediately(query)
        }
    }

    private fun showSearchHistory() {
        val hasHistory = !viewModel.observeHistory.value.isNullOrEmpty()
        binding.trackHistory.visibility = if (hasHistory && binding.queryInput.text.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun hideSearchHistory() {
        binding.trackHistory.visibility = View.GONE
    }

    private fun showEmptyState() {
        binding.placeholderMessageText.text = getString(R.string.nothing_found)
        binding.placeholderMessageImage.setImageResource(R.drawable.nothing_found_placeholder)
        binding.placeholderMessageButton.visibility = View.GONE
    }

    private fun showErrorState() {
        binding.placeholderMessageText.text = getString(R.string.connection_problem)
        binding.placeholderMessageImage.setImageResource(R.drawable.connection_problem_placeholder)
        binding.placeholderMessageButton.visibility = View.VISIBLE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.queryInput.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(VALUE_EDIT_TEXT, valueEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        valueEditText = savedInstanceState.getString(VALUE_EDIT_TEXT)
        binding.queryInput.setText(valueEditText ?: "")
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadSearchHistory()
    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
    }
}