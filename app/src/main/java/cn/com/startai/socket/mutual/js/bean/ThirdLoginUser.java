package cn.com.startai.socket.mutual.js.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: Guoqiang_Sun
 * date : 2018/6/7 0007
 * desc :
 */
public class ThirdLoginUser {


//    {
//        "userID": "156821558496182",   // 用户ID
//            "firstName": "Mrsheng",        // 名字（教名）
//            "middleName": "nil",           // 本人名
//            "lastName": "Zhang",           // 姓氏
//            "name": "Mrsheng Zhang",       // 全名
//            "linkURL": "nil",
//            "refreshDate": "2018-06-01 07:17:48 UTC"  // 更新时间
//    }

    public String userID = "";
    public String firstName = "";
    public String middleName = "";
    public String lastName = "";
    public String name = "";
    public String linkURL = "";
    public String refreshDate = "";

    public String toJsonStr() {

        JSONObject obj = new JSONObject();

        try {
            obj.put("userID", userID);
            obj.put("firstName", firstName);
            obj.put("middleName", middleName);
            obj.put("lastName", lastName);
            obj.put("name", name);
            obj.put("linkURL", linkURL);
            obj.put("refreshDate", refreshDate);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return obj.toString();
    }


    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer(128);

        sb.append(" userID : " + userID);
        sb.append(" firstName : " + firstName);
        sb.append(" middleName : " + middleName);
        sb.append(" lastName : " + lastName);
        sb.append(" name : " + name);
        sb.append(" linkURL : " + linkURL);
        sb.append(" refreshDate : " + refreshDate);
        return sb.toString();


    }
}
