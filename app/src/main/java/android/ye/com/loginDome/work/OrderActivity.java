package android.ye.com.loginDome.work;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.ye.com.loginDome.MainActivity;
import android.ye.com.loginDome.R;
import android.ye.com.loginDome.domain.CommonBean;
import android.ye.com.loginDome.domain.OrderBean;
import android.ye.com.loginDome.list.DTListViewListener;
import android.ye.com.loginDome.list.SwipeMenu;
import android.ye.com.loginDome.list.SwipeMenuCreator;
import android.ye.com.loginDome.list.SwipeMenuItem;
import android.ye.com.loginDome.list.SwipeMenuListView;
import android.ye.com.loginDome.myConstant.Constant;
import android.ye.com.loginDome.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
/**
 * Created by And on 2015/12/15.
 */
public class OrderActivity extends Activity implements DTListViewListener{
    /**
     * 存照片文件
     */
    private String file_str = Environment.getExternalStorageDirectory().getPath();
    private File mars_file = new File(file_str + "/my_camera");
    private File file_go = new File(file_str + "/my_camera/file.jpg");
    private DisplayMetrics dm;
    //请求码
    private static final int REQUEST_CODE = 100;
    //显示的
    private String id;
    private String biaoti;
    private String miaoshu;
    //
    private List<OrderBean> orders;
    //List数据
    private List<CommonBean> commons;
    private List<CommonBean> commonsSon;
    private OrderBean orderBean;
    //组件
    private SwipeMenuListView lv_order;
    private Button btn_query;
    private EditText et_query;
    private Button btn_dataquery;
    //没有记录
    private FrameLayout fl_neirong;
    private TextView tv_no;
    //加载框
    private ProgressDialog dialog;

    //适配器
    private MyAdapter myAdapter;
    private int posi;

    //初始化SOAP对象
    private String dengluming;
    private String denglumima;
    private String riqi1;
    private String riqi2;
    private String yeshu = "0";
    private String guanjianzi="";
    public static String URL ;
    private String ipAddress;
    private Object result;

    //监听方法
    private View.OnClickListener mClickListener;

    //文件存储
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private boolean succeed = false;

    //日期
    private Date mDate;
    private Utils utils;
    //下拉用的
    private int n;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if(commons.size()==0) {
                        dialog.dismiss();
                        tv_no.setVisibility(View.VISIBLE);
                    }else {
                        tv_no.setVisibility(View.GONE);
                        dialog.dismiss();
                        myAdapter = new MyAdapter(OrderActivity.this, commons);
                        lv_order.setAdapter(myAdapter);
                    }
                    //隐藏“没用记录”
                   // onDTListRefresh();
                    break;
                case 1:
                    int position = (int) msg.obj;
                    System.out.println(position);
                    break;
                case 2:
                    Toast.makeText(OrderActivity.this, "网络连接超时，请检查网络", Toast.LENGTH_SHORT).show();
                    break;
                /**
                 * 日期查询
                 */
                case 3:
                    riqi1 = (String) msg.obj;
                    if(riqi1.equals(Utils.getPreDate())){
                        riqi1 = Utils.getPreDate();
                        riqi2 = Utils.getDate();
                    }else {
                        riqi2 = (String) msg.obj;
                    }
                    if(commons.size()!=0) {
                        myAdapter.clearList();
                    }
                    dialog = ProgressDialog.show(OrderActivity.this, "请稍等...", "获取数据中...", true);
                    getData(dengluming,denglumima,riqi1,riqi2,yeshu, guanjianzi);
                    if(myAdapter.getList().size()==0){
                        tv_no.setVisibility(View.VISIBLE);
                    }else {
                        tv_no.setVisibility(View.GONE);
                        myAdapter.notifyDataSetChanged();

                    }
                    riqi1 = Utils.getPreDate();
                    riqi2 = Utils.getDate();
                    break;
                /**
                 * 查询
                 */
                case 4:
                    if(myAdapter.getList().size()!=0) {
                        tv_no.setVisibility(View.GONE);
                        myAdapter.notifyDataSetChanged();

                    }else {
                        tv_no.setVisibility(View.VISIBLE);
                    }
                    et_query.clearFocus();
                    Toast.makeText(OrderActivity.this,"订单查询成功",Toast.LENGTH_SHORT).show();
                    guanjianzi="";
                    et_query.setText("");
                    break;
                case 5:
                    for (int i = 0; i < 20; i++) {
                        try {
                            myAdapter.addItemLast(commonsSon.get(i));
                        }catch (Exception e){
                            Toast.makeText(OrderActivity.this, "加载完成",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    myAdapter.notifyDataSetChanged();
                    lv_order.setSelection(n * 20);
                    lv_order.stopLoadMore();
                    lv_order.stopRefresh();
                    break;
                case 6:
                        commons = (List<CommonBean>) msg.obj;
                        tv_no.setVisibility(View.GONE);
                        lv_order.requestLayout();
                        myAdapter.setDeviceList(commons);
                        lv_order.stopLoadMore();
                        lv_order.stopRefresh();
                    break;
                case 8:
                    Toast.makeText(OrderActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
                case 10:
                    Toast.makeText(OrderActivity.this,"连接超时,请检查网络",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
            }
        }
    };
    private CommonBean info;
    private String fukuanyaoqiu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
        initData();
        //设置数据
        setData();
        //初始化list
        initList();
        //监听事件
        setLinster();
    }

    private void initView() {
        lv_order = (SwipeMenuListView) findViewById(R.id.lv_order);
        btn_query = (Button) findViewById(R.id.btn_query);
        et_query = (EditText) findViewById(R.id.et_query);
        tv_no = (TextView) findViewById(R.id.tv_no);
        fl_neirong = (FrameLayout)findViewById(R.id.fl_neirong);
        btn_dataquery = (Button) findViewById(R.id.btn_dataquery);
        utils = new Utils();
    }

    private void initData() {
        sp = getSharedPreferences(MainActivity.HANTHINK, MODE_PRIVATE);
        orders = new ArrayList<>();
        orderBean = new OrderBean();
        commons = new ArrayList<>();
        dialog = ProgressDialog.show(OrderActivity.this, "请稍等...", "获取数据中...", true);
    }


    private void setData() {
        lv_order.setPullLoadEnable(true);
        lv_order.setPullRefreshEnable(true);
        lv_order.setXListViewListener(this);
        /**
         * 初始化日期的值
         */
        riqi2 = Utils.getDate();
        riqi1 = Utils.getPreDate();
        sp = getSharedPreferences(MainActivity.HANTHINK, MODE_PRIVATE);
        dengluming = sp.getString(MainActivity.HANTHINK_DENGLUMING, "");
        denglumima= sp.getString(MainActivity.HANTHINK_DENGLUMIMA, "");
        ipAddress = sp.getString(MainActivity.HANTHINK_IPADDRESS,"");
        URL = "http://"+ipAddress+"/hanthinkserver/service1.asmx";
        editor = sp.edit();
        getData(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
        //如果小于20行，不能加载更多
        if(commons.size()<20){
            lv_order.setPullLoadEnable(false);
        }
    }



    private void setLinster() {
        /**
         * 跳转到mx界面，传递主表id，总金额
         */
        lv_order.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
                try {
                    intent.putExtra(Constant.JSPEISONG_ZHUBIAOID, commons.get(position).getId());
                    intent.putExtra(Constant.ZONGJINE, orders.get(position).getPrice());
                }catch (Exception e){
                    //Toast.makeText(OrderActivity.this, "正在刷新", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
            }
        });
        /**
         * 点击事件，查询按钮和日期查询
         */
        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()) {
                    case R.id.btn_query:
                        guanjianzi = et_query.getText().toString().trim();
                        InputMethodManager imm = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        if(guanjianzi.isEmpty()){
                            Toast.makeText(OrderActivity.this,"查询不能为空",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        myAdapter.clearList();
                        dialog = ProgressDialog.show(OrderActivity.this, "请稍等...", "获取数据中...", true);
                        getQuery();
                        break;
                    case R.id.fl_neirong:
                        getData(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
                        break;
                    case R.id.btn_dataquery:
                        View dateView = View.inflate(OrderActivity.this, R.layout.dialog_date, null);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(new Date());
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int day = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePicker dp = (DatePicker) dateView.findViewById(R.id.datePicker);
                        dp.init(year, month, day, new DatePicker.OnDateChangedListener() {
                            @Override
                            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                mDate  = new GregorianCalendar(year,monthOfYear,dayOfMonth).getTime();
                                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                                riqi1 = formatter.format(mDate);
                                Log.e("XXXXXXXX",riqi1);
                            }
                        });
                        AlertDialog dialog = new AlertDialog.Builder(OrderActivity.this)
                                .setView(dateView)
                                .setTitle("设置日期")
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Message msg = Message.obtain();
                                        msg.obj = riqi1;
                                        msg.what = 3;
                                        handler.sendMessage(msg);
                                    }
                                }).create();
                        dialog.show();
                }
            }
        };
        btn_query.setOnClickListener(mClickListener);
        btn_dataquery.setOnClickListener(mClickListener);
        fl_neirong.setOnClickListener(mClickListener);
    }
    private void getQuery() {
        new Thread() {
            @Override
            public void run() {
                String str = null;
                try {
                    str = getPeiSongDan(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
                    JsonPeiSongDan(str);

                    dialog.dismiss();
                    handler.sendEmptyMessage(4);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(10,2000);
                }
            }
        }.start();
    }

    /**
     * 获取数据
     * @param dengluming
     * @param denglumima
     * @param riqi1
     * @param riqi2
     * @param yeshu
     * @param guanjianzi
     */
    public void getData(final String dengluming, final String denglumima, final String riqi1, final String riqi2, final String yeshu, final  String guanjianzi) {
        new Thread() {
            @Override
            public void run() {
                String str = null;
                try {
                    str = getPeiSongDan(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
                    Log.e("@@@@@", str);
                    JsonPeiSongDan(str);
                    handler.sendEmptyMessage(0);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(10,3000);
                }
            }
        }.start();
    }

    /**
     * Json解析
     * @param str
     * @throws JSONException
     */
    private void JsonPeiSongDan(String str) throws JSONException {
        JSONObject jsonObject = new JSONObject(str);
        JSONArray jsonArray = jsonObject.getJSONArray("Table");
        for (int i = 0; i < jsonArray.length(); i++) {
            OrderBean order = new OrderBean();
            CommonBean commonBean = new CommonBean();
            JSONObject ordera = (JSONObject) jsonArray.get(i);
            id = ordera.getString("id");
            biaoti = ordera.getString("biaoti");
            miaoshu = ordera.getString("miaoshu");
            fukuanyaoqiu = ordera.getString("fukuanyaoqiu");
            commonBean.setId(id);
            editor.putString("zhubiaoid", id).apply();
            commonBean.setBiaoti(biaoti);
            commonBean.setMiaoshu(miaoshu);
            commonBean.setFukuanyaoqiu(fukuanyaoqiu);
            order = Utils.getOrderBean(id, biaoti, miaoshu);
            orders.add(order);
            commons.add(commonBean);
        }
    }

    /**
     * 获取数据字符串
     * @param dengluming
     * @param denglumima
     * @param riqi1
     * @param riqi2
     * @param yeshu
     * @param guanjianzi
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String getPeiSongDan(String dengluming, String denglumima, String riqi1, String riqi2, String yeshu, String guanjianzi) throws IOException, XmlPullParserException {
        // 指定WebService的命名空间和调用的方法名
        SoapObject request = new SoapObject(Constant.NAMESPACE,Constant.JSPEISONG_METHOD_NAME);
        //调用的参数
        request.addProperty("dengluming", dengluming);
        request.addProperty("denglumima", denglumima);
        request.addProperty("riqi1", riqi1);
        request.addProperty("riqi2", riqi2);
        request.addProperty("yeshu", yeshu);
        request.addProperty("guanjianzi",guanjianzi);
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

            androidHttpTransport.call(Constant.JSPEISONG_SOAP_ACTION, envelope);
            result = envelope.getResponse();
            String str = result.toString();
            return str;
    }

    /**
     * 适配器类
     */
    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<CommonBean> commons;
        private LayoutInflater layoutInflater;
        public MyAdapter(Context context, List<CommonBean> commons) {
            this.context = context;
            this.commons = commons;
            layoutInflater = LayoutInflater.from(context);
        }
        /**
         * 清空列表
         */
        public void clearList() {
            if (commons != null)
                commons.clear();
        }

        @SuppressWarnings("unchecked")
        public void setDeviceList(List<CommonBean> list) {
            if (list != null) {
                getList();
                notifyDataSetChanged();
            }
        }

        public void clearDeviceList() {
            if (commons!= null) {
                commons.clear();
            }
            notifyDataSetChanged();
        }
        /**
         * 得到List
         * @return
         */
        public List<CommonBean> getList() {
            return commons;
        }

        public void removeItem(int index) {
            commons.remove(index);
        }

        public void removeItem(CommonBean info) {
            if (commons.contains(info))
                commons.remove(info);
        }

        /**
         * 添加List
         */
        public void addList(List<CommonBean> list) {
            if (list != null && list.size() > 0)
                commons.addAll(list);
        }

        /**
         * 添加以列表数据到尾部
         *
         *
         */
        public void addItemLast(CommonBean info) {
            if (info != null) {
                commons.add(info);
            }
        }

        /**
         * 添加数据在某一位置
         *
         * @param
         */
        public void addItem(CommonBean info, int index) {
            if (info != null) {
                commons.add(index, info);
            }
        }

        @Override
        public int getCount() {
            return commons.size();
        }

        @Override
        public Object getItem(int position) {
            return orders.get(position);
        }
        public Object getItems(int position) {
            return commons.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            MyHolyed holyed = null;
            final CommonBean commonBean = (CommonBean) getItems(position);
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.list_common, null);
                holyed = new MyHolyed();
                holyed.tv_bianhao = (TextView) convertView.findViewById(R.id.tv_biaohao);
                holyed.tv_miaoshu = (TextView) convertView.findViewById(R.id.tv_miaoshu);
                holyed.btn_receivable = (Button) convertView.findViewById(R.id.btn_receivable);
                final MyHolyed finalViewHolder = holyed;
                holyed.btn_receivable.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OrderBean order = (OrderBean) getItem(position);
                        Intent intent = new Intent(OrderActivity.this, ReceivableActivity.class);
                        intent.putExtra("price", order.getPrice());
                        startActivityForResult(intent, REQUEST_CODE);
                        info = (CommonBean) finalViewHolder.btn_receivable.getTag();
                    }
                });
                convertView.setTag(holyed);
                holyed.btn_receivable.setTag(commonBean);
            } else {
                holyed = (MyHolyed) convertView.getTag();
                holyed.btn_receivable.setTag(commonBean);
            }
            holyed.tv_bianhao.setText(commonBean.getBiaoti());
            holyed.tv_miaoshu.setText(commonBean.getMiaoshu());
            if(commonBean.getFukuanyaoqiu().equals("")){
                holyed.btn_receivable.setVisibility(View.INVISIBLE);
            }else {
                holyed.btn_receivable.setVisibility(View.VISIBLE);
                if (commonBean.getIsshoukuan()) {
                    holyed.btn_receivable.setText("已付款");
                    holyed.btn_receivable.setEnabled(false);
                } else {
                    holyed.btn_receivable.setText("未付款");
                    holyed.btn_receivable.setEnabled(true);
                }
            }

            System.out.println(sp.getBoolean("price", false));
            return convertView;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case OrderActivity.REQUEST_CODE:
                if(resultCode==ReceivableActivity.RESULT_CODE) {
                    succeed = data.getBooleanExtra("succeed", false);
                    if (succeed) {
                        info.setIsshoukuan(true);
                        myAdapter.notifyDataSetChanged();
                        et_query.clearFocus();
                        //签收成功后，记录下来
                        //sp.edit().putBoolean("qianshou", true).apply();
                    } else {

                    }
                }
                break;
        }
        // TODO Auto-generated method stub
        // 判断请求码和结果码是否正确，如果正确的话就在activity上显示刚刚所拍照的图片;
        if (requestCode == 0x1 && resultCode == this.RESULT_OK) {
            /* 使用BitmapFactory.Options类防止OOM(Out Of Memory)的问题；
　　         创建一个BitmapFactory.Options类用来处理bitmap；*/
            BitmapFactory.Options myoptions = new BitmapFactory.Options();
            myoptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file_go.getAbsolutePath(), myoptions);
            //根据在图片的宽和高，得到图片在不变形的情况指定大小下的缩略图,设置宽为222；
            int height = myoptions.outHeight * 222 / myoptions.outWidth;
            myoptions.outWidth = 222;
            myoptions.outHeight = height;
            //在重新设置玩图片显示的高和宽后记住要修改，Options对象inJustDecodeBounds的属性为false;
            //不然无法显示图片;
            myoptions.inJustDecodeBounds = false;
            //还没完这里才刚开始,要节约内存还需要几个属性，下面是最关键的一个；
            myoptions.inSampleSize = myoptions.outWidth / 222;
            //还可以设置其他几个属性用于缩小内存；
            myoptions.inPurgeable = true;
            myoptions.inInputShareable = true;
            myoptions.inPreferredConfig = Bitmap.Config.ARGB_4444;// 默认是Bitmap.Config.ARGB_8888
            //成功了，下面就显示图片咯；
            Bitmap bitmat = BitmapFactory.decodeFile(file_go.getAbsolutePath(), myoptions);
            Toast.makeText(OrderActivity.this,"拍照成功",Toast.LENGTH_SHORT).show();
            utils.compress(file_go.getAbsolutePath(), OrderActivity.this, file_str);
            //img.setImageBitmap(bitmat);
        } else {
            System.out.println("不显示图片");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private static class MyHolyed {
        private TextView tv_bianhao, tv_miaoshu;
        private Button  btn_receivable;
    }

    /**
     * 侧滑
     */
    private void initList() {
        /**
         * 设置侧滑样式
         */
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(getApplicationContext());
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9, 0xCE)));
                openItem.setWidth(300);
                openItem.setTitleSize(18);
                openItem.setTitle("拍照");
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        // set creator
        lv_order.setMenuCreator(creator);

        // step 2. listener item click event
        /**
         * 拍照点击方法
         */
        lv_order.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0:
                        TakePhoto();
                        break;
                }
                return false;
            }
        });
        // set SwipeListener
        lv_order.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });
    }

    private void TakePhoto() {
        // 验证sd卡是否正确安装：
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            // 先创建父目录，如果新创建一个文件的时候，父目录没有存在，那么必须先创建父目录，再新建文件。
            if (!mars_file.exists()) {
                mars_file.mkdirs();
            }
            // 设置跳转的系统拍照的activity为：MediaStore.ACTION_IMAGE_CAPTURE ;
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // 并设置拍照的存在方式为外部存储和存储的路径；
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(file_go));
            //跳转到拍照界面;
            startActivityForResult(intent, 0x1);
        } else {
            Toast.makeText(OrderActivity.this, "请先安装好sd卡",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDTListRefresh() {
        // TODO Auto-generated method stub
        myAdapter.clearList();
        Log.e("Common",commons.toString());
        yeshu ="0";
        getData3(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);

    }

    private void getData3(final String dengluming, final String denglumima, final String riqi1, final String riqi2, final String yeshu,final String guanjianzi) {
        new Thread() {
            @Override
            public void run() {
                String str = null;
                try {
                    str = getPeiSongDan(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
                    JsonPeiSongDan(str);
                    while(commons.size()==0){
                        Log.e("BigBang","BigBang");
                        getData3(dengluming, denglumima, riqi1, riqi2, yeshu, guanjianzi);
                    }
                    Log.e("Common",commons.toString());
                    Message msg = Message.obtain();
                    msg.obj=commons;
                    msg.what=6;
                    handler.sendMessageDelayed(msg, 500);
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(10, 2000);
                }
            }
        }.start();
    }

    @Override
    public void onDTListLoadMore() {
        n = Integer.valueOf(yeshu);
        n++;
        yeshu = n+"";
        getData2(dengluming, denglumima, riqi1, riqi2,yeshu);
    }

    private void getData2(final String dengluming, final String denglumima, final String riqi1, final String riqi2, final String yeshu) {
        new Thread() {
            @Override
            public void run() {
                Log.e("shuaxin", n + "");
                //命名空间
                String NAMESPACE = "http://HanThink.com/";
                // 调用的方法名称
                String METHOD_NAME = "jsgetpeisongdan";
                // SOAP Action
                String SOAP_ACTION = "http://HanThink.com/jsgetpeisongdan";
                //初始化SOAP对象
                // 指定WebService的命名空间和调用的方法名
                SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
                //调用的参数
                request.addProperty("dengluming", dengluming);
                request.addProperty("denglumima", denglumima);
                request.addProperty("riqi1", riqi1);
                request.addProperty("riqi2", riqi2);
                request.addProperty("yeshu", yeshu);
                Log.e("yeshu",yeshu);

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
                try {
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    //隐藏“没用记录”
                    tv_no.setVisibility(View.GONE);
                    // 获得SOAP调用的结果
                    result = envelope.getResponse();
                    String str = result.toString();
                    commonsSon = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(str);
                    JSONArray jsonArray = jsonObject.getJSONArray("Table");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        OrderBean order = new OrderBean();
                        CommonBean commonBean = new CommonBean();
                        JSONObject ordera = (JSONObject) jsonArray.get(i);
                        id = ordera.getString("id");
                        biaoti = ordera.getString("biaoti");
                        miaoshu = ordera.getString("miaoshu");
                        commonBean.setId(id);
                        editor.putString("zhubiaoid", id).apply();
                        commonBean.setBiaoti(biaoti);
                        commonBean.setMiaoshu(miaoshu);
                        order = Utils.getOrderBean(id, biaoti, miaoshu);
                        orders.add(order);
                        commonsSon.add(commonBean);
                    }
                    handler.sendEmptyMessage(5);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (SoapFault soapFault) {
                    soapFault.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
     }
}
