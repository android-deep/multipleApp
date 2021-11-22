package com.ft.mapp.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ft.mapp.R;
import com.ft.mapp.bean.FaqDataBean;
import com.ft.mapp.home.TutorialsActivity;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_CONTENT;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_IMG;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_MIXTURE;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_SRCNAME;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_TEXT;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_TITLE;
import static com.ft.mapp.home.activity.FaqDetailActvity.ACTIVITY_TYPE;

public class FaqActivityForJava extends AppCompatActivity {
    public static final int TYPE_FOOTER = 1001;
    public static final int TYPE_COMMON = 1002;
    public static final int TYPE_DIVISION = 1003;
    private ArrayList<FaqDataBean> dataList;
    private RecyclerView faqRv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorAccent));
        faqRv = findViewById(R.id.faq_recycler_view);
//        loadData()
        initData();
        showFAQ(dataList);
    }

    private void initData() {
        dataList = new ArrayList<>();
        dataList.add(new FaqDataBean("新手教程", TYPE_COMMON));
        dataList.add(new FaqDataBean("应用多开分身双开助手功能说明", TYPE_COMMON));
        dataList.add(new FaqDataBean("如何获得VIP?", TYPE_COMMON));
        dataList.add(new FaqDataBean("如何使用VIP特权功能？", TYPE_COMMON));
        dataList.add(new FaqDataBean("分割？", TYPE_DIVISION));
        dataList.add(new FaqDataBean("应用多开分身双开助手如何收费？", TYPE_COMMON));
        dataList.add(new FaqDataBean("分身是否会导致封号？", TYPE_COMMON));
        dataList.add(new FaqDataBean("OPPO手机分身启动失败的处理方案？", TYPE_COMMON));
        dataList.add(new FaqDataBean("永久会员账号出现异常如何处理?", TYPE_COMMON));
        dataList.add(new FaqDataBean("安装了64位插件微信分身依然打不开?", TYPE_COMMON));
        dataList.add(new FaqDataBean("如何将分身添加到桌面?", TYPE_COMMON));
        dataList.add(new FaqDataBean("一台手机能开多少个分身?", TYPE_COMMON));
        dataList.add(new FaqDataBean("购买VIP后，更换手机或刷机的问题?", TYPE_COMMON));
        dataList.add(new FaqDataBean("应用多开分身双开助手卸载后的影响?", TYPE_COMMON));
        dataList.add(new FaqDataBean("手机系统升级对应用多开分身双开助手及分身的影响?", TYPE_COMMON));
        dataList.add(new FaqDataBean("64位插件图标消失对使用是否有影响?", TYPE_COMMON));
        dataList.add(new FaqDataBean("最后一项", TYPE_FOOTER));
    }

    private void showFAQ(ArrayList<FaqDataBean>  data) {
        faqRv.setLayoutManager(new LinearLayoutManager(this));
        FAQAdapter faqAdapter = new FAQAdapter(data);
        faqRv.setAdapter(faqAdapter);
        faqAdapter.notifyDataSetChanged();
    }

    class FAQAdapter extends BaseMultiItemQuickAdapter<FaqDataBean, BaseViewHolder> {
        FAQAdapter(List<FaqDataBean> data) {
            super(data);
            addItemType(TYPE_FOOTER, R.layout.item_faq_footer);
            addItemType(TYPE_COMMON, R.layout.item_faq);
            addItemType(TYPE_DIVISION, R.layout.item_faq_division);
        }

        @Override
        protected void convert(@NonNull BaseViewHolder helper, FaqDataBean item) {
            if (helper.getItemViewType() == TYPE_COMMON) {
                helper.setText(R.id.item_faq_tv, item.getData());
                if(helper.getPosition() == 3 || helper.getPosition() == 15){
                    helper.getView(R.id.view_division).setVisibility(View.INVISIBLE);
                }
                helper.setOnClickListener(R.id.container, view -> {
                    int adapterPosition = helper.getAdapterPosition();
                    Intent intent;
                    Bundle bundle;
                    switch (adapterPosition) {
                        case 0:
                            intent = new Intent(FaqActivityForJava.this, TutorialsActivity.class);
                            startActivity(intent);
                            break;
                        case 1:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_IMG);
                            bundle.putString(ACTIVITY_SRCNAME, "faq_form1");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 2:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.way_to_vip));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 3:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.how_to_use_vip));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 4:
                            break;
                        case 5:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_MIXTURE);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.how_to_charge));
                            bundle.putString(ACTIVITY_SRCNAME, "faq_form2");
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 6:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_clearhonor));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 7:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_oppo_faile));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 8:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_vip_exception));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 9:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_64_exception));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 10:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.add_to_desk));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 11:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.count_of_spare));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 12:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_root));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 13:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_uninstall));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 14:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_update_influence));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                        case 15:
                            intent = new Intent(FaqActivityForJava.this, FaqDetailActvity.class);
                            bundle = new Bundle();
                            bundle.putString(ACTIVITY_TITLE, item.getData());
                            bundle.putInt(ACTIVITY_TYPE, ACTIVITY_TEXT);
                            bundle.putString(ACTIVITY_CONTENT, getResources().getString(R.string.about_disapper_influence));
                            intent.putExtras(bundle);
                            startActivity(intent);
                            break;
                    }
                });
            }
        }

    }

}
