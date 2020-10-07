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
    private var count : Int =0
    private var path : File =Environment.getExternalStoragePublicDirectory("TremorApp")
    private var timer_flag : Boolean = false
    private var save_timer : Long = 0.toLong()
    private var saveTimer : Long = 0.toLong()
    private val pathTrace: MutableList<PathTraceData> = mutableListOf()
    private val timer = object : CountDownTimer(Long.MAX_VALUE, 1000 / 60) {
        override fun onTick(millisUntilFinished: Long) {
            if(timer_flag) pathTrace.add(PathTraceData(currentX, currentY, (Long.MAX_VALUE - millisUntilFinished).toInt()))
            else pathTrace.add(PathTraceData(currentX, currentY, (saveTimer+(Long.MAX_VALUE - millisUntilFinished)).toInt()))
            save_timer= Long.MAX_VALUE - millisUntilFinished
            Log.v("SpiralActivity", "ActivityyyySave"+currentX+" "+currentY+" "+(Long.MAX_VALUE - millisUntilFinished).toInt()+" "+saveTimer)
        }

        override fun onFinish() {}
    }
    private var clinicID : String=""
    private var patientName :String=""
    private var task :String = ""
    private var both : String = ""
    private var resut_image_path : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spiral)
        clinicID = intent.getStringExtra("clinicID")
        patientName = intent.getStringExtra("patientName")
        task = intent.getStringExtra("task")
        both = intent.getStringExtra("both")

        Log.v("SpiralActivity", "SpiralActivity"+task+both)
        Log.v("SpiralActivity", "SSSSSS"+filename)
        path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/$clinicID/$task$both")
        count = readCSV(path,clinicID+"_"+task+both+".csv")
        val layout = writingcanvasLayout
        val view = DrawView(this)
        val baseLine = baseView(this)
        layout.addView(view)
        layout.addView(baseLine)

        image_path = "$clinicID/$task/$both/$count.jpg"
        filename = task+"_"+both+"_"+count+"_RowData"

         //그림 그리고 나서, 다음으로 넘어가는 버튼
        writingfinish.setSafeOnClickListener {
            timer.cancel()
            var prevData: PathTraceData? = null
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
                    var fos = FileOutputStream(path)
                    captureView.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()
                    fos.flush()
                }catch (e : FileNotFoundException){
                    e.printStackTrace()
                }
                val baos = ByteArrayOutputStream()
                captureView.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                var uri = Uri.fromFile(path)
                val data = baos.toByteArray()
                /* ******************************** save image local *************************************/
                try{
                    onCap(captureView, path, count)
                } catch (e : java.lang.Exception){

                }finally {
                    captureView.recycle();
                }
                val metaData = "positionX,positionY,time"
                //val path = File("${this.filesDir.path}/testData") // raw save to file dir(data/com.bcilab....)
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
                val data_path = image_path.replace("Image", "Data").replace("jpg", "csv")
                val intent = Intent(this, AnalysisActivity::class.java)
                intent.putExtra("filename", "${clinicID}_$filename.csv")
                filename = SimpleDateFormat("yyyyMMdd_HH_mm").format(Calendar.getInstance().time)
                intent.putExtra("timestamp", filename)
                intent.putExtra("clinicID", clinicID)
                intent.putExtra("patientName", patientName)
                intent.putExtra("task", task)
                intent.putExtra("both", both)
                intent.putExtra("image_path", resut_image_path)
                intent.putExtra("data_path", data_path)
                startActivity(intent)
                Toast.makeText(this, "Wait...", Toast.LENGTH_LONG).show()
                loadingEnd()
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
            Log.v("SpiralActivity", "Activityyyy_Touch"+currentX+" "+currentY+"Spiral")
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!flag) {
                        flag=true
                        timer_flag = true
                        timer.start()
                    }
                    else timer.start()
                }

                MotionEvent.ACTION_UP -> {
                    if(flag) {
                        timer.cancel()
                        timer_flag=false
                        saveTimer +=save_timer
                    }
                }
            }
            return super.onTouchEvent(event)
        }

        override fun clearLayout() {
            Log.v("SpiraleActivity", "ClearLLLayout")
            super.clearLayout()
            pathTrace.clear()
            timer.cancel()
        }
    }
    inner class baseView(context: Context) : View(context) {
        private val startX = (this.resources.displayMetrics.widthPixels / 2.1).toInt()
        private val startY = (this.resources.displayMetrics.heightPixels / 2.0).toInt()

        private val theta = FloatArray(750) { (((it * (Math.PI / 180)) / 3) * 2).toFloat() }
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
            val baseData = StringBuilder()
            baseData.append("baseX,baseY")
            basePath.moveTo(startX.toFloat(), startY.toFloat())
            var baseX = startX.toFloat()
            var baseY = startY.toFloat()
            for (t in theta) {
                baseX = (t * Math.cos(2.5 * t) * 60 + startX).toFloat()
                baseY = (t * Math.sin(2.5 * t) * 60 + startY).toFloat()
                basePath.lineTo(baseX, baseY)
                baseData.append("\n$baseX,$baseY")

            }
            canvas.drawPath(basePath, basePaint)

            val baseCsv = File(path, clinicID+"_"+task+"_"+both+"_"+count+"_BaseData.csv")

            try {
                val write = FileWriter(baseCsv, false)
                val csv = PrintWriter(write)
                csv.println(baseData)
                csv.close()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

    }
    @Throws(Exception::class)
    private fun onCap(bm: Bitmap, path: File, count: Int) {
        try {
            val path = Environment.getExternalStoragePublicDirectory(
                    "/TremorApp/$clinicID/$task$both")
            var first = false
            val file_name = clinicID + "_" + task + both + ".csv"
            val foder = path.listFiles()
            for (name in foder) {
                if (name.getName() == file_name) {
                    first = true
                }
            }
            var imgFile = ""
            if (first==false) {
                imgFile = "/"+clinicID+"_"+task+"_"+both+"_1.jpg"
            }
            else{
                imgFile = "/"+clinicID+"_"+task+"_"+both+"_"+count+".jpg"
            }

            val imgPath = StringBuffer(path.toString())
            val saveFile = File(imgPath.toString())
            if (!saveFile.isDirectory) {
                saveFile.mkdirs()
            }
            imgPath.append(imgFile)
            resut_image_path = imgPath.toString()
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

    fun readCSV(path: File, file: String): Int {
        var line_length = 0
        var br: BufferedReader? = null
        val spiralCSV = File(path, file)

        try {
            br = BufferedReader(FileReader(spiralCSV))
            var line = ""
            while (br.readLine() != null) {
                line_length++
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                br?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        if (line_length==0) line_length=1
        return line_length
    }
}
