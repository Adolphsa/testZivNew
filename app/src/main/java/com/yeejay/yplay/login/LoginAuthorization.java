package com.yeejay.yplay.login;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.model.ContactsInfo;
import com.yeejay.yplay.utils.YPlayConstant;

import java.util.ArrayList;
import java.util.List;

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
            switch (requestCode){
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    getLonLat();
                    System.out.println("地理位置权限申请成功");
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    getContacts();
                    System.out.println("地理位置权限申请成功");
                    break;
            }

//            if(AndPermission.hasPermission(LoginAuthorization.this,grantPermissions)) {
//                System.out.println("两个权限都获取成功，跳转页面");
//                //startActivity(new Intent(LoginAuthorization.this,ClassList.class));
//            } else {
//                // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
//                AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
//            }

            //授权成功跳转
            authorizationSuccess();

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            switch (requestCode){
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    System.out.println("回调失败的地理位置权限申请失败");
                    getLonLat();
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_CONTACTS:
                    System.out.println("回调失败的通讯录权限申请失败");
                    getContacts();
                    break;
            }
//            if(AndPermission.hasPermission(LoginAuthorization.this,deniedPermissions)) {
//                System.out.println("在失败的回调方法中，两个权限都获取成功，跳转页面");
//                //startActivity(new Intent(LoginAuthorization.this,ClassList.class));
//            } else {
//                // 使用AndPermission提供的默认设置dialog，用户点击确定后会打开App的设置页面让用户授权。
//                AndPermission.defaultSettingDialog(LoginAuthorization.this, 400).show();
//            }
            authorizationSuccess();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 400: { // 这个400就是上面defineSettingDialog()的第二个参数。
                // 你可以在这里检查你需要的权限是否被允许，并做相应的操作。
                System.out.println("回调了");
                startActivity(new Intent(LoginAuthorization.this,ClassList.class));
                break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_authorization);

        mBtnGetAddressAuthority = (Button) findViewById(R.id.laz_btn_address);
        mBtnGetNumberBookAuthority = (Button) findViewById(R.id.laz_btn_address_book);

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

    }

    //获取地址位置权限
    private void getAddressAuthority(){

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
    private void getNumberBookAuthority(){
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
    private void getLonLat(){
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);//获得位置服务
        mProvider = judgeProvider(mLocationManager);
        if ( Build.VERSION.SDK_INT >= 23 &&
                LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                LoginAuthorization.this.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return  ;
        }
        mLocation = mLocationManager.getLastKnownLocation(mProvider);
        if (mLocation != null){
            System.out.println("地理位置权限申请成功");
            addressAuthoritySuccess = true;
            System.out.println("当前维度---" + mLocation.getLatitude() + "当前精度---" + mLocation.getLongitude());
        }
    }

    //判断是否有可用的内容提供者
    private String judgeProvider(LocationManager locationManager) {
        List<String> prodiverlist = locationManager.getProviders(true);
        if(prodiverlist.contains(LocationManager.NETWORK_PROVIDER)){
            return LocationManager.NETWORK_PROVIDER;
        }else if(prodiverlist.contains(LocationManager.GPS_PROVIDER)) {
            return LocationManager.GPS_PROVIDER;
        }else{
            Toast.makeText(LoginAuthorization.this,"没有可用的位置提供器",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    //获取通讯录联系人
    private void getContacts(){
         mContactsList = new ArrayList<ContactsInfo>();
        if (Build.VERSION.SDK_INT >= 23
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                && LoginAuthorization.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("无读取联系人权限");
            return;
        }

        try {
            Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = getContentResolver().query(contactUri,
                    new String[]{"display_name", "sort_key", "contact_id","data1"},
                    null, null, "sort_key");
            String contactName;
            String contactNumber;
            //String contactSortKey;
            //int contactId;
            while (cursor!= null && cursor.moveToNext()) {
                contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                contactNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                //contactId = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                //contactSortKey =getSortkey(cursor.getString(1));
                ContactsInfo contactsInfo = new ContactsInfo(contactName,contactNumber);
                if (contactName!=null)
                    mContactsList.add(contactsInfo);
            }
            cursor.close();//使用完后一定要将cursor关闭，不然会造成内存泄露等问题
            if (mContactsList.size() > 0){
                numberBookAuthoritySuccess = true;
                System.out.println("通讯录权限申请成功");
                System.out.println("通讯录长度---" + mContactsList.size());
                ContactsInfo testContactInfo = mContactsList.get(0);
                System.out.println("姓名---" + testContactInfo.getName() + "号码---" + testContactInfo.getNumber());
            }

        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }
    }

    //排序
    private String getSortkey(String sortKeyString){
        String key =sortKeyString.substring(0,1).toUpperCase();
        if (key.matches("[A-Z]")){
            return key;
        }else
            return "#";   //获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
    }

    //授权成功
    private void authorizationSuccess(){
        if((addressAuthoritySuccess && numberBookAuthoritySuccess) ||
                (numberBookAuthoritySuccess && addressAuthoritySuccess)){

            Intent intent = new Intent(LoginAuthorization.this,ClassList.class);
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE,mLocation.getLatitude());
            intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE,mLocation.getLongitude());
            //上传通讯录（后续）

            startActivity(intent);
        }

    }
}