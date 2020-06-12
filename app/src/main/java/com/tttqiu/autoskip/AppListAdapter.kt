package com.tttqiu.autoskip

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AppListAdapter(context: Context, data: List<AppBean>) :
    RecyclerView.Adapter<AppListAdapter.ViewHolder>() {

    private val mContext = context
    private val mData = data
    private val mSP = mContext.getSharedPreferences("sp", 0)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val mIcon: ImageView = itemView.findViewById(R.id.app_icon)
        val mLabel: TextView = itemView.findViewById(R.id.app_label)
        val mPackage: TextView = itemView.findViewById(R.id.package_name)
        val mCheckBox: CheckBox = itemView.findViewById(R.id.check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.view_app_list_item, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("ApplySharedPref")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mData[position]
        holder.mIcon.setImageDrawable(item.getIcon())
        holder.mLabel.text = item.getLabel()
        holder.mPackage.text = item.getPackageName()
        holder.mCheckBox.setOnCheckedChangeListener(null)
        holder.mCheckBox.isChecked = mSP.getBoolean(item.getPackageName(), false)
        holder.mCheckBox.setOnCheckedChangeListener { _: CompoundButton, _: Boolean ->
            mSP.edit().putBoolean(item.getPackageName(), holder.mCheckBox.isChecked).commit()
        }
        holder.itemView.setOnClickListener {
            holder.mCheckBox.isChecked = !holder.mCheckBox.isChecked
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }
}
