package com.tttqiu.autoskip

import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingActivity : AppCompatActivity() {

    private var mData = ArrayList<AppBean>()
    private lateinit var mSP: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        mSP = getSharedPreferences("sp", 0)
        getAppList()
        initView()
    }

    override fun onDestroy() {
        Toast.makeText(this, "改动后，请重新关闭-开启服务", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    private fun initView() {
        val list = findViewById<RecyclerView>(R.id.list)
        val adapter = AppListAdapter(this, mData)
        list.adapter = adapter
        list.layoutManager = LinearLayoutManager(this)

        val editText = findViewById<EditText>(R.id.edit)
        editText.setText(mSP.getInt("auto_skip_check_interval", 5).toString())
        editText.setOnEditorActionListener { _: TextView, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mSP.edit()
                    .putInt("auto_skip_check_interval", Integer.valueOf(editText.text.toString()))
                    .apply()
            }
            false
        }
    }

    private fun getAppList() {
        val pm = packageManager
        val packages = pm.getInstalledPackages(0)
        for (packageInfo in packages) {
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) { // 第三方应用
                mData.add(
                    AppBean(
                        packageInfo.applicationInfo.loadIcon(pm),
                        packageInfo.applicationInfo.loadLabel(pm).toString(),
                        packageInfo.packageName
                    )
                )
            }
        }
    }
}