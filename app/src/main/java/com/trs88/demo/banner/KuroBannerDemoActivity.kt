package com.trs88.demo.banner

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.trs88.kuro_library.R
import com.trs88.kuro_ui.banner.core.KuroBanner
import com.trs88.kuro_ui.banner.core.KuroBannerMo
import com.trs88.kuro_ui.banner.indicator.KuroCircleIndicator
import com.trs88.kuro_ui.banner.indicator.KuroIndicator
import kotlinx.android.synthetic.main.activity_kuro_banner_demo.*

class KuroBannerDemoActivity : AppCompatActivity() {
//    private var urls = arrayOf(
//        "https://www.devio.org/img/beauty_camera/beauty_camera1.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera3.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera4.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera5.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera2.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera6.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera7.jpg",
//        "https://www.devio.org/img/beauty_camera/beauty_camera8.jpeg"
//    )

    private var urls = arrayOf(
        "https://m2mled.net/file/download?id=5fe2d9249e7994f4057e3cd4",
        "https://m2mled.net/file/download?id=5fe2d9269e7994f4057e3d01"
    )

    private var autoPlay:Boolean =false
    private var isVisiblePlay:Boolean =false
    private var kuroIndicator:KuroIndicator<*>? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuro_banner_demo)
        initView(KuroCircleIndicator(this),true,true)

        auto_play.setOnCheckedChangeListener{_,isChecked ->
            autoPlay =isChecked
            initView(kuroIndicator,autoPlay,true)
        }

        tv_switch.setOnClickListener{
            if (kuroIndicator is KuroCircleIndicator){

            }else{
                initView(
                    KuroCircleIndicator(
                        this
                    ),autoPlay,true)
            }
        }
        indicator_switch.setOnCheckedChangeListener{_,isChecked ->
            isVisiblePlay =isChecked
            initView(kuroIndicator,autoPlay,isVisiblePlay)
        }
    }

    private fun initView(kuroIndicator: KuroIndicator<*>?,autoPlay:Boolean,isVisiblePlay:Boolean) {
        this.kuroIndicator =kuroIndicator
        kuroIndicator?.isVisible(isVisiblePlay)
        val mKuroBanner =findViewById<KuroBanner>(R.id.banner)
        val moList:MutableList<KuroBannerMo> =ArrayList()
        for (i in 0..1){
            val mo =BannerMo()
            mo.url =urls[i%urls.size]
            moList.add(mo)
        }
//        mKuroBanner.setScrollDuration(5000)
        mKuroBanner.setKuroIndicator(kuroIndicator)
        mKuroBanner.setAutoPlay(autoPlay)
        mKuroBanner.setIntervalTime(10000)
//        mKuroBanner.setBannerData(R.layout.banner_item_layout,moList)
        mKuroBanner.setBannerData(moList)
        mKuroBanner.setBindAdapter{viewHolder, mo, position ->
            val video = VideoView(this)
            video.setVideoPath(mo.url)
            viewHolder.addView(video)
            video.setOnPreparedListener { mediaPlayer ->
//                mediaPlayer.start()
                mediaPlayer.isLooping = true
                mediaPlayer.start()
//                    video.start()
            }
//            val imageView: ImageView = viewHolder.findViewById(R.id.iv_image)
//            Glide.with(this@KuroBannerDemoActivity).load(mo.url).into(image)
//            val titleView: TextView = viewHolder.findViewById(R.id.tv_title)
//            titleView.text = mo.url
        }
    }
}
