package com.example.greenify.activity.event

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greenify.R
import com.example.greenify.activity.adapter.MemberListAdapter
import com.example.greenify.util.ApplicationUtils


class EventMemberListActivity : AppCompatActivity() {

    private lateinit var memListLayout: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_member_list)
        ApplicationUtils.configureWindowInsets(this@EventMemberListActivity)

        val intent = intent
        if (intent != null) {
            if (intent.hasExtra("MEM_LIST")) {
                val receivedMemberList = getIntent().getStringArrayListExtra("MEM_LIST")

                val emptyTextView: TextView = findViewById(R.id.mem_list_empty)
                memListLayout = findViewById(R.id.mem_list_recycler)

                if (receivedMemberList.isNullOrEmpty()) {
                    emptyTextView.visibility = View.VISIBLE
                    memListLayout.visibility = View.GONE
                } else {
                    emptyTextView.visibility = View.GONE
                    memListLayout.visibility = View.VISIBLE
                    val memberListAdapter = MemberListAdapter(receivedMemberList)


                    memListLayout.adapter = memberListAdapter
                    memListLayout.layoutManager = LinearLayoutManager(this)
                }
            } else {
                Toast.makeText(this@EventMemberListActivity, "Failed to load", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

        val btnCloseView: TextView = findViewById(R.id.btn_close_view)
        btnCloseView.setOnClickListener {
            finish()
        }
    }
}
