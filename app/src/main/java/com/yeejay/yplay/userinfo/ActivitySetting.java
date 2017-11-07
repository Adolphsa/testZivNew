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
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.login.ChoiceSex;
import com.yeejay.yplay.login.ClassList;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ImageUploadBody;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.model.UserInfoResponde;
import com.yeejay.yplay.utils.FriendFeedsUtil;
import com.yeejay.yplay.utils.SharePreferenceUtil;
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

public class ActivitySetting extends AppCompatActivity {

    @BindView(R.id.layout_title_back)
    Button layoutTitleBack;
    @BindView(R.id.layout_title)
    TextView layoutTitle;
    @BindView(R.id.setting_img_header)
    EffectiveShapeView settingImgHeader;
    @BindView(R.id.setting_name)
    TextView settingName;
    @BindView(R.id.setting_user_name)
    TextView settingUserName;
    @BindView(R.id.setting_gender)
    TextView settingGender;
    @BindView(R.id.setting_school_info)
    TextView settingSchoolInfo;
    @BindView(R.id.setting_phone_number)
    TextView settingPhoneNumber;
    @BindView(R.id.setting_exit)
    Button settingButton;

    @OnClick(R.id.layout_title_back)
    public void back() {
        finish();
    }

    int tag;

    //头像
    @OnClick(R.id.setting_img_header)
    public void settingImgHeader() {
        System.out.println("头像");
        tag = 0;
        applyForAlbumAuthority();

    }

    //姓名
    @OnClick(R.id.setting_name)
    public void settingName() {
        System.out.println("姓名");
        tag = 1;
        showInputDialog("输入真实姓名","只有两次修改机会");
    }

    //用户名
    @OnClick(R.id.setting_user_name)
    public void setSettingUserName() {
        System.out.println("用户名");
        tag = 2;
        showInputDialog("修改用户名","");
    }

    //性别
    @OnClick(R.id.setting_gender)
    public void setSettingGender() {
        System.out.println("性别");
        Intent intent = new Intent(ActivitySetting.this, ChoiceSex.class);
        intent.putExtra("activity_setting",1);
        startActivityForResult(intent,REQUEST_CODE_CHOICE_GENDER);
    }

    //学校信息
    @OnClick(R.id.setting_school_info)
    public void setSettingSchoolInfo() {
        System.out.println("学校信息");
        Intent intent = new Intent(ActivitySetting.this, ClassList.class);
        intent.putExtra("activity_setting_school",10);
        startActivityForResult(intent,REQUEST_CODE_SCHOOL);
    }

    //电话号码
    @OnClick(R.id.setting_phone_number)
    public void settingPhoneNumber() {
        System.out.println("电话号码");
    }

    //退出
    @OnClick(R.id.setting_exit)
    public void settingExit() {
        System.out.println("退出");

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

        layoutTitle.setText("设置");
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
    private void initView(UserInfoResponde.PayloadBean.InfoBean infoBean){
        String url = infoBean.getHeadImgUrl();
        if (!TextUtils.isEmpty(url))
            Picasso.with(ActivitySetting.this).load(url).resize(93,93).into(settingImgHeader);
            settingName.setText(infoBean.getNickName());
            settingUserName.setText(infoBean.getUserName());
            settingGender.setText(infoBean.getGender() == 1 ? "男" : "女");
            StringBuilder str = new StringBuilder(infoBean.getSchoolName());
            str.append(FriendFeedsUtil.schoolType(infoBean.getSchoolType(),infoBean.getGrade()));
            settingSchoolInfo.setText(str);
            settingPhoneNumber.setText(infoBean.getPhone());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case REQ_CODE_SEL_IMG:
                    //获取选择的图片的URI
                    if (data != null){
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
        }else if (requestCode == REQUEST_CODE_CHOICE_GENDER){
            if (data != null){
                String  gender = data.getStringExtra("activity_setting_gender");
                System.out.println("性别---" + gender);
                settingGender.setText(gender);
            }
        }else if (requestCode == REQUEST_CODE_SCHOOL){
            System.out.println("那你看那看---" + requestCode);
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
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SEL_IMG);
    }

    /**
     * 裁剪图片
     * @param uri
     */
    private void cropImage(Uri uri){
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
    private void uploadImage(){

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
                .uploadHeaderImg(IMAGE_AUTHORIZATION, imageName , upload,aa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUploadRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {}

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ImageUploadRespond imageUploadRespond) {
                        System.out.println("图片上传返回---" + imageUploadRespond.toString());
                        if (imageUploadRespond.getCode() == 0){
                            //保存图片id
                            SharePreferenceUtil.put(ActivitySetting.this, YPlayConstant.YPLAY_HEADER_IMG,imageName);
                            updateHeaderImg(imageName,null,0,null);
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
    private void updateHeaderImg(String headImgId, final String nickName, int gender, final String userName){

        Map<String,Object> imgMap = new HashMap<>();
        if (!TextUtils.isEmpty(nickName))
            imgMap.put("nickName",nickName);
        if (gender == 1 || gender == 2)
            imgMap.put("gender",gender);
        if (!TextUtils.isEmpty(headImgId))
            imgMap.put("headImgId",headImgId);
        if (!TextUtils.isEmpty(userName))
            imgMap.put("userName",userName);

        imgMap.put("uin", SharePreferenceUtil.get(ActivitySetting.this, YPlayConstant.YPLAY_UIN,0));
        imgMap.put("token",SharePreferenceUtil.get(ActivitySetting.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        imgMap.put("ver",SharePreferenceUtil.get(ActivitySetting.this,YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .updateHeaderImg(imgMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {}

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BaseRespond baseRespond) {
                        if (baseRespond.getCode() == 0){
                            if (tag == 0){
                                System.out.println("修改图像成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this,"头像修改成功",Toast.LENGTH_SHORT).show();
                            }else if (tag == 1){
                                System.out.println("修改姓名成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this,"修改姓名成功",Toast.LENGTH_SHORT).show();
                                settingName.setText(nickName);
                            }else if (tag == 2){
                                System.out.println("修改用户名成功---" + baseRespond.toString());
                                Toast.makeText(ActivitySetting.this,"修改用户名成功",Toast.LENGTH_SHORT).show();
                                settingUserName.setText(userName);
                            }

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
    private void getMyInfo(){

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
                        if (userInfoResponde.getCode() == 0){
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

    //展示对话框
     private void showInputDialog(String title,String message){

         final EditText editText = new EditText(ActivitySetting.this);
         AlertDialog.Builder inputDialog =
                 new AlertDialog.Builder(ActivitySetting.this);
         inputDialog.setTitle(title).setView(editText);
         if (!TextUtils.isEmpty(message)){
             inputDialog.setMessage(message);
         }
         inputDialog.setPositiveButton("确定",
                 new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         String name = editText.getText().toString().trim();
                         if (name.length() > 0){
                             if (tag == 1){
                                 System.out.println("修改姓名---" + name);
                                 updateHeaderImg(null,name,0,null);
                             }else if (tag == 2){
                                 System.out.println("修改用户名---" + name);
                                 updateHeaderImg(null,null,0,name);
                             }
                         }else {
                             Toast.makeText(ActivitySetting.this,"字符长度不能为0",Toast.LENGTH_SHORT).show();
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
