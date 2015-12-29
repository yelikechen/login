package android.ye.com.loginDome.domain;

import java.io.Serializable;

/**
 * Created by And on 2015/12/16.
 */
public class ProductBean implements Serializable{
    private String mingcheng;
    private String jiliangdanwei;
    private String mount;
    private String price;
    private String money;
    private String qianshoumount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getQianshoumount() {
        if(qianshoumount==null){
            return getMount();
        }else {
            return Float.parseFloat(qianshoumount)*1.0+"";
        }
    }

    public void setQianshoumount(String qianshoumount) {
        this.qianshoumount = qianshoumount;
    }

    public String getJiliangdanwei() {
        return jiliangdanwei;
    }

    public void setJiliangdanwei(String jiliangdanwei) {
        this.jiliangdanwei = jiliangdanwei;
    }

    public String getMingcheng() {
        return mingcheng;
    }

    public void setMingcheng(String mingcheng) {
        this.mingcheng = mingcheng;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMount() {
        return mount;
    }

    public void setMount(String mount) {
        this.mount = mount;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    @Override
    public String toString() {
        return "ProductBean{" +
                "mingcheng='" + mingcheng + '\'' +
                ", jiliangdanwei='" + jiliangdanwei + '\'' +
                ", mount='" + mount + '\'' +
                ", price='" + price + '\'' +
                ", money='" + money + '\'' +
                ", qianshoumount='" + qianshoumount + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
