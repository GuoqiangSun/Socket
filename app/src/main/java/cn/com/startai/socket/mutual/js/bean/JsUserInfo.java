package cn.com.startai.socket.mutual.js.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/30 0030
 * desc :
 */

@Entity
public class JsUserInfo {


    private String email;          // 邮箱
    private String mobile;         // 手机号

    private int isHavePwd = -1000;

    private String userName;    // 登录用户名
    private String birthday; // 生日

    private String province;  // 省
    private String city;// 市
    private String town; // 区

    private String address;// 详细地址
    private String nickName;  // 昵称
    private String headPic;  // 头像
    private String sex;// 性别
    private String firstName;// 名
    private String lastName;// 姓

    private String userid;

    private String thirdInfosJson;


    @Id(autoincrement = true)
    private Long id;

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

            if (thirdInfosJson != null) {
                JSONArray array = new JSONArray(thirdInfosJson);
                data.put("thirdInfos", array);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data.toString();
    }


    public static class ThirdInfos {
        private String nickName;
        private int type;

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }

    @Generated(hash = 171564962)
    public JsUserInfo(String email, String mobile, int isHavePwd, String userName,
            String birthday, String province, String city, String town,
            String address, String nickName, String headPic, String sex,
            String firstName, String lastName, String userid, String thirdInfosJson,
            Long id) {
        this.email = email;
        this.mobile = mobile;
        this.isHavePwd = isHavePwd;
        this.userName = userName;
        this.birthday = birthday;
        this.province = province;
        this.city = city;
        this.town = town;
        this.address = address;
        this.nickName = nickName;
        this.headPic = headPic;
        this.sex = sex;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userid = userid;
        this.thirdInfosJson = thirdInfosJson;
        this.id = id;
    }


    @Generated(hash = 1034667690)
    public JsUserInfo() {
    }

    public void setThirdInfos(List<ThirdInfos> thirdInfos) {

        if (thirdInfos != null && thirdInfos.size() > 0) {
            JSONArray array = new JSONArray();

            for (ThirdInfos mThirdInfos : thirdInfos) {
                JSONObject obj = new JSONObject();
                String nickName = mThirdInfos.getNickName();
                int type = mThirdInfos.getType();
                try {
                    obj.put("nickName", nickName);
                    obj.put("type", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                array.put(obj);
            }

            thirdInfosJson = array.toString();
        }
    }

    public List<ThirdInfos> getThirdInfos() {
        if (thirdInfosJson != null) {
            try {
                JSONArray array = new JSONArray(thirdInfosJson);

                int length = array.length();

                if (length > 0) {
                    List<ThirdInfos> thirdInfos = new ArrayList<>(length);

                    ThirdInfos mThirdInfos;
                    for (int i = 0; i < length; i++) {
                        mThirdInfos = new ThirdInfos();

                        JSONObject jsonObject = array.getJSONObject(i);
                        String nickName = jsonObject.getString("nickName");
                        mThirdInfos.setNickName(nickName);

                        int type = jsonObject.getInt("type");
                        mThirdInfos.setType(type);

                        thirdInfos.add(mThirdInfos);
                    }

                    return thirdInfos;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
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


    public int getIsHavePwd() {
        return isHavePwd;
    }

    public void setIsHavePwd(int isHavePwd) {
        this.isHavePwd = isHavePwd;
    }


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


    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getUserid() {
        return this.userid;
    }


    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getThirdInfosJson() {
        return this.thirdInfosJson;
    }


    public void setThirdInfosJson(String thirdInfosJson) {
        this.thirdInfosJson = thirdInfosJson;
    }

}
