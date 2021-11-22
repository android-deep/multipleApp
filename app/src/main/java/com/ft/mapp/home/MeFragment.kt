package com.ft.mapp.home

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.ft.mapp.R
import com.ft.mapp.dialog.LogoutDialog
import com.ft.mapp.dialog.ShareDialog
import com.ft.mapp.home.activity.LoginActivity
import com.ft.mapp.home.activity.SettingActivity
import com.ft.mapp.home.activity.VipActivity
import com.ft.mapp.home.activity.WebViewActivity
import com.ft.mapp.listener.OnDialogListener
import com.ft.mapp.utils.ActFillUtils
import com.ft.mapp.utils.CommonUtil
import com.ft.mapp.utils.ToastUtil
import com.ft.mapp.widgets.MineRowView
import com.umeng.socialize.UMShareAPI
import com.xqb.user.net.engine.AdAgent
import com.xqb.user.net.engine.UserAgent
import java.text.SimpleDateFormat
import java.util.*

class MeFragment : Fragment() {
    private lateinit var ivBanner: ImageView
    private var receiveView: LottieAnimationView? = null
    private lateinit var tvUser: TextView
    private lateinit var tvExpire: TextView
    private lateinit var mLoginRow: MineRowView
    private lateinit var tvOpenVip: TextView
    private lateinit var ivVipLabel: ImageView
    private lateinit var layoutMockLocation: LinearLayout
    private lateinit var layoutMockSteps: LinearLayout

    override fun onViewCreated(
            view: View, savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_me, container, false)
    }

    companion object {
        const val REQUEST_CODE_LOGIN = 0x11

        @JvmStatic
        fun newInstance() = MeFragment()
    }

    private fun bindViews(view: View) {
        tvOpenVip = view.findViewById(R.id.me_tv_open_vip)
        ivVipLabel = view.findViewById(R.id.me_iv_vip_label)

        tvUser = view.findViewById(R.id.me_tv_user_account)
        tvExpire = view.findViewById(R.id.me_tv_vip_expire_time)

        ivBanner = view.findViewById(R.id.me_iv_banner)

        layoutMockLocation = view.findViewById(R.id.me_layout_vip_fun_mock_location)
        layoutMockSteps = view.findViewById(R.id.me_layout_vip_fun_mock_steps)
        if (!UserAgent.getInstance(requireContext()).isVirtualLocationOn) {
            layoutMockLocation.visibility = View.GONE
            layoutMockSteps.visibility = View.GONE
        }

        view.findViewById<MineRowView>(R.id.setting_tutorials)?.setOnClickListener {
            startActivity(Intent(requireContext(), TutorialsActivity::class.java))
        }

        view.findViewById<MineRowView>(R.id.setting_feedback)?.setOnClickListener {
//            FeedbackActivity.gotoFeedback(activity)
            UserAgent.getInstance(requireContext()).versionBean
            val versionBean = UserAgent.getInstance(requireContext()).versionBean
            if (versionBean == null || TextUtils.isEmpty(versionBean.kefu)) {
                ToastUtil.show(requireContext(), "客服资料获取失败,请稍候再试")
            } else {
                joinQQ(versionBean.kefu)
            }
//            joinQQGroup("Dqzr5GyvcYHR-mKWDNdyYbgJwCvaYoxF")
        }

        view.findViewById<MineRowView>(R.id.setting_about)?.setOnClickListener {
            startActivity(Intent(requireContext(), AboutActivity::class.java))
        }
        view.findViewById<MineRowView>(R.id.setting_option)?.setOnClickListener {
            SettingActivity.start(requireContext())
        }

        view.findViewById<LinearLayout>(R.id.me_layout_vip_function_1)?.setOnClickListener {
            if (UserAgent.getInstance(requireContext()).isPayVipUser) {
                guideVipFunction()
            } else {
                VipActivity.go(requireContext())
            }
        }
        view.findViewById<LinearLayout>(R.id.me_layout_vip_function_2)?.setOnClickListener {
            if (UserAgent.getInstance(requireContext()).isPayVipUser) {
                guideVipFunction()
            } else {
                VipActivity.go(requireContext())
            }
        }

        receiveView = view.findViewById(R.id.me_view_receive)

        mLoginRow = view.findViewById(R.id.setting_login)
        updateLoginState()
        mLoginRow.setOnClickListener {
            val userInfo = UserAgent.getInstance(requireContext()).userInfo
            if (userInfo == null) {
                startActivityForResult(
                        Intent(requireContext(), LoginActivity::class.java),
                        MeFragment.REQUEST_CODE_LOGIN)
            } else {
                if (TextUtils.isEmpty(userInfo.mobile) && TextUtils.isEmpty(userInfo.name)) {
                    startActivityForResult(
                            Intent(requireContext(), LoginActivity::class.java),
                            MeFragment.REQUEST_CODE_LOGIN)
                } else {
                    LogoutDialog(requireContext()).setOnDialogLisenter(object : OnDialogListener {
                        override fun onCancel() {

                        }

                        override fun onOk() {
                            UserAgent.getInstance(requireContext()).clearUserInfo()
                            updateLoginState()
                        }

                    }).show()
                }
            }
        }

        ivVipLabel.setOnClickListener {
            VipActivity.go(requireContext())
        }

        tvOpenVip.setOnClickListener {
            VipActivity.go(requireContext())
        }

        view.findViewById<MineRowView>(R.id.setting_single)?.setOnClickListener {
            startActivity(Intent(requireContext(), AppLaunchConfigActivity::class.java))
        }
        view.findViewById<MineRowView>(R.id.setting_share)?.setOnClickListener {
            ShareDialog(requireContext(), "me").show()
        }

        if (AdAgent.actMeBannerOn()) {
            ivBanner.visibility = View.VISIBLE
            val meBannerAct = AdAgent.loadMeBannerAct()
            ActFillUtils.fillAd(ivBanner, meBannerAct)
        } else {
            ivBanner.visibility = View.GONE
        }
    }

    private fun guideVipFunction() {
        val ownActivity = requireActivity()
        if (ownActivity is HomeActivity){
            ownActivity.guideVip()
        }
    }

    /**
     * 跳转QQ聊天界面
     */
    private fun joinQQ(kefu: String) {
        try {
            //第二种方式：可以跳转到添加好友，如果qq号是好友了，直接聊天
            val url = "mqqwpa://im/chat?chat_type=wpa&uin=$kefu"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            e.printStackTrace()
            ToastUtil.show(activity, "请检查是否安装QQ")
        }
    }

    fun joinQQGroup(key: String): Boolean {
        val intent = Intent()
        intent.data = Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26jump_from%3Dwebapi%26k%3D$key")
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        return try {
            startActivity(intent)
            true
        } catch (e: java.lang.Exception) {
            // 未安装手Q或安装的版本不支持
            ToastUtil.show(activity, "请检查是否安装QQ")
            false
        }
    }

    var tempUser: Boolean = false
    val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    open fun updateLoginState() {
        checkReceive()

        tempUser = false
        val userInfo = UserAgent.getInstance(requireContext()).userInfo
        val isVip = UserAgent.getInstance(requireContext()).isVipUser
        mLoginRow.setDetailMsg("")
        tvUser.setOnClickListener(null)
        //登录状态
        if (userInfo != null) {
            val expireDate = "会员有效期：" + sdf.format(userInfo.expireTime)
            val username = checkTouristName()
            if (TextUtils.isEmpty(username)) {
                //游客身份
                tempUser = true
                mLoginRow.setTvTitle(getString(R.string.user_login))
                tvUser.text = "立即登录"
                tvUser.setOnClickListener {
                    LoginActivity.start(requireContext())
                }
                if (isVip) {
                    if (userInfo.vipReceive == 1) {
                        //游客2天VIP体验中
                        tvExpire.text = expireDate
                    } else {
                        //游客购买VIP
//                        tvUser.text = getString(R.string.vip_hello)
                        tvExpire.text = expireDate
                        mLoginRow.setDetailMsg(getString(R.string.vip_login_tips))
                    }
                    ivVipLabel.visibility = View.VISIBLE
                    tvOpenVip.text = getString(R.string.renewal_vip)
                } else {
                    //游客体验vip过期
//                    tvUser.text = getString(R.string.vip_has_expired)
                    tvOpenVip.text = getString(R.string.renewal_vip)
                    tvExpire.text = getString(R.string.not_vip)
                    ivVipLabel.visibility = View.GONE
                }
            } else {
                tvUser.text = username
                mLoginRow.setTvTitle(getString(R.string.user_logout))
                if (isVip) {
                    tvOpenVip.text = getString(R.string.renewal_vip)
                    tvExpire.text = expireDate
                    ivVipLabel.visibility = View.VISIBLE
                } else {
                    tvExpire.text = getString(R.string.not_vip)
                    tvOpenVip.text = getString(R.string.open_vip)
                    ivVipLabel.visibility = View.GONE
                }
                tvUser.setOnClickListener(null)
            }
        } else {
            val loginTips = "请先登录"
            tvUser.text = loginTips
            val builder = SpannableStringBuilder(loginTips)
            builder.setSpan(TextClick(requireContext()), loginTips.length - 2, loginTips.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            tvUser.movementMethod = LinkMovementMethod.getInstance()
            tvUser.text = builder
            ivVipLabel.visibility = View.GONE
            tvExpire.text = "您尚未开通VIP会员"
            tvOpenVip.visibility = View.VISIBLE
            tvOpenVip.text = getString(R.string.open_vip)
            mLoginRow.setTvTitle(getString(R.string.user_login))
            tvUser.setOnClickListener {
                LoginActivity.start(requireContext())
            }
        }

    }

    private fun checkReceive() {
        receiveView?.let {
            it.setOnClickListener {
                (activity as? HomeActivity)?.checkDeviceFirstRegister()
            }
            val userInfo = UserAgent.getInstance(requireContext()).userInfo
            if (userInfo != null && userInfo.vipReceive == 0 && UserAgent.getInstance(requireContext()).isRewardOn) {
                it.visibility = View.VISIBLE
            } else {
                it.visibility = View.GONE
            }
        }

    }

    class TextClick(val context: Context) : ClickableSpan() {
        override fun onClick(widget: View) {
            LoginActivity.start(context)
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = context.resources.getColor(R.color.white)
            ds.isUnderlineText = true
        }
    }

    private fun checkTouristName(): String {
        val userInfo = UserAgent.getInstance(requireContext()).userInfo
        var username = userInfo.name
        if (TextUtils.isEmpty(username)) {
            username = userInfo.mobile
        }
        return username
    }

    override fun onActivityResult(
            requestCode: Int, resultCode: Int, data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MeFragment.REQUEST_CODE_LOGIN) {
            if (resultCode == Activity.RESULT_OK) {
                mLoginRow.setTvTitle(getString(R.string.user_logout))
            }
        }
        UMShareAPI.get(requireContext()).onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
//        Log.i("-------------","meFragment Attach")
    }

    override fun onDestroy() {
        super.onDestroy()
        UMShareAPI.get(requireContext()).release()
    }

    private var firstLoad = true
    override fun onResume() {
        super.onResume()
        if (firstLoad) {
            firstLoad = false
            return
        }
        updateLoginState()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser && !firstLoad) {
            updateLoginState()
        }
    }

}
