package com.yeejay.yplay.userinfo;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.PhotoUtils;
import com.donkingliang.imageselector.utils.ToastUtils;
import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.SettingDialog;
import com.yeejay.yplay.BuildConfig;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.AppManager;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.customview.CustomDialog;
import com.yeejay.yplay.customview.CustomGenderDialog;
import com.yeejay.yplay.login.ClassList;
import com.yeejay.yplay.login.Login;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ImageUploadBody;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tangxiaolv.com.library.EffectiveShapeView;

import static com.donkingliang.imageselector.ImageSelectorActivity.hasSdcard;
import static com.yeejay.yplay.R.layout.activity_setting;

public class ActivitySetting extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;

    @BindView(R.id.setting_img_header)
    EffectiveShapeView settingImgHeader;

    //个人资料
    @BindView(R.id.rl_header)
    RelativeLayout rlHeaderView;
    @BindView(R.id.setting_name)
    TextView settingNmae;
    @BindView(R.id.setting_user_name)
    TextView settingUserNmae;
    @BindView(R.id.setting_gender)
    TextView settingGender;
    @BindView(R.id.rl_gender)
    RelativeLayout rlGenderView;
    @BindView(R.id.rl_user_name)
    RelativeLayout rlUserName;
    @BindView(R.id.rl_nick_name)
    RelativeLayout rlNickName;

    @BindView(R.id.setting_school)
    RelativeLayout settingSchool;
    @BindView(R.id.setting_school_name)
    TextView settingSchoolName;
    @BindView(R.id.setting_grade)
    TextView settingGrade;

    @BindView(R.id.setting_phone_number)
    TextView settingPhoneNumber;
    @BindView(R.id.setting_exit)
    TextView settingExitButton;
    @BindView(R.id.version)
    TextView versionText;
    @BindView(R.id.rl_contacts_us)
    RelativeLayout rlContactsUsView;


    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    int tag;

    //头像
    @OnClick(R.id.rl_header)
    public void settingImgageHeader() {
        System.out.println("头像");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            tag = 0;
//            ImageSelectorUtils.openPhotoAndClip(ActivitySetting.this,REQUEST_CODE);
            showImageBottomDialog();
        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    //姓名
    @OnClick(R.id.rl_nick_name)
    public void settingName() {

        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            tag = 1;
            queryUserUpdateLeftCount(1);

        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    //用户姓名
    @OnClick(R.id.rl_user_name)
    public void settingUserName() {

        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            tag = 2;
            queryUserUpdateLeftCount(2);
//            showInputDialog("修改用户名", "");
        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    //性别
    @OnClick(R.id.rl_gender)
    public void setSettingGender() {
        System.out.println("性别");
        tag = 4;
        queryUserUpdateLeftCount(4);
//        Intent intent = new Intent(ActivitySetting.this, ChoiceSex.class);
//        intent.putExtra("activity_setting", 1);
//        startActivityForResult(intent, REQUEST_CODE_CHOICE_GENDER);
    }

    //修改学校信息
    @OnClick(R.id.setting_school)
    public void setSettingSchoolInfo() {
        System.out.println("学校信息");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            tag = 3;
            queryUserUpdateLeftCount(3);
        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }
    }

    //电话号码
    @OnClick(R.id.setting_phone_number)
    public void settingPhoneNumber() {
        System.out.println("电话号码");
    }

    //联系我们
    @OnClick(R.id.rl_contacts_us)
    public void contacts() {
        System.out.println("联系我们");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            new AlertDialog.Builder(ActivitySetting.this)
                    .setMessage("咨询、建议，欢迎联系QQ:" + "\n" + "2137930181（^-^）")
                    .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    //退出
    @OnClick(R.id.rl_logout)
    public void settingExit() {
        System.out.println("退出");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)) {
            new AlertDialog.Builder(ActivitySetting.this)
                    .setMessage("退出后不会删除任何历史数据，下次登录依然可以使用本账号")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout();

                        }
                    })
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        } else {
            Toast.makeText(ActivitySetting.this, "网络异常", Toast.LENGTH_SHORT).show();
        }

    }

    private static final String TAG = "ActivitySetting";
    private static final int REQ_CODE_SEL_IMG = 15;
    private static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 200;
    private static final int REQUEST_CODE_PERMISSION_SINGLE_IMAGE = 201;
    private static final int REQUEST_CODE_CHOICE_GENDER = 201;
    private static final int REQUEST_CODE_SCHOOL = 202;

    private static final int REQUEST_CODE = 0x00000011;
    private static final int CODE_CAMERA_REQUEST = 0xa01;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0xa03;

    private File fileUri;
    private Uri imageUri;

    private static final String BASE_URL_USER = "http://sh.file.myqcloud.com";
    private static final String IMAGE_AUTHORIZATION = "ZijsNfCd4w8zOyOIAnbyIykTgBdhPTEyNTMyMjkzNTUmYj15cGxheSZrPUFLSURyWjFFRzQwejcyaTdMS3NVZmFGZm9pTW15d2ZmbzRQViZlPTE1MTcxMjM1ODcmdD0xNTA5MzQ3NTg3JnI9MTAwJnU9MCZmPQ==";
    private final int INVALID_NUM = 100000;
    private static int GENDER_VALUE = 0;//2 represents female; 1 represents male;
    private final static int GENDER_MALE = 1;
    private final static int GENDER_FEMALE = 2;
    private final static int TYPE_NICKNAME = 1;
    private final static int TYPE_USERNAME = 2;
    private final static int TYPE_CLASSSCHOOL = 3;
    private final static int TYPE_GENDER = 4;

    String dirStr;
    boolean addressAuthoritySuccess = false;
    boolean locationServiceSuccess = false;
    LocationManager mLocationManager;

    public LocationClient mLocationClient = null;
    boolean isFirstShowDialog = true;
    boolean isGetLonLat = true;

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_IMAGE:
                    System.out.println("相册权限申请成功");
                    if (Build.VERSION.SDK_INT >= 23 && ActivitySetting.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivitySetting.this.requestPermissions(new String[]{
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {

                    }
                    break;
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    getLonLat();
                    break;
            }

        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {

            switch (requestCode) {
                case REQUEST_CODE_PERMISSION_SINGLE_LOCATION:
                    System.out.println("回调失败的地理位置权限申请失败");
                    getLonLat();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_setting);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivitySetting.this, true);

        //获得位置服务
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationClient = new LocationClient(getApplicationContext());

        layoutTitle.setText("资料详情");

        getVersion();
        initData();
        getMyInfo();
    }

    private void getVersion() {
        String ver = BuildConfig.VERSION_NAME +
                "_" + BuildConfig.BUILD_TIMESTAMP + "_beta";
        versionText.setText(ver);
    }


    //初始化资料
    private void initView(UserInfoResponde.PayloadBean.InfoBean infoBean) {
        String url = infoBean.getHeadImgUrl();
        if (!TextUtils.isEmpty(url)) {
            Picasso.with(ActivitySetting.this).load(url).into(settingImgHeader);
        }

        settingNmae.setText(infoBean.getNickName());
        settingUserNmae.setText(infoBean.getUserName());
        settingGender.setText(infoBean.getGender() == 1 ? "男" : "女");
        GENDER_VALUE = infoBean.getGender() == 1 ? GENDER_MALE : GENDER_FEMALE;

        settingSchoolName.setText(infoBean.getSchoolName());
        String grade = FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade());
        settingGrade.setText(grade);
        settingPhoneNumber.setText(infoBean.getPhone());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (data != null){
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                String imagePath = images.get(0);
                Log.i(TAG, "onActivityResult: 图片URL---" + imagePath);
                System.out.println("图片位置---" + imagePath);
                String imageName = imagePath.substring(imagePath.length() - 17, imagePath.length());
                Bitmap bm1 = ImageUtil.decodeImage(imagePath,200,200);
                settingImgHeader.setImageBitmap(bm1);
                uploadImage(imagePath, imageName,bm1);
            }
        } else if (requestCode == CODE_CAMERA_REQUEST) {

            String imagePath = fileUri.getAbsolutePath();
            String imageName = imagePath.substring(imagePath.length() - 17, imagePath.length());
            Log.i(TAG, "onActivityResult: 拍照imagePath---" + imagePath + ",imageName---" + imageName);
            Bitmap bm1 = ImageUtil.decodeImage(imagePath,200,200);
            settingImgHeader.setImageBitmap(bm1);
            uploadImage(imagePath, imageName,bm1);

        } else if (requestCode == REQUEST_CODE_CHOICE_GENDER) {
            if (data != null) {
                String gender = data.getStringExtra("activity_setting_gender");
                System.out.println("性别---" + gender);
                settingGender.setText(gender);
            }
        } else if (requestCode == REQUEST_CODE_SCHOOL) {
            getMyInfo();
        } else if (requestCode == 400) {
            Log.i(TAG, "onActivityResult: requestCode == 400");
            getLonLat();
        } else if (requestCode == 402) {
            getLonLat();
        }
    }

    /**
     * 选择图片文件
     */
//    private void selectImage() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("image/*");
//        startActivityForResult(intent, REQ_CODE_SEL_IMG);
//    }

    //上传图头像
    private void uploadImage(String imagePath, final String imageName,Bitmap bitmap) {

        System.out.println("imageName---" + imageName);

//        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        Log.i(TAG, "uploadImage: 图片大小---" + datas.length);
        RequestBody upload = RequestBody.create(MediaType.parse("text/plain"), "upload");
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        new File(imagePath)
                );
        MultipartBody.Part aa = MultipartBody.Part.createFormData("filecontent", imageName, requestFile);

        ImageUploadBody body = new ImageUploadBody();
        body.setOp("upload");
        body.setFilecontent(datas);

        YPlayApiManger.getInstance().getZivApiServiceParameters(BASE_URL_USER)
                .uploadHeaderImg(IMAGE_AUTHORIZATION, imageName, upload, aa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUploadRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ImageUploadRespond imageUploadRespond) {
                        System.out.println("图片上传返回---" + imageUploadRespond.toString());
                        if (imageUploadRespond.getCode() == 0) {
                            //保存图片id
                            SharePreferenceUtil.put(ActivitySetting.this, YPlayConstant.YPLAY_HEADER_IMG, imageName);
                            updateHeaderImg(imageName, null, 0, null);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("图片上传异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    //修改头像
    private void updateHeaderImg(String headImgId, final String nickName, int gender, final String userName) {

        Map<String, Object> imgMap = new HashMap<>();
        if (!TextUtils.isEmpty(nickName)) {
            imgMap.put("nickName", nickName);
            imgMap.put("flag", 1);
        }

        if (gender == 1 || gender == 2)
            imgMap.put("gender", gender);
        if (!TextUtils.isEmpty(headImgId))
            imgMap.put("headImgId", headImgId);
        if (!TextUtils.isEmpty(userName)) {
            imgMap.put("userName", userName);
            imgMap.put("flag", 1);
        }

        imgMap.put("uin", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_UIN, 0));
        imgMap.put("token", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        imgMap.put("ver", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_VER, 0));


        YPlayApiManger.getInstance().getZivApiService()
                .updateHeaderImg(imgMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BaseRespond baseRespond) {
                        if (baseRespond.getCode() == 0) {
                            if (tag == 0) {
                                System.out.println("修改图像成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this, "头像修改成功", Toast.LENGTH_SHORT).show();
                            } else if (tag == 1) {
                                System.out.println("修改姓名成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this, "修改姓名成功", Toast.LENGTH_SHORT).show();
                                settingNmae.setText(nickName);
                            } else if (tag == 2) {
                                System.out.println("修改用户名成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this, "修改用户名成功", Toast.LENGTH_SHORT).show();
                                settingUserNmae.setText(userName);
                            }

                        } else if (baseRespond.getCode() == 11011) {
                            Toast.makeText(ActivitySetting.this, R.string.username_already_exist, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("修改图像异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取自己的资料
    private void getMyInfo() {

        Map<String, Object> myInfoMap = new HashMap<>();
        myInfoMap.put("uin", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_UIN, 0));
        myInfoMap.put("token", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        myInfoMap.put("ver", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getMyInfo(myInfoMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UserInfoResponde userInfoResponde) {
                        System.out.println("获取自己的资料---" + userInfoResponde.toString());
                        if (userInfoResponde.getCode() == 0) {
                            initView(userInfoResponde.getPayload().getInfo());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("获取自己的资料异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //查询用户的修改配额
    private void queryUserUpdateLeftCount(int field) {

        Map<String, Object> leftCountMap = new HashMap<>();
        leftCountMap.put("field", field);
        leftCountMap.put("uin", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_UIN, 0));
        leftCountMap.put("token", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        leftCountMap.put("ver", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getUserUpdateCount(leftCountMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserUpdateLeftCountRespond>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(UserUpdateLeftCountRespond userUpdateLeftCountRespond) {
                        System.out.println("剩余修改次数---" + userUpdateLeftCountRespond.toString());
                        if (userUpdateLeftCountRespond.getCode() == 0) {
                            int letCount = userUpdateLeftCountRespond.getPayload().getInfo().getLeftCnt();
                            int tempField = userUpdateLeftCountRespond.getPayload().getInfo().getField();
                            System.out.println(tempField + "---编号");
                            if (tag == 1) {

                                if (letCount > 0 && letCount != INVALID_NUM) {
                                    //showInputDialog("输入真实姓名", "只有" + letCount + "次修改机会,请珍惜喵~");
                                    showDialogTips(tag, letCount);
                                } else if (letCount == INVALID_NUM) {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "姓名修改次数已用完咯");
                                } else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "姓名修改次数已用完咯");
                                }

                            } else if (tag == 2) {
                                if (letCount > 0 && letCount != INVALID_NUM) {
                                    //showInputDialog("修改用户名", "只有" + letCount + "次修改机会,请珍惜喵~");
                                    showDialogTips(tag, letCount);
                                } else if (letCount == INVALID_NUM) {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "用户名修改次数已用完咯");
                                } else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "用户名修改次数已用完咯");
                                }
                            } else if (tag == 3) {
                                if (letCount > 0 && letCount != INVALID_NUM) {
                                    AlertDialog dialog = new AlertDialog.Builder(ActivitySetting.this)
                                            .setMessage("只有" + letCount + "次修改机会,请珍惜喵~")
                                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                    //查看地理位置权限
//                                                    getLonLat();
//                                                    if (!addressAuthoritySuccess) { //无权限
//
//                                                    }
                                                    isGetLonLat = true;
                                                    isFirstShowDialog = true;
                                                    getAddressAuthority();

                                                }
                                            })
                                            .show();
                                } else if (letCount == INVALID_NUM) {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "年级学校修改次数已用完咯");
                                } else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "年级学校修改次数已用完咯");
                                }
                            } else if (tag == 4) {//gender modifer;
                                if (letCount > 0 && letCount != INVALID_NUM) {
//                                    AlertDialog dialog = new AlertDialog.Builder(ActivitySetting.this)
//                                            .setMessage("只有" + letCount + "次修改机会,请珍惜喵~")
//                                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
//                                                @Override
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                    Intent intent = new Intent(ActivitySetting.this, ChoiceSex.class);
//                                                    intent.putExtra("activity_setting", 1);
//                                                    startActivityForResult(intent, REQUEST_CODE_CHOICE_GENDER);
//                                                }
//                                            })
//                                            .show();
                                    showDialogTips(tag, letCount);
                                } else if (letCount == INVALID_NUM) {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "性别修改次数已用完咯");
                                } else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this, "性别修改次数已用完咯");
                                }
                            }

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void showDialogTips(int tag, int letCount) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        switch (tag) {
            case TYPE_NICKNAME://nick name
                View userNameLayout = inflater.inflate(R.layout.dialog_content_username_layout, null);
                TextView tips1 = (TextView) userNameLayout.findViewById(R.id.tips1);
                tips1.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                        Integer.toString(letCount)));

                final EditText userNameView = (EditText) userNameLayout.findViewById(R.id.username);

                userNameView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                userNameView.setSingleLine();
                userNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEND
                                || actionId == EditorInfo.IME_ACTION_DONE
                                || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                                && KeyEvent.ACTION_DOWN == event.getAction())) {
                            return true;
                        }
                        return false;
                    }
                });

                CustomDialog.Builder userBuilder = new CustomDialog.Builder(this);
                userBuilder.setTitle(R.string.title_modify_name);
                userBuilder.setContentView(userNameLayout);
                userBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //设置你的操作事项
                        System.out.println("modify username" + userNameView.getText().toString().trim());
                        updateHeaderImg(null, userNameView.getText().toString().trim(),
                                0, null);
                    }
                });

                userBuilder.setNegativeButton(getString(R.string.cancel),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                CustomDialog customDlg = userBuilder.create();
                final Button positiveBtn = (Button) userBuilder.getCustomView().findViewById(R.id.positiveButton);
                positiveBtn.setEnabled(false);

                userNameView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() <= 0) {
                            positiveBtn.setEnabled(false);
                        } else {
                            positiveBtn.setEnabled(true);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().length() <= 0) {
                            positiveBtn.setEnabled(false);
                        } else {
                            positiveBtn.setEnabled(true);
                        }
                    }
                });

                customDlg.show();

                break;
            case TYPE_USERNAME://userkname
                View nickNameLayout = inflater.inflate(R.layout.dialog_content_nickname_layout, null);
                TextView nickNameTips1 = (TextView) nickNameLayout.findViewById(R.id.tips1);
                nickNameTips1.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                        Integer.toString(letCount)));

                final EditText nickNameView = (EditText) nickNameLayout.findViewById(R.id.nickname);
                nickNameView.setKeyListener(new DigitsKeyListener() {
                    @Override
                    public int getInputType() {
                        return InputType.TYPE_TEXT_VARIATION_PASSWORD;
                    }

                    @Override
                    protected char[] getAcceptedChars() {
                        char[] data = getResources().getString(R.string.login_only_can_input).toCharArray();
                        return data;
                    }
                });
                nickNameView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

                CustomDialog.Builder nickBuilder = new CustomDialog.Builder(this);
                nickBuilder.setTitle(R.string.title_modify_nickname);
                nickBuilder.setContentView(nickNameLayout);
                nickBuilder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //设置你的操作事项
                        System.out.println("modify nickname" + nickNameView.getText().toString().trim());
                        updateHeaderImg(null, null, 0, nickNameView.getText().toString().trim());
                    }
                });

                nickBuilder.setNegativeButton(getString(R.string.cancel),
                        new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                CustomDialog usernameDlg = nickBuilder.create();
                final Button usernamePosBtn = (Button) nickBuilder.getCustomView().findViewById(R.id.positiveButton);
                usernamePosBtn.setEnabled(false);

                nickNameView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (s.toString().trim().length() <= 0) {
                            usernamePosBtn.setEnabled(false);
                        } else {
                            usernamePosBtn.setEnabled(true);
                        }
                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.toString().trim().length() <= 0) {
                            usernamePosBtn.setEnabled(false);
                        } else {
                            usernamePosBtn.setEnabled(true);
                        }
                    }
                });

                usernameDlg.show();

                break;
            case TYPE_CLASSSCHOOL:

                break;
            case TYPE_GENDER://gender
//                View GenderLayout = inflater.inflate(R.layout.dialog_content_gender_layout, null);
//                TextView genderTips = (TextView) GenderLayout.findViewById(R.id.tips1);
//                genderTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
//                        Integer.toString(letCount)));
//
//
//
//                CustomDialog.Builder genderBuilder = new CustomDialog.Builder(this);
//                genderBuilder.setContentView(GenderLayout);
//                genderBuilder.create().show();
                CustomGenderDialog.Builder genderBuilder = new CustomGenderDialog.Builder(this);
                final CustomGenderDialog genderDialog = genderBuilder.create();
                final View genderContentView = genderBuilder.getContentView();
                TextView genderTips = (TextView) genderContentView.findViewById(R.id.tips1);
                genderTips.setText(String.format(getResources().getString(R.string.tips1_name_mofify_num),
                        Integer.toString(letCount)));
                ImageView cancelView = (ImageView) genderContentView.findViewById(R.id.cancel);
                cancelView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        genderDialog.dismiss();
                    }
                });

                ImageView girlView = (ImageView) genderContentView.findViewById(R.id.girl_icon);
                ImageView boyView = (ImageView) genderContentView.findViewById(R.id.boy_icon);
                if (GENDER_VALUE == 1) {
                    girlView.setImageResource(R.drawable.girl_unselected);
                    boyView.setImageResource(R.drawable.boy_selected);
                } else if (GENDER_VALUE == 2) {
                    girlView.setImageResource(R.drawable.girl_selected);
                    boyView.setImageResource(R.drawable.boy_unselected);
                }

                girlView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GENDER_VALUE == GENDER_MALE) {
                            ((ImageView) genderContentView.findViewById(R.id.girl_icon)).setImageResource(R.drawable.girl_selected);
                            ((ImageView) genderContentView.findViewById(R.id.boy_icon)).setImageResource(R.drawable.boy_unselected);
                            settingGender.setText(R.string.female);
                            choiceSex(GENDER_FEMALE);
                            genderDialog.dismiss();
                        }
                    }
                });

                boyView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (GENDER_VALUE == GENDER_FEMALE) {
                            ((ImageView) genderContentView.findViewById(R.id.girl_icon)).setImageResource(R.drawable.girl_unselected);
                            ((ImageView) genderContentView.findViewById(R.id.boy_icon)).setImageResource(R.drawable.boy_selected);
                            settingGender.setText(R.string.male);
                            choiceSex(GENDER_MALE);
                            genderDialog.dismiss();
                        }
                    }
                });

                genderDialog.show();

                break;
            default:
        }
    }

    //选择性别
    private void choiceSex(final int gender) {

        Map<String, Object> sexMap = new HashMap<>();
        System.out.println("gender---" + gender);
        sexMap.put("gender", gender);
        sexMap.put("flag", 1);
        sexMap.put("uin", SharePreferenceUtil.get(this, YPlayConstant.YPLAY_UIN, 0));
        sexMap.put("token", SharePreferenceUtil.get(this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        sexMap.put("ver", SharePreferenceUtil.get(this, YPlayConstant.YPLAY_VER, 0));

        YPlayApiManger.getInstance().getZivApiService()
                .choiceSex(sexMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull BaseRespond baseRespond) {
                        if (baseRespond.getCode() == 0) {
                            System.out.println("gender set successfully---" + baseRespond.toString());
//                            if (isActivitySetting == 1){
//                                Intent intent = new Intent();
//                                String str = gender == 1 ? "男" : "女";
//                                intent.putExtra("activity_setting_gender",str);
//                               // ChoiceSex.this.setResult(201,intent);
//                                //ChoiceSex.this.finish();
//                            }else {
//                                startActivity(new Intent(this,UserInfo.class));
//                                //jumpToWhere();
//                            }

                        } else {
                            System.out.println("gender set error---" + baseRespond.toString());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("gender set exception---" + e.getMessage());

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //退出登录
    private void logout() {

        Map<String, Object> logoutMap = new HashMap<>();
        logoutMap.put("uin", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_UIN, 0));
        logoutMap.put("token", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN, "yplay"));
        logoutMap.put("ver", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_VER, 0));
        YPlayApiManger.getInstance().getZivApiService()
                .getMyInfo(logoutMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserInfoResponde>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(UserInfoResponde userInfoResponde) {
                        System.out.println("退出登录---" + userInfoResponde.toString());

                        //登出
                        TIMManager.getInstance().logout(new TIMCallBack() {
                            @Override
                            public void onError(int code, String desc) {

                                //错误码code和错误描述desc，可用于定位请求失败原因
                                //错误码code列表请参见错误码表
                                Log.d("ActivitySetting", "logout failed. code: " + code + " errmsg: " + desc);
                            }

                            @Override
                            public void onSuccess() {
                                //登出成功
                                System.out.println("im退出成功");
                            }
                        });

                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_UIN);
                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN);
                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_VER);
                        AppManager.getAppManager().AppExit(ActivitySetting.this);
                        startActivity(new Intent(ActivitySetting.this, Login.class));
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("退出登录---异常" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    EditText editText;

    //展示对话框
    private void showInputDialog(String title, String message) {

        editText = new EditText(ActivitySetting.this);
        LinearLayout.LayoutParams etParam = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etParam.setMargins(20, 0, 20, 0);
        editText.setLayoutParams(etParam);

        if (tag == 1) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            editText.setSingleLine();
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN == event.getAction())) {
                        System.out.println("回车键被点击");
                        return true;
                    }
                    return false;
                }
            });
        } else if (tag == 2) {
            editText.setKeyListener(new DigitsKeyListener() {
                @Override
                public int getInputType() {
                    return InputType.TYPE_TEXT_VARIATION_PASSWORD;
                }

                @Override
                protected char[] getAcceptedChars() {
                    char[] data = getResources().getString(R.string.login_only_can_input).toCharArray();
                    return data;
                }
            });
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }

        AlertDialog.Builder inputDialog =
                new AlertDialog.Builder(ActivitySetting.this);
        inputDialog.setTitle(title).setView(editText);

        if (!TextUtils.isEmpty(message)) {
            inputDialog.setMessage(message);
        }
        inputDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString().trim();
                        if (name.length() > 0) {
                            if (tag == 1) {
                                System.out.println("修改姓名---" + name);
                                updateHeaderImg(null, name, 0, null);
                            } else if (tag == 2) {
                                System.out.println("修改用户名---" + name);
                                updateHeaderImg(null, null, 0, name);
                            }
                        } else {
                            Toast.makeText(ActivitySetting.this, "昵称不能为空哦", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
        inputDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.out.println("取消按钮");
                    }
                });
        inputDialog.show();
    }

    //获取地址位置权限
    private void getAddressAuthority() {

        AndPermission.with(ActivitySetting.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_LOCATION)
                .permission(Permission.LOCATION)
                .callback(mPermissionListener)
                .start();
        Log.i(TAG, "getAddressAuthority: 申请地理位置");
    }

    //获取当前经纬度
    private void getLonLat() {

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
                double latitude = bdLocation.getLatitude();
                //获取经度信息
                double longitude = bdLocation.getLongitude();

                Log.i(TAG, "onReceiveLocation: 百度地图---lat---" + latitude + ",lon---" + longitude);
                addressAuthoritySuccess = true;

                if (isGetLonLat) {
                    Log.i(TAG, "onReceiveLocation: isGetLonLat---已经获取到经纬度");
                    isGetLonLat = false;
                    Intent intent = new Intent(ActivitySetting.this, ClassList.class);
                    intent.putExtra("activity_setting_school", 10);
                    intent.putExtra(YPlayConstant.YPLAY_FIRST_LATITUDE, latitude);
                    intent.putExtra(YPlayConstant.YPLAY_FIRST_LONGITUDE, longitude);
                    startActivityForResult(intent, REQUEST_CODE_SCHOOL);
                }
            } else if (errorCode == 62) {
                if (isFirstShowDialog) {
                    Log.i(TAG, "onReceiveLocation: isFirstShowDialog---" + isFirstShowDialog);
                    isFirstShowDialog = false;
                    SettingDialog settingDialog = AndPermission.defaultSettingDialog(ActivitySetting.this, 402);
                    settingDialog.setTitle("开启位置权限");
                    settingDialog.setMessage("通过您的位置定位离您最近的学校");
                    settingDialog.show();
                }
            }
        }
    };

    //显示底部对话框
    private void showImageBottomDialog() {

        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_dialog_content_circle, null);
        bottomDialog.setContentView(contentView);

        Button msgProBt = (Button) contentView.findViewById(R.id.message_profile);
        msgProBt.setText("相册");
        Button msgDeleteBt = (Button) contentView.findViewById(R.id.message_delete);
        msgDeleteBt.setText("拍照");
        msgDeleteBt.setTextColor(getResources().getColor(R.color.message_profile_blue));
        Button msgCancelBt = (Button) contentView.findViewById(R.id.message_cancel);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.message_profile:
                        Log.i(TAG, "onClick: 相册");
                        ImageSelectorUtils.openPhotoAndClip(ActivitySetting.this, REQUEST_CODE);
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_delete:
                        Log.i(TAG, "onClick: 拍照");
                        initData();
                        autoObtainCameraPermission();
                        bottomDialog.dismiss();
                        break;
                    case R.id.message_cancel:
                        Log.i(TAG, "onClick: 取消");
                        bottomDialog.dismiss();
                        break;
                }
            }
        };

        msgProBt.setOnClickListener(onClickListener);
        msgDeleteBt.setOnClickListener(onClickListener);
        msgCancelBt.setOnClickListener(onClickListener);

        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) contentView.getLayoutParams();
        params.width = getResources().getDisplayMetrics().widthPixels - DensityUtil.dp2px(this, 16f);
        params.bottomMargin = DensityUtil.dp2px(this, 8f);
        contentView.setLayoutParams(params);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
    }

    /**
     * 自动获取相机权限
     */
    private void autoObtainCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                ToastUtils.showShort(this, "您已经拒绝过一次");
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(ActivitySetting.this, "com.donkingliang.imageselector", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                Log.i(TAG, "autoObtainCameraPermission: CODE_CAMERA_REQUEST---" + CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, "设备没有SD卡！");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (hasSdcard()) {
                    imageUri = Uri.fromFile(fileUri);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                        imageUri = FileProvider.getUriForFile(ActivitySetting.this, "com.donkingliang.imageselector", fileUri);//通过FileProvider创建一个content类型的Uri
                    PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    ToastUtils.showShort(this, "设备没有SD卡！");
                }
            } else {

                ToastUtils.showShort(this, "请允许打开相机！！");
            }
        }
    }

    private void initData() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirStr = root + File.separator + "yplay" + File.separator + "image";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String tempImage = String.valueOf(System.currentTimeMillis());
        fileUri = new File(dirStr, tempImage + ".jpg");

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
    protected void onDestroy() {
        super.onDestroy();
    }
}
