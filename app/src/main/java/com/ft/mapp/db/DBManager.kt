package com.ft.mapp.db

import android.content.Context
import com.ft.mapp.db.gen.DaoMaster
import com.ft.mapp.db.gen.DaoSession

object DBManager {

    private lateinit var mDaoMaster: DaoMaster
    private lateinit var mDaoSession: DaoSession

    fun initDB(context: Context) {
        val devOpenHelper = DaoMaster.DevOpenHelper(context, "multiple.db", null)
        mDaoMaster = DaoMaster(devOpenHelper.writableDatabase)
        mDaoSession = mDaoMaster.newSession()
    }

    fun getDaoSession(): DaoSession {
        return mDaoSession
    }

}