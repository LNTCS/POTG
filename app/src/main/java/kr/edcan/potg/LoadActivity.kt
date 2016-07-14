package kr.edcan.potg

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_load.*
import java.io.File
import java.io.FileOutputStream

class LoadActivity : AppCompatActivity() {
    val PICK_IMAGE = 1235

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)
        title = "로딩화면"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow);
        val gradient = intArrayOf(
                Color.parseColor("#e08BCEE5"),
                Color.parseColor("#aa8BCEE5"),
                Color.parseColor("#888BCEE5"),
                Color.parseColor("#338BCEE5"))
        val shader = LinearGradient(0f, 0f, 0f, tvAction.textSize,
                gradient,
                floatArrayOf(0f, 0.2f, 0.5f, 1f), Shader.TileMode.CLAMP)
        tvAction.paint.shader = shader

        edAction.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvAction.text = s.toString()
//                tvTitle_s.text = s.toString()
            }
        })
        edLocate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvLocate.text = s.toString()
//                tvName_s.text = s.toString()
            }
        })
        edTip.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvTip.text = s.toString()
//                tvSub_s.text = s.toString()
                if(s.toString().length == 0) {
                    layTip.visibility = View.GONE
                }else{
                    layTip.visibility = View.VISIBLE
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
                file = File(folder, "${tvAction.text}_${tvLocate.text}${System.currentTimeMillis() % 1000}.png")
                while(file!!.exists()){
                    Log.e("asdf", "asdfsad")
                    file = File(folder, "${tvAction.text}_${tvLocate.text}${System.currentTimeMillis() % 1000}.png")
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
}
