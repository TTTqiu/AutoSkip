package com.tttqiu.autoskip

import android.accessibilityservice.AccessibilityService
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import java.lang.ref.WeakReference

private const val skip = "跳过"

class AutoSkipService : AccessibilityService() {

    private lateinit var mSP: SharedPreferences

    private var mEnable = true
    private val mHandler = Handler(Looper.getMainLooper())

    class FindTask(private val serviceRef: WeakReference<AutoSkipService>) :
        AsyncTask<AccessibilityService, Unit, AccessibilityNodeInfo?>() {
        override fun doInBackground(vararg params: AccessibilityService): AccessibilityNodeInfo? {
            val nodes = params[0].rootInActiveWindow?.findAccessibilityNodeInfosByText(skip)
            if (nodes != null) {
                for (node in nodes) {
                    var view = node
                    while (view != null && !view.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        view = view.parent
                        if (view == null) {
                            return null
                        }
                    }
                    return view
                }
            }
            return null
        }

        override fun onPostExecute(result: AccessibilityNodeInfo?) {
            super.onPostExecute(result)
            if (result == null) {
                return
            }
            val service = serviceRef.get()
            if (service != null) {
                Toast.makeText(serviceRef.get(), "自动跳过", Toast.LENGTH_SHORT).show()
                Log.d("ppqq", "[skipped]: $skip")
                Log.d("ppqq", "packageName: " + result.packageName)
                Log.d("ppqq", "className: " + result.className)
                Log.d("ppqq", "viewIdResourceName: " + result.viewIdResourceName)
                service.mEnable = false
                service.mHandler.postDelayed({
                    service.mEnable = true
                }, service.mSP.getInt("auto_skip_check_interval", 5).toLong() * 1000)
            }
        }
    }

    override fun onServiceConnected() {
        mSP = getSharedPreferences("sp", 0)
        val sp = getSharedPreferences("sp", 0)
        val packageNames = ArrayList<String>()
        val labels = ArrayList<String>()
        val pm = packageManager
        val packages = pm.getInstalledPackages(0)
        for (packageInfo in packages) {
            if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) { // 第三方应用
                if (sp.getBoolean(packageInfo.packageName, false)) {
                    packageNames.add(packageInfo.packageName)
                    val label = packageInfo.applicationInfo.loadLabel(pm).toString()
                    Log.d("ppqq", "Add package: " + packageInfo.packageName + " - " + label)
                    labels.add(label)
                }
            }
        }
        val newInfo = serviceInfo
        newInfo.packageNames = packageNames.toTypedArray()
        serviceInfo = newInfo
        if (labels.size > 0) {
            val stringBuilder = StringBuilder("自动跳过：")
            for (label in labels) {
                stringBuilder.append(" “$label”")
            }
            Toast.makeText(this, stringBuilder.toString(), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "监听所有 - 影响性能", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        Log.d("ppqq", event.toString())
        if (mEnable) {
            FindTask(WeakReference(this)).execute(this)
        }
    }

    override fun onInterrupt() {
        Log.d("ppqq", "onInterrupt")
    }
}
