package android.ye.com.loginDome.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by And on 2015/12/14.
 * 商品类
 */
public class Bean implements Serializable{
    private List<Table> productBeans;

    public List<Table> getProductBeans() {
        return productBeans;
    }

    public void setProductBeans(List<Table> productBeans) {
        this.productBeans = productBeans;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "productBeans=" + productBeans +
                '}';
    }

    public static class Table implements Serializable{
        private String mingcheng;
        private String jiliangdanwei;
        private String mount;
        private String price;
        private String money;

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
                    '}';
        }
    }
}
