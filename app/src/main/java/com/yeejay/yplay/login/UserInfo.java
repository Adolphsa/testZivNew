package com.yeejay.yplay.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.utils.ImageUtil;
import com.donkingliang.imageselector.utils.PhotoUtils;
import com.donkingliang.imageselector.utils.ToastUtils;
import com.yeejay.yplay.R;
import com.yeejay.yplay.YplayApplication;
import com.yeejay.yplay.api.YPlayApiManger;
import com.yeejay.yplay.base.BaseActivity;
import com.yeejay.yplay.model.BaseRespond;
import com.yeejay.yplay.model.ImSignatureRespond;
import com.yeejay.yplay.model.ImageUploadBody;
import com.yeejay.yplay.model.ImageUploadRespond;
import com.yeejay.yplay.utils.BaseUtils;
import com.yeejay.yplay.utils.DensityUtil;
import com.yeejay.yplay.utils.GsonUtil;
import com.yeejay.yplay.utils.LogUtils;
import com.yeejay.yplay.utils.SharePreferenceUtil;
import com.yeejay.yplay.utils.YPlayConstant;
import com.yeejay.yplay.wns.WnsAsyncHttp;
import com.yeejay.yplay.wns.WnsRequestListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import tangxiaolv.com.library.EffectiveShapeView;

import static com.donkingliang.imageselector.ImageSelectorActivity.hasSdcard;

public class UserInfo extends BaseActivity {

    private static final String TAG = "UserInfo";
    private static final int REQUEST_CODE = 0x00000011;
    private static final int CODE_CAMERA_REQUEST = 0xa01;
    private static final int CAMERA_PERMISSIONS_REQUEST_CODE = 0xa03;

    private File fileUri;
    private Uri imageUri;
    private static final String BASE_URL_USER = "http://sh.file.myqcloud.com";

    EffectiveShapeView userHeadImage;
    EditText userName;
    private String mImSig;

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
                //显示拍照和相册选项
                showImageBottomDialog();

            }
        });
        nextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到答题页面
                settingName(userName.getText().toString());
            }
        });


    }

    private void initData() {

        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dirStr = root + File.separator + "yplay" + File.separator + "image";
        File dir = new File(dirStr);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String tempImage = String.valueOf(System.currentTimeMillis());
        fileUri = new File(dirStr,tempImage + ".jpg");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String imagePath = "";
        String imageName = "";

        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);

                imagePath = images.get(0);
                imageName = imagePath.substring(imagePath.length() - 17, imagePath.length());
                Log.i(TAG, "onActivityResult: 相册imagePath---" + imagePath + ",imageName---" + imageName);

                Bitmap bm1 = ImageUtil.decodeImage(imagePath, 200, 200);
                if (bm1 != null) {
                    userHeadImage.setImageBitmap(bm1);
                    uploadImage(imagePath, imageName, bm1);
                }
            }

        } else if (requestCode == CODE_CAMERA_REQUEST) {

            imagePath = fileUri.getAbsolutePath();
            imageName = imagePath.substring(imagePath.length() - 17, imagePath.length());
            Log.i(TAG, "onActivityResult: 拍照imagePath---" + imagePath + ",imageName---" + imageName);

            Bitmap bm1 = ImageUtil.decodeImage(imagePath,200,200);
            if (bm1 != null){
                userHeadImage.setImageBitmap(bm1);
                uploadImage(imagePath, imageName,bm1);
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

    //获取上传头像签名
    private void getUploadImgSig(final String imagePath, final String imageName, final Bitmap bitmap) {
        final Map<String, Object> imMap = new HashMap<>();
        final int uin = (int) SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_UIN, (int) 0);
        imMap.put("uin", uin);
        imMap.put("token", SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_TOKEN, "yplay"));
        imMap.put("ver", SharePreferenceUtil.get(YplayApplication.getContext(), YPlayConstant.YPLAY_VER, 0));
        imMap.put("identifier", String.valueOf(uin));

        WnsAsyncHttp.wnsRequest(YPlayConstant.BASE_URL + YPlayConstant.API_GETHEADIMGUPLOADSIG, imMap,
                new WnsRequestListener() {

                    @Override
                    public void onNoInternet() {

                    }

                    @Override
                    public void onStartLoad(int value) {

                    }

                    @Override
                    public void onComplete(String result) {
                        ImSignatureRespond imSignatureRespond = GsonUtil.GsonToBean(result, ImSignatureRespond.class);
                        if (imSignatureRespond.getCode() == 0) {
                            mImSig = imSignatureRespond.getPayload().getSig();

                            uploadImageImpl(imagePath, imageName, bitmap);
                        }
                    }

                    @Override
                    public void onTimeOut() {

                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    //上传图头像前先获取im签名;
    private void uploadImage(String imagePath, final String imageName, final Bitmap bitmap) {
        SharedPreferences sp = YplayApplication.getContext().getSharedPreferences(
                YPlayConstant.SP_KEY_IM_SIG,
                Context.MODE_PRIVATE);
        mImSig = sp.getString("im_Sig", "");
        long expireTime = sp.getLong("expireTime", 0);
        long currentTime = BaseUtils.getCurrentDayTimeMillis();
        LogUtils.getInstance().debug("im_Sig = {}, expireTime = {}, currentTime = {}",
                mImSig, expireTime, currentTime);
        if(TextUtils.isEmpty(mImSig) || expireTime >= currentTime) {
            //说明需要从服务器重新获取最新的上传头像签名;
            getUploadImgSig(imagePath, imageName, bitmap);
        } else {
            //说明当前上传头像签名依然有效,则去真正的上传头像：
            uploadImageImpl(imagePath, imageName, bitmap);
        }
    }

    //上传图头像
    private void uploadImageImpl(String imagePath, final String imageName, final Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] datas = baos.toByteArray();
        Log.i(TAG, "uploadImage: 图片大小---" + datas.length);
        RequestBody upload = RequestBody.create(MediaType.parse("text/plain"), "upload");
        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        new File(imagePath));
        MultipartBody.Part aa = MultipartBody.Part.createFormData("filecontent", imageName, requestFile);

        ImageUploadBody body = new ImageUploadBody();
        body.setOp("upload");
        body.setFilecontent(datas);

        YPlayApiManger.getInstance().getZivApiServiceParameters(BASE_URL_USER)
                .uploadHeaderImg(mImSig, imageName, upload, aa)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ImageUploadRespond>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull ImageUploadRespond imageUploadRespond) {
                        Log.i(TAG, "onNext: 图片上传返回---" + imageUploadRespond.toString());
                        if (imageUploadRespond.getCode() == 0) {
                            //保存图片id
                            SharePreferenceUtil.put(UserInfo.this, YPlayConstant.YPLAY_HEADER_IMG, imageName);
                            updateHeaderImg(imageName);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Log.i(TAG, "onNext: 图片上传异常---" + e.getMessage());
                        Toast.makeText(UserInfo.this, R.string.head_img_upload_error,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //修改头像
    private void updateHeaderImg(String headImgId){

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_SET_AGE_URL;
        Map<String,Object> imgMap = new HashMap<>();
        imgMap.put("headImgId",headImgId);
        imgMap.put("uin", SharePreferenceUtil.get(UserInfo.this, YPlayConstant.YPLAY_UIN,0));
        imgMap.put("token",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        imgMap.put("ver",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_VER,0));

        WnsAsyncHttp.wnsRequest(url, imgMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 修改图像---" + result);
                BaseRespond baseRespond = GsonUtil.GsonToBean(result,BaseRespond.class);
                if (baseRespond.getCode() == 0){
                    Log.i(TAG, "onComplete: set header image success " + baseRespond.toString());
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });
    }

    //设置姓名
    private void settingName(String name){

        if (name.length() <= 0){
            Toast.makeText(UserInfo.this, R.string.uif_set_name, Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.length() > 20){
            Toast.makeText(UserInfo.this, R.string.uif_legal_name_length, Toast.LENGTH_SHORT).show();
            return;
        }

        String url = YPlayConstant.YPLAY_API_BASE + YPlayConstant.API_SET_AGE_URL;
        Map<String,Object> nameMap = new HashMap<>();
        nameMap.put("nickname",name);
        nameMap.put("uin", SharePreferenceUtil.get(UserInfo.this, YPlayConstant.YPLAY_UIN,0));
        nameMap.put("token",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_TOKEN,"yplay"));
        nameMap.put("ver",SharePreferenceUtil.get(UserInfo.this,YPlayConstant.YPLAY_VER,0));

        WnsAsyncHttp.wnsRequest(url, nameMap, new WnsRequestListener() {
            @Override
            public void onNoInternet() {

            }

            @Override
            public void onStartLoad(int value) {

            }

            @Override
            public void onComplete(String result) {
                Log.i(TAG, "onComplete: 设置名字---" + result);
                BaseRespond baseRespond = GsonUtil.GsonToBean(result,BaseRespond.class);
                if (baseRespond.getCode() == 0){
                    startActivity(new Intent(UserInfo.this, AddFriendGuide.class));
                }
            }

            @Override
            public void onTimeOut() {

            }

            @Override
            public void onError() {

            }
        });

    }

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
                        ImageSelectorUtils.openPhotoAndClip(UserInfo.this, REQUEST_CODE);
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
                ToastUtils.showShort(this, getString(R.string.uif_refuse_one));
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, CAMERA_PERMISSIONS_REQUEST_CODE);
        } else {//有权限直接调用系统相机拍照
            if (hasSdcard()) {
                imageUri = Uri.fromFile(fileUri);
                //通过FileProvider创建一个content类型的Uri
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(UserInfo.this, "com.donkingliang.imageselector", fileUri);
                }
                PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                Log.i(TAG, "autoObtainCameraPermission: CODE_CAMERA_REQUEST---" + CODE_CAMERA_REQUEST);
            } else {
                ToastUtils.showShort(this, getString(R.string.uif_device_no_sd));
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
                        imageUri = FileProvider.getUriForFile(UserInfo.this, "com.donkingliang.imageselector", fileUri);//通过FileProvider创建一个content类型的Uri
                    PhotoUtils.takePicture(this, imageUri, CODE_CAMERA_REQUEST);
                } else {
                    ToastUtils.showShort(this, getString(R.string.uif_device_no_sd));
                }
            } else {

                ToastUtils.showShort(this, getString(R.string.uif_please_open_camera));
            }
        }
    }
}
