package com.tttqiu.autoskip

import android.graphics.drawable.Drawable

class AppBean(icon: Drawable, label: String, packageName: String) {

    private var mLabel = label
    private var mPackageName = packageName
    private var mIcon = icon

    fun getLabel(): String? {
        return mLabel
    }

    fun setLabel(mLabel: String) {
        this.mLabel = mLabel
    }

    fun getPackageName(): String? {
        return mPackageName
    }

    fun setPackageName(mPackage: String) {
        this.mPackageName = mPackage
    }

    fun getIcon(): Drawable? {
        return mIcon
    }

    fun setIcon(mIcon: Drawable) {
        this.mIcon = mIcon
    }

}