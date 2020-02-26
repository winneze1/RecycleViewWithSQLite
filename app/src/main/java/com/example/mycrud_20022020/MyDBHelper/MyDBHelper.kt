package com.example.mycrud_20022020.MyDBHelper

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.mycrud_20022020.Common.Constants
import com.example.mycrud_20022020.Model.ModelRecord

class MyDBHelper(context: Context?):SQLiteOpenHelper(
    context,
    Constants.DB_NAME,
    null,
    Constants.DB_VERSION ){
    override fun onCreate(db: SQLiteDatabase) {
        //create table on that db
        db.execSQL(Constants.CREATE_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS "+ Constants.TABLE_NAME)
        onCreate(db)
    }
    //insert record to db
    fun insertRecord(
        name : String?,
        image : String?,
        bio : String?,
        phone : String?,
        email : String?,
        dob : String?,
        addedTime : String?,
        updatedTime:String?
        ):Long{
        //get writeable db because we want to write data
        val db = this.writableDatabase
        val values = ContentValues()
        //id will be inserted AUTOINCREMENT
        //insert data
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_IMAGE,image)
        values.put(Constants.C_BIO, bio)
        values.put(Constants.C_PHONE, phone)
        values.put(Constants.C_EMAIL, email)
        values.put(Constants.C_DOB, dob)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)

        //insert row, it will return record id of saved record
        val id = db.insert(Constants.TABLE_NAME, null, values)
        db.close()
        return id
    }

    //update record
    fun updateRecord(id:String,
                     name:String?,
                     image:String?,
                     bio:String?,
                     phone:String?,
                     email: String?,
                     dob: String?,
                     addedTime: String?,
                     updatedTime: String?) : Long{
        //get write database
        val db = this.writableDatabase
        val values = ContentValues()
        //id will be inserted
        values.put(Constants.C_NAME, name)
        values.put(Constants.C_IMAGE,image)
        values.put(Constants.C_BIO, bio)
        values.put(Constants.C_PHONE, phone)
        values.put(Constants.C_EMAIL, email)
        values.put(Constants.C_DOB, dob)
        values.put(Constants.C_ADDED_TIMESTAMP, addedTime)
        values.put(Constants.C_UPDATED_TIMESTAMP, updatedTime)

        //update
        return db.update(
            Constants.TABLE_NAME,
            values,
            "${Constants.C_ID}=?",
            arrayOf(id)).toLong()


    }

    fun getAllRecord(orderBy:String):ArrayList<ModelRecord>{
        val recordList = ArrayList<ModelRecord>()

        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} ORDER BY $orderBy"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do {
                val modelRecord = ModelRecord(
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                //add record to list
                recordList.add(modelRecord)
            }while (cursor.moveToNext())
        }
        //close db
        db.close()
        return recordList
    }

    //search data
    fun searchRecords(query:String):ArrayList<ModelRecord>{
        val recordList = ArrayList<ModelRecord>()

        val selectQuery = "SELECT * FROM ${Constants.TABLE_NAME} WHERE ${Constants.C_NAME} LIKE '%$query%'"
        val db = this.writableDatabase
        val cursor = db.rawQuery(selectQuery, null)
        if (cursor.moveToFirst()){
            do {
                val modelRecord = ModelRecord(
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ID)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_NAME)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_BIO)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_PHONE)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_DOB)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP)),
                    "" + cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))
                )
                //add record to list
                recordList.add(modelRecord)
            }while (cursor.moveToNext())
        }
        //close db
        db.close()
        return recordList
    }
    //get total number of records
    fun recordCount():Int{
        val countQuery = "SELECT * FROM ${Constants.TABLE_NAME}"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        return count
    }
    //delete
    fun deleteRecords(id : String){
        val db = writableDatabase
        db.delete(
            Constants.TABLE_NAME,
            "${Constants.C_ID} = ?",
            arrayOf(id))
        db.close()
    }

    fun deleteAllRecourds(){
        val db = writableDatabase
        db.execSQL("DELETE FROM ${Constants.TABLE_NAME}")
        db.close()
    }

}