package kr.edcan.potg

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.support.percent.PercentRelativeLayout
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.Html
import android.text.Spanned
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_kill.*
import java.io.File
import java.io.FileOutputStream
import java.util.*

class KillActivity : AppCompatActivity() {
    val PICK_IMAGE = 1239
    var tvList : ArrayList<TextView> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kill)
        title = resources.getString(R.string.act_kill)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow)
        killTxt1.text = makeHtml(resources.getString(R.string.in_name), "100")
        setImageSize()
        addScoreWatcher(edScore1, edName1, killTxt1, killLay1)
        addScoreWatcher(edScore2, edName2, killTxt2, killLay2)
        addScoreWatcher(edScore3, edName3, killTxt3, killLay3)
        addScoreWatcher(edScore4, edName4, killTxt4, killLay4)
        addScoreWatcher(edScore5, edName5, killTxt5, killLay5)
        addScoreWatcher(edScore6, edName6, killTxt6, killLay6)
        btnSelect.setOnClickListener {
            var intent = Intent();
            intent.type = "image/*";
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        btnSave.setOnClickListener {
            mainLay.isDrawingCacheEnabled = true
            val bitmap = mainLay.drawingCache
            val folder = File("${Environment.getExternalStorageDirectory()}/DCIM/" + resources.getString(R.string.folder))
            var file : File? = null
            try {
                if (!folder.exists()){
                    folder.mkdirs()
                }
                file = File(folder, "${resources.getString(R.string.act_kill)}${System.currentTimeMillis() % 1000}.png")
                while(file!!.exists()){
                    Log.e("asdf", "asdfsad")
                    file = File(folder, "${resources.getString(R.string.act_kill)}${System.currentTimeMillis() % 1000}.png")
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

    private fun addScoreWatcher(edScore: EditText, edName: EditText, killTxt: TextView, killLay: LinearLayout) {
        edScore.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                killTxt.text = makeHtml(edName.text.toString(), edScore.text.toString())
            }
        })
        edName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(s.toString().length == 0){
                    killLay.visibility = View.GONE
                }else {
                    killLay.visibility = View.VISIBLE
                    killTxt.text = makeHtml(edName.text.toString(), edScore.text.toString())
                }
                reloadMargin()
            }

        })

    }

    private fun makeHtml(name: String, score: String) : Spanned{
        if(resources.getString(R.string.language).equals("en")){
            return Html.fromHtml("<i><font color=#ffffff>ELIMINATED </font><font color=#ff0000>$name</font><font color=#ffffff> $score </font></i>")
        }else {
            return Html.fromHtml("<i><font color=#ff0000>$name</font> <font color=#ffffff> 처치 </font><font color=#ff0000>[+$score] </font></i>")
        }
    }

    private fun setImageSize() {
        killTxt1.post{
            val height = killTxt1.height
            val params = LinearLayout.LayoutParams(height, height)
            killImg1.layoutParams = params
            killImg2.layoutParams = params
            killImg3.layoutParams = params
            killImg4.layoutParams = params
            killImg5.layoutParams = params
            killImg6.layoutParams = params
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
    private fun reloadMargin() {
        var cnt = 0
        cnt += computeVisible(killLay1)
        cnt += computeVisible(killLay2)
        cnt += computeVisible(killLay3)
        cnt += computeVisible(killLay4)
        cnt += computeVisible(killLay5)
        cnt += computeVisible(killLay6)
        val info = (killViewLay.layoutParams as PercentRelativeLayout.LayoutParams).percentLayoutInfo
        info.topMarginPercent = 0.72f - (cnt*0.02f)
        killViewLay.requestLayout()
    }

    private fun computeVisible(lay: LinearLayout): Int {
        return if(lay.visibility == View.GONE){
            0
        }else{
            1
        }
    }
}