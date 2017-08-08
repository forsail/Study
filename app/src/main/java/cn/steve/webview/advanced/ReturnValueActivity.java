package cn.steve.webview.advanced;

/**
 * Created by Steve on 2017/3/3.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.eclipsesource.v8.V8;

import cn.steve.study.R;

/**
 * @author Steve
 */
public class ReturnValueActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_button);
        this.textView = (TextView) findViewById(R.id.textView);
        V8 runtime = V8.createV8Runtime();
        String script = ""
                        + "var hello = 'hello, ';\n"
                        + "var world = 'world!';\n"
                        + "hello.concat(world).length;\n";
        int result = runtime.executeIntScript(script);
        this.textView.setText("" + result);
        runtime.release();
    }
}
