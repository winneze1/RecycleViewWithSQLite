package com.example.mycrud_20022020

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import com.example.mycrud_20022020.Common.Constants
import com.example.mycrud_20022020.MyDBHelper.MyDBHelper
import kotlinx.android.synthetic.main.activity_record_detail.*
import kotlinx.android.synthetic.main.activity_record_detail.dobTv
import kotlinx.android.synthetic.main.activity_record_detail.emailTv
import kotlinx.android.synthetic.main.activity_record_detail.nameTv
import kotlinx.android.synthetic.main.activity_record_detail.phoneTv
import kotlinx.android.synthetic.main.row_record.profileIv
import java.util.*

class RecordDetailActivity : AppCompatActivity() {

    //actionbar
    private var actionBar:ActionBar?=null

    //db helper
    private var dbHelper: MyDBHelper?=null

    private var recordId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)

        //setting up actionbar
        actionBar = supportActionBar
        actionBar!!.title = "Record Details"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        //init db helper
        dbHelper = MyDBHelper(this)

        //get record id from intent
        val intent = intent
        recordId = intent.getStringExtra("RECORD_ID")

        showRecordDetails()
    }

    private fun showRecordDetails() {
        //get records

        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_ID} = \"$recordId\""

        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()){
            do {
                val id = ""+cursor.getString(cursor.getColumnIndex(Constants.C_ID))
                val name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val image = ""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val bio = ""+cursor.getString(cursor.getColumnIndex(Constants.C_BIO))
                val phone =""+cursor.getString(cursor.getColumnIndex(Constants.C_PHONE))
                val email=""+cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL))
                val dob =""+cursor.getString(cursor.getColumnIndex(Constants.C_DOB))
                val addedTimestamp =""+cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP))
                val updatedTimestamp = ""+cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                //convert timestamp
                val calendar1 = Calendar.getInstance(Locale.getDefault())
                calendar1.timeInMillis = addedTimestamp.toLong()
                val timeAdded = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar1)

                val calendar2 = Calendar.getInstance(Locale.getDefault())
                calendar2.timeInMillis = updatedTimestamp.toLong()
                val timeUpdate = android.text.format.DateFormat.format("dd/MM/yyyy hh:mm:aa",calendar2)

                //set data

                nameTv.text = name
                bioTv.text = bio
                phoneTv.text = phone
                emailTv.text = email
                dobTv.text = dob
                addedDateTv.text= timeAdded
                updatedDateTv.text = timeUpdate

                if (image == "null"){
                    //no image
                    //ROW
                    profileIv.setImageResource(R.drawable.ic_person_black)
                }else{
                    //have image
                    profileIv.setImageURI(Uri.parse(image))
                }
            }while (cursor.moveToNext())
        }
        //
        db.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
