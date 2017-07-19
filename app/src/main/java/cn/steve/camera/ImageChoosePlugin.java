package cn.steve.camera;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.webkit.ValueCallback;

import java.io.File;

import cn.steve.study.BuildConfig;

import static android.app.Activity.RESULT_OK;


/**
 * Created by SteveYan on 2017/7/17.
 */

public class ImageChoosePlugin {

    private final static int FILECHOOSER_RESULTCODE = 0x00012;
    private Activity activity;
    private Uri takePhotoUri;
    private boolean aboveL;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mFilePathCallbackArray;
    private boolean takePhoto = false;

    public ImageChoosePlugin(Activity activity) {
        this.activity = activity;
    }

    public void setValueCallback(ValueCallback<Uri> mUploadMessage, ValueCallback<Uri[]> filePathCallbackArray) {
        this.mUploadMessage = mUploadMessage;
        this.mFilePathCallbackArray = filePathCallbackArray;
        this.aboveL = filePathCallbackArray != null;
    }

    private Intent getGalleryIntent() {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
        gallery.addCategory(Intent.CATEGORY_OPENABLE);
        gallery.setType("image/*");
        gallery.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        return gallery;
    }


    private Intent getCameraIntent() {
        Intent intent = new Intent();
        // 指定开启系统相机的Action
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        File outDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!outDir.exists()) {
            boolean success = outDir.mkdirs();
        }
        File outFile = new File(outDir, System.currentTimeMillis() + ".jpg");
        // 把文件地址转换成Uri格式
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            takePhotoUri = Uri.fromFile(outFile);
        } else {
            takePhotoUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider", outFile);
        }
        // 设置系统相机拍摄照片完成后图片文件的存放地址
        intent.putExtra(MediaStore.EXTRA_OUTPUT, takePhotoUri);
        // 此值在最低质量最小文件尺寸时是0，在最高质量最大文件尺寸时是１
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        return intent;
    }

    public void showMyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        CharSequence[] items = new CharSequence[]{"相机", "相册"};
        builder
            .setTitle("选择图片方式")
            .setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = null;
                    switch (which) {
                        case 0:
                            takePhoto = true;
                            intent = getCameraIntent();
                            Intent cameraIntent = Intent.createChooser(intent, "选择相机");
                            activity.startActivityForResult(cameraIntent, FILECHOOSER_RESULTCODE);
                            break;
                        case 1:
                            takePhoto = false;
                            intent = getGalleryIntent();
                            Intent galleryIntent = Intent.createChooser(intent, "选择相册");
                            activity.startActivityForResult(galleryIntent, FILECHOOSER_RESULTCODE);
                            break;
                    }
                }
            })
            .setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    if (aboveL) {
                        mFilePathCallbackArray.onReceiveValue(null);
                    } else {
                        mUploadMessage.onReceiveValue(null);
                    }
                }
            });
        builder.show();
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case FILECHOOSER_RESULTCODE:
                if (!aboveL) {
                    onActivityResultBelowL(requestCode, resultCode, data);
                } else {
                    if (null == mFilePathCallbackArray) {
                        return;
                    }
                    onActivityResultAboveL(requestCode, resultCode, data);
                }
        }
    }

    private void onActivityResultBelowL(int requestCode, int resultCode, Intent data) {
        if (null == mUploadMessage) {
            return;
        }
        Uri selectedBelowL = null;
        if (resultCode == RESULT_OK) {
            if (takePhoto) {
                selectedBelowL = data == null ? takePhotoUri : data.getData();
            } else {
                selectedBelowL = data == null ? null : data.getData();
            }
        }
        mUploadMessage.onReceiveValue(selectedBelowL);
        mUploadMessage = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mFilePathCallbackArray == null) {
            return;
        }
        Uri[] selectedAboveL = null;
        if (resultCode == RESULT_OK) {
            if (takePhoto) {
                selectedAboveL = new Uri[]{takePhotoUri};
            } else {
                if (data != null) {
                    String dataString = data.getDataString();
                    ClipData clipData = data.getClipData();
                    if (clipData != null) {
                        selectedAboveL = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            selectedAboveL[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        selectedAboveL = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
        }
        mFilePathCallbackArray.onReceiveValue(selectedAboveL);
        mFilePathCallbackArray = null;
    }

}
