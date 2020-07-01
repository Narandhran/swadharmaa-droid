package com.swadharmaa.general

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.squareup.picasso.Picasso
import com.swadharmaa.R


class ViewPagerAdapter(
    private val flag: Boolean,
    private val context: Context,
    private val uri: MutableList<Int>
) :
    PagerAdapter() {
    private var layoutInflater: LayoutInflater? = null
    var images =
        arrayOf(
            R.drawable.img_placeholder,
            R.drawable.img_placeholder,
            R.drawable.img_placeholder
        )

    override fun getCount(): Int {
        return if (uri.size == 0) {
            images.size
        } else {
            uri.size
        }
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    @SuppressLint("InflateParams")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
        val view: View = layoutInflater?.inflate(R.layout.custom_image_banner, null)!!
        val imageView: ImageView = view.findViewById(R.id.img_category) as ImageView

        if (uri.size == 0) {
            Picasso.get().load(images[position])
                .error(R.drawable.img_placeholder)
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)
        } else {
//                val file = File(uri[position])
            Picasso.get().load(uri[position])
                .error(R.drawable.img_placeholder)
                .placeholder(R.drawable.img_placeholder)
                .into(imageView)
        }
        val vp = container as ViewPager
        vp.addView(view)
        return view
    }

    override fun destroyItem(
        container: ViewGroup,
        position: Int,
        `object`: Any
    ) {
        val vp = container as ViewPager
        val view: View = `object` as View
        vp.removeView(view)
    }
}