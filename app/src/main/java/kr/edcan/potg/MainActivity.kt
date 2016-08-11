package kr.edcan.potg

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BlurMaskFilter
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.Spanned
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val REQUEST_CODE_WRITE = 2;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = resources.getString(R.string.app_name)
        applyFilter(tvName, BlurMaskFilter.Blur.INNER)
        applyFilter(tvNameBG, BlurMaskFilter.Blur.INNER)
        applyFilter(tvTitle, BlurMaskFilter.Blur.SOLID)
        applyFilter(tvSub, BlurMaskFilter.Blur.SOLID)
        killTxt1.post{
            val height = killTxt1.height
            val params = LinearLayout.LayoutParams(height, height)
            killImg1.layoutParams = params
        }
        killTxt1.text = makeHtml(resources.getString(R.string.in_name), "100")
        Glide.with(applicationContext).load(R.drawable.highnoon)
                .signature(StringSignature((System.currentTimeMillis() / (24 * 60 * 60 * 1000)).toString()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mcHand)
        Glide.with(applicationContext).load(R.drawable.tactical_visor)
                .signature(StringSignature((System.currentTimeMillis() / (24 * 60 * 60 * 1000)).toString()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(visor)
        permission()
    }

    fun permission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !== PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(applicationContext, resources.getString(R.string.permission), Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(this,
                    arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE)
        } else {
            mainPotg.setOnClickListener { startActivity(Intent(applicationContext, PotgActivity::class.java)) }
            mainHighmoon.setOnClickListener { startActivity(Intent(applicationContext, HighmoonActivity::class.java)) }
            mainLoad.setOnClickListener { startActivity(Intent(applicationContext, LoadActivity::class.java)) }
            mainKill.setOnClickListener { startActivity(Intent(applicationContext, KillActivity::class.java)) }
            mainTTVisor.setOnClickListener { startActivity(Intent(applicationContext, TTVisorActivity::class.java)) }
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
                mainKill.setOnClickListener { startActivity(Intent(applicationContext, KillActivity::class.java)) }
                mainTTVisor.setOnClickListener { startActivity(Intent(applicationContext, TTVisorActivity::class.java)) }
            } else {
                Toast.makeText(applicationContext, "we cant access to sdcard", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun applyFilter(tv: TextView, style: BlurMaskFilter.Blur){
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val radius = tv.textSize / 15
        val filter = BlurMaskFilter(radius, style)
        tv.paint.maskFilter = filter
    }

    private fun makeHtml(name: String, score: String) : Spanned {
        if(resources.getString(R.string.language).equals("en")){
            return Html.fromHtml("<i><font color=#ffffff>ELIMINATED </font><font color=#ff0000>$name</font><font color=#ffffff> $score </font></i>")
        }else {
            return Html.fromHtml("<i><font color=#ff0000>$name</font> <font color=#ffffff> 처치 </font><font color=#ff0000>[+$score] </font></i>")
        }
    }
}
