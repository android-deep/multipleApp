package com.ft.mapp.bean

import com.chad.library.adapter.base.entity.MultiItemEntity

class FaqDataBean(var data: String?, itemType: Int) : MultiItemEntity {
    private var itemType = 0
    private var datas : String?=null
    fun setItemType(itemType: Int) {
        this.itemType = itemType
    }

    override fun getItemType(): Int {
        return itemType
    }

    init {
        this.itemType = itemType
    }
}