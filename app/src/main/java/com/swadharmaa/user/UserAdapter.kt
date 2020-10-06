package com.swadharmaa.user

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.siyamed.shapeimageview.CircularImageView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.squareup.picasso.Picasso
import com.swadharmaa.Home
import com.swadharmaa.R
import com.swadharmaa.family.ActFamily
import com.swadharmaa.general.Enums
import com.swadharmaa.general.getData
import org.apache.commons.lang3.StringUtils

class UserAdapter(
    private var dataList: List<ProData>,
    private val activity: Activity

) :
    RecyclerView.Adapter<UserAdapter.Holder>() {
    lateinit var data: ProData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_user, parent, false)
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat", "ResourceAsColor")
    override fun onBindViewHolder(holder: Holder, @SuppressLint("RecyclerView") position: Int) {
        try {
            data = dataList[position]
            Picasso.get().load(
                getData(
                    "rootPath",
                    activity.applicationContext
                ) + Enums.Dp.value + data.dp
            ).placeholder(R.drawable.ic_dummy_profile).into(holder.imgProfile)
            holder.txtName.text =
                StringUtils.capitalize(data.fname) + " " + StringUtils.capitalize(data.lname)
            holder.txtEmail.text = data.email
            holder.txtMobile.text = data.mobile
            holder.cardUser.setOnClickListener {
                data = dataList[position]
                val intent = Intent(activity, ActFamily::class.java)
                intent.putExtra(activity.getString(R.string.userId), data._id)
                activity.startActivity(intent)
                activity.finish()
            }
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
            e.printStackTrace()
        }
    }

    fun filterList(filteredList: MutableList<ProData>) {
        this.dataList = filteredList
        notifyDataSetChanged()
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
        var imgProfile: CircularImageView = view.findViewById(R.id.img_profile)
        var txtName: MaterialTextView = view.findViewById(R.id.txt_fullname)
        var txtEmail: MaterialTextView = view.findViewById(R.id.txt_email)
        var txtMobile: MaterialTextView = view.findViewById(R.id.txt_mobile)
        var cardUser: MaterialCardView = view.findViewById(R.id.card_user)
    }
}
