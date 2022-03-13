package com.example.mockito.tests_search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mockito.R
import com.example.mockito.databinding.ActivityMainBinding
import com.example.mockito.repository.GitHubApi
import com.example.mockito.repository.GitHubRepository
import com.example.mockito.tests_details.DetailsActivity
import com.example.mockito.tests_search.model.SearchResult
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), ViewSearchContract {

    private lateinit var binding: ActivityMainBinding

    private val adapterUsers by lazy {
        SearchResultAdapter(results = ArrayList())
    }
    private val presenter: PresenterSearchContract = SearchPresenter(this, createRepository())
    private var totalCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUI()
    }

    /**
     * теперь по кнопке TO DETAILS у нас будет открываться экран с количеством найденных репозиториев.
     * totalCount - количество найденных репозиториев
     */
    private fun setUI() {
        binding.toDetailsActivityButton.setOnClickListener {
            startActivity(DetailsActivity.getIntent(this, totalCount))
        }
        setQueryListener()
        setRecyclerView()
    }

    private fun setRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = adapterUsers
    }

    /**
     * кнопку поиска будем кликать на клавиатуре
     */
    private fun setQueryListener() {
        binding.searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = binding.searchEditText.text.toString()
                if (query.isNotBlank()) {
                    presenter.searchGitHub(query)
                    return@OnEditorActionListener true
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.enter_search_word),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@OnEditorActionListener false
                }
            }
            false
        })
    }

    private fun createRepository(): GitHubRepository {
        return GitHubRepository(createRetrofit().create(GitHubApi::class.java))
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun displaySearchResults(
        searchResults: List<SearchResult>,
        totalCount: Int
    ) {
        this.totalCount = totalCount
        adapterUsers.results = searchResults
        adapterUsers.notifyDataSetChanged()
    }

    override fun displayError() {
        Toast.makeText(this, getString(R.string.undefined_error), Toast.LENGTH_SHORT).show()
    }

    override fun displayError(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    override fun displayLoading(show: Boolean) {
        if (show) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    companion object {

        const val BASE_URL = "https://api.github.com"
    }
}