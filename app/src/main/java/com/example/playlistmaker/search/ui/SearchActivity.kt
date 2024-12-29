package com.example.playlistmaker.search.ui

import android.content.Intent
import android.graphics.drawable.Drawable

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
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
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.ui.view_model.SearchPresenter
import com.example.playlistmaker.search.ui.view_model.TrackState
import com.example.playlistmaker.util.Creator
import com.example.playlistmaker.search.ui.view_model.TracksView
import com.example.playlistmaker.util.App
import moxy.MvpActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter


class SearchActivity : MvpActivity(), TracksView {
    @InjectPresenter
    lateinit var searchPresenter: SearchPresenter

    @ProvidePresenter
    fun providePresenter(): SearchPresenter {
        return Creator.provideTrackSearchPresenter(
            context = this.applicationContext,
        )
    }
    private lateinit var clearButton: ImageView
    private lateinit var historylist: LinearLayout
    private lateinit var backButton: Button
    private lateinit var toClearHistory: Button
    private lateinit var  rvTrackSearch: RecyclerView
    private lateinit var inputEditText: EditText
    private lateinit var trackList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var refreshButton: Button
    private lateinit var placeholder: LinearLayout
    private lateinit var textPlaceholder: TextView
    private lateinit var imagePlaceholder: ImageView
    private var searchText: String? = null
    private var tracks = ArrayList<Track>()


    private val adapter = TrackAdapter(tracks,
        object : TrackAdapter.TrackActionListener {
            override fun onClickItem(track: Track) {
                if (clickDebounce()) {
                    searchPresenter!!.addToHistory(track)
                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    playerIntent.putExtra("track",track)
                    startActivity(playerIntent)
                }}
        })
    private var textWatcher: TextWatcher? = null
    private var tracksOnHistory = ArrayList<Track>()
    private lateinit var  adapterHistory : TrackAdapter
    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())
    //private val searchRunnable = Runnable { searchText?.let { searchRequest(it) } }
//    private fun searchDebounce() {
//        handler.removeCallbacks(searchRunnable)
//        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        placeholder = findViewById(R.id.llPlaceholderMessage)
        inputEditText = findViewById(R.id.imputEditText)
        trackList = findViewById(R.id.rvTrack)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
        clearButton = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button)
        historylist = findViewById(R.id.llhistory)
        toClearHistory = findViewById(R.id.bttntoClearHistory)
        textPlaceholder = findViewById(R.id.tvPlaceholderMessage)
        imagePlaceholder = findViewById(R.id.ivPlaceholderImage)
        rvTrackSearch=findViewById(R.id.rvTrackSearch)

        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackList.adapter = adapter
        adapterHistory = TrackAdapter(tracksOnHistory,
            object : TrackAdapter.TrackActionListener {
                override fun onClickItem(track: Track) {
                    if (clickDebounce()) {
                        searchPresenter?.historyToHistory(track)
                        val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                        playerIntent.putExtra("track",track)
                        startActivity(playerIntent)
                    }}
            })
        rvTrackSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvTrackSearch.adapter = adapterHistory






        inputEditText.requestFocus()
        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {

                historylist.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && !searchPresenter!!.checkEmptyHistoryList()) View.VISIBLE else View.GONE
                trackList.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && !searchPresenter!!.checkEmptyHistoryList()) View.GONE else View.VISIBLE
                if ( !searchPresenter!!.checkEmptyHistoryList()){
                    tracksOnHistory = searchPresenter!!.showHistoryList()
                    adapterHistory.submitList(tracksOnHistory)}
                    clearButton.visibility = clearButtonVisibility(s)
                    searchPresenter!!.searchDebounce(
                    changedText = s?.toString() ?: ""
                )
                trackList.visibility = if(progressBar.visibility== View.VISIBLE) View.GONE else View.VISIBLE
                textWatcher?.let { inputEditText.addTextChangedListener(it) }
            }

            override fun afterTextChanged(p0: Editable?) {
                searchText = p0.toString()
            }
        }
        inputEditText.addTextChangedListener(textWatcher)
        inputEditText.setOnFocusChangeListener{view, hasFocus ->
            historylist.visibility = if (hasFocus && inputEditText.text.isEmpty() &&!searchPresenter!!.checkEmptyHistoryList())
                View.VISIBLE
            else View.GONE
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

        }
        backButton.setOnClickListener {
            finish()
            tracks.clear()
        }

        clearButton.setOnClickListener {
            tracks.clear()
            adapter.notifyDataSetChanged()
            refreshButton.visibility =View.GONE
            placeholder.visibility = View.GONE
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }

        refreshButton.setOnClickListener {
            searchPresenter!!.refreshRequest()
        }

        if (!searchPresenter!!.checkEmptyHistoryList()){
            tracksOnHistory = searchPresenter!!.showHistoryList()
            historylist.visibility =View.VISIBLE
            adapterHistory.submitList(tracksOnHistory)
        }

        toClearHistory.setOnClickListener {
            searchPresenter!!.clearHistory()
            tracksOnHistory.clear()
            adapterHistory.submitList(tracksOnHistory)
            historylist.visibility = View.GONE

            tracks.clear()
        }


        if (savedInstanceState != null) {
            onGetInstanceState(savedInstanceState)
        }


    }
    override fun onStart() {
        super.onStart()

    }

    override fun onResume() {
        super.onResume()

    }
    override fun onPause() {
        super.onPause()

    }
    override fun onStop() {
        super.onStop()
        searchPresenter!!.onStop()

    }
    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }

        searchPresenter!!.onDestroy()

    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString("my_text", searchText)
    }
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchText = savedInstanceState.getString("my_text")
        findViewById<EditText>(R.id.imputEditText).setText(searchText)
    }
    fun onGetInstanceState(savedInstanceState: Bundle?){
        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("my_text")
            findViewById<EditText>(R.id.imputEditText).setText(searchText)
        }
    }
    fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }



        companion object{
    private const val CLICK_DEBOUNCE_DELAY = 1000L
}
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }



    fun updateTracksList(newTracksList: List<Track>) {
        tracks.clear()
        tracks.addAll(newTracksList)
        adapter.notifyDataSetChanged()
    }

    override fun updateHistoryList(newHistoryList: List<Track>) {
        adapterHistory.submitList(tracksOnHistory)
    }

    fun showLoading() {
        trackList.visibility = View.GONE
        placeholder.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        refreshButton.visibility =View.GONE
    }

    fun showError(errorMessage:String,errorImage:Drawable) {
        progressBar.visibility = View.GONE
        trackList.visibility = View.GONE
        placeholder.visibility = View.VISIBLE
        refreshButton.visibility = View.VISIBLE
        textPlaceholder.text = errorMessage
        imagePlaceholder.setImageDrawable(errorImage)

    }

    fun showEmpty(emptyMessage:String,emptyImage:Drawable){
        showError(emptyMessage,emptyImage)
    }


    fun showContent(tracks: List<Track>) {

        trackList.visibility = View.VISIBLE
        placeholder.visibility = View.GONE
        progressBar.visibility = View.GONE
        refreshButton.visibility =View.GONE
        updateTracksList(tracks)
    }

    override fun render(state: TrackState) {
        when(state) {
            is TrackState.Loading -> showLoading()
            is TrackState.Content -> showContent(state.tracks)
            is TrackState.Error -> showError(state.errorMessage,state.errorImage)
            is TrackState.Empty -> showEmpty(state.message,state.emptyImage)
        }
    }
}
