package cn.steve.flavors;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import cn.steve.study.BuildConfig;
import cn.steve.study.R;

/**
 * Created by yantinggeng on 2017/1/24.
 */

public class ProductFlavorMainActivity extends AppCompatActivity {

    private Button buttonMain;
    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        assignViews();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("BuildConfig.addField:").append(BuildConfig.addField);
        stringBuffer.append("\n");
        stringBuffer.append("addResString: ").append(getResources().getString(R.string.addResString));
        stringBuffer.append("\n");
        stringBuffer.append("MetaData: ").append(readMetaDataFromApplication());
        stringBuffer.append("\n");
        textView.setText(stringBuffer);
    }

    private void assignViews() {
        buttonMain = (Button) findViewById(R.id.buttonMain);
        textView = (TextView) findViewById(R.id.textView);
    }

    /**
     * 读取application 节点 meta-data 信息
     */
    private String readMetaDataFromApplication() {
        String productFlavorName;
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            productFlavorName = appInfo.metaData.getString("ProductFlavorName");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            productFlavorName = "";
        }
        return productFlavorName;
    }


}
