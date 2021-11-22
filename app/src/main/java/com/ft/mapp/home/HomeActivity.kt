package com.ft.mapp.home

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import cn.jpush.android.api.JPushInterface
import com.`fun`.vbox.helper.compat.PermissionCompat
import com.airbnb.lottie.LottieAnimationView
import com.bytedance.sdk.openadsdk.*
import com.bytedance.sdk.openadsdk.TTAdNative.RewardVideoAdListener
import com.bytedance.sdk.openadsdk.TTRewardVideoAd.RewardAdInteractionListener
import com.ft.mapp.BuildConfig
import com.ft.mapp.R
import com.ft.mapp.VApp
import com.ft.mapp.abs.ui.VActivity
import com.ft.mapp.ad.AdHelper
import com.ft.mapp.ad.base.RewardAdListener
import com.ft.mapp.ad.base.SplashAdListener
import com.ft.mapp.ad.ttads.TTAdManagerHolder
import com.ft.mapp.dialog.ActDialog
import com.ft.mapp.dialog.FirstGiftDialog
import com.ft.mapp.dialog.TutorialsDialog
import com.ft.mapp.dialog.UpdateDialog
import com.ft.mapp.home.adapters.MyFragmentViewPagerAdapter
import com.ft.mapp.home.pipi.PipiWebFragment
import com.ft.mapp.listener.OnDialogListener
import com.ft.mapp.utils.ActFillUtils
import com.ft.mapp.utils.DownloadAppTask
import com.ft.mapp.utils.ShopAppUtil
import com.ft.mapp.utils.ToastUtil
import com.jaeger.library.StatusBarUtil
import com.xqb.user.bean.UserInfo
import com.xqb.user.net.engine.AdAgent
import com.xqb.user.net.engine.ApiServiceDelegate
import com.xqb.user.net.engine.StatAgent
import com.xqb.user.net.engine.UserAgent
import com.xqb.user.net.lisenter.ApiCallback
import com.xqb.user.util.UmengStat
import com.xqb.user.util.UserSharePref
//import com.yilan.sdk.data.entity.Channel
//import com.yilan.sdk.data.entity.MediaInfo
//import com.yilan.sdk.ui.category.ChannelFragment
//import com.yilan.sdk.ui.configs.YLUIConfig
//import com.yilan.sdk.ui.configs.callback.CommentCallback
//import com.yilan.sdk.ui.configs.callback.ShareCallback
//import com.yilan.sdk.ui.little.YLLittleVideoFragment
//import com.yilan.sdk.ui.video.YLVideoFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.ResourceObserver
import io.reactivex.schedulers.Schedulers
import jonathanfinerty.once.Once
import kotlinx.android.synthetic.main.activity_home.*
import zhy.com.highlight.HighLight
import zhy.com.highlight.shape.CircleLightShape
import java.util.concurrent.TimeUnit

open class HomeActivity : VActivity(), View.OnClickListener {
    private lateinit var adHelper: AdHelper
    private lateinit var menuAnimRes: Array<String>
    private lateinit var menuNorRes: Array<Int>
    private var actDialog: ActDialog? = null

    private var mRewarded: Boolean = false

    private var firstGiftDialog: FirstGiftDialog? = null

    private var updateDialog: UpdateDialog? = null
    private lateinit var launchFragment: LaunchFragment
    private lateinit var meFragment: MeFragment

    private lateinit var mSplashContainer: FrameLayout

    private lateinit var menuIvs: ArrayList<LottieAnimationView>
    private lateinit var menuTvs: ArrayList<TextView>


    private var first = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mSplashContainer = findViewById(R.id.home_layout_splash)
        StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.black))
//        YLUIConfig.getInstance().registerShareCallBack { _, _ -> }
//
//        YLUIConfig.getInstance().registerCommentCallBack(object : CommentCallback {
//            override fun onCommentShow(p0: String?): Boolean {
//                return false
//            }
//
//            override fun onCommentSend(p0: String?) {
//            }
//
//            override fun onCommentClick(p0: String?) {
//            }
//
//            override fun onCommentHide(p0: String?) {
//            }
//        })
        adHelper = AdHelper(this@HomeActivity)
//        StatusBarUtil.setTranslucent(this)
        home_iv_add.setOnClickListener(this)
        home_layout_me.setOnClickListener(this)
        home_layout_video.setOnClickListener(this)
        home_layout_news.setOnClickListener(this)
        home_layout_home.setOnClickListener(this)
        home_layout_act.setOnClickListener(this)
        main_iv_float_act_close.setOnClickListener(this)
        main_iv_float_act.setOnClickListener(this)
        bindViews()
        showUpdateDialog()
//        loadPermission()
        showAddTips()

        checkAct()

        val registrationID = JPushInterface.getRegistrationID(this)
        Log.i("--regist--", "registrationID=$registrationID")

//        mTTAdNative = TTAdManagerHolder.get().createAdNative(this)
        if (UserAgent.getInstance(this).isRewardOn) {
//            loadRewardAd()
            checkDeviceFirstRegister()
        }

    }

    private fun checkAct() {
        if (AdAgent.actHomeTabOn()) {
            home_layout_news.visibility = View.GONE
            home_layout_act.visibility = View.VISIBLE
            ActFillUtils.fillAd(home_layout_act, home_iv_act, AdAgent.loadHomeTabAct())
        } else {
            home_layout_act.visibility = View.GONE
//            if (UserAgent.getInstance(this).isThirdOn) {
//                home_layout_news.visibility = View.VISIBLE
//                home_layout_video.visibility = View.VISIBLE
//                home_layout_tips.visibility = View.VISIBLE
//            } else {
                home_layout_news.visibility = View.GONE
                home_layout_video.visibility = View.GONE
                home_layout_tips.visibility = View.GONE
//            }
        }
        if (AdAgent.actHomeDialogOn()) {
            showActDialog()
        }
    }

    private fun showActDialog() {
        if (actDialog == null) {
            actDialog = ActDialog(this)
        }
        actDialog?.show()
        actDialog?.setOnDismissListener {
            main_layout_float_act.visibility = if (AdAgent.actHomeFloatOn()) View.VISIBLE else View.GONE
            ActFillUtils.fillAd(main_iv_float_act, AdAgent.loadHomeFloatAct())
        }
        Once.markDone("TAG_FORE_AD")
    }

    private fun loadPermission() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
        if (!checkPermission(permissions)) {
            PermissionCompat.startRequestPermissions(this, false, permissions
            ) { _: Int, _: Array<String?>?, grantResults: IntArray? ->
                val result = PermissionCompat.isRequestGranted(grantResults)
                result
            }
        }

    }

    open fun checkPermission(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            val per = context.checkPermission(permission, android.os.Process.myPid(), android.os.Process.myUid())
            if (PackageManager.PERMISSION_GRANTED != per) {
                return false
            }
        }
        return true
    }

    private lateinit var highLight: HighLight

    private fun showAddTips() {
        if (UserSharePref.getInstance(VApp.getApp()).getBoolean(UserSharePref.KEY_GUIDE_ADD_APP)) {
            return
        }
        highLight = HighLight(this)
                .setOnLayoutCallback {
                    highLight.addHighLight(R.id.home_iv_add, R.layout.layout_tips_add, HighLight.OnPosCallback { _, bottomMargin, rectF, marginInfo ->
                        rectF?.let {
                            marginInfo?.rightMargin = 0f
                            marginInfo?.bottomMargin = bottomMargin
                        }
                    }, CircleLightShape())
                    highLight.show()
                }
                .setOnRemoveCallback {
                    UserSharePref.getInstance(VApp.getApp()).putBoolean(UserSharePref.KEY_GUIDE_ADD_APP, true)
                }
    }

    open fun checkDeviceFirstRegister() {
//        hasReceive = UserSharePref.getInstance(this).getBoolean(UserSharePref.KEY_HAS_RECEIVE_GIFT, false);
//        if (hasReceive) {
//            return;
//        }

        val userInfo = UserAgent.getInstance(this).userInfo ?: return
        if (userInfo.vipReceive == 0 && UserAgent.getInstance(this).isRewardOn) {
            showGift()
        }
    }

    private fun showGift() {
        firstGiftDialog = FirstGiftDialog(this, "Home")
        firstGiftDialog?.apply {
            show()
            setOnReceiveGiftListener {
                showLoading(true)
//                if (mttRewardVideoAd == null) {
//                    if (UserAgent.getInstance(this@HomeActivity).isRewardOn) {
//                        loadRewardAd()
//                    }
//                } else {
//                    mttRewardVideoAd?.showRewardVideoAd(this@HomeActivity)
//                }
                adHelper.showRewardAd(object : RewardAdListener {
                    override fun onRewardVerify() {
                        mRewarded = true
                    }

                    override fun onAdClicked() {
                    }

                    override fun onAdSkip() {
                    }

                    override fun onAdShow() {
                    }

                    override fun onTimeout() {
                    }

                    override fun onAdClose() {
                        if (mRewarded) {
                            receiveVip()
                        }
                    }

                    override fun onError(code: Int, msg: String?) {
                        ToastUtil.show(this@HomeActivity, getString(R.string.ad_not_prepare))
                    }
                })
            }
        }

    }

    /**
     * 加载开屏广告
     */
    private fun loadSplashAd() {
        adHelper.showSplashAd(object : SplashAdListener {
            override fun onAdClicked() {
            }

            override fun onAdSkip() {
                mSplashContainer.removeAllViews()
                mSplashContainer.visibility = View.GONE
            }

            override fun onSplashAdLoad(adView: View?) {
                mSplashContainer.visibility = View.VISIBLE
                mSplashContainer.removeAllViews()
                mSplashContainer.addView(adView)
                Once.markDone("TAG_FORE_AD")
            }

            override fun onAdShow(adView: View?) {
            }

            override fun onTimeout() {
                mSplashContainer.removeAllViews()
                mSplashContainer.visibility = View.GONE
            }

            override fun onAdClose() {
                mSplashContainer.removeAllViews()
                mSplashContainer.visibility = View.GONE
            }

            override fun onError(code: Int, msg: String?) {
            }

        })
    }

    private fun receiveVip() {
        ApiServiceDelegate(this@HomeActivity).receiveVip()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : ResourceObserver<UserInfo?>() {
                    override fun onNext(t: UserInfo?) {
                        UserSharePref.getInstance(this@HomeActivity).putBoolean(UserSharePref.KEY_HAS_RECEIVE_GIFT, true)
                        ToastUtil.show(this@HomeActivity, "已获得两天VIP体验时间")
                        meFragment.updateLoginState()
                        StatAgent.onEvent(context, UmengStat.VIP_RECEIVE, "name", "Home_" + UserAgent.getInstance(context).userType())
                    }

                    override fun onError(e: Throwable) {
                        if (TextUtils.isEmpty(e.message)) {
                            ToastUtil.show(this@HomeActivity, "网络状况不佳")
                        } else {
                            ToastUtil.show(this@HomeActivity, e.message)
                        }
                    }

                    override fun onComplete() {}
                })
    }

    private fun showUpdateDialog() {
        val versionBean = UserAgent.getInstance(this@HomeActivity).versionBean ?: return
        if (versionBean.version_code <= BuildConfig.VERSION_CODE) {
            return
        }
        if (!versionBean.show_dialog) {
            return
        }
        if (updateDialog == null) {
            updateDialog = UpdateDialog(this, versionBean.force)
        }
        updateDialog?.let {
            it.setInfo(versionBean)
            it.setOnDialogListener(object : OnDialogListener {
                override fun onCancel() {
                }

                override fun onOk() {
                    if (!PermissionCompat
                                    .checkPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE), false)) {
                        PermissionCompat.startRequestPermissions(context, false, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                        ) { _: Int, _: Array<String?>?, grantResults: IntArray? ->
                            val result = PermissionCompat.isRequestGranted(grantResults)
                            if (result) {
                                val downloadAppTask = DownloadAppTask(context)
                                downloadAppTask.execute(versionBean.file, versionBean.url, versionBean.version_code.toString())
                            }
                            result
                        }
                    } else {
                        val downloadAppTask = DownloadAppTask(context)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home_iv_add -> {
                viewPager.setCurrentItem(0, false)
                launchFragment.addApp()
            }
            R.id.home_layout_me ->
                viewPager.setCurrentItem(1, false)
            R.id.home_layout_news -> {
                viewPager.setCurrentItem(2, false)
                StatAgent.onEvent(this, UmengStat.NEWS_CLICK)
            }
            R.id.home_layout_video -> {
                viewPager.setCurrentItem(1, false)
                StatAgent.onEvent(this, UmengStat.VIDEOS_CLICK)
                home_layout_tips.visibility = View.GONE
            }
            R.id.home_layout_home ->
                viewPager.setCurrentItem(0, false)
            R.id.main_iv_float_act_close ->
                main_layout_float_act.visibility = View.GONE
//            R.id.home_layout_act, R.id.main_iv_float_act ->
////                WebViewActivity.start(this, UserAgent.actUrl)
//                ShopAppUtil.openTaoBaoApp(this,"",UserAgent.actUrl)
        }
    }

    private fun bindViews() {
        menuIvs = ArrayList(4)
        menuIvs.add(home_iv_home)
//        menuIvs.add(home_iv_video)
//        menuIvs.add(home_iv_news)
        menuIvs.add(home_iv_me)

        menuTvs = ArrayList(4)
        menuTvs.add(home_tv_home)
//        menuTvs.add(home_tv_video)
//        menuTvs.add(home_tv_news)
        menuTvs.add(home_tv_me)

//        menuAnimRes = arrayOf("home.json", "video.json", "news.json", "me.json")
//        menuNorRes = arrayOf(R.drawable.icon_home_nor, R.drawable.icon_video_nor, R.drawable.icon_news_nor, R.drawable.icon_me_nor)
        menuAnimRes = arrayOf("home.json", "me.json")
        menuNorRes = arrayOf(R.drawable.icon_home_nor, R.drawable.icon_me_nor)
        launchFragment = LaunchFragment.newInstance()
        meFragment = MeFragment.newInstance()
        val list: ArrayList<Fragment> = ArrayList()
        list.add(launchFragment)
//        list.add(YLLittleVideoFragment.newInstance())
        //三方视频页有toast提示
//        if (UserAgent.getInstance(this).isThirdOn) {
//            list.add(ChannelFragment())
//        } else {
//            list.add(WebFragment())
//        }
        list.add(meFragment)
        val pagerAdapter = MyFragmentViewPagerAdapter(
                supportFragmentManager, list)
        viewPager?.let {
            it.adapter = pagerAdapter
            it.currentItem = 0
        }
        viewPager.offscreenPageLimit = 4
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                menuChosen(position)
            }

        })
    }

    fun menuChosen(targetIndex: Int) {
        for ((index, tv) in menuTvs.withIndex()) {
            if (index == targetIndex) {
                tv.setTextColor(resources.getColor(R.color.colorPrimary))
                menuIvs[index].setAnimation(menuAnimRes[index])
                menuIvs[index].playAnimation()
                menuIvs[index].frame = -1
            } else {
                tv.setTextColor(resources.getColor(R.color.mainTextColor))
                menuIvs[index].cancelAnimation()
                menuIvs[index].setImageResource(menuNorRes[index])
            }
        }
    }

    open fun guideVip() {
        viewPager.currentItem = 0
        TutorialsDialog(this).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        for (fragment in supportFragmentManager.fragments) {
            fragment?.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val actUrl = intent?.getStringExtra("ACT_URL")
        val actType = intent?.getStringExtra("ACT_TYPE")
        if (!TextUtils.isEmpty(actUrl)) {
            ShopAppUtil.openThirdApp(this, actType, actUrl)
//            ShopAppUtil.openTaoBaoApp(this, "", actUrl)
        }
    }

    companion object {
        @JvmStatic
        fun goHome(context: Context) {
            val intent =
                    Intent(context, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    /**
     * App前后台状态
     */
    private var isForeground = true

    override fun onResume() {
        super.onResume()
        if (first) {
            first = false
            return
        }

        if (mRewarded) {
            firstGiftDialog?.dismiss()
            meFragment.updateLoginState()
            mRewarded = false
            return
        }

        val versionBean = UserAgent.getInstance(this@HomeActivity).versionBean ?: return

        if (versionBean.version_code > BuildConfig.VERSION_CODE && !first && versionBean.force) {
            showUpdateDialog()
            return
        }

//        val currentTime = System.currentTimeMillis()
//        if (!isForeground && currentTime - splashTime >= 120000L) {
//            //由后台切换到前台
//            showSplashAd(currentTime)
//            isForeground = true
//        }

        if (!isForeground) {
            //由后台切换到前台
            if (!Once.beenDone(
                            TimeUnit.MINUTES,
                            if (versionBean.actDialogInterval < 2) 2 else versionBean.actDialogInterval.toLong(),
                            "TAG_FORE_AD"
                    )
            ) {
                if (AdAgent.actHomeDialogOn()) {
                    showActDialog()
                } else if (UserAgent.getInstance(this).isSplashOn) {
                    showSplashAd()
                }
            }
            isForeground = true
        }

    }

    private fun showSplashAd() {
        if (UserAgent.getInstance(this).isSplashOn) {
            loadSplashAd()
        }
    }

    private var backPressTime: Long = 0L
    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        if (curTime - backPressTime > 2000) {
            ToastUtil.show(this, "再按一次退出应用")
            backPressTime = curTime
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
//        YLUIConfig.getInstance().unRegisterShareCallBack()
//        YLUIConfig.getInstance().unregisterCommentCallBack()
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        if (!isAppOnForeground()) {
            //由前台切换到后台
            isForeground = false
        }
    }

    /**
     * 判断app是否处于前台
     *
     * @return
     */
    private fun isAppOnForeground(): Boolean {
        val activityManager: ActivityManager = applicationContext
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = applicationContext.packageName

        /**
         * 获取Android设备中所有正在运行的App
         */
        val appProcesses: List<ActivityManager.RunningAppProcessInfo> = activityManager.runningAppProcesses
                ?: return false
        for (appProcess in appProcesses) {
            // The name of the process that this object is associated with.
            if (!(!appProcess.processName.equals(packageName) || appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)) {
                return true
            }
        }
        return false
    }

}