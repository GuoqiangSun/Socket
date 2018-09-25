package cn.com.startai.socket.sign.js;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/30 0030
 * desc :
 */
public class JsUserInfo {

    //    {
//        "userName": "QQ",    // 登录用户名
//            "birthday": "",      // 生日
//            "province": "",      // 省
//            "city": "",          // 市
//            "town": "",          // 区
//            "address": "",       // 详细地址
//            "nickName": "",      // 昵称
//            "headPic": "",       // 头像
//            "sex": "",           // 性别
//            "firstName": "",     // 名
//            "lastName": ""       // 姓
//      "email":"",          // 邮箱
//              "mobile":""          // 手机号
//    }

    public String toJsonStr() {
        JSONObject data = new JSONObject();
        try {
            data.put("userName", userName);
            data.put("birthday", birthday);
            data.put("province", province);
            data.put("city", city);
            data.put("town", town);
            data.put("address", address);
            data.put("nickName", nickName);
            data.put("headPic", headPic);
            data.put("sex", sex);
            data.put("firstName", firstName);
            data.put("lastName", lastName);
            data.put("email", email);
            data.put("mobile", mobile);

            if (isHavePwd != -1000) { // 表示没有修改过这个值，所以不设置进来
                data.put("isHavePwd", isHavePwd == 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    private String email;          // 邮箱
    private String mobile;         // 手机号

    public int getIsHavePwd() {
        return isHavePwd;
    }

    public void setIsHavePwd(int isHavePwd) {
        this.isHavePwd = isHavePwd;
    }

    private int isHavePwd = -1000;

    private String userName;    // 登录用户名
    private String birthday; // 生日

    private String province;  // 省
    private String city;// 市
    private String town; // 区

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
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

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String address;// 详细地址
    private String nickName;  // 昵称
    private String headPic;  // 头像
    private String sex;// 性别
    private String firstName;// 名
    private String lastName;// 姓


    @Override
    public String toString() {
        return "JsUserInfo{" +
                "isHavePwd=" + isHavePwd +
                ", userName='" + userName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", town='" + town + '\'' +
                ", address='" + address + '\'' +
                ", nickName='" + nickName + '\'' +
                ", headPic='" + headPic + '\'' +
                ", sex='" + sex + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }


}
