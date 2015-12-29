package android.ye.com.loginDome.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.ye.com.loginDome.MainActivity;
import android.ye.com.loginDome.R;
import android.ye.com.loginDome.domain.ProductBean;
import android.ye.com.loginDome.myConstant.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by And on 2015/12/15.
 */
public class OrderDetailActivity extends Activity {
    //文件
    private SharedPreferences sp;
    private String id;
    private Object result;
    private List<ProductBean> products;
    private ProductBean productBean;
    private ViewHolyed holyed;
    //组件
    private ListView listView;
    private MyAdapter myAdapter;
    //提交按钮
    private Button btn_upload;
    private EditText et_mount;
    private TextView sum;
    private Button btn_receivable;
    //后退
    private TextView btn_back;
    private View myView;
    //初始化SOAP对象
    private String money;
    private String dengluming;
    private String denglumima;
    //照片位置
    String file_str = Environment.getExternalStorageDirectory().getPath();
    File mars_file = new File(file_str + "/my_camera");
    File file_go = new File(file_str + "/my_camera/file.jpg");

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    myAdapter = new MyAdapter(OrderDetailActivity.this,products);
                    dialog.dismiss();
                    listView.setAdapter(myAdapter);
                    break;
                case 1:
                    products = (List<ProductBean>) msg.obj;
                    myAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    tupian = "";
                    Toast.makeText(OrderDetailActivity.this,"签收成功",Toast.LENGTH_SHORT).show();
                    Timer timer = new Timer();// 实例化Timer类
                    timer.schedule(new TimerTask() {
                        public void run() {
                            finish();
                        }
                    }, 500);// 这里百毫秒

                    break;
                case 3:
                    Toast.makeText(OrderDetailActivity.this,"网络连接超时",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case 4:
                    Toast.makeText(OrderDetailActivity.this,"签收失败",Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private ProgressDialog dialog;
    private String tupian;
    private String zhubiaoid;
    private String jsneirong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //初始化控件
        initView();
        //初始化数据
        initData();
        //设置数据
        setData();
        getData(dengluming, denglumima, id);
        setListener();
    }

    private void setData() {
        dengluming = sp.getString(MainActivity.HANTHINK_DENGLUMING, "");
        denglumima = sp.getString(MainActivity.HANTHINK_DENGLUMIMA,"");
        sum.setText(sum.getText() + money);
    }

    private void initData() {
        id = getIntent().getStringExtra(Constant.JSPEISONG_ZHUBIAOID);
        money = getIntent().getStringExtra(Constant.ZONGJINE);
        products = new ArrayList<>();
        sp = getSharedPreferences(MainActivity.HANTHINK,MODE_PRIVATE);
    }
    private void initView() {
        listView = (ListView) findViewById(R.id.listView);
        sum = (TextView) findViewById(R.id.sum);
        btn_back = (TextView) findViewById(R.id.btn_back);
        btn_upload = (Button)findViewById(R.id.btn_upload);
        btn_receivable = (Button) findViewById(R.id.btn_receivable);
        dialog = new ProgressDialog(OrderDetailActivity.this);
        dialog.setMessage("正在加载。。。。");
        dialog.show();
    }
    /**
     * 生成json
     * @return
     * @throws JSONException
     */
    private String shengChengJSon() throws JSONException {
        JSONArray json = new JSONArray();
        for (int i = 0; i < products.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            productBean = products.get(i);
            jsonObject.put("id",productBean.getId());
            jsonObject.put("mingcheng", productBean.getMingcheng());
            jsonObject.put("jiliangdanwei", productBean.getJiliangdanwei());
            jsonObject.put("数量", productBean.getMount());
            jsonObject.put("单价", productBean.getPrice());
            jsonObject.put("金额", productBean.getMoney());
            jsonObject.put("chuliliang", productBean.getQianshoumount());
            json.put(jsonObject);
            jsneirong = json.toString();
        }
        return jsneirong;
    }
    private void setListener() {
        final View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btn_receivable:



                        break;
                    case R.id.btn_back:
                        finish();
                        break;
                    /**
                     * 提交订单
                     */
                    case R.id.btn_upload:
                        try {
                            tiJiaoPhoto();
                            zhubiaoid = id;
                            jsneirong = shengChengJSon();
                            jsqianshou(dengluming, denglumima, tupian, zhubiaoid, jsneirong);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            Log.e("@@@@@","出错了");
                        }
//                        }  catch (Exception e) {
//                            Toast.makeText(OrderDetailActivity.this,"配送单提交失败",Toast.LENGTH_SHORT).show();
//                        }
                        break;
                }
            }
        };
        btn_back.setOnClickListener(mClickListener);
        btn_upload.setOnClickListener(mClickListener);
        btn_receivable.setOnClickListener(mClickListener);
        /**
         * 点击列表项修改签收订单
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.e("POSITION",position+"");
                myView = LayoutInflater.from(OrderDetailActivity.this).inflate(R.layout.view_mount, null);
                et_mount = (EditText) myView.findViewById(R.id.et_mount);
                productBean = products.get(position);
                Log.e("OrderDetailActivity", productBean.toString());
                et_mount.setText(productBean.getMount());
                //全选的
                et_mount.setSelectAllOnFocus(true);
                AlertDialog dialog = new AlertDialog.Builder(OrderDetailActivity.this)
                        .setView(myView)
                        .setTitle("修改签收 " + productBean.getMingcheng() + " 数量")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String shuliang = et_mount.getText().toString().trim();
                                Log.e("OrderDetailActivity", productBean.toString());
                                productBean = products.get(position);
                                productBean.setQianshoumount(shuliang);
                                Log.e("OrderDeatilActivity", productBean.toString());
                                products.set(position, productBean);
                                Log.e("OPSITION", position + "");
                                Log.e("OrderDeatilActivity", products.toString());
                                Message msg = Message.obtain();
                                msg.obj = products;
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                /** 3.自动弹出软键盘 **/
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    public void onShow(DialogInterface dialog) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(et_mount, InputMethodManager.SHOW_IMPLICIT);
                    }
                });
                dialog.show();
            }
        });
    }

    /**
     * 将照片转为字符串
     * @throws Exception
     */
    private void tiJiaoPhoto() throws Exception {
        if(!file_go.exists())
        {
            file_go.createNewFile();
        }
        FileInputStream fis = null;
            fis = new FileInputStream(file_go);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int count = 0;
            while((count = fis.read(buffer)) >= 0){
                baos.write(buffer, 0, count);
            }
            tupian  = new String(Base64.encode(baos.toByteArray(),2));  //进行Base64编码
    }

    /**
     * 签收方法
     * @param dengluming
     * @param denglumima
     * @param tupian
     * @param zhubiaoid
     * @param jsneirong
     */
    public void jsqianshou(final String dengluming, final String denglumima, final String tupian, final String zhubiaoid, final String jsneirong){
        new Thread() {
            @Override
            public void run() {
                try {
                    qingShou(dengluming, denglumima, zhubiaoid, tupian, jsneirong);
                } catch (Exception e) {
                    handler.sendEmptyMessage(4);
                }
                handler.sendEmptyMessage(2);
            }
        }.start();
    }

    /**
     * 签收上传
     * @param dengluming
     * @param denglumima
     * @param zhubiaoid
     * @param tupian
     * @param jsneirong
     * @throws IOException
     * @throws XmlPullParserException
     */
    private void qingShou(String dengluming, String denglumima, String zhubiaoid, String tupian, String jsneirong) throws IOException, XmlPullParserException {
        //初始化SOAP对象
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(MainActivity.NAMESPACE, Constant.JSQINGSHOU_METHOD_NAME);
        //调用的参数
        request.addProperty(MainActivity.HANTHINK_DENGLUMING, dengluming);
        request.addProperty(MainActivity.HANTHINK_DENGLUMIMA, denglumima);
        request.addProperty(Constant.TUIPIAN,tupian);
        request.addProperty("zhubiaoid", zhubiaoid);
        request.addProperty(Constant.JSNEIRONG,jsneirong);

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
            androidHttpTransport.call(Constant.JSQINGSHOU_SOAP_ACTION, envelope);
            // 获得SOAP调用的结果
            result = envelope.getResponse();
            String str = result.toString();
    }

    /**
     * 获得详细信息
     * @param dengluming
     * @param denglumima
     * @param zhubiaoid
     */
    public void getData(final String dengluming, final String denglumima,final String zhubiaoid ) {
        new Thread() {
            @Override
            public void run() {
                String str = null;
                try {
                    str = getPeiSongMX(dengluming, denglumima, zhubiaoid);
                    JSonJieXi(str);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(3, 2000);
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * json解析,获得products列表
     * @param str
     * @throws JSONException
     */
    private void JSonJieXi(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);
        JSONArray jsonArray = jsonObject.getJSONArray("Table");
        for(int i=0;i<jsonArray.length();i++){
            JSONObject object = (JSONObject) jsonArray.get(i);
            productBean = new ProductBean();
            productBean.setId(object.getString("id"));
            productBean.setJiliangdanwei(object.getString("jiliangdanwei"));
            productBean.setMingcheng(object.getString("mingcheng"));
            productBean.setPrice(object.getString("单价"));
            productBean.setMoney(object.getString("金额"));
            productBean.setMount(object.getString("数量"));
            productBean.setQianshoumount(object.getString("数量"));
            products.add(productBean);
            Log.e("Products", productBean.toString());
        }
    }

    /**
     * 获得MXjson数据
     * @param dengluming
     * @param denglumima
     * @param zhubiaoid
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String getPeiSongMX(String dengluming, String denglumima, String zhubiaoid) throws IOException, XmlPullParserException {
        //初始化SOAP对象
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(Constant.NAMESPACE,Constant.JSPEISONGMX_METHOD_NAME);
        //调用的参数
        Log.e("DADA", dengluming + denglumima + zhubiaoid);
        request.addProperty(MainActivity.HANTHINK_DENGLUMING, dengluming);
        request.addProperty(MainActivity.HANTHINK_DENGLUMIMA, denglumima);
        request.addProperty("zhubiaoid", zhubiaoid);

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
        androidHttpTransport.call(Constant.JSPEISONGMX_SOAP_ACTION, envelope);
        // 获得SOAP调用的结果
        result = envelope.getResponse();
        String str = result.toString();
        Log.e("@@@@",str);
        return str;
    }

    /**
     * listview适配器
     */
    private class MyAdapter extends BaseAdapter{
        private Context context;
        private List<ProductBean> products;
        private LayoutInflater layoutInflater;
        public MyAdapter(Context context,List<ProductBean> products){
            this.context = context;
            this.products = products;
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            return products.size();
        }

        @Override
        public Object getItem(int position) {
            return products.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            productBean = products.get(position);
            if(convertView==null){
                convertView = layoutInflater.inflate(R.layout.detail_item,null);
                holyed = new ViewHolyed();
                holyed.tv_jiliangdanwei = (TextView) convertView.findViewById(R.id.tv_jiliangdanwei);
                holyed.tv_jiliangdanwei2 = (TextView) convertView.findViewById(R.id.tv_jiliangdanwei2);
                holyed.tv_mingcheng = (TextView)convertView.findViewById(R.id.tv_mingcheng);
                holyed.tv_money = (TextView)convertView.findViewById(R.id.tv_money);
                holyed.tv_mount = (TextView)convertView.findViewById(R.id.tv_mount);
                holyed.tv_qianshoumount = (TextView)convertView.findViewById(R.id.tv_qianshoumount);
                holyed.tv_price = (TextView)convertView.findViewById(R.id.tv_price);
                convertView.setTag(holyed);
            }else {
                holyed = (ViewHolyed) convertView.getTag();
            }
            holyed.tv_jiliangdanwei.setText(productBean.getJiliangdanwei());
            holyed.tv_mount.setText("数量:" + productBean.getMount());
            holyed.tv_money.setText("金额:"+productBean.getMoney());
            holyed.tv_price.setText("单价:"+productBean.getPrice());
            holyed.tv_qianshoumount.setText("签收数量:" + productBean.getQianshoumount());
            holyed.tv_jiliangdanwei2.setText(productBean.getJiliangdanwei());
            holyed.tv_mingcheng.setText(productBean.getMingcheng());
            return convertView;
        }
    }
    static class ViewHolyed{
        private TextView tv_mingcheng,
                tv_mount,
                tv_jiliangdanwei,
                tv_price,
                tv_money,
                tv_qianshoumount,
                tv_jiliangdanwei2;
    }
}
