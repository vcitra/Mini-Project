package com.example.projectprodia

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ArticleAdapter(
    private val context: Context,
    private var dataList: ArrayList<ResponseModel>
) : RecyclerView.Adapter<ArticleAdapter.MyViewHolder>() {

    private var filteredDataList = ArrayList<ResponseModel>(dataList)

    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val ivArticle: ImageView = view.findViewById(R.id.iv_article)
        val btnDetail: Button = view.findViewById(R.id.btn_detail)
//        val cvArticle: CardView = view.findViewById(R.id.cv_article)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemView = layoutInflater.inflate(R.layout.items_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val article = filteredDataList[position]
        holder.tvTitle.text = article.title

        Glide.with(context)
            .load(article.imageUrl)
            .into(holder.ivArticle)

        holder.btnDetail.setOnClickListener {
            // Pindah ke ArticleDetailActivity ketika button diklik
            val intent = Intent(context, ArticleDetailActivity::class.java)
            intent.putExtra("published_at", article.publishedAt)
            intent.putExtra("summary", article.summary)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = filteredDataList.size

    fun setData(data: ArrayList<ResponseModel>) {
        dataList = data
        filteredDataList = ArrayList(data)
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredDataList = if (query.isEmpty()) {
            ArrayList(dataList)
        } else {
            dataList.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.newsSite.contains(query, ignoreCase = true)
            } as ArrayList<ResponseModel>
        }
        notifyDataSetChanged()
    }
}
