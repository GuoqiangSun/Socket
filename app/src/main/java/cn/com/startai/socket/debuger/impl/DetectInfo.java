package cn.com.startai.socket.debuger.impl;

/**
 * author: Guoqiang_Sun
 * date : 2018/5/17 0017
 * desc :
 */
public class DetectInfo {

    // 最大发送的次数
    public int mMaxSendTimes = Integer.MAX_VALUE;
    //ms
    public int mSendInterval = 1000 / 2;


    // 总共发送了多少次
    public int mTotalSendCount;
    // 总共接收了多少次
    public int mTotalRecCount;

    // 当前发送了多少次
    public int mCurSendCount;
    public int mCurRecCount;

    /**
     * 发送一次
     */
    public void sendOnce() {
        mCurSendCount++;
        mTotalSendCount++;
    }

    public void receiveOnce() {
        mCurRecCount++;
        mTotalRecCount++;
    }

    public boolean isSend() {
        return (mCurSendCount < mMaxSendTimes);
    }

    public boolean isRec() {
        return (mCurRecCount < mMaxSendTimes);
    }

    public boolean startup;

    public void toggle() {
        this.startup = !startup;

        this.mCurRecCount = 0;
        this.mCurSendCount = 0;

    }

    public void setStartup() {
        this.startup = true;
    }

    public void setFinish() {
        this.startup = false;
    }

    public void setSendFinish() {
//        this.mCurSendCount = 0;
        this.startup = false;
    }

    public void setRecFinish() {
//        this.mCurRecCount = 0;
    }

    public String mUIName;
    public String name;
    public int type;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);

        sb.append("type:");
        sb.append(Integer.toHexString(type));

        sb.append(", mMaxSendTimes:");
        sb.append(String.valueOf(mMaxSendTimes));

        sb.append(", mSendInterval:");
        sb.append(String.valueOf(mSendInterval));

        sb.append(", mTotalSendCount:");
        sb.append(String.valueOf(mTotalSendCount));

        sb.append(", mCurSendCount:");
        sb.append(String.valueOf(mCurSendCount));

        sb.append(", mTotalRecCount:");
        sb.append(String.valueOf(mTotalRecCount));

        sb.append(", mCurRecCount:");
        sb.append(String.valueOf(mCurRecCount));

        sb.append(", startup:");
        sb.append(String.valueOf(startup));

        sb.append(" .");

        return sb.toString();
    }
}
