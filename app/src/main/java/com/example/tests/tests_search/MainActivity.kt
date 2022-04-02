package com.example.tests.tests_search

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tests.BuildConfig
import com.example.tests.R
import com.example.tests.databinding.ActivityMainBinding
import com.example.tests.repository.FakeGitHubRepository
import com.example.tests.repository.GitHubRepository
import com.example.tests.repository.GitHubService
import com.example.tests.repository.RepositoryContract
import com.example.tests.tests_details.DetailsActivity
import com.example.tests.tests_search.model.SearchResult
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Locale

class MainActivity : AppCompatActivity(), ViewSearchContract {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val adapterUsers by lazy {
        SearchResultAdapter(results = ArrayList())
    }
    private val presenter: PresenterSearchContract = SearchPresenter(this, createRepository())
    private var totalCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
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
        binding.searchEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
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

    /**
     * создаем Репозиторий для презентера. Метод createRepository() теперь возвращает интерфейс, а его реализация зависит от сборки.
     */
    private fun createRepository(): RepositoryContract {
        return if (BuildConfig.TYPE == FAKE) {
            FakeGitHubRepository()
        } else {
            GitHubRepository(createRetrofit().create(GitHubService::class.java))
        }
    }

    /**
     * Ретрофит нужно создавать в отдельном классе и инжектить в Презентер. В нашем проекте код упрощен,
     * чтобы сконцентрироваться на главной теме — тестировании.
     */
    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun displaySearchResults(
        searchResults: List<SearchResult>,
        totalCount: Int
    ) {
        with(binding.totalCountTextViewMain) {
            visibility = View.VISIBLE
            text = String.format(Locale.getDefault(), getString(R.string.results_count), totalCount)
        }
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

        const val FAKE = "FAKE"
        const val BASE_URL = "https://api.github.com"
    }
}