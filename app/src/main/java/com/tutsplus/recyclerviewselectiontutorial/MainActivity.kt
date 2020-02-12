package com.tutsplus.recyclerviewselectiontutorial

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.widget.LinearLayoutManager
import android.view.ActionMode
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.recyclerview.selection.*
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Files.size
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class MainActivity : AppCompatActivity() {

    private var tracker: SelectionTracker<Long>? = null // bo theo gioi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val myList = listOf(
                Person("Alice", "555-0111"),
                Person("Bob", "555-0119"),
                Person("Carol", "555-0141"),
                Person("Dan", "555-0155"),
                Person("Eric", "555-0180"),
                Person("Craig", "555-0145")
        )

        my_rv.layoutManager = LinearLayoutManager(this)
        my_rv.setHasFixedSize(true)

        val adapter = MyAdapter(myList, this)
        my_rv.adapter = adapter

        tracker = SelectionTracker.Builder<Long>(
                "selection-1",
                my_rv,
                StableIdKeyProvider(my_rv),
                MyLookup(my_rv),
                StorageStrategy.createLongStorage()
        ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
         .build()

        if(savedInstanceState != null) tracker?.onRestoreInstanceState(savedInstanceState) // load lai neu bi xoay

        adapter.setTracker(tracker!!)

        tracker?.addObserver(object: SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                if (tracker!!.hasSelection() && actionMode == null) { // chua bat dau
                     actionMode = startSupportActionMode(actionModeCallBack)
                    actionMode?.title = tracker!!.selection.size().toString()
                } else if (!tracker!!.hasSelection() && actionMode !=null) { // ket thuc
                    actionMode?.finish()
                    actionMode = null
                } else { // tang so dem
                    actionMode?.title = tracker!!.selection.size().toString()
                }

            }
        })
    }
var actionMode: android.support.v7.view.ActionMode? = null

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        if(outState != null)
            tracker?.onSaveInstanceState(outState)
    }

    val actionModeCallBack = object : android.support.v7.view.ActionMode.Callback {
        override fun onActionItemClicked(p0: android.support.v7.view.ActionMode?, p1: MenuItem?): Boolean {
            return false
        }

        override fun onCreateActionMode(p0: android.support.v7.view.ActionMode?, p1: Menu?): Boolean {
            menuInflater.inflate(R.menu.actionmode_menu,p1)
           return true
        }

        override fun onPrepareActionMode(p0: android.support.v7.view.ActionMode?, p1: Menu?): Boolean {
            return false
        }

        override fun onDestroyActionMode(p0: android.support.v7.view.ActionMode?) {
            tracker?.clearSelection()

        }

    }


}
