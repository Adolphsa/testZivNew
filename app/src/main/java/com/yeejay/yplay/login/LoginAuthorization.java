package com.yeejay.yplay.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ContactsInfo;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginAuthorization extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 100;
    private static final int REQUEST_CODE_PERMISSION_SINGLE_CONTACTS = 101;

    Button mBtnGetAddressAuthority;
    Button mBtnGetNumberBookAuthority;
    boolean addressAuthoritySuccess = false;
    boolean numberBookAuthoritySuccess = false;

    String mProvider;//位置提供器
    LocationManager mLocationManager;//位置服务
    Location mLocation;

    List<ContactsInfo> mContactsList;

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    getLonLat();
                    //addressAuthoritySuccess = true;
                    //System.out.println("地理位置权限申请成功");
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    getContacts();
//                    numberBookAuthoritySuccess = true;
                    //System.out.println("通讯库权限申请成功");
                    break;
            }
            //授权成功跳转
            authorizationSuccess();
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    System.out.println("回调失败的地理位置权限申请失败");
                    getLonLat();
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    System.out.println("回调失败的通讯录权限申请失败");
                    getContacts();
                    break;
            }

            //检查是否真的获取到联系人和地理位置


            if (AndPermission.hasAlwaysDeniedPermission(LoginAuthorization.this, deniedPermissions)) {
                if (requestCode == REQUEST_CODE_PERMISSION_SINGLE_LOCATION) {
                    AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
                } else {
                    AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
                }
                // 第一种：用AndPermission默认的提示语。
                System.out.println("权限拒绝提示");
            } else {
                //授权成功跳转
                authorizationSuccess();
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
                //startActivity(new Intent(LoginAuthorization.this,ClassList.class));
                if (AndPermission.hasPermission(LoginAuthorization.this, Permission.CONTACTS)) {
                    System.out.println("通讯录有权限");
                    setContactBackground();
                }
                if (AndPermission.hasPermission(LoginAuthorization.this, Permission.LOCATION)) {
                    System.out.println("地理位置有权限");
                    setLocationBackground();
                }

                if (numberBookAuthoritySuccess && addressAuthoritySuccess){
                    authorizationSuccess();
                }
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorization);

        getWindow().setStatusBarColor(getResources().getColor(R.color.edit_title_text_color));

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
                getNumberBookAuthority();
            }
        });


//        if (AndPermission.hasPermission(LoginAuthorization.this, Permission.CONTACTS)) {
//            System.out.println("通讯录有权限");
//            setContactBackground();
//        }
//        getContacts();
//        if (numberBookAuthoritySuccess) {
//            setContactBackground();
//        }
//        if (AndPermission.hasPermission(LoginAuthorization.this, Permission.LOCATION)) {
//            System.out.println("地理位置有权限");
//            setLocationBackground();
//        }
//        getLonLat();
//        if (addressAuthoritySuccess) {
//            setLocationBackground();
//        }
//
//        if (addressAuthoritySuccess && numberBookAuthoritySuccess) {
//            Intent intent = new Intent(LoginAuthorization.this, ClassList.class);
//            intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE, mLocation.getLatitude());
//            intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE, mLocation.getLongitude());
//            System.out.println("授权页面---latitude" + mLocation.getLatitude() +
//                    "longitude" + mLocation.getLongitude());
//            startActivity(intent);
//        }
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

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mProvider = judgeProvider(mLocationManager);
        if (Build.VERSION.SDK_INT >= 23 &&
                LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null) {
            System.out.println("地理位置权限申请成功2");
            addressAuthoritySuccess = true;

            setLocationBackground();

            System.out.println("当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
        }
    }

    //判断是否有可用的内容提供者
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if (prodiverlist.contains(LocationManager.NETWORK_PROVIDER)) {
            return LocationManager.NETWORK_PROVIDER;
        } else if (prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        } else {
            Toast.makeText(LoginAuthorization.this, "没有可用的位置提供器", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    //获取通讯录联系人
    private void getContacts() {
        mContactsList = new ArrayList<ContactsInfo>();
        if (Build.VERSION.SDK_INT >= 23
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            System.out.println("contactUri---" + contactUri);
            if (contactUri != null) {
                numberBookAuthoritySuccess = true;
                System.out.println("通讯录权限申请成功");
                System.out.println("通讯录长度---" + mContactsList.size());
            }
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
                ContactsInfo contactsInfo = new ContactsInfo(contactName, contactNumber);
                if (contactName != null)
                    mContactsList.add(contactsInfo);
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题

            setContactBackground();

            if (mContactsList.size() > 0) {
                ContactsInfo testContactInfo = mContactsList.get(0);
                System.out.println("姓名---" + testContactInfo.getName() + "号码---" + testContactInfo.getNumber());
                upLoadingContacts();
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
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE, mLocation.getLatitude());
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE, mLocation.getLongitude());

//            SharePreferenceUtil.put(LoginAuthorization.this, YPlayConstant.YPLAY_LATITUDE, String.valueOf(mLocation.getLatitude()));
//            SharePreferenceUtil.put(LoginAuthorization.this, YPlayConstant.YPLAY_LONGITUDE, String.valueOf(mLocation.getLongitude()));
            //上传通讯录（后续，有风险。应该改为起一个服务，然后在服务中上传）

            startActivity(intent);
        }
    }

    //上传通讯录
    private void upLoadingContacts() {

        Map<String, Object> contactsMap = new HashMap<>();
        String contactString = GsonUtil.GsonString(mContactsList);
        String encodedString = Base64.encodeToString(contactString.getBytes(), Base64.DEFAULT);
        contactsMap.put("data", encodedString);
        contactsMap.put("uin", SharePreferenceUtil.get(LoginAuthorization.this, YPlayConstant.YPLAY_UIN, 0));
        contactsMap.put("token", SharePreferenceUtil.get(LoginAuthorization.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        contactsMap.put("ver", SharePreferenceUtil.get(LoginAuthorization.this, YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .updateContacts(contactsMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BaseRespond baseRespond) {

                        if (baseRespond.getCode() == 0) {
                            System.out.println("上传通讯录成功---" + baseRespond.toString());
                        } else {
                            System.out.println("上传通讯录失败---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("上传通讯录失败---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
}
