package com.ft.mapp.home.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import cn.jpush.android.api.JPushInterface
import com.`fun`.vbox.helper.compat.PermissionCompat
import com.ft.mapp.BuildConfig
import com.ft.mapp.R
import com.ft.mapp.dialog.LogoutDialog
import com.ft.mapp.dialog.UpdateDialog
import com.ft.mapp.home.MeFragment
import com.ft.mapp.home.PrivacyPolicyActivity
import com.ft.mapp.home.UserProtocolActivity
import com.ft.mapp.listener.OnDialogListener
import com.ft.mapp.utils.DownloadAppTask
import com.ft.mapp.utils.ToastUtil
import com.jaeger.library.StatusBarUtil
import com.xqb.user.net.engine.ApiServiceDelegate
import com.xqb.user.net.engine.UserAgent
import com.xqb.user.net.lisenter.ApiCallback
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.colorAccent))

        updateState()

        setting_layout_check_update.setOnClickListener {
            val versionBean = UserAgent.getInstance(this).versionBean
            if (versionBean != null) {
                if (versionBean.version_code > BuildConfig.VERSION_CODE) {
                    showUpdateDialog()
                } else {
                    ToastUtil.show(this, "已经是最新版本了")
                }
            } else {
                checkUpdate()
            }
        }
        setting_view_uuid.setOnClickListener {
            showUUID()
        }
        setting_tv_uuid.setOnClickListener {
            Log.i("userIdGen", "decUserId = ${decode(setting_tv_uuid.text.toString())}")
        }
        setting_layout_privacy.setOnClickListener {
            startActivity(Intent(this, PrivacyPolicyActivity::class.java))
        }
        setting_layout_user_protocol.setOnClickListener {
            startActivity(Intent(this, UserProtocolActivity::class.java))
        }
        setting_tv_logout.setOnClickListener {
            logout()
        }
        layout_cancel_account.setOnClickListener {
            UnregisterActivity.start(this)
        }
        checkStatus()
    }

    private fun checkStatus() {
        if (UserAgent.getInstance(this).userInfo == null) {
            layout_cancel_account.visibility = View.GONE
        } else {
            layout_cancel_account.visibility = View.VISIBLE
        }
    }

    private var uuidCount = 5
    private fun showUUID() {
        if (setting_layout_uuid.visibility == View.VISIBLE || UserAgent.getInstance(this).userInfo == null) {
            return
        }
        uuidCount--
        if (uuidCount <= 0) {
            ToastUtil.show(this, "已获得用户UUID")
            setting_layout_uuid.visibility = View.VISIBLE
            val uuid = UserAgent.getInstance(this).userInfo.app_uuid
            if (uuid==null){
                ToastUtil.show(this, "设备信息过期，请重启应用")
                return
            }
            Log.i("userIdGen", "oldUserId = $uuid")
            val charArray = uuid.toCharArray()
            for ((index, char) in charArray.withIndex()) {
                when (char) {
                    48.toChar() -> {
                        charArray[index] = 65.toChar()
                    }
                    97.toChar() -> {
                        charArray[index] = 90.toChar()
                    }
                    else -> {
                        charArray[index] = char - 1
                    }
                }
            }
            val newUserId = String(charArray)
            Log.i("userIdGen", "newUserId = $newUserId")
            setting_tv_uuid.text = newUserId
        }

    }

    private fun decode(target: String): String {
        val charArray = target.toCharArray()
        for ((index, char) in charArray.withIndex()) {
            when (char) {
                65.toChar() -> {
                    charArray[index] = 48.toChar()
                }
                90.toChar() -> {
                    charArray[index] = 97.toChar()
                }
                else -> {
                    charArray[index] = char + 1
                }
            }
        }
        val result = String(charArray)
        Log.i("userIdGen", "compare : ${result == UserAgent.getInstance(this).userInfo.app_uuid}")
        return result
    }

    private fun logout() {
        val userInfo = UserAgent.getInstance(this).userInfo
        if (userInfo == null) {
            startActivityForResult(
                    Intent(this, LoginActivity::class.java),
                    MeFragment.REQUEST_CODE_LOGIN)
        } else {
            if (TextUtils.isEmpty(userInfo.mobile) && TextUtils.isEmpty(userInfo.name)) {
                startActivityForResult(
                        Intent(this, LoginActivity::class.java),
                        MeFragment.REQUEST_CODE_LOGIN)
            } else {
                LogoutDialog(this).setOnDialogLisenter(object : OnDialogListener {
                    override fun onCancel() {

                    }

                    override fun onOk() {
                        UserAgent.getInstance(this@SettingActivity).clearUserInfo()
                        setting_tv_logout.visibility = View.GONE
                        finish()
                    }

                }).show()
            }
        }
    }

    private var updateDialog: UpdateDialog? = null
    private var tempUser: Boolean = false
    private fun updateState() {
        val version = "V" + BuildConfig.VERSION_NAME
        setting_tv_cur_version.text = version

        val versionBean = UserAgent.getInstance(this).versionBean
        if (versionBean == null) {
            setting_tv_update_label.visibility = View.GONE
        } else {
            if (versionBean.version_code > BuildConfig.VERSION_CODE) {
                setting_tv_update_label.visibility = View.VISIBLE
            } else {
                setting_tv_update_label.visibility = View.GONE
            }
        }

        tempUser = false
        val userInfo = UserAgent.getInstance(this).userInfo
        //登录状态
        if (userInfo != null) {
            val username = checkTouristName()
            if (TextUtils.isEmpty(username)) {
                //游客身份
                tempUser = true
                setting_tv_logout.visibility = View.GONE
            } else {
                setting_tv_logout.visibility = View.VISIBLE
            }
        } else {
            setting_tv_logout.visibility = View.GONE
        }

    }

    private fun checkTouristName(): String {
        val userInfo = UserAgent.getInstance(this).userInfo
        var username = userInfo.name
        if (TextUtils.isEmpty(username)) {
            username = userInfo.mobile
        }
        return username
    }

    private fun checkUpdate() {
        setting_pb.visibility = View.VISIBLE
        ApiServiceDelegate(this).checkUpdate(object : ApiCallback {
            override fun onSuccess() {
                setting_pb.visibility = View.GONE
                showUpdateDialog()
            }

            override fun onFail(errorMsg: String?) {
                ToastUtil.show(this@SettingActivity, errorMsg)
                setting_pb.visibility = View.GONE
            }

        })
    }

    private fun showUpdateDialog() {
        val versionBean = UserAgent.getInstance(this@SettingActivity).versionBean ?: return
        if (versionBean.version_code <= BuildConfig.VERSION_CODE) {
            return
        }
        if (updateDialog == null) {
            updateDialog = UpdateDialog(this, false)
        }
        updateDialog?.let {
            it.setInfo(versionBean)
            it.setOnDialogListener(object : OnDialogListener {
                override fun onCancel() {
                    it.dismiss()
                }

                override fun onOk() {
                    if (!PermissionCompat
                                    .checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), false)) {
                        PermissionCompat.startRequestPermissions(this@SettingActivity, false, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) { _: Int, _: Array<String?>?, grantResults: IntArray? ->
                            val result = PermissionCompat.isRequestGranted(grantResults)
                            if (result) {
                                val downloadAppTask = DownloadAppTask(this@SettingActivity)
                                downloadAppTask.execute(versionBean.file, versionBean.version_code.toString())
                            }
                            result
                        }
                    } else {
                        val downloadAppTask = DownloadAppTask(this@SettingActivity)
                        downloadAppTask.execute(versionBean.file, versionBean.url, versionBean.version_code.toString())
                    }
//                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(versionBean.url)))

                }
            })
            if (!it.isShowing) {
                it.show()
            }
        }

    }

    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, SettingActivity::class.java))
        }
    }

    private var firstInit:Boolean = true

    override fun onResume() {
        super.onResume()
        if (firstInit){
            firstInit = false
            return
        }
        if (layout_cancel_account != null) {
            updateState()
            checkStatus()
        }
    }

}
