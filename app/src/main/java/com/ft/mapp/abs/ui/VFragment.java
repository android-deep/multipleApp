package com.ft.mapp.abs.ui;


import android.app.Activity;

import com.ft.mapp.abs.BasePresenter;
import com.xqb.user.net.engine.VUiKit;

import org.jdeferred2.android.AndroidDeferredManager;

import androidx.fragment.app.Fragment;

/**
 *
 */
public class VFragment<T extends BasePresenter> extends Fragment {

	protected T mPresenter;

	public T getPresenter() {
		return mPresenter;
	}

	public void setPresenter(T presenter) {
		this.mPresenter = presenter;
	}

	protected AndroidDeferredManager defer() {
		return VUiKit.defer();
	}

	public void finishActivity() {
		Activity activity = getActivity();
		if (activity != null) {
			activity.finish();
		}
	}

	public void destroy() {
		finishActivity();
	}
}
