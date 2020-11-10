package com.ailoveyou.trashrecognition.views

import android.os.Bundle
import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.ailoveyou.trashrecognition.R
import com.ailoveyou.trashrecognition.tflite.Classifier
import kotlinx.android.synthetic.main.activity_image_classifier.*
import java.io.IOException

class ImageClassifierActivity : AppCompatActivity() {
    private lateinit var mClassifier: Classifier
    private lateinit var mBitmap: Bitmap

    private val mGalleryRequestCode = 2

    private val mInputSize = 224
    private val mModelPath = "model_unquant.tflite"
    private val mLabelPath = "labels.txt"
    private val mSamplePath = "soybean.jpg"


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_image_classifier)
        mClassifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)

        resources.assets.open(mSamplePath).use {
            mBitmap = BitmapFactory.decodeStream(it)
            mBitmap = Bitmap.createScaledBitmap(mBitmap, mInputSize, mInputSize, true)
            iv_1.setImageBitmap(mBitmap)
        }


        img_pick_btn4.setOnClickListener {
            val callGalleryIntent = Intent(Intent.ACTION_PICK)
            callGalleryIntent.type = "image/*"
            startActivityForResult(callGalleryIntent, mGalleryRequestCode)
        }
        img_pick_btn2.setOnClickListener {
            val results = mClassifier.recognizeImage(mBitmap).firstOrNull()
            textView.text = results?.title + "\n Confidence: " +
                    String.format("%.2f", (results?.confidence?.times(100))) + "%"

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == mGalleryRequestCode) {
                if (data != null) {
                    val uri = data.data

                    try {
                        mBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    println("Success!!!")
                    mBitmap = scaleImage(mBitmap)
                    iv_1.setImageBitmap(mBitmap)

                }
            } else {
                Toast.makeText(this, "Unrecognized request code", Toast.LENGTH_LONG).show()

            }
        }
    }

        private fun scaleImage(bitmap: Bitmap?): Bitmap {
            val originalWidth = bitmap!!.width
            val originalHeight = bitmap.height
            val scaleWidth = mInputSize.toFloat() / originalWidth
            val scaleHeight = mInputSize.toFloat() / originalHeight
            val matrix = Matrix()
            matrix.postScale(scaleWidth, scaleHeight)
            return Bitmap.createBitmap(bitmap, 0, 0, originalWidth, originalHeight, matrix, true)
        }

    }

