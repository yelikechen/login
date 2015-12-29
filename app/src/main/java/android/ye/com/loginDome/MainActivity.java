package android.ye.com.loginDome;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.ye.com.loginDome.utils.Utils;
import android.ye.com.loginDome.work.OrderActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1001;
    public static String URL;
    public static final String HANTHINK = "hanthink";
    public static  final String HANTHINK_DENGLUMING = "dengluming";
    public static  final String HANTHINK_DENGLUMIMA = "denglumima";
    public static  final String HANTHINK_IPADDRESS = "ipaddress";
    //命名空间
    public static final String NAMESPACE = "http://HanThink.com/";
    // 调用的方法名称
    public static final String METHOD_NAME = "jsyanzheng";
    // SOAP Action
    public static final String SOAP_ACTION = "http://HanThink.com/jsyanzheng";


    private String dengluming;
    private String denglumima;

    private EditText ed_id;
    private EditText ed_password;
    private SharedPreferences sp;
    private Button btn_login;
    private TextView tv_regist;
    private String ipAddress;
    private String result;
    private EditText ed_fuwuqi;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show();
                    if("6".equals(result)){
                        Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                        sp.edit().putString(HANTHINK_DENGLUMING, ed_id.getText().toString().trim()).apply();
                        sp.edit().putString(HANTHINK_DENGLUMIMA, denglumima).apply();
                        sp.edit().putString(HANTHINK_IPADDRESS, ed_fuwuqi.getText().toString().trim()).apply();
                        startActivity(intent);
                        finish();
                    }else if("4".equals(result)){
                        Toast.makeText(MainActivity.this,"用户名或密码错误",Toast.LENGTH_SHORT).show();
                        ed_password.setText("");
                        return;
                    }
                    break;
                case 1:
                    Toast.makeText(MainActivity.this,"网络异常,加载失败",Toast.LENGTH_SHORT).show();
                    return;

            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initView();
        setData();
        setListener();
    }

    /**
     * 设置初始值
     */
    private void setData() {
        ed_id.setText(sp.getString(HANTHINK_DENGLUMING, ""));
        ed_fuwuqi.setText(sp.getString(HANTHINK_IPADDRESS, ""));

    }

    /**
     * 定义值
     */
    private void initView() {
        ed_id = (EditText) findViewById(R.id.ed_id);
        ed_password = (EditText) findViewById(R.id.ed_password);
        btn_login = (Button) findViewById(R.id.btn_login);
        tv_regist = (TextView) findViewById(R.id.tv_regist);
        ed_fuwuqi = (EditText)findViewById(R.id.ed_fuwuqi);
        sp = getSharedPreferences(HANTHINK, MODE_PRIVATE);
    }

    public void setListener() {
        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_login:
                        btn_login.setText("正在登陆....");
                        dengluming = ed_id.getText().toString().trim();
                        denglumima = ed_password.getText().toString().trim();
                        denglumima = Utils.jiami(denglumima);
                        ipAddress = ed_fuwuqi.getText().toString().trim();
                        URL = "http://"+ipAddress+"/hanthinkserver/service1.asmx";
                        Log.e("URL", URL);
                        //给密码加密
                        if (dengluming.isEmpty() || denglumima.isEmpty()) {
                            Toast.makeText(MainActivity.this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //调用验证的接口jsyanzheng
                        jsyanzheng(dengluming, denglumima);
                        break;
                    case R.id.tv_regist:
                        Intent intent = new Intent(MainActivity.this, AllocationActivity.class);
                        startActivityForResult(intent, REQUEST_CODE);
                        break;
                }
            }
        };
        tv_regist.setOnClickListener(mClickListener);
        btn_login.setOnClickListener(mClickListener);
    }

    /**
     * 验证用户名和密码
     * @param dengluming
     * @param denglumima
     */
    public void jsyanzheng(final String dengluming, final String denglumima) {
        new Thread() {
            @Override
            public void run() {
                try {
                    getYanZheng();
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(1,2000);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * 验证的方法
     * @return
     */
    private String getYanZheng() throws IOException, XmlPullParserException {
        //初始化SOAP对象
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
        //调用的参数
        request.addProperty(HANTHINK_DENGLUMING, dengluming);
        request.addProperty(HANTHINK_DENGLUMIMA, denglumima);
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = re;
        envelope.setOutputSoapObject(request);
        //调用SOAP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(MainActivity.URL);
        androidHttpTransport.debug = true;
        androidHttpTransport.call(SOAP_ACTION, envelope);
            // 获得SOAP调用的结果
        SoapObject resultObject = (SoapObject) envelope.bodyIn;
        result = resultObject.getProperty(0).toString();
        return result;
    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == AllocationActivity.REULIT_CODE) {
            switch (requestCode) {
                case REQUEST_CODE:
                    ipAddress = data.getStringExtra(AllocationActivity.IPADDRESS);
                    ed_fuwuqi.setText(ipAddress);
                    break;
            }
        }
    }
}
