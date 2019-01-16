package cn.com.startai.socket.mutual.js.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONException;
import org.json.JSONObject;
import org.greenrobot.greendao.annotation.Generated;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/30 0030
 * desc :
 */

@Entity
public class JsWeatherInfo {

    @Id(autoincrement = true)
    private Long id;

    private String userid;
    private String lat;
    private String lng;
    private String province;
    private String city;
    private String district;
    private String qlty;
    private String tmp;
    private String weather;
    private String weatherPic;
    private long timestamp;

    @Generated(hash = 882384425)
    public JsWeatherInfo(Long id, String userid, String lat, String lng,
            String province, String city, String district, String qlty, String tmp,
            String weather, String weatherPic, long timestamp) {
        this.id = id;
        this.userid = userid;
        this.lat = lat;
        this.lng = lng;
        this.province = province;
        this.city = city;
        this.district = district;
        this.qlty = qlty;
        this.tmp = tmp;
        this.weather = weather;
        this.weatherPic = weatherPic;
        this.timestamp = timestamp;
    }

    @Generated(hash = 1626177919)
    public JsWeatherInfo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getQlty() {
        return qlty;
    }

    public void setQlty(String qlty) {
        this.qlty = qlty;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getWeatherPic() {
        return weatherPic;
    }

    public void setWeatherPic(String weatherPic) {
        this.weatherPic = weatherPic;
    }


    @Override
    public String toString() {
        return "JsWeatherInfo{" +
                "id=" + id +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", qlty='" + qlty + '\'' +
                ", tmp='" + tmp + '\'' +
                ", weather='" + weather + '\'' +
                ", weatherPic='" + weatherPic + '\'' +
                '}';
    }

    public String toJsonStr() {

        JSONObject obj = new JSONObject();
        try {
            obj.put("lat", this.getLat());
            obj.put("lng", this.getLng());
            obj.put("province", this.getProvince());
            obj.put("city", this.getCity());
            obj.put("district", this.getDistrict());
            obj.put("qlty", this.getQlty());
            obj.put("tmp", this.getTmp());
            obj.put("weather", this.getWeather());
            obj.put("weatherPic", this.getWeatherPic());
            obj.put("timestamp", this.getTimestamp());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj.toString();
    }

    public String getUserid() {
        return this.userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

}
