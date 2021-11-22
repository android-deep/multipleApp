package com.ft.mapp.home.activity

import android.os.Bundle
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.ft.mapp.R
import com.ft.mapp.abs.nestedadapter.SmartRecyclerAdapter.TYPE_FOOTER
import com.ft.mapp.bean.FaqDataBean
import com.ft.mapp.utils.ToastUtil
import com.jaeger.library.StatusBarUtil
import com.xqb.user.bean.FaqResp
import com.xqb.user.net.engine.ApiServiceDelegate
import com.xqb.user.util.GsonUtil
import com.xqb.user.util.ResponseCode
import kotlinx.android.synthetic.main.activity_faq.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FaqActivity : AppCompatActivity() {
     val TYPE_FOOTER = 1001
     val TYPE_COMMON = 1002
     val TYPE_DIVISION = 1003
    private lateinit var dataList : ArrayList<FaqDataBean>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_faq)
        StatusBarUtil.setColorNoTranslucent(this, resources.getColor(R.color.colorAccent))
//        loadData()
        initData();
        showFAQ(dataList);
    }

    private fun initData() {
        dataList = ArrayList<FaqDataBean>()
        dataList.add(FaqDataBean("新手教程", TYPE_COMMON))
        dataList.add(FaqDataBean("应用多开分身双开助手功能说明", TYPE_COMMON))
        dataList.add(FaqDataBean("如何获得VIP?", TYPE_COMMON))
        dataList.add(FaqDataBean("如何使用VIP特权功能？", TYPE_COMMON))
        dataList.add(FaqDataBean("分割？", TYPE_DIVISION))
        dataList.add(FaqDataBean("应用多开分身双开助手如何收费？", TYPE_COMMON))
        dataList.add(FaqDataBean("分身是否会导致封号？", TYPE_COMMON))
        dataList.add(FaqDataBean("OPPO手机分身启动失败的处理方案？", TYPE_COMMON))
        dataList.add(FaqDataBean("永久会员账号出现异常如何处理?", TYPE_COMMON))
        dataList.add(FaqDataBean("VIVO手机微信分身登录时无法获取验证码，如何处理?", TYPE_COMMON))
        dataList.add(FaqDataBean("安装了64位插件微信分身依然打不开?", TYPE_COMMON))
        dataList.add(FaqDataBean("如何将分身添加到桌面?", TYPE_COMMON))
        dataList.add(FaqDataBean("一台手机能开多少个分身?", TYPE_COMMON))
        dataList.add(FaqDataBean("购买VIP后，更换手机或刷机的问题?", TYPE_COMMON))
        dataList.add(FaqDataBean("应用多开分身双开助手卸载后的影响?", TYPE_COMMON))
        dataList.add(FaqDataBean("如何进入权限检查设置项?", TYPE_COMMON))
        dataList.add(FaqDataBean("手机系统升级对应用多开分身双开助手及分身的影响?", TYPE_COMMON))
        dataList.add(FaqDataBean("64位插件图标消失对使用是否有影响?", TYPE_COMMON))
        dataList.add(FaqDataBean("最后一项", TYPE_FOOTER))
    }

    private fun loadData() {
        ApiServiceDelegate(this).loadFAQ(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (!response.isSuccessful) {
                    ToastUtil.show(this@FaqActivity, getString(com.xqb.user.R.string.error_network))
                    return
                }
                val faqResponse = GsonUtil.gson2Bean(response.body(),FaqResp::class.java)
                if (faqResponse == null) {
                    ToastUtil.show(this@FaqActivity, "数据解析失败")
                    return
                }
                if (faqResponse.code == ResponseCode.CODE_SUCCESS) {
//                    showFAQ(faqResponse.data)
                } else {
                    ToastUtil.show(this@FaqActivity, faqResponse.code.toString() + "-" + faqResponse.msg)
                }
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {}
        })
    }

    private fun showFAQ(data: MutableList<FaqDataBean>) {
        data.let {
            faq_recycler_view.layoutManager = LinearLayoutManager(this)
            faq_recycler_view.adapter = FAQAdapter(it)
        }
    }


    class FAQAdapter(data: MutableList<FaqDataBean>?) : BaseMultiItemQuickAdapter<FaqDataBean, BaseViewHolder>(data) {
        val TYPE_FOOTER = 1001
        val TYPE_COMMON = 1002
        val TYPE_DIVISION = 1003
        init {
            addItemType(TYPE_COMMON, R.layout.item_faq)
            addItemType(TYPE_FOOTER, R.layout.item_faq_footer)
            addItemType(TYPE_DIVISION, R.layout.item_faq_division)
        }
        override fun convert(helper: BaseViewHolder, item: FaqDataBean) {
            when (helper.itemViewType) {
                TYPE_COMMON -> {
                    helper.setText(R.id.item_faq_tv, item.data)
                }
            }
        }
    }

}
