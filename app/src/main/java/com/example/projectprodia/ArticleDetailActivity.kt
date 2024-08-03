package com.example.projectprodia

import android.os.Bundle
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import com.example.projectprodia.databinding.ActivityArticleDetailBinding
import java.text.SimpleDateFormat
import java.util.Locale

class ArticleDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengambil data dari Intent
        val publishedAt = intent.getStringExtra("published_at") ?: ""
        val summary = intent.getStringExtra("summary") ?: ""

        // Format published_at
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = dateFormat.parse(publishedAt)
        val formattedDate = DateFormat.format("dd MMMM yyyy, HH:mm", date).toString()

        // Menampilkan formatted date dan summary
        binding.tvPublishedAt.text = formattedDate
        binding.tvSummary.text = getSummaryBeforeFirstPeriod(summary)
    }

    // Mengekstrak summary hingga kalimat pertama
    private fun getSummaryBeforeFirstPeriod(summary: String): String {
        val periodIndex = summary.indexOf('.')
        return if (periodIndex != -1) {
            summary.substring(0, periodIndex + 1)
        } else {
            summary
        }
    }
}
