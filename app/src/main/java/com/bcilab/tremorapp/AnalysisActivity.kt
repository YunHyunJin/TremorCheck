package com.bcilab.tremorapp

import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.bcilab.tremorapp.Function.main

class AnalysisActivity : AppCompatActivity() {

    private val filename: String by lazy { intent.extras.getString("filename") }
    var clinicID : String = ""
    var patientName : String = ""
    var task: String = ""
    var both : String = ""  ;
    var data_path : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analysis)

        val intent = intent
        var spiral_result: DoubleArray = doubleArrayOf()
        clinicID = intent.getStringExtra("clinicID") ;
        patientName = intent.getStringExtra("patientName")
        task = intent.getStringExtra("task")
        both = intent.getStringExtra("both")
        data_path = intent.getStringExtra("data_path")

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

        spiral_result = main.main("${this.filesDir.path}/testData/$filename", applicationContext, clinicID, data_path)
        dialog.dismiss()
        val intent1 = Intent(this, ResultActivity::class.java)
        intent1.putExtra("spiral_result", spiral_result)
        intent1.putExtra("clinicID", clinicID)
        intent1.putExtra("patientName", patientName)
        startActivity(intent1)
        finish()

    }
}