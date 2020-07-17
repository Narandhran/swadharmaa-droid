package com.swadharmaa.category

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.swadharmaa.R
import com.swadharmaa.discover.SeeAll
import com.swadharmaa.general.Enums
import com.swadharmaa.general.getData
import org.apache.commons.lang3.StringUtils


class CategoryAdapter(
    private var dataList: List<CategoryData>,
    private val activity: Activity

) :
    RecyclerView.Adapter<CategoryAdapter.Holder>() {
    lateinit var data: CategoryData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_category, parent, false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ResourceAsColor")
    override fun onBindViewHolder(holder: Holder, @SuppressLint("RecyclerView") position: Int) {
        try {
            data = dataList[position]
            holder.txtName.text = StringUtils.capitalize(data.name)
            Picasso.get().load(
                getData(
                    "rootPath",
                    activity.applicationContext
                ) + Enums.Category.value + data.thumbnail
            ).placeholder(R.drawable.img_placeholder)
                .fit().into(holder.imgCategory)

            holder.cardCategory.setOnClickListener {
                data = dataList[position]
                val intent = Intent(activity, SeeAll::class.java)
                intent.putExtra(
                    activity.getString(R.string.data),
                    activity.getString(R.string.loadBooksByCategory)
                )
                intent.putExtra(activity.getString(R.string.id), data._id)
                intent.putExtra(activity.getString(R.string.name), data.name)
                activity.startActivity(intent)
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            e.printStackTrace()
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        var imgCategory: PorterShapeImageView = view.findViewById(R.id.img_category)
        var txtName: MaterialTextView = view.findViewById(R.id.txt_name)
        var cardCategory: MaterialCardView = view.findViewById(R.id.card_category)
    }
}
