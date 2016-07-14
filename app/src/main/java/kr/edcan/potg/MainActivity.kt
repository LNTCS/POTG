package kr.edcan.potg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_WRITE = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "고오급 짤 제조기"
        applyFilter(tvName, BlurMaskFilter.Blur.INNER)
        applyFilter(tvNameBG, BlurMaskFilter.Blur.INNER)
        applyFilter(tvTitle, BlurMaskFilter.Blur.SOLID)
        applyFilter(tvSub, BlurMaskFilter.Blur.SOLID)
        permission()
    }

    fun permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, "합성한 짤을 저장하기 위해 쓰기 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE)
        } else {
            mainPotg.setOnClickListener { startActivity(Intent(applicationContext, PotgActivity::class.java)) }
            mainHighmoon.setOnClickListener { startActivity(Intent(applicationContext, HighmoonActivity::class.java)) }
            mainLoad.setOnClickListener { startActivity(Intent(applicationContext, LoadActivity::class.java)) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_WRITE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mainPotg.setOnClickListener { startActivity(Intent(applicationContext, PotgActivity::class.java)) }
                mainHighmoon.setOnClickListener { startActivity(Intent(applicationContext, HighmoonActivity::class.java)) }
                mainLoad.setOnClickListener { startActivity(Intent(applicationContext, LoadActivity::class.java)) }
            } else {
                Toast.makeText(applicationContext, "앱 쓰기 싫음 말던가", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun applyFilter(tv: TextView, style: BlurMaskFilter.Blur){
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val radius = tv.textSize / 15
        val filter = BlurMaskFilter(radius, style)
        tv.paint.maskFilter = filter
    }
}
