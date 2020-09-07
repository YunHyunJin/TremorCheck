package com.bcilab.tremorapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import com.bcilab.tremorapp.Data.PathTraceData
import com.bcilab.tremorapp.Function.SafeClickListener
import com.bcilab.tremorapp.Function.fitting
import com.bcilab.tremorapp.functions.Drawable
import kotlinx.android.synthetic.main.activity_spiral.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class SpiralActivity : AppCompatActivity() {
    private var filename: String = ""
    private lateinit var progressDialog : ProgressDialog
    private var currentX: Float = 0.toFloat()
    private var currentY: Float = 0.toFloat()
    private var isdraw : Boolean = false
    private var image_path : String = ""
    private val pathTrace: MutableList<PathTraceData> = mutableListOf()
    private val timer = object : CountDownTimer(Long.MAX_VALUE, 1000 / 60) {
        override fun onTick(millisUntilFinished: Long) {
            pathTrace.add(PathTraceData(currentX, currentY, (Long.MAX_VALUE - millisUntilFinished).toInt()))
        }

        override fun onFinish() {}
    }
    private var clinicID : String=""
    private var patientName :String=""
    private var task :String = ""
    private var both : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spiral)
        clinicID = intent.getStringExtra("clinicID")
        patientName = intent.getStringExtra("patientName")
        task = intent.getStringExtra("task")
        both = intent.getStringExtra("both")

        filename = SimpleDateFormat("yyyyMMdd_HH_mm").format(Calendar.getInstance().time)

        val layout = writingcanvasLayout
        val view = DrawView(this)
        val baseLine = baseView(this)
        layout.addView(view)
        layout.addView(baseLine)

        image_path = "Sprial/Right/Image/1.jpg"

        // 그림 그리고 나서, 다음으로 넘어가는 버튼
        writingfinish.setSafeOnClickListener {
            timer.cancel()
            var prevData: PathTraceData? = null
            val metaData = "$clinicID,$filename"
            val path = File("${this.filesDir.path}/testData") // raw save to file dir(data/com.bcilab....)
            if (!path.exists()) path.mkdirs()
            val file = File(path, "${clinicID}_$filename.csv")
            try {
                PrintWriter(file).use { out ->
                    out.println(metaData)
                    for (item in pathTrace)
                        out.println(item.joinToString(","))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error on writing file", Toast.LENGTH_LONG).show()
                println(e.message)
            }
            if(!isdraw)
            {
                Toast.makeText(this, "나선을 그리고 다음버튼을 눌러주세요", Toast.LENGTH_LONG).show()
            }
            else
            {
                loading()
                Log.v("분석중입니다.","분석중입니다.")
                var v1 = window.decorView
                v1.isDrawingCacheEnabled = true
                v1.buildDrawingCache()
                var captureView = v1.drawingCache
                try {
                    var fos = FileOutputStream("sdcard/Download/")
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    fos.flush()
                }catch (e : FileNotFoundException){
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                var uri = Uri.fromFile(File("sdcard/Download/"))
                val data = baos.toByteArray()
                /* ******************************** save image local *************************************/
                try{
                    onCap(captureView)
                } catch (e : java.lang.Exception){

                }finally {
                    captureView.recycle();
                }
                val data_path = image_path.replace("Image", "Data").replace("jpg", "csv")
                val intent = Intent(this, AnalysisActivity::class.java)
                intent.putExtra("filename", "${clinicID}_$filename.csv")
                intent.putExtra("clinicID", clinicID)
                intent.putExtra("patientName", patientName)
                intent.putExtra("task", task)
                intent.putExtra("both", both)
                intent.putExtra("data_path", data_path)
                startActivity(intent)
                Toast.makeText(this, "Wait...", Toast.LENGTH_LONG).show()
                //loadingEnd()
                finish()

                }
            /* ******************************** processing image file *************************************/

            if (pathTrace.size > 2) {
                prevData = pathTrace[pathTrace.size - 1]
                for (i in (pathTrace.size - 2) downTo 0) {
                    if (prevData.isSamePosition(pathTrace[i]))
                        pathTrace.removeAt(i)
                    else
                        break
                }
            }

        }

    }
    inner class DrawView(context: Context) : Drawable(context) {
        private var flag = false

        override fun onTouchEvent(event: MotionEvent): Boolean {
            currentX = event.x
            currentY = event.y
            isdraw = true
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!flag) {
                        flag = true
                        timer.start()
                    }
                }
            }
            return super.onTouchEvent(event)
        }

        override fun clearLayout() {
            super.clearLayout()
            pathTrace.clear()
            timer.cancel()
        }
    }

    inner class baseView(context: Context) : View(context) {
        private val startX = this.resources.displayMetrics.widthPixels / 2
        private val startY = this.resources.displayMetrics.heightPixels / 2

        private val theta = FloatArray(720) { (((it * (Math.PI / 180)) / 3) * 2).toFloat() }
        private val basePath = Path()
        private val basePaint = Paint()

        init {
            basePaint.style = Paint.Style.STROKE
            basePaint.strokeWidth = 2f
            basePaint.alpha = 50
            basePaint.isAntiAlias = true
            fitting.startX = startX
            fitting.startY = startY
        }

        override fun onDraw(canvas: Canvas) {
            basePath.moveTo(startX.toFloat(), startY.toFloat())
            for (t in theta)
                basePath.lineTo((t * Math.cos(2.5 * t) * 50 + startX).toFloat(), (t * Math.sin(2.5 * t) * 50 + startY).toFloat())

            canvas.drawPath(basePath, basePaint)
        }

    }
    @Throws(Exception::class)
    private fun onCap(bm: Bitmap) {
        try {
            val imgFile = "save.jpg"
            val imgPath = StringBuffer("sdcard/Download/")
            val saveFile = File(imgPath.toString())
            if (!saveFile.isDirectory) {
                saveFile.mkdirs()
            }
            imgPath.append(imgFile)
            var out = FileOutputStream(imgPath.toString())
            bm.compress(Bitmap.CompressFormat.JPEG, 100, out)
            sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
        } catch (e: Exception) {

        } finally {
            if (System.out != null)
                System.out.close()
            // saveFile = null
        }
    }
    fun loading() {
        //로딩
        android.os.Handler().postDelayed(
                {
                    progressDialog = ProgressDialog(this@SpiralActivity)
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    progressDialog.setIndeterminate(true)
                    progressDialog.setMessage("분석 중입니다.")
                    progressDialog.show()
                }, 0)
    }
    fun loadingEnd() {
        android.os.Handler().postDelayed(
                { progressDialog.dismiss() }, 0)
    }
    fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
        val safeClickListener = SafeClickListener {
            onSafeClick(it)
        }
        setOnClickListener(safeClickListener)
    }
}
