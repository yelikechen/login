package android.ye.com.loginDome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by And on 2015/12/14.
 */
public class AllocationActivity extends Activity{
    public static final int REULIT_CODE = 1002;
    private EditText ed_submit;
    public static final String IPADDRESS = "ipAddress";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);

        ed_submit = (EditText) findViewById(R.id.ed_ip);
        findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ipAddress = ed_submit.getText().toString().trim();
                Intent intent = new Intent();
                intent.putExtra(IPADDRESS,ipAddress);
                setResult(REULIT_CODE,intent);
                finish();
            }
        });
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
