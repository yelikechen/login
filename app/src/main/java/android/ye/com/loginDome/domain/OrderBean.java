package android.ye.com.loginDome.domain;

import java.io.Serializable;

/**
 * Created by And on 2015/12/15.
 * 订单类
 */
public class OrderBean implements Serializable{
    //id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    //客户
    private String client;
    //日期
    private String date;
    //订单号
    private String orderNum;

    private String price;

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPrice() {
        return price;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderNum() {
        return orderNum;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id='" + id + '\'' +
                ", client='" + client + '\'' +
                ", date='" + date + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", price='" + price + '\'' +
                '}';
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
}
