package com.yeejay.yplay.login;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;
import com.yeejay.yplay.R;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ImageUploadBody;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tangxiaolv.com.library.EffectiveShapeView;

public class UserInfo extends BaseActivity {

    private static final int REQ_CODE_SEL_IMG = 10;
    private static final int CROP_IMAGE = 11;
    private static final String BASE_URL_USER = "http://sh.file.myqcloud.com";
    private static final String IMAGE_AUTHORIZATION = "ZijsNfCd4w8zOyOIAnbyIykTgBdhPTEyNTMyMjkzNTUmYj15cGxheSZrPUFLSURyWjFFRzQwejcyaTdMS3NVZmFGZm9pTW15d2ZmbzRQViZlPTE1MTcxMjM1ODcmdD0xNTA5MzQ3NTg3JnI9MTAwJnU9MCZmPQ==";

    private File tempFile;
    private Uri tempUri;

    EffectiveShapeView userHeadImage;
    String imageName;
    EditText userName;
    String dirStr;

    private static final int REQUEST_CODE_PERMISSION_SINGLE_LOCATION = 200;

    PermissionListener mPermissionListener = new PermissionListener() {
        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
            System.out.println("相册权限申请成功");
            if (Build.VERSION.SDK_INT >= 23 && UserInfo.this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                UserInfo.this.requestPermissions(new String[]{
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
        setContentView(R.layout.activity_user_info);

        getWindow().setStatusBarColor(getResources().getColor(R.color.feeds_title_color));

        ImageButton btnBack = (ImageButton) findViewById(R.id.uif_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userHeadImage = (EffectiveShapeView) findViewById(R.id.uif_imgBtn);
        userName = (EditText) findViewById(R.id.uif_edt_name);
        Button nextStep = (Button) findViewById(R.id.uif_btn_next);

        userHeadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到系统相册
                System.out.println("跳转到系统相册");
                applyForAlbumAuthority();

            }
        });
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到答题页面
                System.out.println("跳转到答题页面");
                settingName(userName.getText().toString());
            }
        });

        initData();
    }

    private void initData() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        dirStr = root + File.separator + "yplay" + File.separator + "image";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK){
            switch(requestCode){
                case REQ_CODE_SEL_IMG:
                    //获取选择的图片的URI
                    Uri uri = data.getData();
                    cropImage(uri);
                    break;
                case CROP_IMAGE:
                    //图片裁剪完，已经保存到文件中
                    Bitmap bm = BitmapFactory.decodeFile(tempFile.getAbsolutePath());
                    System.out.println("图片位置---" + tempFile.getAbsolutePath());
                    uploadImage();
                    userHeadImage.setImageBitmap(bm);
                    break;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //点击空白处隐藏键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (UserInfo.this.getCurrentFocus() != null) {
                if (UserInfo.this.getCurrentFocus().getWindowToken() != null) {
                    imm.hideSoftInputFromWindow(UserInfo.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        }
        return super.onTouchEvent(event);
    }

    //跳转到系统相册
    private void applyForAlbumAuthority() {
        AndPermission.with(UserInfo.this)
                .requestCode(REQUEST_CODE_PERMISSION_SINGLE_LOCATION)
                .permission(Permission.STORAGE)
                .callback(mPermissionListener)
                .rationale(new RationaleListener() {
                    @Override
                    public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                        AndPermission.rationaleDialog(UserInfo.this, rationale).show();
                    }
                })
                .start();
    }

    /**
     * 选择图片文件
     */
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
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
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ImageUploadRespond imageUploadRespond) {
                        System.out.println("图片上传返回---" + imageUploadRespond.toString());
                        if (imageUploadRespond.getCode() == 0){
                            //保存图片id
                            SharePreferenceUtil.put(UserInfo.this,YPlayConstant.YPLAY_HEADER_IMG,imageName);
                            updateHeaderImg(imageName);
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
    private void updateHeaderImg(String headImgId){

        Map<String,Object> imgMap = new HashMap<>();
        imgMap.put("headImgId",headImgId);
        imgMap.put("uin", SharePreferenceUtil.get(UserInfo.this, YPlayConstant.YPLAY_UIN,0));
        imgMap.put("token",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        imgMap.put("ver",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_VER,0));


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
                        if (baseRespond.getCode() == 0){
                            System.out.println("修改图像成功---" + baseRespond.toString());
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

    //设置姓名
    private void settingName(String name){

        if (name.length() <= 0){
            Toast.makeText(UserInfo.this,"请设置姓名", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() > 20){
            Toast.makeText(UserInfo.this,"请输入合理的姓名长度", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,Object> nameMap = new HashMap<>();
        nameMap.put("nickname",name);
        nameMap.put("uin", SharePreferenceUtil.get(UserInfo.this, YPlayConstant.YPLAY_UIN,0));
        nameMap.put("token",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        nameMap.put("ver",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_VER,0));

        YPlayApiManger.getInstance().getZivApiService()
                .settingName(nameMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull BaseRespond baseRespond) {
                        System.out.println("设置名字---" + baseRespond.toString());
                        if (baseRespond.getCode() == 0){
                            startActivity(new Intent(UserInfo.this, AddFriendGuide.class));
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        System.out.println("设置名字异常---" + e.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
