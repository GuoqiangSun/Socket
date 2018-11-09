package cn.com.startai.socket.global;

import android.app.Application;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.com.swain.baselib.app.IApp.IApp;
import cn.com.swain169.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date : 2018/8/23 0023
 * desc :
 */
public class WXLoginHelper implements IApp {

    private WXLoginHelper() {
    }

    private static final class ClassHolder {
        private static final WXLoginHelper WX = new WXLoginHelper();
    }

    public static WXLoginHelper getInstance() {
        return ClassHolder.WX;
    }

    private IWXAPI wxapi;

    @Override
    public void init(Application app) {
        //

        if (wxapi == null) {
            synchronized (this) {
                if (wxapi == null) {

                    String appid = null;

                    if (CustomManager.getInstance().isTriggerWiFi()) {
                        appid = Consts.APP_ID_TRIGGER_WIFI;
                    } else if (CustomManager.getInstance().isMUSIK()) {
                        appid = Consts.APP_ID_MUSIK;
                    } else {
                        Tlog.e("WXLoginHelper unknown custom ");
                    }

                    if (appid != null) {
                        //通过WXAPIFactory工厂获取IWXApI的示例
                        wxapi = WXAPIFactory.createWXAPI(app, appid, true);
                        //将应用的appid注册到微信
                        wxapi.registerApp(appid);
                    }

                }
            }
        }

    }


    public IWXAPI getWXApi(Application app) {
        init(app);
        return wxapi;
    }

    public void releaseWxApi() {
        wxapi = null;
    }


    /**
     * author: Guoqiang_Sun
     * date: 2018/11/5 0005
     * Desc:
     */
    public static class Consts {

        public static final String APP_ID_TRIGGER_WIFI = "wx83c620b25ed78545";
        public static final String APP_SECRET_WIFISOCKET = "dce3b336470ae028d4b76db39203cd3d";


        public static final String APP_ID_MUSIK = "wx437231624c76739a";
        public static final String APP_SECRET_MUSIK = "9e55e990268e5461fbbb0c904077a602";

        //https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code

        public static final String URL_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token?";
        //https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN
        public static final String URL_REFRESH_TOKEN = "https://api.weixin.qq.com/sns/oauth2/refresh_token?";
        //https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
        public static final String URL_GET_SUSERINFO = "https://api.weixin.qq.com/sns/userinfo?";


    }
}
