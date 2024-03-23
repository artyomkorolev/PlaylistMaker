package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Placeholder
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {
    private val iTunesSearchBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesSearchBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchService = retrofit.create(iTunesSearchApi::class.java)

    private lateinit var clearButton: ImageView
    private lateinit var backButton: Button
    private var searchText: String? = null
    private lateinit var inputEditText: EditText
    private lateinit var refreshButton: Button
    private lateinit var placeholder: LinearLayout
    private lateinit var textPlaceholder: TextView
    private lateinit var imagePlaceholder: ImageView


    private lateinit var trackList: RecyclerView

    private val tracks = ArrayList<Track>()

    private val adapter = TrackAdapter(tracks)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        placeholder = findViewById(R.id.llPlaceholderMessage)
        textPlaceholder = findViewById(R.id.tvPlaceholderMessage)
        imagePlaceholder = findViewById(R.id.ivPlaceholderImage)
        inputEditText = findViewById(R.id.imputEditText)
        clearButton = findViewById(R.id.clearIcon)
        backButton = findViewById(R.id.back_button)
        trackList = findViewById(R.id.rvTrack)
        refreshButton = findViewById(R.id.refreshButton)
        trackList.adapter = adapter
        trackList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        var lastFailedSearchQuery: String? = null

        backButton.setOnClickListener {
            val backIntent = Intent(this, MainActivity::class.java)
            startActivity(backIntent)
        }
        inputEditText.requestFocus()

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        clearButton.setOnClickListener {
            tracks.clear()
            adapter.notifyDataSetChanged()
            inputEditText.setText("")
            val inputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
        }

        refreshButton.setOnClickListener {
            lastFailedSearchQuery?.let { query ->
                val call = iTunesSearchService.search(query)
                call.enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        if (response.code() == 200) {
                            tracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showMessage(getString(R.string.nothing_found), ContextCompat.getDrawable(this@SearchActivity, R.drawable.placeholder_no_results) ?: return)
                                refreshButton.visibility =View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(getString(R.string.something_went_wrong), ContextCompat.getDrawable(this@SearchActivity, R.drawable.placeholder_no_internet) ?: return)
                    }
                })
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                clearButton.visibility = clearButtonVisibility(s)
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

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (inputEditText.text.isNotEmpty()) {
                    val searchText = inputEditText.text.toString()
                    val call = iTunesSearchService.search(searchText)
                    call.enqueue(object : Callback<TrackResponse> {
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            if (response.code() == 200) {
                                tracks.clear()
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.addAll(response.body()?.results!!)
                                    adapter.notifyDataSetChanged()
                                }
                                if (tracks.isEmpty()) {

                                    showMessage(getString(R.string.nothing_found), ContextCompat.getDrawable(this@SearchActivity, R.drawable.placeholder_no_results) ?: return)


                                }
                            }
                        }

                        override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                            lastFailedSearchQuery = searchText
                            showMessage(getString(R.string.something_went_wrong),  ContextCompat.getDrawable(this@SearchActivity, R.drawable.placeholder_no_internet) ?: return)
                            refreshButton.visibility=View.VISIBLE
                        }
                    })
                }

                true
            }
            false
        }


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

}