package com.yeejay.yplay.userinfo;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tencent.imsdk.TIMCallBack;
import com.tencent.imsdk.TIMManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.AppManager;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.login.ChoiceSex;
import com.yeejay.yplay.login.ClassList;
import com.yeejay.yplay.login.Login;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ImageUploadBody;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.model.UserUpdateLeftCountRespond;
import com.yeejay.yplay.utils.DialogUtils;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.NetWorkUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.StatuBarUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
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

import static com.yeejay.yplay.R.layout.activity_setting;

public class ActivitySetting extends BaseActivity {

    @BindView(R.id.layout_title_back2)
    ImageButton layoutTitleBack;
    @BindView(R.id.layout_title2)
    TextView layoutTitle;

    @BindView(R.id.setting_img_header)
    EffectiveShapeView settingImgHeader;

    //个人资料
    @BindView(R.id.setting_name)
    TextView settingNmae;
    @BindView(R.id.setting_user_name)
    TextView settingUserNmae;
    @BindView(R.id.setting_gender)
    TextView settingGender;

    @BindView(R.id.setting_school)
    TextView settingSchool;
    @BindView(R.id.setting_school_name)
    TextView settingSchoolName;
    @BindView(R.id.setting_grade)
    TextView settingGrade;

    @BindView(R.id.setting_phone_number)
    TextView settingPhoneNumber;
    @BindView(R.id.setting_exit)
    TextView settingExitButton;


    @OnClick(R.id.layout_title_back2)
    public void back() {
        finish();
    }

    int tag;

    //头像
    @OnClick(R.id.setting_img_header)
    public void settingImgHeader() {
        System.out.println("头像");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
            tag = 0;
            applyForAlbumAuthority();
        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    //姓名
    @OnClick(R.id.setting_name)
    public void settingName(){

        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
            tag = 1;
            queryUserUpdateLeftCount(1);

        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    //用户姓名
    @OnClick(R.id.setting_user_name)
    public void settingUserName(){

        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
            tag = 2;
            showInputDialog("修改门牌号","");
        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    //性别
    @OnClick(R.id.setting_gender)
    public void setSettingGender(){
        System.out.println("性别");
        Intent intent = new Intent(ActivitySetting.this, ChoiceSex.class);
        intent.putExtra("activity_setting",1);
        startActivityForResult(intent,REQUEST_CODE_CHOICE_GENDER);
    }

    //修改学校信息
    @OnClick(R.id.setting_school)
    public void setSettingSchoolInfo() {
        System.out.println("学校信息");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
            tag = 3;
            queryUserUpdateLeftCount(3);
        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }
    }

    //电话号码
    @OnClick(R.id.setting_phone_number)
    public void settingPhoneNumber() {
        System.out.println("电话号码");
    }

    //联系我们
    @OnClick(R.id.setting_contacts_us)
    public void contacts() {
        System.out.println("联系我们");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
            new AlertDialog.Builder(ActivitySetting.this)
                    .setMessage("咨询、建议，欢迎联系QQ:" + "\n" + "2137930181（^-^）")
                    .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .show();
        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    //退出
    @OnClick(R.id.setting_exit)
    public void settingExit() {
        System.out.println("退出");
        if (NetWorkUtil.isNetWorkAvailable(ActivitySetting.this)){
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
        }else {
            Toast.makeText(ActivitySetting.this,"网络异常",Toast.LENGTH_SHORT).show();
        }

    }

    private static final int REQ_CODE_SEL_IMG = 15;
    private static final int CROP_IMAGE = 11;
    private static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 200;
    private static final int REQUEST_CODE_CHOICE_GENDER = 201;
    private static final int REQUEST_CODE_SCHOOL = 202;
    private static final String BASE_URL_USER = "http://sh.file.myqcloud.com";
    private static final String IMAGE_AUTHORIZATION = "ZijsNfCd4w8zOyOIAnbyIykTgBdhPTEyNTMyMjkzNTUmYj15cGxheSZrPUFLSURyWjFFRzQwejcyaTdMS3NVZmFGZm9pTW15d2ZmbzRQViZlPTE1MTcxMjM1ODcmdD0xNTA5MzQ3NTg3JnI9MTAwJnU9MCZmPQ==";

    private String imageName;
    private File tempFile;
    private Uri tempUri;
    String dirStr;

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            System.out.println("相册权限申请成功");
            if (Build.VERSION.SDK_INT >= 23 && ActivitySetting.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivitySetting.this.requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {
                selectImage();
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            System.out.println("相册权限申请失败");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_setting);
        ButterKnife.bind(this);

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
        StatuBarUtil.setMiuiStatusBarDarkMode(ActivitySetting.this, true);

        layoutTitle.setText("资料详情");

        initData();
        getMyInfo();
    }

    private void initData() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        dirStr = root + File.separator + "yplay" + File.separator + "image";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }

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

        settingSchoolName.setText(infoBean.getSchoolName());
        String grade = FriendFeedsUtil.schoolType(infoBean.getSchoolType(), infoBean.getGrade());
        settingGrade.setText(grade);
        settingPhoneNumber.setText(infoBean.getPhone());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_SEL_IMG:
                    //获取选择的图片的URI
                    if (data != null) {
                        Uri uri = data.getData();
                        cropImage(uri);
                    }
                    break;
                case CROP_IMAGE:
                    //图片裁剪完，已经保存到文件中
                    Bitmap bm = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                    System.out.println("图片位置---" + tempFile.getAbsolutePath());
                    uploadImage();
                    settingImgHeader.setImageBitmap(bm);
                    break;
            }
        } else if (requestCode == REQUEST_CODE_CHOICE_GENDER) {
            if (data != null) {
                String gender = data.getStringExtra("activity_setting_gender");
                System.out.println("性别---" + gender);
                settingGender.setText(gender);
            }
        } else if (requestCode == REQUEST_CODE_SCHOOL) {
            getMyInfo();
        }
    }

    //跳转到系统相册
    private void applyForAlbumAuthority() {
        AndPermission.with(ActivitySetting.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_LOCATION)
                .permission(Permission.STORAGE)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(ActivitySetting.this, rationale).show();
                    }
                })
                .start();
    }

    /**
     * 选择图片文件
     */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SEL_IMG);
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.putExtra("crop", "true");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        imageName = System.currentTimeMillis() + ".jpg";
        tempFile = new File(dirStr + File.separator + imageName);
        System.out.println("文件位置---" + tempFile.getPath());
        tempUri = Uri.fromFile(tempFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        intent.putExtra("return-data", false); //裁剪后的数据不以bitmap的形式返回
        startActivityForResult(intent, CROP_IMAGE);
    }

    //上传图头像
    private void uploadImage() {

        System.out.println("imageName---" + imageName);

        Bitmap bitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        RequestBody upload = RequestBody.create(MediaType.parse("text/plain"), "upload");
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        tempFile
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
        if (!TextUtils.isEmpty(nickName)){
            imgMap.put("nickName", nickName);imgMap.put("flag",1);
        }

        if (gender == 1 || gender == 2)
            imgMap.put("gender", gender);
        if (!TextUtils.isEmpty(headImgId))
            imgMap.put("headImgId", headImgId);
        if (!TextUtils.isEmpty(userName))
            imgMap.put("userName", userName);

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
                            Toast.makeText(ActivitySetting.this, "门牌号已经存在", Toast.LENGTH_SHORT).show();
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
    private void queryUserUpdateLeftCount(int field){

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
                        if (userUpdateLeftCountRespond.getCode() == 0){
                            int letCount = userUpdateLeftCountRespond.getPayload().getInfo().getLeftCnt();
                            int tempField = userUpdateLeftCountRespond.getPayload().getInfo().getField();
                            System.out.println(tempField + "---编号");
                            if (tag == 1){

                                if (letCount > 0){
                                    showInputDialog("输入真实姓名","只有" + letCount + "次修改机会,请珍惜喵~");
                                }else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this,"姓名修改次数已用完咯");
                                }

                            }else if (tag == 3){
                                if (letCount > 0){
                                    AlertDialog dialog = new AlertDialog.Builder(ActivitySetting.this)
                                            .setMessage("只有" + letCount + "次修改机会,请珍惜喵~")
                                            .setPositiveButton("知道了", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent = new Intent(ActivitySetting.this, ClassList.class);
                                                    intent.putExtra("activity_setting_school", 10);
                                                    startActivityForResult(intent, REQUEST_CODE_SCHOOL);
                                                }
                                            })
                                            .show();
                                }else {
                                    DialogUtils.showInviteDialogInfo(ActivitySetting.this,"年级学校修改次数已用完咯");
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
                    public void onSubscribe(Disposable d) {}

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

                        SharePreferenceUtil.clear(ActivitySetting.this);
//                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_UIN);
//                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_TOKEN);
//                        SharePreferenceUtil.remove(ActivitySetting.this, YPlayConstant.YPLAY_VER);
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
        editText .setLayoutParams(etParam);

        if (tag == 1){
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
            editText.setSingleLine();
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEND
                            || actionId == EditorInfo.IME_ACTION_DONE
                            || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode()
                            && KeyEvent.ACTION_DOWN == event.getAction())){
                        System.out.println("回车键被点击");
                        return true;
                    }
                    return false;
                }
            });
        }else if (tag == 2){
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
}
