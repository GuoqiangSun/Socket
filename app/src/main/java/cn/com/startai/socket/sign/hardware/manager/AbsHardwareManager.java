package cn.com.startai.socket.sign.hardware.manager;

import android.content.Intent;

import com.tencent.mm.opensdk.modelbase.BaseResp;

import cn.com.startai.socket.mutual.js.bean.MobileBind;
import cn.com.startai.socket.mutual.js.bean.MobileLogin;
import cn.com.startai.socket.mutual.js.bean.UserRegister;
import cn.com.startai.socket.mutual.js.bean.UserUpdateInfo;
import cn.com.startai.socket.mutual.js.bean.WiFiConfig;
import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;
import cn.com.startai.socket.sign.hardware.AbsHardware;
import cn.com.startai.socket.sign.hardware.IControlBle;
import cn.com.startai.socket.sign.hardware.IControlWiFi;
import cn.com.startai.socket.sign.scm.bean.LanBindInfo;
import cn.com.startai.socket.sign.scm.bean.LanBindingDevice;
import cn.com.startai.socket.sign.scm.bean.UpdateVersion;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolInput;
import cn.com.swain.support.protocolEngine.pack.ResponseData;

/**
 * author: Guoqiang_Sun
 * date: 2019/1/17 0017
 * Desc:
 */
public abstract class AbsHardwareManager extends AbsHardware implements IControlBle, IControlWiFi {

    public abstract void onActivityResult(int requestCode, int resultCode, Intent data);

    public abstract void onWxLoginResult(BaseResp baseResp);
}
