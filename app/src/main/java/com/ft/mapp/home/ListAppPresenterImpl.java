package com.ft.mapp.home;

import android.app.Activity;

import com.ft.mapp.home.models.AppInfo;
import com.ft.mapp.home.repo.AppDataSource;
import com.ft.mapp.home.repo.AppRepository;

import org.jdeferred2.android.AndroidExecutionScope;
import org.jdeferred2.android.AndroidFailCallback;

import java.util.List;

/**
 *
 */
class ListAppPresenterImpl implements ListAppContract.ListAppPresenter {

    private Activity mActivity;
    private ListAppContract.ListAppView mView;
    private AppDataSource mRepository;

    ListAppPresenterImpl(Activity activity, ListAppContract.ListAppView view) {
        mActivity = activity;
        mView = view;
        mRepository = new AppRepository(activity);
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        mView.setPresenter(this);
        mView.startLoading();
        mRepository.getInstalledApps(mActivity).done((List<AppInfo> infoList) -> {
            if (mView != null && mActivity != null) {
                mView.loadFinish(infoList);
            }
        })
                .fail(new AndroidFailCallback<Throwable>() {
                    @Override
                    public void onFail(Throwable result) {
                        result.getLocalizedMessage();
                    }

                    @Override
                    public AndroidExecutionScope getExecutionScope() {
                        return null;
                    }
                });
    }
}
