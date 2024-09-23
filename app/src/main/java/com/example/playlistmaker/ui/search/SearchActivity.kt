package com.example.playlistmaker.ui.search

import android.content.Intent
import android.content.SharedPreferences
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
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.data.SearchHistory
import com.example.playlistmaker.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.domain.api.HistoryInteractor
import com.example.playlistmaker.domain.api.TrackInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.main.MainActivity
import com.example.playlistmaker.ui.player.PlayerActivity
import com.example.playlistmaker.util.Creator


class SearchActivity : AppCompatActivity() {





    private lateinit var clearButton: ImageView
    private lateinit var historylist: LinearLayout
    private lateinit var backButton: Button
    private var searchText: String? = null
    private lateinit var toClearHistory: Button
    private lateinit var inputEditText: EditText
    private lateinit var refreshButton: Button
    private lateinit var placeholder: LinearLayout
    private lateinit var textPlaceholder: TextView
    private lateinit var imagePlaceholder: ImageView
    private lateinit var  rvTrackSearch: RecyclerView
    private lateinit var trackList: RecyclerView
    private lateinit var progressBar: ProgressBar
    private var lastFailedSearchQuery: String? = null
    private val tracks = ArrayList<Track>()
    private lateinit var historyInteractor: HistoryInteractor
    private lateinit var trackInteractor: TrackInteractor
    private val adapter = TrackAdapter(tracks,
        object : TrackAdapter.TrackActionListener {
        override fun onClickItem(track: Track) {
            if (clickDebounce()) {
                historyInteractor.addtoHistory(track)
            //searchHistoryRepositoryImpl.saveTrackToHistory(track)
            //searchHistory.write(sharedPrefs,track)
            val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
            playerIntent.putExtra("track",track)
            startActivity(playerIntent)
        }}
    })

    private var tracksOnHistory = ArrayList<Track>()
    private lateinit var  adapterHistory : TrackAdapter








    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { searchText?.let { searchRequest(it) } }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //searchHistoryRepositoryImpl = SearchHistoryRepositoryImpl(SearchHistory(),this)
        historyInteractor = Creator.provideHistoryInteractor(this)
        trackInteractor = Creator.provideTrackInteractor(this)
        setContentView(R.layout.activity_search)
        rvTrackSearch=findViewById(R.id.rvTrackSearch)
        placeholder = findViewById(R.id.llPlaceholderMessage)
        textPlaceholder = findViewById(R.id.tvPlaceholderMessage)
        imagePlaceholder = findViewById(R.id.ivPlaceholderImage)
        inputEditText = findViewById(R.id.imputEditText)
        clearButton = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button)
        trackList = findViewById(R.id.rvTrack)
        refreshButton = findViewById(R.id.refreshButton)
        historylist = findViewById(R.id.llhistory)
        toClearHistory = findViewById(R.id.bttntoClearHistory)
        progressBar = findViewById(R.id.progressBar)
        trackList.adapter = adapter
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)





        adapterHistory = TrackAdapter(tracksOnHistory,
            object : TrackAdapter.TrackActionListener {
                override fun onClickItem(track: Track) {
                    if (clickDebounce()) {
                        //searchHistoryRepositoryImpl.saveTrackToHistory(track)
                        historyInteractor.addtoHistory(track)
                        if (!historyInteractor.showHistory().isEmpty()
                            ){
                            tracksOnHistory = historyInteractor.showHistory().isEmpty() as ArrayList<Track>
                            adapterHistory.submitList(tracksOnHistory)
                        }

                    val playerIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                    playerIntent.putExtra("track",track)
                    startActivity(playerIntent)

                }}
            })








        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        inputEditText.requestFocus()


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
            lastFailedSearchQuery?.let { query ->
                searchRequest(query)
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                historylist.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && !historyInteractor.showHistory().isEmpty()) View.VISIBLE else View.GONE
                trackList.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true && !historyInteractor.showHistory().isEmpty()) View.GONE else View.VISIBLE
                if ( !historyInteractor.showHistory().isEmpty()){
                tracksOnHistory = historyInteractor.showHistory() as ArrayList<Track>
                adapterHistory.submitList(tracksOnHistory)}
                clearButton.visibility = clearButtonVisibility(s)
                searchDebounce()
                trackList.visibility = if(progressBar.visibility==View.VISIBLE) View.GONE else View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {
                searchText = s.toString()
            }

        }
        inputEditText.addTextChangedListener(textWatcher)

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString("my_text")
            inputEditText.setText(searchText)
        }

        rvTrackSearch.adapter = adapterHistory
        rvTrackSearch.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


    inputEditText.setOnFocusChangeListener{view, hasFocus ->
        historylist.visibility = if (hasFocus && inputEditText.text.isEmpty() &&!historyInteractor.showHistory().isEmpty())
            View.VISIBLE
        else View.GONE
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)

    }


    if (!historyInteractor.showHistory().isEmpty()){
        tracksOnHistory = historyInteractor.showHistory() as ArrayList<Track>
        historylist.visibility =View.VISIBLE
        adapterHistory.submitList(tracksOnHistory)
    }

    toClearHistory.setOnClickListener {
        historyInteractor.clearHistory()
        //sharedPrefs.edit().clear().apply()
        tracksOnHistory.clear()
        adapterHistory.submitList(tracksOnHistory)
        historylist.visibility = View.GONE
        tracks.clear()
    }




    }

    override fun onStop() {
        super.onStop()
        historyInteractor.onStopActivityHistory(tracksOnHistory)

    }

    private fun showMessage(text: String, image: Drawable) {
        if (text.isNotEmpty()) {
            placeholder.visibility = View.VISIBLE
            tracks.clear()
            adapter.notifyDataSetChanged()
            imagePlaceholder.setImageDrawable(image)
            textPlaceholder.text = text
        } else {
            placeholder.visibility=View.GONE
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("my_text", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("my_text")
        inputEditText.setText(searchText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

companion object{
    const val MY_PREFERENCES= "MyPreferences"
    private const val CLICK_DEBOUNCE_DELAY = 1000L
    private const val SEARCH_DEBOUNCE_DELAY = 2000L
}
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }


    private fun searchRequest(searchText:String){

        if(searchText.isNotEmpty()){
        progressBar.visibility=View.VISIBLE
        trackList.visibility = View.GONE
        placeholder.visibility = View.GONE
         trackInteractor.searchTrack(searchText,object :TrackInteractor.TrackConsumer{
             override fun consume(foundTracks: List<Track>?,errorMessage:String?) {
                 handler.post {
                     progressBar.visibility=View.GONE
                     if(foundTracks!= null){
                     tracks.clear()
                     refreshButton.visibility =View.GONE
                     placeholder.visibility = View.GONE
                     trackList.visibility=View.VISIBLE
                     tracks.addAll(foundTracks)
                     adapter.notifyDataSetChanged()}
                     if(errorMessage != null){
                         progressBar.visibility=View.GONE
                         lastFailedSearchQuery = searchText
                         showMessage(getString(R.string.something_went_wrong),  ContextCompat.getDrawable(this@SearchActivity,
                         R.drawable.placeholder_no_internet
                         ) ?: return@post)
                         refreshButton.visibility=View.VISIBLE
                     }else if (tracks.isEmpty()){ progressBar.visibility=View.GONE
                        refreshButton.visibility =View.GONE
                        showMessage(getString(R.string.nothing_found), ContextCompat.getDrawable(this@SearchActivity,
                            R.drawable.placeholder_no_results
                        ) ?: return@post)
                     }

                 }
             }

         })

     }
    }
}
