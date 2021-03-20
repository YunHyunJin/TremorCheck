package com.bcilab.tremorapp

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import com.bcilab.tremorapp.Function.main
import com.bcilab.tremorapp.Function.main1

class AnalysisActivity : AppCompatActivity() {

    private val filename: String by lazy { intent.extras.getString("filename") }
    var clinicID : String = ""
    var patientName : String = ""
    var task: String = ""
    var both : String = ""
    var data_path : String = ""
    var firstdate : Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        val intent = intent
        var result: DoubleArray = doubleArrayOf()
        clinicID = intent.getStringExtra("clinicID") ;
        patientName = intent.getStringExtra("patientName")
        task = intent.getStringExtra("task")
        both = intent.getStringExtra("both")
        data_path = intent.getStringExtra("data_path")
        firstdate = intent.getBooleanExtra("firstdate",true)

        val path = Environment.getExternalStoragePublicDirectory(
                "/TremorApp/$clinicID/$task$both")
        val dialog = ProgressDialog(this)
        dialog.setMessage("Analysing...")
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", DialogInterface.OnClickListener { dialog, which -> run {
            dialog.dismiss()
            val cancel_Intent = Intent(this, PatientListActivity::class.java)
            cancel_Intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(cancel_Intent)
        }})
        dialog.show()
        if(task.equals("Spiral")) result = main.main("$path/$filename", applicationContext, clinicID, data_path, task, both)
        else result = main1.main1("$path/$filename", applicationContext, clinicID, data_path, task, both)
        dialog.dismiss()
        dialog.dismiss()
        val intent1 = Intent(this, ResultActivity::class.java)
        intent1.putExtra("spiral_result", result)
        intent1.putExtra("clinicID", clinicID)
        intent1.putExtra("firstdate",firstdate)
        intent1.putExtra("patientName", patientName)
        intent1.putExtra("task", task)
        intent1.putExtra("both", both)
        intent1.putExtra("timestamp", intent.getStringExtra("timestamp"))
        intent1.putExtra("image_path", intent.getStringExtra("image_path"))
        startActivity(intent1)
        finish()

    }
}
