package android.ye.com.loginDome.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.ye.com.loginDome.domain.OrderBean;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by And on 2015/12/14.
 */
public class Utils {
    private static String key = "a6U&1$Ip[Jr/sed]Rfvn=O>Mz+}lXN*%-gLcGD|0";
    private DisplayMetrics dm;

    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            //s = new BASE64Encoder().encode(b);
        }
        return s;
    }
    public static String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
    public static String getPreDate() {
        Date dNow = new Date(); //当前时间
        Date dBefore = new Date();
        Calendar calendar = Calendar.getInstance(); //得到日历
        calendar.setTime(dNow);//把当前时间赋给日历
        calendar.add(calendar.MONTH, -3); //设置为前3月
        dBefore = calendar.getTime(); //得到前3月的时间

        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd"); //设置时间格式
        String defaultStartDate = sdf.format(dBefore); //格式化前3月的时间
        return defaultStartDate;
    }

    /**
     * 加密
     * @param decript
     * @return
     */
    public static String jiami(String decript) {
        try {
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("SHA-1");
            digest.update(decript.getBytes("UTF-8"));

            byte messageDigest[] = digest.digest();
            return new String(Base64.encode(messageDigest,2));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * json 解析
     */
    public static OrderBean getOrderBean(String id, String biaoti, String miaoshu){
        OrderBean orderBean = new OrderBean();
        orderBean.setId(id);
        orderBean.setClient(biaoti.substring(0,biaoti.indexOf("201")));
        orderBean.setDate(biaoti.substring(biaoti.indexOf("201")));
        orderBean.setOrderNum(miaoshu.substring(0,miaoshu.indexOf("金")));
        orderBean.setPrice(miaoshu.substring(miaoshu.indexOf(":")+1));
        return orderBean;
    }

    /**
     * 压缩照片
     * @param srcPath
     * @param ctx
     * @param file_str
     */
    public void compress(String srcPath,Activity ctx,String file_str) {
        dm = new DisplayMetrics();
        ctx.getWindowManager().getDefaultDisplay().getMetrics(dm);

        float hh = dm.heightPixels;
        float ww = dm.widthPixels;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, opts);
        opts.inJustDecodeBounds = false;
        int w = opts.outWidth;
        int h = opts.outHeight;
        int size = 0;
        if (w <= ww && h <= hh) {
            size = 1;
        } else {
            double scale = w >= h ? w / ww : h / hh;
            double log = Math.log(scale) / Math.log(2);
            double logCeil = Math.ceil(log);
            size = (int) Math.pow(2, logCeil);
        }
        opts.inSampleSize = size;
        bitmap = BitmapFactory.decodeFile(srcPath, opts);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
        System.out.println(baos.toByteArray().length);
        while (baos.toByteArray().length > 20 * 1024) {
            baos.reset();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            quality -= 5;
            System.out.println(baos.toByteArray().length);
        }
        try {
            baos.writeTo(new FileOutputStream(file_str + "/my_camera/file.jpg"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.flush();
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

