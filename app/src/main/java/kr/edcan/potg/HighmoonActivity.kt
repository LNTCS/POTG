package kr.edcan.potg

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.StringSignature
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_highmoon.*
import kr.edcan.potg.view.StickerView
import java.io.File
import java.io.FileOutputStream
import java.util.*

class HighmoonActivity : AppCompatActivity() {
    val PICK_IMAGE = 1234

    private val mViews = ArrayList<View>()
    private var mCurrentView : StickerView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highmoon)
        title = resources.getString(R.string.act_highmoon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_arrow);
        Glide.with(applicationContext).load(R.drawable.highnoon)
                .signature(StringSignature((System.currentTimeMillis() / (24 * 60 * 60 * 1000)).toString()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mcHand)
        btnSelect.setOnClickListener {
            var intent = Intent();
            intent.type = "image/*";
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        }
        btnSave.setOnClickListener {
            if(mCurrentView != null)
                mCurrentView!!.setInEdit(false);
            mainLay.isDrawingCacheEnabled = true
            val bitmap = mainLay.drawingCache
            val folder = File("${android.os.Environment.getExternalStorageDirectory()}/DCIM/"+resources.getString(R.string.folder))
            var file : File? = null
            try {
                if (!folder.exists()){
                    folder.mkdirs()
                }
                file = File(folder, resources.getString(R.string.act_highmoon) + "${System.currentTimeMillis() % 1000}.png")
                while(file!!.exists()){
                    file = File(folder, resources.getString(R.string.act_highmoon) + "${System.currentTimeMillis() % 1000}.png")
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
        stickerDead.setOnClickListener {
            addStickerView(R.drawable.dead);
        }
        stickerNormal.setOnClickListener {
            addStickerView(R.drawable.normal);
        }
    }

    private fun addStickerView(res: Int) {
        val stickerView = StickerView(this)
        stickerView.setImageResource(res)
        stickerView.setOperationListener(object : StickerView.OperationListener {
            override fun onDeleteClick() {
                mViews.remove(stickerView)
                stickerRoot.removeView(stickerView)
            }

            override fun onEdit(stickerView: StickerView) {
                mCurrentView!!.setInEdit(false)
                mCurrentView = stickerView
                mCurrentView!!.setInEdit(true)
            }

            override fun onTop(stickerView: StickerView) {
                val position = mViews.indexOf(stickerView)
                if (position == mViews.size - 1) {
                    return
                }
                val stickerTemp = mViews.removeAt(position) as StickerView
                mViews.add(mViews.size, stickerTemp)
            }
        })
        val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
        stickerRoot.addView(stickerView, lp)
        mViews.add(stickerView)
        setCurrentEdit(stickerView)
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
    private fun setCurrentEdit(stickerView: StickerView) {
        if (mCurrentView != null) {
            mCurrentView!!.setInEdit(false)
        }
        mCurrentView = stickerView
        stickerView.setInEdit(true)
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
}
