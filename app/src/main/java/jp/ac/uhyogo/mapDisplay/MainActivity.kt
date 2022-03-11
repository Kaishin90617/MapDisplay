package jp.ac.uhyogo.mapDisplay

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import java.io.File
import java.nio.file.Path

class MainActivity : AppCompatActivity() {

    companion object{
        var mapPath: String? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // mapパスの設定
        setMapPath()

        if (mapPath.isNullOrEmpty()) {
            setContentView(R.layout.activity_no_map)    // mapがないとき
        } else {
            setContentView(R.layout.activity_main)

            val linearLayout: LinearLayout = findViewById(R.id.linear)
            val mapView = MapView(this)
            linearLayout.addView(mapView)
        }
    }

    /*
    * mapパスの読み込み
    * */
    private fun setMapPath() {
        val mapDir  = getExternalFilesDir("map").toString()
        val mapList = File(mapDir).list()

        if (mapList!!.isNotEmpty()){
            mapPath = "$mapDir/${mapList[0]}"
        }
    }
}