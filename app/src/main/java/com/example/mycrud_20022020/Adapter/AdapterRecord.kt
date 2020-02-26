package com.example.mycrud_20022020.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.mycrud_20022020.*
import com.example.mycrud_20022020.Model.ModelRecord
import com.example.mycrud_20022020.MyDBHelper.MyDBHelper

class AdapterRecord() : RecyclerView.Adapter<AdapterRecord.HolderRecord>() {

    private var context : Context? = null
    private var recordList : ArrayList<ModelRecord>? = null

    lateinit var dbHelper: MyDBHelper



    constructor(context: Context?, recordList: ArrayList<ModelRecord>?) : this() {
        this.context = context
        this.recordList = recordList

        dbHelper = MyDBHelper(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.row_record, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return recordList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        //get data
        val model = recordList!![position]

        val id = model.id
        val name = model.name
        val image = model.image
        val bio = model.bio
        val phone = model.phone
        val email = model.email
        val dob = model.dob
        var addedTime = model.addedTime
        var updatedTime = model.updatedTime

        //set data
        holder.nameTv.text = name
        holder.phoneTv.text = phone
        holder.emailTv.text = email
        holder.dobTv.text = dob

        if (image == "null"){
            //no image
            holder.profileIv.setImageResource(R.drawable.ic_person_black)
        }else{
            //have image
            holder.profileIv.setImageURI(Uri.parse(image))
        }

        //show record
            holder.itemView.setOnClickListener {
                //pass id to next activity
                val intent = Intent(context, RecordDetailActivity::class.java)
                intent.putExtra("RECORD_ID", id)
                context!!.startActivity(intent)
            }

        //handle more button click
        holder.moreBtn.setOnClickListener {
            //show more options
            showMoreOptions(
                position,
                id,
                name,
                phone,
                email,
                dob,
                bio,
                image,
                addedTime,
                updatedTime
            )
        }
    }

    private fun showMoreOptions(
        position: Int,
        id: String,
        name: String,
        phone: String,
        email: String,
        dob: String,
        bio: String,
        image: String,
        addedTime: String,
        updatedTime: String
    ) {
        //options to display
        val options = arrayOf("Edit", "Delete")
        //dialog
        val dialog:AlertDialog.Builder = AlertDialog.Builder(context!!)
        //set item and click listener
        dialog.setItems(options) { dialog, which ->
            if (which == 0) {
                //edit
                val intent = Intent(context, AddUpdateRecordActivity::class.java)
                intent.putExtra("ID", id)
                intent.putExtra("NAME", name)
                intent.putExtra("PHONE", phone)
                intent.putExtra("EMAIL", email)
                intent.putExtra("DOB", dob)
                intent.putExtra("BIO", bio)
                intent.putExtra("IMAGE", image)
                intent.putExtra("ADDED_TIME",addedTime)
                intent.putExtra("UPDATED_TIME",updatedTime)
                intent.putExtra("isEditMode", true)
                context!!.startActivity(intent)

            } else{
                //delete
                dbHelper.deleteRecords(id)
                //refresh record by call activity onResume
                (context as MainActivity)!!.onResume()
            }
        }
        dialog.show()
    }

    inner class HolderRecord(itemView: View): RecyclerView.ViewHolder(itemView) {

        //view from layout row_record
        var profileIv:ImageView = itemView.findViewById(R.id.profileIv)
        var nameTv: TextView = itemView.findViewById(R.id.nameTv)
        var phoneTv:TextView = itemView.findViewById(R.id.phoneTv)
        var emailTv:TextView = itemView.findViewById(R.id.emailTv)
        var dobTv:TextView = itemView.findViewById(R.id.dobTv)
        var moreBtn:ImageButton = itemView.findViewById(R.id.moreBtn)


    }


}