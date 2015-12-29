package android.ye.com.loginDome.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.ye.com.loginDome.MainActivity;
import android.ye.com.loginDome.R;
import android.ye.com.loginDome.myConstant.Constant;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by And on 2015/12/17.
 */
public class ReceivableActivity extends Activity {
    public static final int RESULT_CODE = 111;
    private TextView tv_receivable,btn_back;
    private SharedPreferences sp;
    private EditText et_received;
    private Button btn_submit;

    private String result;

    private String dengluming;
    private String denglumima;
    private String zhubiaoid;
    private String position;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Intent intent = new Intent();
                    intent.putExtra("succeed", true);
                    setResult(RESULT_CODE, intent);
                    finish();
                    break;
                case 1:
                    Toast.makeText(ReceivableActivity.this,"提交失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivable);
        initView();
        initData();
        setData();

        /**
         * 延迟.5秒提出键盘
         */
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) et_received.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(et_received, 0);
            }

        }, 500);
        setListener();
    }

    private void setData() {
        String price = getIntent().getStringExtra("price");
        tv_receivable.setText(tv_receivable.getText() + "  " + price);
        //输入框焦点和全选
        et_received.setFocusable(true);
        et_received.setSelectAllOnFocus(true);
    }

    private void initData() {
        sp = getSharedPreferences(MainActivity.HANTHINK, MODE_PRIVATE);
        dengluming = sp.getString(MainActivity.HANTHINK_DENGLUMING, "");
        denglumima = sp.getString(MainActivity.HANTHINK_DENGLUMIMA,"");
        zhubiaoid = sp.getString(Constant.JSPEISONG_ZHUBIAOID, "");
    }
    private void setListener() {
        /**
         * 后退键
         */
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("succeed",false);
                setResult(RESULT_CODE, intent);
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_received.getText().toString().trim().isEmpty()){
                    Toast.makeText(ReceivableActivity.this, "应收款不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                final String str = et_received.getText().toString().trim();
                AlertDialog dialog = new AlertDialog.Builder(ReceivableActivity.this)
                        .setTitle("提交信息")
                        .setMessage("应收款:" +str + "\n是否确定提交")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendData(dengluming, denglumima, zhubiaoid, str);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        });
    }
    private void initView() {
        tv_receivable = (TextView) findViewById(R.id.tv_receivable);
        et_received = (EditText) findViewById(R.id.et_received);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_back = (TextView)findViewById(R.id.btn_back);
    }

    /**
     * 提交金额的方法
     * @param dengluming
     * @param denglumima
     * @param zhubiaoid
     * @param jine
     */
    public void sendData(final String dengluming, final String denglumima,final String zhubiaoid,final String jine) {
        new Thread() {
            @Override
            public void run() {
                try {
                    getData(dengluming, denglumima, zhubiaoid, jine);
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(1,2000);
                }
            }
        }.start();
    }
    private void getData(String dengluming, String denglumima, String zhubiaoid, String jine) throws IOException, XmlPullParserException {
        //初始化SOAP对象
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(Constant.NAMESPACE, Constant.JSSHOUKUAN_METHOD_NAME);
        //调用的参数
        request.addProperty(MainActivity.HANTHINK_DENGLUMING, dengluming);
        request.addProperty(MainActivity.HANTHINK_DENGLUMIMA, denglumima);
        request.addProperty(Constant.JSPEISONG_ZHUBIAOID,zhubiaoid);
        request.addProperty(Constant.ZONGJINE,jine);
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = request;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = true;
        // 等价于envelope.bodyOut = re;
        envelope.setOutputSoapObject(request);
        //调用SOAP
        HttpTransportSE androidHttpTransport = new HttpTransportSE(OrderActivity.URL);
        androidHttpTransport.debug = true;

        androidHttpTransport.call(Constant.JSSHOUKUAN_SOAP_ACTION, envelope);
        // 获得SOAP调用的结果
        SoapObject resultObject = (SoapObject) envelope.bodyIn;
        result = resultObject.getProperty(0).toString();
        System.out.println(result);
    }
}
