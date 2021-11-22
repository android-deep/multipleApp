package com.ft.mapp.dialog;

import android.content.Context;
import android.content.pm.ShortcutManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ft.mapp.home.models.AppData;
import com.ft.mapp.utils.CommonUtil;
import com.ft.mapp.utils.ToastUtil;
import com.xqb.user.net.engine.StatAgent;
import com.ft.mapp.R;
import com.ft.mapp.utils.AppSharePref;
import com.fun.vbox.client.core.VCore;
import com.xqb.user.util.UmengStat;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.content.ContextCompat;

public class ShortcutDialog extends AppCompatDialog implements View.OnClickListener {
    private ImageView mCloseIv;
    private TextView mOkTv;
    private Context mContext;
    private EditText mEditText;
    private int mUserId;
    private String mPkg;
    private AppData appData;

    public ShortcutDialog(Context context, int userId, String packagename, AppData appData) {
        super(context, R.style.VBDialogTheme);
        setContentView(R.layout.layout_custom_shortcut);
        this.mContext = context;
        this.mUserId = userId;
        this.mPkg = packagename;
        this.appData = appData;

        initView();
        initData();

        if (getWindow() != null) {
            getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        setCanceledOnTouchOutside(false);
    }

    private void initData() {
        String name = CommonUtil.getAppFakeName(appData);
        if (mUserId != 0) {
            int nameIndex = mUserId + 1;
            String shortCutName = name + "(" + nameIndex + ")";
            mEditText.setText(shortCutName);
        } else {
            mEditText.setText(name);
        }
        mEditText.setSelection(mEditText.getText().toString().trim().length());
    }

    private void initView() {
        mCloseIv = findViewById(R.id.native_close_iv);
        mOkTv = findViewById(R.id.native_ok);
        mCloseIv.setOnClickListener(this);
        mOkTv.setOnClickListener(this);
        mEditText = findViewById(R.id.cut_name_et);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.native_close_iv) {
            dismiss();
        } else if (v.getId() == R.id.native_ok) {
            if (TextUtils.isEmpty(mEditText.getText().toString().trim())){
                ToastUtil.show(getContext(),"app名不能为空");
                return;
            }
            handleCreateShortcutInner();
        }
    }

    private void handleCreateShortcutInner() {
        if (!AppSharePref.getInstance(mContext).getBoolean("shortcut_guide")) {
            new ShortcutPermissionDialog(mContext).show();
        }
        VCore.OnEmitShortcutListener listener = new VCore.OnEmitShortcutListener() {
            @Override
            public Bitmap getIcon(Bitmap originIcon) {
                Bitmap iconBitmap = getIconBitmap(getContext(), CommonUtil.getAppFakeIcon(appData));
                return iconBitmap==null?originIcon:iconBitmap;
            }

            @Override
            public String getName(String originName) {
                return mEditText.getText().toString().trim();
            }
        };
        VCore.get().createShortcut(mUserId, mPkg, listener);
        dismiss();
        StatAgent.onEvent(mContext, UmengStat.PLUGIN_CLICK, "name",
                mContext.getString(R.string.create_shortcut));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ShortcutManager shortcutManager = (ShortcutManager) getContext().getSystemService(Context.SHORTCUT_SERVICE);
            if (shortcutManager == null || !shortcutManager.isRequestPinShortcutSupported()) {
                new ShortcutPermissionDialog(mContext).show();
            }
        }

//        boolean showShortcutGuide = AppSharePref.getInstance(mContext).getBoolean("shortcut_guide",
//                true);
//        if (showShortcutGuide) {
//            new ShortcutPermissionDialog(mContext).show();
//            AppSharePref.getInstance(mContext).putBoolean("shortcut_guide",
//                    false);
//        }
    }
    public static Bitmap getIconBitmap(Context context, Drawable icon) {
        try {
            if (icon == null) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && icon instanceof AdaptiveIconDrawable) {
                Bitmap bitmap = Bitmap.createBitmap(icon.getIntrinsicWidth(), icon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                icon.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                icon.draw(canvas);
                return bitmap;
            } else {
                return ((BitmapDrawable) icon).getBitmap();
            }
        } catch (Exception e) {
            return null;
        }
    }

}
