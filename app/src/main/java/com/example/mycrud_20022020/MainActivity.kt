package com.example.mycrud_20022020

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import com.example.mycrud_20022020.Adapter.AdapterRecord
import com.example.mycrud_20022020.Common.Constants
import com.example.mycrud_20022020.MyDBHelper.MyDBHelper

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //db helper
    lateinit var dbHelper: MyDBHelper

    //orderby queries
    private val NEWEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} DESC"
    private val OLDEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} ASC"
    private val TITLE_ASC = "${Constants.C_ADDED_TIMESTAMP} ASC"
    private val TITLE_DESC = "${Constants.C_ADDED_TIMESTAMP} DESC"

    private var recentSortOrder = NEWEST_FIRST
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //init db helper
        dbHelper = MyDBHelper(this)

        loadRecords(NEWEST_FIRST) //default we will load newest

        addRecordBtn.setOnClickListener {
            val intent = Intent(this, AddUpdateRecordActivity::class.java)
            intent.putExtra("isEditMode",false)
            startActivity(intent)

        }
    }

    private fun loadRecords(orderBy:String) {
        recentSortOrder = orderBy
        val adapterRecord = AdapterRecord(
            this,
            dbHelper.getAllRecord(orderBy)
        )

        recordsRv.adapter = adapterRecord
    }

    private fun searchRecords(query:String) {
        val adapterRecord = AdapterRecord(
            this,
            dbHelper.searchRecords(query)
        )

        recordsRv.adapter = adapterRecord
    }

    private fun sortDialog() {
        //Options to display in dialog
        val options = arrayOf("Title Ascending","Title Descending","Newest", "Oldest")
        //dialog
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
            .setItems(options){
                dialog, which ->
                if (which == 0){
                    //name asc
                    loadRecords(TITLE_ASC)
                }else if(which == 1){
                    //name desc
                    loadRecords(TITLE_DESC)
                }else if(which == 2){
                    //newest
                    loadRecords(NEWEST_FIRST)
                }else if(which == 3){
                    //oldest
                    loadRecords(OLDEST_FIRST)
                }
            }.show()
    }

    public override fun onResume() {
        super.onResume()
        loadRecords(recentSortOrder)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_main, menu)

        //searchview
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    searchRecords(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchRecords(query)
                }
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu item clicks
        val id = item.itemId
        if (id == R.id.action_sort){
            sortDialog()
        }else if (id == R.id.action_deleteall){
            //delete all records
            dbHelper.deleteAllRecourds()
            onResume()
        }
        return super.onOptionsItemSelected(item)
    }


}
