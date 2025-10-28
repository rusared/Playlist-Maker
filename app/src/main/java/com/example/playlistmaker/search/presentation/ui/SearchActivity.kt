package com.example.playlistmaker.search.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.model.Track
import com.example.playlistmaker.player.presentation.ui.PlayerActivity
import com.example.playlistmaker.search.presentation.debounce.ClickDebouncer
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel
import com.example.playlistmaker.search.presentation.view_model.SearchViewModel.SearchState
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var tracksAdapter: TracksAdapter
    private lateinit var tracksHistoryAdapter: TracksAdapter

    private val clickDebouncer: ClickDebouncer by inject()
    private val vm by viewModel<SearchViewModel>()

    private var valueEditText: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.searchBackButton)

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
            vm.addTrackToHistory(track)
        }

        tracksAdapter = TracksAdapter(emptyList(), trackClickListener, clickDebouncer)
        tracksHistoryAdapter = TracksAdapter(emptyList(), trackClickListener, clickDebouncer)

        binding.tracksList.layoutManager = LinearLayoutManager(this)
        binding.trackHistoryList.layoutManager = LinearLayoutManager(this)
        binding.tracksList.adapter = tracksAdapter
        binding.trackHistoryList.adapter = tracksHistoryAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        clickDebouncer.reset()
        tracksAdapter.onDestroy()
        tracksHistoryAdapter.onDestroy()
    }

    private fun setupObservers() {
        vm.observeState.observe(this) { state ->
            handleSearchState(state)
        }

        vm.observeHistory.observe(this) { history ->
            handleHistory(history)
        }
    }

    private fun handleSearchState(state: SearchState) {
        binding.progressBar.isVisible = state is SearchState.Loading
        binding.tracksList.isVisible = state is SearchState.Content
        binding.placeholderMessage.isVisible = state is SearchState.Empty || state is SearchState.Error

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
        binding.trackHistory.isVisible = shouldShowHistory
    }

    private fun setupClickListeners() {
        binding.clearIcon.setOnClickListener {
            binding.queryInput.setText("")
            vm.clearSearch()
            hideKeyboard()
        }

        binding.placeholderMessageButton.setOnClickListener {
            performSearch()
        }

        binding.trackHistoryClear.setOnClickListener {
            vm.clearSearchHistory()
        }

        binding.searchBackButton.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupTextWatcher() {
        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearIcon.isVisible = !s.isNullOrEmpty()
                valueEditText = s?.toString()

                if (s.isNullOrEmpty()) {
                    vm.cancelSearch()
                    vm.clearSearch()
                    vm.loadSearchHistory()
                    showSearchHistory()
                } else {
                    vm.searchDebounce(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        binding.queryInput.addTextChangedListener(simpleTextWatcher)

        binding.queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.queryInput.text.isNotEmpty()) {
                    vm.cancelSearch()
                    performSearch()
                }
                true
            } else {
                false
            }
        }

        binding.queryInput.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && binding.queryInput.text.isEmpty()) {
                vm.loadSearchHistory()
            }
        }
    }

    private fun performSearch() {
        val query = binding.queryInput.text.toString()
        if (query.isNotEmpty()) {
            vm.searchTracksImmediately(query)
        }
    }

    private fun showSearchHistory() {
        val hasHistory = !vm.observeHistory.value.isNullOrEmpty()
        binding.trackHistory.isVisible = (hasHistory && binding.queryInput.text.isEmpty())
    }

    private fun hideSearchHistory() {
        binding.trackHistory.isVisible = false
    }

    private fun showEmptyState() {
        binding.placeholderMessageText.text = getString(R.string.nothing_found)
        binding.placeholderMessageImage.setImageResource(R.drawable.nothing_found_placeholder)
        binding.placeholderMessageButton.isVisible = false
    }

    private fun showErrorState() {
        binding.placeholderMessageText.text = getString(R.string.connection_problem)
        binding.placeholderMessageImage.setImageResource(R.drawable.connection_problem_placeholder)
        binding.placeholderMessageButton.isVisible = true
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
        vm.loadSearchHistory()
    }

    companion object {
        private const val VALUE_EDIT_TEXT = "value_edit_text"
    }
}