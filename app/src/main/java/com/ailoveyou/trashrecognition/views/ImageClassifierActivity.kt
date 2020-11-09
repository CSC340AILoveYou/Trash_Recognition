package com.ailoveyou.trashrecognition.views


import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ailoveyou.trashrecognition.R
import com.ailoveyou.trashrecognition.tflite.Classifier
import java.text.NumberFormat;


class ImageClassifierActivity : AppCompatActivity(), View.OnClickListener {

    private val mInputSize = 224
    private val mModelPath = "model_unquant.tflite"
    private val mLabelPath = "labels.txt"
    private lateinit var classifier: Classifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_classifier)
        initClassifier()
        initViews()
    }

    private fun initClassifier() {
        classifier = Classifier(assets, mModelPath, mLabelPath, mInputSize)
    }

    private fun initViews() {
        findViewById<ImageView>(R.id.iv_1).setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        val defaultFormat = NumberFormat.getPercentInstance()
        defaultFormat.minimumFractionDigits = 1;
        val bitmap = ((view as ImageView).drawable as BitmapDrawable).bitmap
        val result = classifier.recognizeImage(bitmap)
        runOnUiThread { Toast.makeText(
            this,
            result?.get(0)?.title +" "
                    + String.format("%.2f",(result?.get(0)?.confidence?.times(100)))+"%",
            Toast.LENGTH_SHORT
        ).show()


        }
    }
}