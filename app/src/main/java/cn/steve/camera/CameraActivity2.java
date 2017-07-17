package cn.steve.camera;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.steve.study.BuildConfig;
import cn.steve.study.R;

/**
 * Created by SteveYan on 2017/7/17.
 */

public class CameraActivity2 extends Activity {

    public final static int ALBUM_REQUEST_CODE = 1;
    public final static int CROP_REQUEST = 2;
    public final static int CAMERA_REQUEST_CODE = 3;
    public static String SAVED_IMAGE_DIR_PATH = Environment.getExternalStorageDirectory().getPath() + "/AppName/camera/";// 拍照路径
    String cameraPath;

    /**
     * 启动相机
     */
    public String startCamera(Activity activity, int requestCode) {
        // 指定相机拍摄照片保存地址
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent();
            // 指定开启系统相机的Action
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            if (!outDir.exists()) {
                outDir.mkdirs();
            }
            File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
            // 把文件地址转换成Uri格式
            Uri uri = FileProvider.getUriForFile(CameraActivity2.this, BuildConfig.APPLICATION_ID + ".provider", outFile);
            // 设置系统相机拍摄照片完成后图片文件的存放地址
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            // 此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
            activity.startActivityForResult(intent, requestCode);
            return outFile.getAbsolutePath();
        } else {
            Toast.makeText(activity, "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
            return null;
        }
    }

    @NonNull
    private List<Intent> getIntents() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LVMM");
        // Create the storage directory if it does not exist
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri imageUri = FileProvider.getUriForFile(CameraActivity2.this, BuildConfig.APPLICATION_ID + ".provider", file);

        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        return cameraIntents;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        findViewById(R.id.buttonMain).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera(CameraActivity2.this, CAMERA_REQUEST_CODE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST_CODE) {
            }
        }
    }

}
