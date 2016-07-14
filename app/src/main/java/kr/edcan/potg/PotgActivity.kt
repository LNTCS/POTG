package kr.edcan.potg

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_potg.*
import java.io.File
import java.io.FileOutputStream

class PotgActivity : AppCompatActivity() {
    val PICK_IMAGE = 1230
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_potg)
        title = "POTG"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow);
        applyFilter(tvName,BlurMaskFilter.Blur.INNER)
        applyFilter(tvNameBG,BlurMaskFilter.Blur.INNER)
        applyFilter(tvTitle,BlurMaskFilter.Blur.SOLID)
        applyFilter(tvSub,BlurMaskFilter.Blur.SOLID)
        edTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvTitle.text = s.toString()
                if(s.toString().length == 0) {
                    tvTitle.text = "최고의 플레이"
                }
            }
        })
        edName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvName.text = s.toString()
                if(s.toString().length == 0) {
                    tvName.text = "도지"
                }
                tvNameBG.text = s.toString()
                if(s.toString().length == 0) {
                    tvNameBG.text = "도지"
                }
            }
        })
        edSub.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvSub.text = s.toString()
                if(s.toString().length == 0) {
                    tvSub.text = "댕댕이"
                }
            }
        })
        btnSelect.setOnClickListener {
            var intent = Intent();
            intent.type = "image/*";
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        btnSave.setOnClickListener {
            mainLay.isDrawingCacheEnabled = true
            val bitmap = mainLay.drawingCache
            val folder = File("${android.os.Environment.getExternalStorageDirectory()}/DCIM/고오급 짤")
            var file : File? = null
            try {
                if (!folder.exists()){
                    folder.mkdirs()
                }
                file = File(folder, "${tvName.text}_${tvTitle.text}${System.currentTimeMillis() % 1000}.png")
                while(file!!.exists()){
                    Log.e("asdf", "asdfsad")
                    file = File(folder, "${tvName.text}_${tvTitle.text}${System.currentTimeMillis() % 1000}.png")
                }
                if (!file.exists()) {
                    file.createNewFile()
                }
                val outstream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outstream)
                outstream.close()
                sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.absolutePath)))
                Toast.makeText(this, "저장 완료\n"+file.absolutePath, Toast.LENGTH_SHORT).show()
                intent = Intent();
                intent.action = Intent.ACTION_VIEW;
                intent.setDataAndType(Uri.parse("file://${file.absolutePath}"), "image/*");
                startActivity(intent);
                mainLay.invalidate()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mainLay.isDrawingCacheEnabled = false
            }
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (requestCode === PICK_IMAGE && resultCode === Activity.RESULT_OK) {
            if (result == null) {
                return
            }
            CropImage.activity(result.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(16,9)
                    .setFixAspectRatio(true)
                    .start(this);
        }
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(result)
            if (resultCode === Activity.RESULT_OK) {
                val resultUri = result.uri
                Glide.with(applicationContext).load(resultUri)
                        .signature(StringSignature((System.currentTimeMillis() / (24 * 60 * 60 * 1000)).toString()))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                        .into(mainImg)
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun applyFilter(tv: TextView, style: BlurMaskFilter.Blur){
        tv.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        val radius = tv.textSize / 15
        val filter = BlurMaskFilter(radius, style)
        tv.paint.maskFilter = filter
    }
}
