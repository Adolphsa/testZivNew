package com.yeejay.yplay.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.greendao.ContactsInfo;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.service.ContactsService;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.Arrays;
import java.util.List;

public class LoginAuthorization extends BaseActivity {

    private static final String TAG = "LoginAuthorization";

    private static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 100;
    private static final int REQUEST_CODE_PERMISSION_SINGLE_CONTACTS = 101;

    Button mBtnGetAddressAuthority;
    Button mBtnGetNumberBookAuthority;
    boolean addressAuthoritySuccess = false;
    boolean numberBookAuthoritySuccess = false;
    boolean locationServiceSuccess = false;
    boolean isFirstShowDialog = true;

    public LocationClient mLocationClient = null;
    private double latitude;
    private double longitude;

    Location mLocation;
    LocationManager mLocationManager;

    ContactsInfoDao contactsInfoDao;

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            Log.i(TAG, "onSucceed: requestCode---" + requestCode);
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    Log.i(TAG, "onSucceed: getLonlat");
                    getLonLat();
                    if (locationServiceSuccess){
                        if (addressAuthoritySuccess) {
                            Log.i(TAG, "onSucceed: 地理位置有权限");

                        } else {
                            Log.i(TAG, "onSucceed: 地理位置无权限");
                            AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
                        }
                    }
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    getContacts();
                    if (numberBookAuthoritySuccess) {
                        Log.i(TAG, "onSucceed: 通讯录有权限");
                    } else {
                        Log.i(TAG, "onSucceed: 通讯录无权限");
                        AndPermission.defaultSettingDialog(LoginAuthorization.this, 401).show();
                    }
                    break;
            }
            //授权成功跳转
            authorizationSuccess();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            Log.i(TAG, "onFailed: requestCode---" + requestCode);
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    System.out.println("回调失败的地理位置权限申请失败");
                    getLonLat();
                    if (locationServiceSuccess){
                        if (addressAuthoritySuccess) {
                            Log.i(TAG, "onFailed: 读到地理位置权限了addressAuthoritySuccess---" + addressAuthoritySuccess);
                        } else {
                            if (AndPermission.hasAlwaysDeniedPermission(LoginAuthorization.this, deniedPermissions)) {

                                AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
                            }
                        }
                    }
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    System.out.println("回调失败的通讯录权限申请失败");
                    getContacts();
                    if (numberBookAuthoritySuccess) {
                        Log.i(TAG, "onFailed: 读到通讯录权限了numberBookAuthoritySuccess---" + numberBookAuthoritySuccess);
                    } else {
                        if (AndPermission.hasAlwaysDeniedPermission(LoginAuthorization.this, deniedPermissions)) {

                            AndPermission.defaultSettingDialog(LoginAuthorization.this, 401).show();

                        }
                    }
                    break;
            }

            authorizationSuccess();

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 400: { // 这个400就是上面defineSettingDialog()的第二个参数。
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                System.out.println("回调了");
                if (!addressAuthoritySuccess) {
                    getLonLat();
                }
                System.out.println("回调addressAuthoritySuccess---" + addressAuthoritySuccess);
                break;
            }
            case 401:
                if (!numberBookAuthoritySuccess) {
                    getContacts();
                }
                System.out.println("回调numberBookAuthoritySuccess---" + numberBookAuthoritySuccess);
                break;
            case 402:
                getLonLat();
                break;
        }
        authorizationSuccess();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorization);

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_title_text_color));

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mLocationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类

        //注册监听函数

        contactsInfoDao = YplayApplication.getInstance().getDaoSession().getContactsInfoDao();

        mBtnGetAddressAuthority = (Button) findViewById(R.id.laz_btn_address);
        mBtnGetNumberBookAuthority = (Button) findViewById(R.id.laz_btn_address_book);
        ImageButton back = (ImageButton) findViewById(R.id.laz_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnGetAddressAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAddressAuthority();
                System.out.println("获取地理位置被点击");
            }
        });
        mBtnGetNumberBookAuthority.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("获取通讯录被点击");
                getNumberBookAuthority();
            }
        });
    }

    //获取地址位置权限
    private void getAddressAuthority() {

        AndPermission.with(LoginAuthorization.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_LOCATION)
                .permission(Permission.LOCATION)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(LoginAuthorization.this, rationale).show();
                    }
                })
                .start();
        Log.i(TAG, "getAddressAuthority: 申请地理位置");
    }

    //获取通讯录权限
    private void getNumberBookAuthority() {

        AndPermission.with(LoginAuthorization.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_CONTACTS)
                .permission(Permission.CONTACTS)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(LoginAuthorization.this, rationale).show();
                    }
                })
                .start();
    }

    //获取当前经纬度
    private void getLonLat() {

        Log.i(TAG, "getLonLat: mLocationManager---" + mLocationManager);
        Log.i(TAG, "GPS是否打开 " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.i(TAG, "网络定位是否打开 " + mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        // 判断GPS模块是否开启，如果没有则开启
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

            isFirstShowDialog = true;

            Log.i(TAG, "getLonLat: 位置服务已开启");
            LocationClientOption option = new LocationClientOption();

            option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            option.setCoorType("bd09ll");
            option.setScanSpan(1000);
            option.setOpenGps(true);
            option.setLocationNotify(true);
            option.setIgnoreKillProcess(true);
            option.SetIgnoreCacheException(false);
            option.setWifiCacheTimeOut(5*60*1000);
            option.setEnableSimulateGps(false);

            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(bdListener);
            mLocationClient.start();

//            String mProvider = judgeProvider(mLocationManager);
//            if (Build.VERSION.SDK_INT >= 23 &&
//                    LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                    LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
//            if (mProvider == null) {    //位置提供者为空
//                Log.i(TAG, "getLonLat: 位置提供者为空");
//                return;
//            }
//
//            locationServiceSuccess = true;
//            Log.i(TAG, "getLonLat: locationServiceSuccess---" + locationServiceSuccess);
//            mLocationManager.requestLocationUpdates(mProvider, 0, 0, locationListener);
//            mLocation = mLocationManager.getLastKnownLocation(mProvider);
//
//            if (mLocation != null) {
//                Log.i(TAG, "getLonLat: 地位位置已读到数据");
//                addressAuthoritySuccess = true;
//
//                setLocationBackground();
//
//                System.out.println("当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
//            }else {
//                Log.i(TAG, "getLonLat: mLocation ---" + mLocation);
//            }

        }else {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("请开启位置服务");
            dialog.setPositiveButton("确定",
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            arg0.dismiss();
                            // 转到手机设置界面，用户设置GPS
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 402); // 设置完成后返回到原来的界面

                        }
                    });
            dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            } );
            dialog.show();
        }
    }

//    LocationListener locationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            Log.i(TAG, "onLocationChanged: lat---" + location.getLatitude() +
//                    "lon---" + location.getLongitude());
//            if (location.getLatitude() != 0 && location.getLongitude() != 0){
//                addressAuthoritySuccess = true;
//                mLocation = location;
//                setLocationBackground();
//            }
//        }
//
//        @Override
//        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String provider) {
//            Log.i(TAG, "onProviderEnabled: provider---" + provider);
//        }
//
//        @Override
//        public void onProviderDisabled(String provider) {
//            Log.i(TAG, "onProviderDisabled: provider---" + provider);
//        }
//    };

    BDAbstractLocationListener bdListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int errorCode = bdLocation.getLocType();
            Log.i(TAG, "onReceiveLocation: errorCode---" + errorCode);
            if (errorCode == 61 || errorCode == 161){
                //获取纬度信息
                latitude = bdLocation.getLatitude();
                //获取经度信息
                longitude = bdLocation.getLongitude();

                Log.i(TAG, "onReceiveLocation: 百度地图---lat---" + latitude + ",lon---" + longitude);
                addressAuthoritySuccess = true;
                setLocationBackground();
                authorizationSuccess();
            }else if (errorCode == 62){
                if (AndPermission.hasAlwaysDeniedPermission(LoginAuthorization.this, Arrays.asList(Permission.LOCATION))) {
                    if (isFirstShowDialog){
                        isFirstShowDialog = false;
                        AndPermission.defaultSettingDialog(LoginAuthorization.this, 401).show();
                    }
                }
            }


        }
    };


    //判断是否有可用的内容提供者
    private String judgeProvider(LocationManager locationManager) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);//低精度，如果设置为高精度，依然获取不了location。
        criteria.setAltitudeRequired(false);//不要求海拔
        criteria.setBearingRequired(false);//不要求方位
        criteria.setCostAllowed(true);//允许有花费
        criteria.setPowerRequirement(Criteria.POWER_LOW);

        return locationManager.getBestProvider(criteria,true);

//        List<String> prodiverlist = locationManager.getProviders(true);
//        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
//            return LocationManager.NETWORK_PROVIDER;
//        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
//            return LocationManager.GPS_PROVIDER;
//        } else {
//            Toast.makeText(LoginAuthorization.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
//        }
//        return null;
    }

    //获取通讯录联系人
    private void getContacts() {

        if (Build.VERSION.SDK_INT >= 23
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            System.out.println("contactUri---" + contactUri);
            Cursor cursor = getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id", "data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            //String contactSortKey;
            //int contactId;
            while (cursor != null && cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                //contactSortKey =getSortkey(cursor.getString(1));
                com.yeejay.yplay.greendao.ContactsInfo contactsInfo = new com.yeejay.yplay.greendao.ContactsInfo(null, contactName, contactNumber);
                contactsInfoDao.insert(contactsInfo);
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题
            List<ContactsInfo> tempList = contactsInfoDao.loadAll();
            if (tempList != null && tempList.size() > 0) {
                numberBookAuthoritySuccess = true;
                setContactBackground();
            }

            //开启服务上传通讯录
            startService(new Intent(LoginAuthorization.this, ContactsService.class));

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    //授权成功
    private void authorizationSuccess() {
        if ((addressAuthoritySuccess && numberBookAuthoritySuccess)) {

            Intent intent = new Intent(LoginAuthorization.this, ClassList.class);
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE, latitude);
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE, longitude);

//            SharePreferenceUtil.put(LoginAuthorization.this, YPlayConstant.YPLAY_LATITUDE, String.valueOf(mLocation.getLatitude()));
//            SharePreferenceUtil.put(LoginAuthorization.this, YPlayConstant.YPLAY_LONGITUDE, String.valueOf(mLocation.getLongitude()));
            //上传通讯录（后续，有风险。应该改为起一个服务，然后在服务中上传）

            startActivity(intent);
        }
    }

    //设置通讯录的背景
    private void setContactBackground() {
        Drawable nav_up = getResources().getDrawable(R.drawable.contacts_yes);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mBtnGetNumberBookAuthority.setCompoundDrawables(null, nav_up, null, null);
        //mBtnGetNumberBookAuthority.setTextColor(getResources().getColor(R.color.edit_text_color3));
        mBtnGetNumberBookAuthority.setEnabled(false);
    }

    //设置地理位置背景
    private void setLocationBackground() {
        Drawable nav_up = getResources().getDrawable(R.drawable.location_yes);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mBtnGetAddressAuthority.setCompoundDrawables(null, nav_up, null, null);
        //mBtnGetAddressAuthority.setTextColor(getResources().getColor(R.color.edit_text_color3));
        mBtnGetAddressAuthority.setEnabled(false);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            return true;//不执行父类点击事件
        return super.onKeyDown(keyCode, event);//继续执行父类其他点击事件
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null){
            mLocationClient.unRegisterLocationListener(bdListener);
            mLocationClient.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
