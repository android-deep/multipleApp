package com.ft.mapp.home.location;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTRewardVideoAd;
import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.VCommends;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.ad.ttads.TTAdManagerHolder;
import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.home.device.DeviceDetailActivity;
import com.ft.mapp.home.models.FakeAppInfo;
import com.ft.mapp.home.models.LocationData;
import com.ft.mapp.home.models.MultiplePackageAppData;
import com.ft.mapp.home.models.PackageAppData;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.FakeAppUtils;
import com.ft.mapp.utils.GPSUtil;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.VipFunctionUtils;
import com.ft.mapp.widgets.fittext.FitTextView;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VLocationManager;
import com.fun.vbox.client.ipc.VirtualLocationManager;
import com.fun.vbox.remote.vloc.VLocation;
import com.jaeger.library.StatusBarUtil;
import com.luck.picture.lib.tools.SPUtils;
import com.xqb.user.net.engine.StatAgent;
import com.xqb.user.net.engine.UserAgent;
import com.xqb.user.util.UmengStat;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import jonathanfinerty.once.Once;

public class ChooseLocationActivity extends VActivity
        implements PoiSearch.OnPoiSearchListener, AMap.OnMyLocationChangeListener,
        View.OnClickListener {

    public static final int REQUEST_CODE = 1001;
    public static final String LOCATION_DATA = "LOCATION_DATA";
    public static final String LOCATION_ADDRESS = "LOCATION_ADDRESS";

    private AMap mAMap;
    private MapView mapView;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private ListView mSearchResult;
    private View mSearchLayout;
    private ArrayAdapter<MapSearchResult> mSearchAdapter;
    private String mCurPkg;
    private int mCurUserId;
    private LocationData mSelectData;
    private Marker mLocMarker;
    private Circle mCircle;
    private LatLng mLastLatLng;
    private GeocodeSearch mGeocoderSearch;
    private FitTextView mLocationTv;
//    private TTAdNative ttAdNative;
//    private TTRewardVideoAd currentAd;
    private boolean mReward;
//    private TTAdNative.RewardVideoAdListener rewardVideoAdListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);
        setResult(Activity.RESULT_CANCELED);
        setContentView(R.layout.activity_mock_location);
        Toolbar toolbar = findViewById(R.id.task_top_toolbar);
        toolbar.setTitle(R.string.plugin_location);
        setSupportActionBar(toolbar);
        enableBackHome();

        mapView = findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        initView();
        initMap();
        initData();
//        ttAdNative = TTAdManagerHolder.get().createAdNative(this);
//        initAd();
    }

    private void initView() {
        mSearchResult = bind(R.id.search_results);
        mSearchLayout = bind(R.id.search_layout);
        mSearchAdapter =
                new ArrayAdapter<>(this, R.layout.simple_list_item_dark, new ArrayList<>());
        mSearchResult.setAdapter(mSearchAdapter);
        MapSearchResult.NULL.setAddress(getString(R.string.tip_no_find_points));
        mSearchResult.setOnItemClickListener((adapterView, view, i, l) -> {
            MapSearchResult searchResult = mSearchAdapter.getItem(i);
            if (searchResult != null && searchResult.address.equals("没有更多了")) {
                return;
            }
            if (searchResult != MapSearchResult.NULL) {
                clearMarker(); //这里要清空一下
                mSearchMenuItem.collapseActionView();
                gotoLocation(searchResult.lat, searchResult.lng);
            }
        });

        findViewById(R.id.mock_iv).setOnClickListener(this);
        findViewById(R.id.search_iv).setOnClickListener(this);
        mLocationTv = findViewById(R.id.tv_address);
    }

    private void initData() {
        //data
        mCurPkg = getIntent().getStringExtra(VCommends.EXTRA_PACKAGE);
        mCurUserId = getIntent().getIntExtra(VCommends.EXTRA_USERID, 0);

        mSelectData = new LocationData(mCurPkg, mCurUserId);
        //临时开启虚拟定位
        VirtualLocationManager.get()
                .setMode(mCurUserId, mCurPkg, VirtualLocationManager.MODE_USE_SELF);
        readLocation(mSelectData);

        if (mSelectData.location != null) {
            gotoLocation(mSelectData.location.getLatitude(), mSelectData.location.getLongitude());
        } else {
            mSelectData.location = new VLocation();
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            mAMap.setMyLocationStyle(myLocationStyle);
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            mAMap.setMyLocationEnabled(true);
            mAMap.setOnMyLocationChangeListener(this);
        }
    }

    private void readLocation(LocationData locationData) {
        try {
            locationData.mode = VirtualLocationManager.get()
                    .getMode(locationData.userId, locationData.packageName);
            locationData.location = VLocationManager.get()
                    .getLocation(locationData.packageName, locationData.userId);
            if (locationData.location != null) {
                double[] doubles = GPSUtil.gps84_To_Gcj02(locationData.location.latitude,
                        locationData.location.longitude);
                locationData.location.latitude = doubles[0];
                locationData.location.longitude = doubles[1];
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void initAd() {
//        if (!UserAgent.getInstance(this).isRewardOn()){
//            return;
//        }
//        AdSlot adSlot = new AdSlot.Builder()
//                .setCodeId(TTAdManagerHolder.getVipRewardId())
//                .setSupportDeepLink(true)
//                .setOrientation(TTAdConstant.VERTICAL)
//                .build();
//        rewardVideoAdListener = new TTAdNative.RewardVideoAdListener() {
//            @Override
//            public void onError(int i, String s) {
//
//            }
//
//            @Override
//            public void onRewardVideoAdLoad(TTRewardVideoAd ttRewardVideoAd) {
//                currentAd = ttRewardVideoAd;
//                currentAd.setRewardAdInteractionListener(new TTRewardVideoAd.RewardAdInteractionListener() {
//                    @Override
//                    public void onAdShow() {
//
//                    }
//
//                    @Override
//                    public void onAdVideoBarClick() {
//
//                    }
//
//                    @Override
//                    public void onAdClose() {
//
//                    }
//
//                    @Override
//                    public void onVideoComplete() {
//                        ttAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
//                    }
//
//                    @Override
//                    public void onVideoError() {
//
//                    }
//
//                    @Override
//                    public void onRewardVerify(boolean b, int i, String s, int i1, String s1) {
//                        mReward = b;
//                    }
//
//                    @Override
//                    public void onSkippedVideo() {
//
//                    }
//                });
//            }
//
//            @Override
//            public void onRewardVideoCached() {
//
//            }
//        };
//        ttAdNative.loadRewardVideoAd(adSlot, rewardVideoAdListener);
//    }

    private void saveLocation(LocationData locationData) {
        VCore.get().killApp(locationData.packageName, locationData.userId);
        if (locationData.location == null || locationData.location.isEmpty()) {
            VirtualLocationManager.get().setMode(locationData.userId, locationData.packageName, 0);
        } else if (locationData.mode != 2) {
            VirtualLocationManager.get().setMode(locationData.userId, locationData.packageName, 2);
        }
        VirtualLocationManager.get()
                .setLocation(locationData.userId, locationData.packageName, locationData.location);
    }

    private void initMap() {
        mAMap = mapView.getMap();
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                LatLng location = cameraPosition.target;
                if (mLastLatLng != null
                        && Math.abs(mLastLatLng.latitude - location.latitude) < 0.00001
                        && Math.abs(mLastLatLng.longitude - location.longitude) < 0.00001) {
                    return;
                }
                gotoLocation(location.latitude, location.longitude, cameraPosition.zoom);
                mLastLatLng = location;
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                //这里会一直调用，不能放这里
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        mSearchMenuItem = menuItem;
        mSearchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        mSearchView.setQueryHint(getString(R.string.tip_input_keywords));
        MenuItemCompat
                .setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mSearchLayout.setVisibility(View.VISIBLE);
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mSearchLayout.setVisibility(View.GONE);
                        return true;
                    }
                });
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                if (!TextUtils.isEmpty(newText)) {
                    doSearchQuery(newText);
                } else {
                    mSearchAdapter.clear();
                    mSearchAdapter.notifyDataSetChanged();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery(String keyword) {
        // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        PoiSearch.Query query = new PoiSearch.Query(keyword, "", "");
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);// 设置查第一页

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    private void gotoLocation(double lat, double lng) {
        gotoLocation(lat, lng, 17f);
    }

    private void gotoLocation(double lat, double lng, float zoom) {
        LatLng location = new LatLng(lat, lng);
        mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom));
        if (mCircle == null) {
            mAMap.clear();
            addCircle(location, 40);//添加定位精度圆
            addMarker(location);//添加定位图标
        } else {
            mCircle.setCenter(location);
            mLocMarker.setPosition(location);
        }
        mSelectData.location.latitude = lat;
        mSelectData.location.longitude = lng;
        updateAddress();
    }

    private void updateAddress() {
        if (mGeocoderSearch == null) {
            mGeocoderSearch = new GeocodeSearch(this);
            mGeocoderSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {

                @Override
                public void onGeocodeSearched(GeocodeResult result, int rCode) {
                }

                @Override
                public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                    String formatAddress = result.getRegeocodeAddress().getFormatAddress();
                    AppSharePref.getInstance(VApp.getApp()).putString("my_address", formatAddress);
                    mLocationTv.setText(formatAddress);
                }
            });
        }
        LatLonPoint lp = new LatLonPoint(mSelectData.location.latitude, mSelectData.location.longitude);
        RegeocodeQuery query = new RegeocodeQuery(lp, 200, GeocodeSearch.AMAP);
        mGeocoderSearch.getFromLocationAsyn(query);
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.fillColor(R.color.blue_60);
        options.strokeWidth(1f);
        options.strokeColor(R.color.blue_60);
        options.center(latlng);
        options.radius(radius);
        mCircle = mAMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = mAMap.addMarker(options);
    }

    private void clearMarker() {
        mCircle = null;
        mLocMarker = null;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (poiResult != null && poiResult.getQuery() != null) {// 搜索poi的结果
                // 取得搜索到的poiitems有多少页
                List<PoiItem> poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    mAMap.clear();// 清理之前的图标
                    mSearchAdapter.clear();

                    for (PoiItem item : poiItems) {
                        MapSearchResult result = new MapSearchResult(item.getTitle(),
                                item.getLatLonPoint().getLatitude(),
                                item.getLatLonPoint().getLongitude());
                        result.setCity(item.getCityName());
                        mSearchAdapter.add(result);
                    }

//                    MapSearchResult noMoreResult = new MapSearchResult();
//                    noMoreResult.setAddress("没有更多了");
//                    mSearchAdapter.add(noMoreResult);
                    mSearchAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(ChooseLocationActivity.this,
                            R.string.no_result);
                }
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public void onMyLocationChange(Location location) {
        gotoLocation(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.mock_iv) {
//            VCore.get().killApp(mCurPkg, mCurUserId);
//            VirtualLocationManager.get()
//                    .setMode(mCurUserId, mCurPkg, VirtualLocationManager.MODE_USE_SELF);
            // VCell vCell = new VCell();
            // vCell.mcc = 460;
            // vCell.lac = 4301;
            // vCell.cid = 20986;
            // VirtualLocationManager.get().setCell(mCurUserId, mCurPkg, vCell);
//            VirtualLocationManager.get().setLocation(mCurUserId, mCurPkg, mSelectData.location);
            if (!UserAgent.getInstance(this).isVipUser()) {
                VipTipsDialog vipTipsDialog = new VipTipsDialog(this, VipFunctionUtils.FUNCTION_MOCK_LOCATION);
                vipTipsDialog.setOnVipAdListener(new VipTipsDialog.OnVipAdListener() {
                    @Override
                    public void adListener() {
                        showRewardAd();
                    }
                });
                vipTipsDialog.show();
            } else {
                mockComplete();
            }
        } else if (v.getId() == R.id.search_iv) {
        }
    }

    private void showRewardAd() {
//        if (currentAd != null) {
//            currentAd.showRewardVideoAd(this);
//            currentAd = null;
//        } else {
//            ToastUtil.show(this, "广告还没准备好，请稍候再试");
//            initAd();
//        }
        new AdHelper(this).showRewardAd(new RewardAdListener() {
            @Override
            public void onError(int code, String msg) {
                ToastUtil.show(ChooseLocationActivity.this, "广告还没准备好，请稍候再试");
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

    private void mockComplete() {
        double[] doubles = GPSUtil.gcj02_To_Gps84(mSelectData.location.latitude, mSelectData.location.longitude);
        mSelectData.location.latitude = doubles[0];
        mSelectData.location.longitude = doubles[1];
        VirtualLocationManager.get().setLocation(mCurUserId, mCurPkg, mSelectData.location);
        Intent intent = getIntent();
        intent.putExtra(LOCATION_DATA, mSelectData);
        intent.putExtra(LOCATION_ADDRESS, mLocationTv.getText().toString());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private static class MapSearchResult {
        private String address;
        private double lat;
        private double lng;
        private String city;
        public static final MapSearchResult NULL = new MapSearchResult();

        public MapSearchResult() {
        }

        public MapSearchResult(String address) {
            this.address = address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public MapSearchResult(String address, double lat, double lng) {
            this.address = address;
            this.lat = lat;
            this.lng = lng;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @Override
        public String toString() {
            return address;
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if (mReward) {
            mockComplete();
            mReward = false;
            VipFunctionUtils.markFunction(VipFunctionUtils.FUNCTION_MOCK_LOCATION);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

}
