package com.ft.mapp.home;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.VCommends;
import com.ft.mapp.abs.ui.VFragment;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.home.adapters.CloneAppListAdapter;
import com.ft.mapp.home.adapters.SearchAppListAdapter;
import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.home.models.AppInfoLite;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.VipFunctionUtils;
import com.ft.mapp.widgets.quicksidebar.QuickSideBarTipsView;
import com.ft.mapp.widgets.quicksidebar.QuickSideBarView;
import com.ft.mapp.widgets.quicksidebar.listener.OnQuickSideBarTouchListener;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.util.UserSharePref;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.shape.RectLightShape;

public class ListAppFragment extends VFragment<ListAppContract.ListAppPresenter>
        implements ListAppContract.ListAppView, OnQuickSideBarTouchListener {
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private CloneAppListAdapter mAdapter;
    private SearchAppListAdapter mSearchAdapter;
    private QuickSideBarView mQuickSideBarView;
    private QuickSideBarTipsView mQuickSideBarTipsView;
    private RecyclerView.SmoothScroller mSmoothScroller;
    private LinkedHashMap<String, Integer> mLetters = new LinkedHashMap<>();
//    private TTAdNative mTTAdNative;
//    private TTRewardVideoAd currentAd;
    private boolean mReward;
    private List<AppInfo> infoList;
    private RecyclerView searchRecyclerView;
    private ImageView ivSearchClose;
    private EditText etSearch;
    private ProgressDialog dialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_app, null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mQuickSideBarTipsView = view.findViewById(R.id.quickSideBarTipsView);
        mQuickSideBarView = view.findViewById(R.id.quickSideBarView);
        mQuickSideBarView.setOnQuickSideBarTouchListener(this);
        mQuickSideBarView.setVisibility(View.INVISIBLE);

        ivSearchClose = view.findViewById(R.id.select_app_iv_search_close);
        etSearch = view.findViewById(R.id.select_app_et_search);
        ivSearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etSearch.setText("");
                etSearch.clearFocus();
                searchRecyclerView.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                ivSearchClose.setImageResource(R.drawable.icon_search);
                hideInputKeyboard(etSearch);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                ivSearchClose.setImageResource(R.drawable.icon_clear_text);
                String newText = s.toString();
                if (!TextUtils.isEmpty(newText)) {
                    showResult(newText);
                }else{
                    ivSearchClose.setImageResource(R.drawable.icon_search);
                    searchRecyclerView.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });

//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                searchRecyclerView.setVisibility(View.GONE);
//                mRecyclerView.setVisibility(View.VISIBLE);
//                return false;
//            }
//        });

        searchRecyclerView = view.findViewById(R.id.select_app_search_recycler_view);
        mRecyclerView = view.findViewById(R.id.select_app_recycler_view);
        mProgressBar = view.findViewById(R.id.select_app_progress_bar);
        LinearLayoutManager
                layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(new ColorDrawable(Color.GRAY));
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        LinearLayoutManager
                layoutManager2 = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        searchRecyclerView.setLayoutManager(layoutManager2);
        searchRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new CloneAppListAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mSearchAdapter = new SearchAppListAdapter(getActivity());
        searchRecyclerView.setAdapter(mSearchAdapter);

        mSearchAdapter.setOnItemClickListener((info, position) -> {
            checkAddLimit(() -> {
                ArrayList<AppInfoLite> dataList = new ArrayList<>(1);
                dataList.add(new AppInfoLite(info.packageName, info.path, info.fastOpen));
                Intent data = new Intent();
                data.putParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST, dataList);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            });
        });

        mAdapter.setOnItemClickListener((info, position) -> {
            checkAddLimit(() -> {
                ArrayList<AppInfoLite> dataList = new ArrayList<>(1);
                dataList.add(new AppInfoLite(info.packageName, info.path, info.fastOpen));
                Intent data = new Intent();
                data.putParcelableArrayListExtra(VCommends.EXTRA_APP_INFO_LIST, dataList);
                getActivity().setResult(Activity.RESULT_OK, data);
                getActivity().finish();
            });
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mRecyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                try {
                    LinearLayoutManager l =
                            (LinearLayoutManager) mRecyclerView.getLayoutManager();
                    int position = l.findFirstVisibleItemPosition();
                    AppInfo info = mAdapter.getItem(position);
                    if (info.firstLetter.equals("#")) {
                        mQuickSideBarView.setChooseLetter(26);
                    } else {
                        int letterIndex = mQuickSideBarView.getLetters().indexOf(info.firstLetter);
                        mQuickSideBarView.setChooseLetter(letterIndex);
                    }
                } catch (Throwable ignore) {
                    //
                }
            });
        }
        new ListAppPresenterImpl(getActivity(), this).start();

        mSmoothScroller = new LinearSmoothScroller(getContext()) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return super.calculateSpeedPerPixel(displayMetrics) / 5;
            }
        };
//        mTTAdNative = TTAdManagerHolder.get().createAdNative(requireActivity());

//        loadRewardAd();
    }

    private void showResult(String newText) {
        if (infoList == null) {
            return;
        }
        List<AppInfo> searchApps = new ArrayList<>();
        for (AppInfo appInfo : infoList) {
            if (appInfo==null||TextUtils.isEmpty(appInfo.name)) {
                continue;
            }
            if (appInfo.name.toString().contains(newText)) {
                searchApps.add(appInfo);
            }
        }
        mSearchAdapter.setList(searchApps);
        searchRecyclerView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void startLoading() {
        dialog = new ProgressDialog(requireContext());
        dialog.setCancelable(false);
        dialog.show();
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
    }

    @Override
    public void loadFinish(List<AppInfo> infoList) {
        mLetters.clear();
        List<String> list = new ArrayList<>();
        int position = 0;
        for (AppInfo appInfo : infoList) {
            String letter = appInfo.firstLetter;
            if (!mLetters.containsKey(letter)) {
                mLetters.put(letter, position);
                list.add(letter);
            }
            position++;
        }

        mQuickSideBarView.setLetters(list);
        mQuickSideBarView.setVisibility(View.VISIBLE);

        this.infoList = infoList;
        mAdapter.setList(infoList, mLetters);

        if (dialog!=null){
            dialog.cancel();
        }
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        showAddTips();
    }

    private HighLight highLight;

    private void showAddTips() {
        if (UserSharePref.getInstance(VApp.getApp()).getBoolean(UserSharePref.KEY_GUIDE_ADD_APP_FROM_LIST)) {
            return;
        }
        if (isDetached()){
            return;
        }
        if (getActivity()==null){
            return;
        }
        highLight = new HighLight(getActivity())
                .setOnLayoutCallback(() -> {
                    highLight.addHighLight(mAdapter.getFirstItemView().findViewById(R.id.btn_add), R.layout.layout_tips_list_clone, (rightMargin, bottomMargin, rectF, marginInfo) -> {
                        marginInfo.rightMargin = 0f;
                        marginInfo.topMargin = rectF.bottom - rectF.height() / 2;
                    }, new RectLightShape());
                    highLight.show();
                }).setOnRemoveCallback(() -> UserSharePref.getInstance(VApp.getApp()).putBoolean(UserSharePref.KEY_GUIDE_ADD_APP_FROM_LIST, true));

    }

    @Override
    public void setPresenter(ListAppContract.ListAppPresenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onLetterChanged(String letter, int position, float y) {
        mQuickSideBarTipsView.setText(letter, position, y);
        if (mLetters.containsKey(letter)) {
            if (mAdapter.getLetterIndex() != null) {
//                mSmoothScroller.setTargetPosition(mAdapter.getLetterIndex().get(letter));
//                mRecyclerView.getLayoutManager().startSmoothScroll(mSmoothScroller);
                LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
                layoutManager.scrollToPositionWithOffset(mAdapter.getLetterIndex().get(letter), 0);
            }

        }
    }

    @Override
    public void onLetterTouching(boolean touching) {
        if (touching) {
            mQuickSideBarTipsView.setVisibility(View.VISIBLE);
        } else {
            mQuickSideBarTipsView.setVisibility(View.INVISIBLE);
        }
    }

    private Runnable limitRunnable;

    private void checkAddLimit(Runnable runnable) {
        if (UserAgent.getInstance(requireContext()).isVipUser() || LaunchFragment.appList.size() < 4) {
            runnable.run();
            return;
        }
        //非会员
        limitRunnable = runnable;
        VipTipsDialog vipTipsDialog = new VipTipsDialog(requireActivity(), VipFunctionUtils.FUNCTION_ADD_LIMIT);
        vipTipsDialog.setOnVipAdListener(this::showAd);
        vipTipsDialog.show();
    }

    private void showAd() {
//        if (currentAd != null) {
//            currentAd.showRewardVideoAd(requireActivity());
//            currentAd = null;
//        } else {
//            ToastUtil.show(requireActivity(), "广告还没准备好，请稍候再试");
//            loadRewardAd();
//        }
        new AdHelper(requireActivity()).showVipRewardAd(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                ToastUtil.show(requireActivity(), "广告还没准备好，请稍候再试");
            }

            @Override
            public void onTimeout() {

            }

            @Override
            public void onAdClicked() {

            }

            @Override
            public void onAdShow() {

            }

            @Override
            public void onRewardVerify() {
                mReward = true;
            }

            @Override
            public void onAdSkip() {

            }

            @Override
            public void onAdClose() {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mReward && limitRunnable != null) {
            limitRunnable.run();
            mReward = false;
            VipFunctionUtils.markFunction(VipFunctionUtils.FUNCTION_ADD_LIMIT);
        }
    }

    /**
     * 隐藏键盘
     * 弹窗弹出的时候把键盘隐藏掉
     */
    protected void hideInputKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}
