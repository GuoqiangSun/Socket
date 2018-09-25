package cn.com.startai.socket.sign.scm.util;

import cn.com.swain.support.protocolEngine.datagram.SocketDataArray;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.support.protocolEngine.utils.ProtocolDataCache;
import cn.com.swain.support.protocolEngine.utils.SocketSecureKey;

/**
 * author: Guoqiang_Sun
 * date : 2018/9/13 0013
 * desc :
 */
public class MySocketDataCache extends ProtocolDataCache {

    private static final class MClassHolder {
        private static final MySocketDataCache CACHE = new MySocketDataCache();
    }

    public static MySocketDataCache getMInstance() {
        return MClassHolder.CACHE;
    }

    public static ResponseData getQueryHistoryCount(String mac, byte[] params) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_CONTROLLER);
        mSecureDataPack.setCmd(MySocketSecureKey.MCmd.CMD_QUERY_HISTORY_COUNT);
        mSecureDataPack.setParams(params);
        return newResponseDataNoRecord(mac, mSecureDataPack);
    }

    public static ResponseData getCurrentAlarmValue2(String mac, int value) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(SocketSecureKey.Cmd.CMD_SET_CURRENT_ALARM_VALUE);

        final byte[] params = new byte[2];
        params[0] = (byte) ((value >> 8) & 0xFF);
        params[1] = (byte) (value & 0xFF);

        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /**
     * 设置费率
     */
    public static ResponseData getSetConstRate(String mac, byte model, byte hour, byte minute, short price) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(MySocketSecureKey.MCmd.CMD_SET_COST_RATE);

        final byte[] params = new byte[5];
        params[0] = model;
        params[1] = hour;
        params[2] = minute;
        params[3] = (byte) ((price >> 8) & 0xFF);
        params[4] = (byte) (model & 0xFF);

        mSecureDataPack.setParams(params);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /**
     * 查询费率
     */
    public static ResponseData getQueryConstRate(String mac) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(MySocketSecureKey.MCmd.CMD_QUERY_COST_RATE);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /**
     * 查询积累参数
     */
    public static ResponseData getQueryCumuParam(String mac) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(MySocketSecureKey.MCmd.CMD_QUERY_CUMU_PARAM);
        return newResponseDataRecord(mac, mSecureDataPack);
    }

    /**
     * 查询最大输出
     */
    public static ResponseData getQueryMaxOutput(String mac) {
        SocketDataArray mSecureDataPack = getMInstance().produceSocketDataArray(mac);
        mSecureDataPack.setType(SocketSecureKey.Type.TYPE_SYSTEM);
        mSecureDataPack.setCmd(MySocketSecureKey.MCmd.CMD_QUERY_MAX_OUTPUT);
        return newResponseDataRecord(mac, mSecureDataPack);
    }
}