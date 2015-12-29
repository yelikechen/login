package android.ye.com.loginDome.domain;

/**
 * Created by And on 2015/12/16.
 */
public class CommonBean {
    private String biaoti;
    private String miaoshu;
    private String id;
    private String fukuanyaoqiu;
    private Boolean isshoukuan = false;

    public void setFukuanyaoqiu(String fukuanyaoqiu) {
        this.fukuanyaoqiu = fukuanyaoqiu;
    }

    public String getFukuanyaoqiu() {
        return fukuanyaoqiu;
    }

    public Boolean getIsshoukuan() {
        return isshoukuan;
    }

    public void setIsshoukuan(Boolean isshoukuan) {
        this.isshoukuan = isshoukuan;
    }


    public Boolean getSucceed() {
        return succeed;
    }

    public void setSucceed(Boolean succeed) {
        this.succeed = succeed;
    }

    private Boolean succeed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBiaoti() {
        return biaoti;
    }

    public void setBiaoti(String biaoti) {
        this.biaoti = biaoti;
    }

    public String getMiaoshu() {
        return miaoshu;
    }

    public void setMiaoshu(String miaoshu) {
        this.miaoshu = miaoshu;
    }

    @Override
    public String toString() {
        return "CommonBean{" +
                "biaoti='" + biaoti + '\'' +
                ", miaoshu='" + miaoshu + '\'' +
                ", id='" + id + '\'' +
                ", isshoukuan=" + isshoukuan +
                ", succeed=" + succeed +
                '}';
    }
}
