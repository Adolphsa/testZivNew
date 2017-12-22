package com.yeejay.yplay.login;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
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
import com.yanzhenjie.permission.SettingDialog;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.greendao.ContactsInfoDao;
import com.yeejay.yplay.service.ContactsService;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

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
    boolean isGetLonLat = true;

    public LocationClient mLocationClient = null;
    private double latitude;
    private double longitude;

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
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    getContacts();
                    if (numberBookAuthoritySuccess) {
                        Log.i(TAG, "onSucceed: 通讯录有权限");
                    } else {
                        Log.i(TAG, "onSucceed: 通讯录无权限");
                        SettingDialog settingDialog = AndPermission.defaultSettingDialog(LoginAuthorization.this, 401);
                        settingDialog.setTitle("开启通讯录权限");
                        settingDialog.setMessage("方便您找到正在使用本产品的好友");
                        settingDialog.show();

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
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    System.out.println("回调失败的通讯录权限申请失败");
                    getContacts();
                    if (numberBookAuthoritySuccess) {
                        Log.i(TAG, "onFailed: 读到通讯录权限了numberBookAuthoritySuccess---" + numberBookAuthoritySuccess);
                    } else {
                        if (AndPermission.hasAlwaysDeniedPermission(LoginAuthorization.this, deniedPermissions)) {
                            SettingDialog settingDialog = AndPermission.defaultSettingDialog(LoginAuthorization.this, 401);
                            settingDialog.setTitle("开启通讯录权限");
                            settingDialog.setMessage("方便您找到正在使用本产品的好友");
                            settingDialog.show();
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
            case 401:
                getContacts();
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

        Log.i(TAG, "onCreate: ");
        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_title_text_color));

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mLocationClient = new LocationClient(getApplicationContext());

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
                .start();
        Log.i(TAG, "getAddressAuthority: 申请地理位置");
    }

    //获取通讯录权限
    private void getNumberBookAuthority() {

        AndPermission.with(LoginAuthorization.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_CONTACTS)
                .permission(Permission.CONTACTS)
                .callback(mPermissionListener)
                .start();
    }

    //获取当前经纬度
    private void getLonLat() {

        if(addressAuthoritySuccess){
            Log.i(TAG, "getLonLat: 已经成功过!! " + latitude + longitude);
            return;
        }

        Log.i(TAG, "getLonLat: mLocationManager---" + mLocationManager);
        Log.i(TAG, "GPS是否打开 " + mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        Log.i(TAG, "网络定位是否打开 " + mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));

        // 判断GPS模块是否开启，如果没有则开启
        if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                mLocationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {

            locationServiceSuccess = true;
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
            option.setWifiCacheTimeOut(5 * 60 * 1000);
            option.setEnableSimulateGps(false);

            mLocationClient.setLocOption(option);
            mLocationClient.registerLocationListener(bdListener);
            mLocationClient.start();

        } else {
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
            });
            dialog.show();
        }
    }

    BDAbstractLocationListener bdListener = new BDAbstractLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            int errorCode = bdLocation.getLocType();
            Log.i(TAG, "onReceiveLocation: errorCode---" + errorCode);
            if (errorCode == 61 || errorCode == 161) {

                //获取纬度信息
                latitude = bdLocation.getLatitude();
                //获取经度信息
                longitude = bdLocation.getLongitude();

                SharePreferenceUtil.put(LoginAuthorization.this,"temp_lat",(float)latitude);
                SharePreferenceUtil.put(LoginAuthorization.this,"temp_lon",(float)longitude);
                SharePreferenceUtil.put(LoginAuthorization.this,"temp_location",1);

                Log.i(TAG, "onReceiveLocation: 百度地图---lat---" + latitude + ",lon---" + longitude);
                addressAuthoritySuccess = true;
                setLocationBackground();

                if (isGetLonLat) {
                    isGetLonLat = false;
                    authorizationSuccess();
                }
            } else if (errorCode == 62) {
                if (isFirstShowDialog) {
                    Log.i(TAG, "onReceiveLocation: isFirstShowDialog---" + isFirstShowDialog);
                    isFirstShowDialog = false;
                    SettingDialog settingDialog = AndPermission.defaultSettingDialog(LoginAuthorization.this, 402);
                    settingDialog.setTitle("开启位置权限");
                    settingDialog.setMessage("通过您的位置定位离您最近的学校");
                    settingDialog.show();

                }
            }
        }
    };

    //获取通讯录联系人
    private void getContacts() {

        if(numberBookAuthoritySuccess){
            Log.i(TAG, "getContacts: numberBookAuthoritySuccess 已经成功过!!");
            return;
        }

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

            int counter = 0;

            //如果有权限count就++
            if (!TextUtils.isEmpty(contactUri.toString())){
                counter++;
            }

            while (cursor != null && cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                //contactSortKey =getSortkey(cursor.getString(1));
                com.yeejay.yplay.greendao.ContactsInfo contactsInfo = new com.yeejay.yplay.greendao.ContactsInfo(null, contactName, contactNumber);
                contactsInfoDao.insert(contactsInfo);
                counter += 1;
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

            Log.i(TAG, "getContacts: 通讯录长度---" + counter);
            if (counter > 0) {
                setContactBackground();
                numberBookAuthoritySuccess = true;
                SharePreferenceUtil.put(LoginAuthorization.this,"temp_book",1);
                //开启服务上传通讯录
                startService(new Intent(LoginAuthorization.this, ContactsService.class));
            }

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
            startActivity(intent);
        }
    }

    //设置通讯录的背景
    private void setContactBackground() {
        Drawable nav_up = getResources().getDrawable(R.drawable.contacts_yes);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mBtnGetNumberBookAuthority.setCompoundDrawables(null, nav_up, null, null);
        mBtnGetNumberBookAuthority.setEnabled(false);
    }

    //设置地理位置背景
    private void setLocationBackground() {
        Drawable nav_up = getResources().getDrawable(R.drawable.location_yes);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(), nav_up.getMinimumHeight());
        mBtnGetAddressAuthority.setCompoundDrawables(null, nav_up, null, null);
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
        Log.i(TAG, "onResume: ");
        float tempLat = (float)SharePreferenceUtil.get(LoginAuthorization.this,"temp_lat",0.0f);
        float tempLon = (float)SharePreferenceUtil.get(LoginAuthorization.this,"temp_lon",0.0f);
        int tempBook = (int)SharePreferenceUtil.get(LoginAuthorization.this,"temp_book",0);
        int tempAddress = (int)SharePreferenceUtil.get(LoginAuthorization.this,"temp_location",0);

        if (tempBook == 1){
            setContactBackground();
            numberBookAuthoritySuccess = true;
        }

        if (tempAddress == 1){
            setLocationBackground();
            latitude = tempLat;
            longitude = tempLon;
            addressAuthoritySuccess = true;
        }

        authorizationSuccess();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLocationClient != null) {
            mLocationClient.unRegisterLocationListener(bdListener);
            mLocationClient.stop();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy:");
    }

}
