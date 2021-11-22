package com.ft.mapp.home.location;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapCustomStyleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiDetailInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.ft.mapp.R;
import com.ft.mapp.VApp;
import com.ft.mapp.VCommends;
import com.ft.mapp.abs.ui.VActivity;
import com.ft.mapp.ad.AdHelper;
import com.ft.mapp.ad.base.RewardAdListener;
import com.ft.mapp.dialog.VipTipsDialog;
import com.ft.mapp.home.models.LocationData;
import com.ft.mapp.utils.AppSharePref;
import com.ft.mapp.utils.GPSUtil;
import com.ft.mapp.utils.ToastUtil;
import com.ft.mapp.utils.VipFunctionUtils;
import com.ft.mapp.widgets.fittext.FitTextView;
import com.fun.vbox.client.core.VCore;
import com.fun.vbox.client.ipc.VLocationManager;
import com.fun.vbox.client.ipc.VirtualLocationManager;
import com.fun.vbox.remote.vloc.VLocation;
import com.ipaynow.plugin.log.LogUtils;
import com.jaeger.library.StatusBarUtil;
import com.xqb.user.net.engine.UserAgent;

import java.util.ArrayList;
import java.util.List;

public class ChooseBDLocationActivity extends VActivity
        implements View.OnClickListener {

    public static final int REQUEST_CODE = 1001;
    public static final String LOCATION_DATA = "LOCATION_DATA";
    public static final String LOCATION_ADDRESS = "LOCATION_ADDRESS";

    private MapView mapView;
    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private ListView mSearchResult;
    private View mSearchLayout;
    private ArrayAdapter<MapSearchResult> mSearchAdapter;
    private String mCurPkg;
    private int mCurUserId;
    private LocationData mSelectData;
    private LatLng mLastLatLng;
    private FitTextView mLocationTv;
    //    private TTAdNative ttAdNative;
//    private TTRewardVideoAd currentAd;
    private boolean mReward;
    private BaiduMap mAMap;
    private LocationClient mLocationClient;
    private SuggestionSearch mSuggestionSearch;
    private GeoCoder geoCoder;
//    private TTAdNative.RewardVideoAdListener rewardVideoAdListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setTranslucent(this, 0);
        setResult(Activity.RESULT_CANCELED);
        setContentView(R.layout.activity_mock_bd_location);
        Toolbar toolbar = findViewById(R.id.task_top_toolbar);
        toolbar.setTitle(R.string.plugin_location);
        setSupportActionBar(toolbar);
        enableBackHome();

        mapView = findViewById(R.id.mapview);
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
            //定位初始化
            mLocationClient = new LocationClient(this);

            //通过LocationClientOption设置LocationClient相关参数
            LocationClientOption option = new LocationClientOption();
            option.setOpenGps(true); // 打开gps
            option.setCoorType("gcj02"); // 设置坐标类型
            option.setScanSpan(1000);

            //设置locationClientOption
            mLocationClient.setLocOption(option);

            //注册LocationListener监听器
//            mAMap.setMyLocationEnabled(true);
            mLocationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {
                    //mapView 销毁后不在处理新接收的位置
                    if (location == null || mapView == null) {
                        return;
                    }
                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(location.getDirection()).latitude(location.getLatitude())
                            .longitude(location.getLongitude()).build();
//                    mAMap.setMyLocationData(locData);
                    gotoLocation(location.getLatitude(), location.getLongitude());
                    mLocationClient.stop();
//                    mAMap.setMyLocationEnabled(false);
                }
            });
            //开启地图定位图层
            mLocationClient.start();
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
        MapStatusUpdate zoomIn = MapStatusUpdateFactory.zoomIn();
        mAMap.setMapStatus(zoomIn);
        mAMap.setMyLocationEnabled(true);
        mAMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng location) {
                if (mLastLatLng != null
                        && Math.abs(mLastLatLng.latitude - location.latitude) < 0.00001
                        && Math.abs(mLastLatLng.longitude - location.longitude) < 0.00001) {
                    return;
                }
                gotoLocation(location.latitude, location.longitude);
                mLastLatLng = location;
            }

            @Override
            public void onMapPoiClick(MapPoi mapPoi) {

            }
        });
        mSuggestionSearch = SuggestionSearch.newInstance();
        geoCoder = GeoCoder.newInstance();
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
        mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
                    return;
                }
                List<SuggestionResult.SuggestionInfo> poiItems = suggestionResult.getAllSuggestions();// 取得第一页的poiitem数据，页数从数字0开始
                if (poiItems != null && poiItems.size() > 0) {
                    mAMap.clear();// 清理之前的图标
                    mSearchAdapter.clear();

                    for (SuggestionResult.SuggestionInfo item : poiItems) {
                        if (item.getPt() == null) {
                            continue;
                        }
                        MapSearchResult result = new MapSearchResult(item.getKey(),
                                item.getPt().latitude,
                                item.getPt().longitude);
                        result.setCity(item.getCity());
                        mSearchAdapter.add(result);
                    }

//                    MapSearchResult noMoreResult = new MapSearchResult();
//                    noMoreResult.setAddress("没有更多了");
//                    mSearchAdapter.add(noMoreResult);
                    mSearchAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(ChooseBDLocationActivity.this,
                            R.string.no_result);
                }
            }
        });
        mSuggestionSearch.requestSuggestion(new SuggestionSearchOption()
                .citylimit(false)
                .city("北京")
                .keyword(keyword));
    }

    private void gotoLocation(double lat, double lng) {
        LatLng location = new LatLng(lat, lng);
        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(location);
        MapStatusUpdate zoom = MapStatusUpdateFactory.zoomTo(16);
        mAMap.setMapStatus(status1);
        mAMap.setMapStatus(zoom);
        mAMap.clear();
        addMarker(location);//添加定位图标
        mSelectData.location.latitude = lat;
        mSelectData.location.longitude = lng;
        updateAddress();
    }

    private void updateAddress() {
        geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                if (reverseGeoCodeResult != null && reverseGeoCodeResult.getPoiList() != null && reverseGeoCodeResult.getPoiList().size() > 0) {
                    PoiInfo poiInfo = reverseGeoCodeResult.getPoiList().get(0);
                    String formatAddress = poiInfo.getAddress();
                    String buildingName = poiInfo.getName();
                    AppSharePref.getInstance(VApp.getApp()).putString("my_address", formatAddress);
                    mLocationTv.setText(String.format("%s%s", formatAddress, buildingName));
                }
            }
        });
        geoCoder.reverseGeoCode(
                new ReverseGeoCodeOption().location(
                        new LatLng(mSelectData.location.latitude, mSelectData.location.longitude)));

    }

    private void addMarker(LatLng point) {
        //定义Maker坐标点
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.navi_map_gps_locked);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .draggable(true)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mAMap.addOverlay(option);
//        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
//                R.drawable.navi_map_gps_locked);
//        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);
//        MarkerOptions options = new MarkerOptions();
//        options.icon(des);
//        options.anchor(0.5f, 0.5f);
//        options.position(latlng);
//        mLocMarker = mAMap.addMarker(options);
    }

    private void clearMarker() {
        mAMap.clear();
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
                ToastUtil.show(ChooseBDLocationActivity.this, "广告还没准备好，请稍候再试");
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
        mAMap.setMyLocationEnabled(false);
        mapView.onDestroy();
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
        geoCoder.destroy();
        mapView = null;
    }

}
