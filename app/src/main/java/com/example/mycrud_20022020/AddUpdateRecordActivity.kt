package com.example.mycrud_20022020

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mycrud_20022020.MyDBHelper.MyDBHelper
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_update_record.*

class AddUpdateRecordActivity : AppCompatActivity() {

    //
    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 100
    //IMAGE PICK
    private val IMAGE_PICK_CAMERA_CODE = 102
    private val IMAGE_PICK_GALLERY_CODE = 103
    //
    private lateinit var cameraPermission : Array<String> // camera and storage
    private lateinit var storagePermission : Array<String> // storage
    //variables that will contain data to save in db
    private var imageUri : Uri? = null
    private var id:String? =""
    private var name:String? =""
    private var phone:String? =""
    private var email:String? =""
    private var dob:String? =""
    private var bio:String? =""
    private var addedTime:String? =""
    private var updatedTime:String? =""

    private var isEditMode = false

    //action bar
    private var actionBar : ActionBar? = null

    lateinit var dbHelper : MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_record)
        //init action bar
        actionBar = supportActionBar
        //title of action bar
        actionBar!!.title = "Add Record"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //get data from intent
        val intent = intent
        isEditMode = intent.getBooleanExtra("isEditMode", false)
        if (isEditMode){
            //editing data, came here from adapter
            actionBar!!.title = "Update Record"

            id = intent.getStringExtra("ID")
            name = intent.getStringExtra("NAME")
            phone = intent.getStringExtra("PHONE")
            email = intent.getStringExtra("EMAIL")
            dob = intent.getStringExtra("DOB")
            bio = intent.getStringExtra("BIO")
            imageUri = Uri.parse(intent.getStringExtra("IMAGE"))
            addedTime = intent.getStringExtra("ADDED_TIME")
            updatedTime = intent.getStringExtra("UPDATED_TIME")

            //set data to view
            //if user didn't attached image while saving record then image uri will be null
            if (imageUri.toString() == "null")
            {
                //no image
                profileIv.setImageResource(R.drawable.ic_person_black)
            }else{
                //have image
                profileIv.setImageURI(imageUri)
            }
            edt_name.setText(name)
            edt_phone.setText(phone)
            edt_userEmail.setText(email)
            edt_userDOB.setText(dob)
            edt_userBio.setText(bio)
        }else{
            //adding new data, came here from MainActivity
            actionBar!!.title = "Add Record"
        }
        //init db helper class
        dbHelper = MyDBHelper(this)

        //init permis
        cameraPermission = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //click to pick image
        profileIv.setOnClickListener {
            imagePickDialog()
        }

        //save
        btn_save.setOnClickListener {
            inputData()
        }

    }


    private fun inputData() {
        //get data
        name =""+edt_name.text.toString().trim()
        phone =""+edt_phone.text.toString().trim()
        email = ""+edt_userEmail.text.toString().trim()
        dob = ""+edt_userDOB.text.toString().trim()
        bio = ""+edt_userBio.text.toString().trim()

        if (isEditMode){
            //editing
            val timeStamp = ""+System.currentTimeMillis()
            dbHelper?.updateRecord(
                "$id",
                "$name",
                "$imageUri",
                "$bio",
                "$phone",
                "$email",
                "$dob",
                "$addedTime",
                "$updatedTime")
            Toast.makeText(this,"Updated...", Toast.LENGTH_SHORT).show()
        }else{
            //adding new
            //save data to db
            val timestamp = System.currentTimeMillis()
            val id = dbHelper.insertRecord(
                "$name",
                "$imageUri",
                "$bio",
                "$phone",
                "$email",
                "$dob",
                "$timestamp",
                "$timestamp"
            )
            Toast.makeText(this, "Record Added against ID $id", Toast.LENGTH_SHORT).show()
        }



    }

    private fun imagePickDialog() {
        //options display in dialog
        val options = arrayOf("Camera","Gallery")
        //dialog
        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
        //set item/options
        builder.setItems(options){dialog, which ->
            //handle item clicks
            if (which == 0) {
                //camera clicked
                if (!checkCameraPermisson()){
                    //permission not granted
                    requestCameraPermission()
                }
                else{
                    //permission granted
                    pickFromCamera()
                }
            }
            else{
                //gallery clicked
                if (!checkStoragePermission()){
                    //permission not granted
                    requestStoragePermission()
                }else{
                    //permission already granted
                    pickFromGallery()
                }
            }
        }
        //show dialog
        builder.show()
    }

    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this,storagePermission, STORAGE_REQUEST_CODE)
    }

    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" //only image to be picked
        startActivityForResult(
            galleryIntent,
            IMAGE_PICK_GALLERY_CODE
        )
    }

    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Description")
        //put image uri
        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //intent to open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(
            cameraIntent,
            IMAGE_PICK_CAMERA_CODE
        )
    }

    private fun requestCameraPermission() {
        //request camera permiss
        ActivityCompat.requestPermissions(this, cameraPermission , CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermisson(): Boolean {
        //check if camera permission camera and storage are enabled or not
        val results = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        val results1 = ContextCompat.checkSelfPermission(this,
            Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED
        return results && results1
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE ->{
                if (grantResults.isNotEmpty()){
                    //if allowed return true otherwise false
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && storageAccepted){
                        pickFromCamera()
                    }
                    else{
                        Toast.makeText(this,"Camera and storage permission are required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE ->{
                if (grantResults.isNotEmpty()){
                    //if allowed true otherwise false
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted){
                        pickFromGallery()
                    }else{
                        Toast.makeText(this, "Storage permission is required", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        if (resultCode == Activity.RESULT_OK)
        {
            //image is pick
            if (requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //pick from gallery and crop image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageUri = resultUri
                    //set image
                    profileIv.setImageURI(resultUri)
                }
                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    val error = result.error
                    Toast.makeText(this,""+error, Toast.LENGTH_SHORT).show()
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
