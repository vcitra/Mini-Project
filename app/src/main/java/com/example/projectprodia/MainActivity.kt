package com.example.projectprodia

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectprodia.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var searchHistoryAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi RecyclerView dan Adapter untuk articles
        articleAdapter = ArticleAdapter(this, arrayListOf())
        binding.rvArticle.layoutManager = LinearLayoutManager(this)
        binding.rvArticle.adapter = articleAdapter

        // Inisialisasi ListView dan Adapter untuk search history
        searchHistoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf())
        binding.lvSearchHistory.adapter = searchHistoryAdapter

        // Memuat article
        loadArticles()

        // Memuat search history dari file
        loadSearchHistoryFromFile()

        // Mempersiapkan search EditText listener
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    // Show search history dan hide articles
                    binding.rlSearchHistoryContainer.visibility = View.VISIBLE
                    binding.rlArticleContainer.visibility = View.GONE
                    updateSearchHistoryAdapter(query)
                } else {
                    // Show articles list dan hide search history
                    binding.rlSearchHistoryContainer.visibility = View.GONE
                    binding.rlArticleContainer.visibility = View.VISIBLE
                    loadArticles() // Reload semua artikel
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Mengatur Enter key press pada EditText
        binding.etSearch.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val query = binding.etSearch.text.toString().trim()
                if (query.isNotEmpty()) {
                    // Menyimpan search query ke SharedPreferences dan file
                    saveSearchQuery(query)
                    fetchArticles(query)
                    // Hide search history dan show articles
                    binding.rlSearchHistoryContainer.visibility = View.GONE
                    binding.rlArticleContainer.visibility = View.VISIBLE
                }
                true
            } else {
                false
            }
        }

        // Menangani item click pada search history
        binding.lvSearchHistory.setOnItemClickListener { _, _, position, _ ->
            val query = searchHistoryAdapter.getItem(position) ?: return@setOnItemClickListener
            binding.etSearch.setText(query)
            fetchArticles(query)
            binding.rlSearchHistoryContainer.visibility = View.GONE
            binding.rlArticleContainer.visibility = View.VISIBLE
        }

        // Menangani Fokus pada EditText
        binding.etSearch.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // Show search history ketika EditText sedang fokus
                binding.rlSearchHistoryContainer.visibility = View.VISIBLE
                binding.rlArticleContainer.visibility = View.GONE
                updateSearchHistoryAdapter("")
            }
        }
    }

    private fun loadArticles() {
        val apiService = ApiClient.apiService
        apiService.getArticles().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.results ?: emptyList()
                    articleAdapter.setData(ArrayList(articles))
                } else {
                    Toast.makeText(this@MainActivity, "Failed to load articles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchArticles(query: String) {
        val apiService = ApiClient.apiService
        apiService.getArticles().enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val articles = response.body()?.results?.filter {
                        it.title.contains(query, ignoreCase = true) ||
                                it.newsSite.contains(query, ignoreCase = true)
                    } ?: emptyList()
                    articleAdapter.setData(ArrayList(articles))
                } else {
                    Toast.makeText(this@MainActivity, "Failed to fetch articles", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Network error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateSearchHistoryAdapter(query: String) {
        val file = File(filesDir, "search_history.txt")
        val history = if (file.exists()) {
            file.readLines()
        } else {
            emptyList()
        }
        val filteredHistory = if (query.isNotEmpty()) {
            history.filter { it.contains(query, ignoreCase = true) }
        } else {
            history // Menampilkan semua history ketika query kosong
        }
        searchHistoryAdapter.clear()
        searchHistoryAdapter.addAll(filteredHistory)
        searchHistoryAdapter.notifyDataSetChanged()
    }

    private fun saveSearchQuery(query: String) {
        val file = File(filesDir, "search_history.txt")
        file.appendText("$query\n")
        updateSearchHistoryAdapter("") // Refresh history list
    }

    private fun loadSearchHistoryFromFile() {
        val file = File(filesDir, "search_history.txt")
        if (file.exists()) {
            val history = file.readLines()
            searchHistoryAdapter.clear()
            searchHistoryAdapter.addAll(history)
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }
}
