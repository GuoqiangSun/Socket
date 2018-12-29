package cn.com.startai.socket.sign.scm.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.com.startai.socket.db.gen.CountAverageElectricityDao;
import cn.com.startai.socket.db.gen.CountElectricityDao;
import cn.com.startai.socket.db.manager.DBManager;
import cn.com.startai.socket.debuger.Debuger;
import cn.com.startai.socket.global.Utils.DateUtils;
import cn.com.startai.socket.mutual.js.bean.CountAverageElectricity;
import cn.com.startai.socket.mutual.js.bean.CountElectricity;
import cn.com.startai.socket.sign.scm.bean.QueryHistoryCount;
import cn.com.startai.socket.sign.scm.util.MySocketDataCache;
import cn.com.startai.socket.sign.scm.util.SocketSecureKey;
import cn.com.swain.support.protocolEngine.IO.IDataProtocolOutput;
import cn.com.swain.support.protocolEngine.pack.ResponseData;
import cn.com.swain.baselib.log.Tlog;

/**
 * author: Guoqiang_Sun
 * date: 2018/11/22 0022
 * Desc:
 */
public class QueryHistoryUtil {

    private static String TAG = SocketScmManager.TAG;

    public static synchronized void queryHistoryCount(QueryHistoryCount mQueryCount,
                                                      ScmDevice scmDevice,
                                                      IDataProtocolOutput mResponse) {

        QueryHistoryCount queryHistoryCount = mQueryCount.cloneMyself();
        scmDevice.putQueryHistoryCount(queryHistoryCount);

        long startTimestamp = mQueryCount.getStartTimestampFromStr();
        long endTimestamp = mQueryCount.getEndTimestampFromStr();
        boolean hasQueryFromServer = false;

        final long curMillis = DateUtils.fastFormatTsToDayTs(System.currentTimeMillis());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, " queryHistoryCount startTimestamp:" + startTimestamp
                    + " " + mQueryCount.startTime
                    + " endTimestamp:" + endTimestamp
                    + " " + mQueryCount.endTime
                    + " interval:" + mQueryCount.interval
                    + " day:" + mQueryCount.day
                    + " curMillis:" + curMillis
                    + " curMillis:" + dateFormat.format(new Date(curMillis))
            );
        }

        QueryHistoryCount mCount = new QueryHistoryCount();
        mCount.mac = mQueryCount.mac;
        mCount.startTime = mQueryCount.startTime;
        mCount.interval = mQueryCount.interval;
        mCount.mDataArray = new ArrayList<>();
        mCount.mDayArray = new ArrayList<>();

        QueryHistoryCount.Day mDay;
        QueryHistoryCount.Data mData;
        QueryHistoryCount.Data mTmpData = new QueryHistoryCount.Data();

        CountElectricityDao countElectricityDao =
                DBManager.getInstance().getDaoSession().getCountElectricityDao();

        CountAverageElectricityDao countAverageElectricityDao
                = DBManager.getInstance().getDaoSession().getCountAverageElectricityDao();

        final byte[] countData = new byte[CountElectricity.SIZE_ONE_DAY];

        StringBuilder sbLog = new StringBuilder();
        StringBuilder sbJsLog = new StringBuilder();

        if (SocketSecureKey.Util.isIntervalWeek((byte) mQueryCount.interval)) {

            while (startTimestamp < endTimestamp) {

                int curWeek = DateUtils.getWeek(startTimestamp);

                if (curWeek == 0) {

                    Tlog.e(TAG, " queryBlobDataHistoryCount isIntervalWeek set startTimestamp is sunday "
                            + dateFormat.format(new Date(startTimestamp)));

                    break;

                }

                startTimestamp += DateUtils.ONE_DAY;

            }
        }


        if (SocketSecureKey.Util.isIntervalMonth((byte) mQueryCount.interval)
                || SocketSecureKey.Util.isIntervalWeek((byte) mQueryCount.interval)) {

            while (startTimestamp < endTimestamp) {

                Tlog.e(TAG, " queryBlobDataHistoryCount " + dateFormat.format(new Date(startTimestamp))
                        + " interval:" + mQueryCount.interval);

                if (SocketSecureKey.Util.isIntervalMonth((byte) mQueryCount.interval)) {

                    int month = DateUtils.getMonth(System.currentTimeMillis());

                    if (DateUtils.getMonth(startTimestamp) == month) {

                        Tlog.e(TAG, " queryBlobDataHistoryCount from db but curMonth=endMonth curTime:"
                                + dateFormat.format(new Date(System.currentTimeMillis())));

                        break;
                    }

                } else if (SocketSecureKey.Util.isIntervalWeek((byte) mQueryCount.interval)) {

                    if (startTimestamp >= (System.currentTimeMillis() - DateUtils.ONE_DAY * 7)) {

                        Tlog.e(TAG, " queryBlobDataHistoryCount from db but startTimestamp>=(curMillis-7) curTime:"
                                + dateFormat.format(new Date(System.currentTimeMillis())));

                        break;

                    }

                }

                List<CountAverageElectricity> listQueryBlob = countAverageElectricityDao.queryBuilder()
                        .where(CountAverageElectricityDao.Properties.Mac.eq(mQueryCount.mac),
                                CountAverageElectricityDao.Properties.Timestamp.eq(startTimestamp),
                                CountAverageElectricityDao.Properties.Interval.eq(mQueryCount.interval)).list();

                CountAverageElectricity countAverageElectricity = null;
                if (listQueryBlob.size() > 0) {
                    countAverageElectricity = listQueryBlob.get(0);
                }

                if (countAverageElectricity == null) {
                    startTimestamp = DateUtils.getLastMonth(startTimestamp);
                    Tlog.e(TAG, " queryBlobDataHistoryCount from db but countAverageElectricity=null "
                            + dateFormat.format(new Date(startTimestamp)));


                    break;
                }


                mDay = new QueryHistoryCount.Day();
                mDay.startTime = startTimestamp;
                mCount.mDayArray.add(mDay);

                mData = new QueryHistoryCount.Data();
                mData.e = countAverageElectricity.getElectricity();
                mData.s = countAverageElectricity.getPrice();
                mCount.mDataArray.add(mData);
                mCount.day++;

                Tlog.v(TAG, " queryBlobDataHistoryCount from DB  add e:" + mData.e + " add s" + mData.s);

                if (SocketSecureKey.Util.isIntervalMonth((byte) mQueryCount.interval)) {

                    long l = DateUtils.fastFormatTsToMonthTs(startTimestamp);

                    startTimestamp = DateUtils.getNextMonth(l);

                    Tlog.e(TAG, " queryBlobDataHistoryCount from DB, one circle finish ,next month: "
                            + dateFormat.format(new Date(startTimestamp)));

                } else if (SocketSecureKey.Util.isIntervalWeek((byte) mQueryCount.interval)) {


                    while (startTimestamp < endTimestamp) {

                        startTimestamp += DateUtils.ONE_DAY;

                        int curWeek = DateUtils.getWeek(startTimestamp);

                        if (curWeek == 0) {

                            Tlog.e(TAG, " queryBlobDataHistoryCount from DB, one circle finish ,next week: "
                                    + dateFormat.format(new Date(startTimestamp)) + " curWeek:" + curWeek);

                            break;

                        }


                    }

                }


            }


        }

        int lastMonth = DateUtils.getMonth(startTimestamp);
        int lastYear = DateUtils.getYear(startTimestamp);

        while (startTimestamp < endTimestamp) {

            if (Debuger.isLogDebug) {
                Tlog.v(TAG, " queryHistoryCount startTime: "
                        + dateFormat.format(new Date(startTimestamp))
                        + "  curMillis:"
                        + dateFormat.format(new Date(curMillis)));
            }

            List<CountElectricity> listElectricitys = countElectricityDao.queryBuilder()
                    .where(CountElectricityDao.Properties.Mac.eq(mQueryCount.mac),
                            CountElectricityDao.Properties.Timestamp.eq(startTimestamp)).list();

            mDay = new QueryHistoryCount.Day();
            mDay.startTime = startTimestamp;
            mCount.mDayArray.add(mDay);

            if (listElectricitys != null && listElectricitys.size() > 0) {
                CountElectricity countElectricity = listElectricitys.get(0);
                mDay.countData = countElectricity.getElectricity();

                if (Debuger.isLogDebug) {
                    Tlog.v(TAG, " queryHistoryCount from DB ");
                }

                if (startTimestamp == curMillis && mQueryCount.needQueryFromServer) {

                    Date mStartDate = new Date(startTimestamp);
                    Date mEndDate = new Date(startTimestamp + DateUtils.ONE_DAY);
                    ResponseData mResponseData = MySocketDataCache.getQueryHistoryCount(mQueryCount.mac,
                            mStartDate, mEndDate);
                    hasQueryFromServer = true;
                    queryHistoryCount.msgSeq = mResponseData.getRepeatMsgModel().getMsgSeq();

                    if (Debuger.isLogDebug) {
                        Tlog.v(TAG, " queryCurDayHistoryCount from server startTimestamp==curMillis ;" + mResponseData.toString());
                    }
                    mResponse.onOutputDataToServer(mResponseData);
                }

            } else {

                mDay.countData = new byte[CountElectricity.ONE_DAY_BYTES];

                if (Debuger.isLogDebug) {
                    Tlog.w(TAG, " queryHistoryCount from DB is null ; new null byte[]");
                }

                long diff = curMillis - startTimestamp;

                if (diff < DateUtils.ONE_DAY * 7 && diff >= 0) {

                    if (mQueryCount.needQueryFromServer) {
                        Date mStartDate = new Date(startTimestamp);
                        Date mEndDate = new Date(startTimestamp + DateUtils.ONE_DAY);
                        ResponseData mResponseData = MySocketDataCache.getQueryHistoryCount(mQueryCount.mac,
                                mStartDate, mEndDate);
                        hasQueryFromServer = true;
                        queryHistoryCount.msgSeq = mResponseData.getRepeatMsgModel().getMsgSeq();

                        if (Debuger.isLogDebug) {
                            Tlog.v(TAG, " queryHistoryCount from server [DB data is null]:" + mResponseData.toString());
                        }
                        mResponse.onOutputDataToServer(mResponseData);

                    } else {
                        if (Debuger.isLogDebug) {
                            Tlog.w(TAG, " queryHistoryCount from server buf break:");
                        }
                    }

                } else {
                    if (Debuger.isLogDebug) {
                        Tlog.w(TAG, " queryHistoryCount from server but out of 7 days ");
                    }
                }

            }

            for (int j = 0; j < CountElectricity.SIZE_ONE_DAY; j++) {

                try {
                    System.arraycopy(mDay.countData, j * CountElectricity.ONE_PKG_LENGTH,
                            countData, 0,
                            CountElectricity.ONE_PKG_LENGTH);

                } catch (Exception e) {
                    Tlog.e(TAG, " e ", e);
                    break;
                }

                int ee = (countData[0] & 0xFF) << 24 | (countData[1] & 0xFF) << 16
                        | (countData[2] & 0xFF) << 8 | (countData[3] & 0xFF);

                int ss = (countData[4] & 0xFF) << 24 | (countData[5] & 0xFF) << 16
                        | (countData[6] & 0xFF) << 8 | (countData[7] & 0xFF);

                float e = ee / 1000F;
                float s = ss / 1000F;


//                if (Debuger.isTest && e == 0) {
//                    e = (int) ((Math.random() * 9 + 1) * 1000);
//                }

                if (SocketSecureKey.Util.isIntervalMonth((byte) mQueryCount.interval)) {

                    int month = DateUtils.getMonth(startTimestamp);
                    int year = DateUtils.getYear(startTimestamp);

//                    Tlog.v(TAG, "--year:" + DateUtils.getYear(startTimestamp)
//                            + " month:" + month
//                            + " day:" + DateUtils.getDays(startTimestamp)
//                            + " lastMonth:" + lastMonth);

                    mTmpData.e += e;
                    mTmpData.s += s;

//                    if (month == 11) {
//                        Tlog.e(" e : " + e + " s:" + s);
//                    }

                    boolean theEndMonth = (startTimestamp == endTimestamp - DateUtils.ONE_DAY);

                    if ((month - lastMonth == 1 || year - lastYear == 1 || theEndMonth)
                            && (j == CountElectricity.SIZE_ONE_DAY - 1)) {

                        Tlog.v(TAG, " startYear:" + DateUtils.getYear(startTimestamp)
                                + " startMonth:" + month
                                + " startDay:" + DateUtils.getDays(startTimestamp)
                                + " lastMonth:" + lastMonth);

                        int daysOfMonth = DateUtils.getDaysOfMonth(lastYear, lastMonth);

                        lastMonth = month;

                        // 一个月一次的平均数据
                        mData = new QueryHistoryCount.Data();
                        mData.e = mTmpData.e;
//                                / daysOfMonth / CountElectricity.SIZE_ONE_DAY;
                        mData.s = mTmpData.s;
//                                / daysOfMonth / CountElectricity.SIZE_ONE_DAY;
                        mCount.mDataArray.add(mData);
                        mCount.day++;

                        if (!theEndMonth) {

                            long insertTimestamp = DateUtils.getLastMonth(startTimestamp);

                            List<CountAverageElectricity> list = countAverageElectricityDao.queryBuilder()
                                    .where(CountAverageElectricityDao.Properties.Mac.eq(mQueryCount.mac),
                                            CountAverageElectricityDao.Properties.Timestamp.eq(insertTimestamp),
                                            CountAverageElectricityDao.Properties.Interval.eq(mQueryCount.interval)).list();

                            CountAverageElectricity countAverageElectricity = null;
                            if (list.size() > 0) {
                                countAverageElectricity = list.get(0);
                            }
                            if (countAverageElectricity == null) {
                                countAverageElectricity = new CountAverageElectricity();
                            }

                            countAverageElectricity.setElectricity(mData.e);
                            countAverageElectricity.setInterval(mQueryCount.interval);
                            countAverageElectricity.setMac(mQueryCount.mac);
                            countAverageElectricity.setTimestamp(insertTimestamp);

                            if (countAverageElectricity.getId() == null) {
                                long insert = countAverageElectricityDao.insert(countAverageElectricity);

                                Tlog.e(TAG, "isIntervalMonth countAverageElectricityDao insert"
                                        + dateFormat.format(new Date(insertTimestamp)) + " " + mData.e + " s:" + mData.s);

                            } else {
                                countAverageElectricityDao.update(countAverageElectricity);

                                Tlog.e(TAG, "isIntervalMonth countAverageElectricityDao update"
                                        + dateFormat.format(new Date(insertTimestamp)) + " " + mData.e + " s:" + mData.s);

                            }
                        }

                        if (Debuger.isLogDebug) {

                            Tlog.e(TAG, "isIntervalMonth tmpData.e:" + mTmpData.e + " tmpData.s:" + mTmpData.s
                                    + " days:" + daysOfMonth + " add e:" + mData.e + " s:" + mData.s);

                            sbJsLog.append(j).append("-e:").append(mData.e)
                                    .append(",s:").append(mData.s).append("; ");

                        }

                        mTmpData.e = 0;
                        mTmpData.s = 0;

                    }

                } else if (SocketSecureKey.Util.isIntervalWeek((byte) mQueryCount.interval)) {

                    int curWeek = DateUtils.getWeek(startTimestamp);

                    mTmpData.e += e;
                    mTmpData.s += s;

                    boolean theEndWeek = (startTimestamp == endTimestamp - DateUtils.ONE_DAY);

                    if ((curWeek == 0 || theEndWeek) && (j == CountElectricity.SIZE_ONE_DAY - 1)) {

                        Tlog.v(TAG, " curWeek: " + curWeek + " " + dateFormat.format(new Date(startTimestamp)));

                        int countNumber = curWeek;
                        if (curWeek == 0) {
                            countNumber = 7;
                        }

                        mData = new QueryHistoryCount.Data();
                        mData.e = mTmpData.e;
//                                / countNumber / CountElectricity.SIZE_ONE_DAY;
                        mData.s = mTmpData.s;
//                                / countNumber / CountElectricity.SIZE_ONE_DAY;
                        mCount.mDataArray.add(mData);
                        mCount.day++;


                        if (!theEndWeek) {
                            List<CountAverageElectricity> list = countAverageElectricityDao.queryBuilder()
                                    .where(CountAverageElectricityDao.Properties.Mac.eq(mQueryCount.mac),
                                            CountAverageElectricityDao.Properties.Timestamp.eq(startTimestamp),
                                            CountAverageElectricityDao.Properties.Interval.eq(mQueryCount.interval)).list();

                            CountAverageElectricity countAverageElectricity = null;
                            if (list.size() > 0) {
                                countAverageElectricity = list.get(0);
                            }
                            if (countAverageElectricity == null) {
                                countAverageElectricity = new CountAverageElectricity();
                            }

                            countAverageElectricity.setElectricity(mData.e);
                            countAverageElectricity.setInterval(mQueryCount.interval);
                            countAverageElectricity.setMac(mQueryCount.mac);
                            countAverageElectricity.setTimestamp(startTimestamp);

                            if (countAverageElectricity.getId() == null) {
                                long insert = countAverageElectricityDao.insert(countAverageElectricity);

                                Tlog.e(TAG, "isIntervalMonth countAverageElectricityDao insert mTmpData.e:"
                                        + mTmpData.e + " tmpData.s:" + mTmpData.s
                                        + " days:" + countNumber + " add e:" + mData.e + " s:" + mData.s);

                            } else {
                                countAverageElectricityDao.update(countAverageElectricity);

                                Tlog.e(TAG, "isIntervalMonth countAverageElectricityDao update mTmpData.e:"
                                        + mTmpData.e + " tmpData.s:" + mTmpData.s
                                        + " days:" + countNumber + " add e:" + mData.e + " s:" + mData.s);

                            }

                        }


                        if (Debuger.isLogDebug) {

                            Tlog.e(TAG, "isIntervalWeek tmpData.e:" + mTmpData.e + " tmpData.s:" + mTmpData.s
                                    + " days:" + countNumber + " add e:" + mData.e + " s:" + mData.s);

                            sbJsLog.append(j).append("-e:").append(mData.e)
                                    .append(",s:").append(mData.s).append("; ");

                        }


                        mTmpData.e = 0;
                        mTmpData.s = 0;

                    }


                } else if (SocketSecureKey.Util.isIntervalDay((byte) mQueryCount.interval)) {

                    mTmpData.e += e;
                    mTmpData.s += s;

                    if (j == CountElectricity.SIZE_ONE_DAY - 1) {

                        int countNumber = CountElectricity.SIZE_ONE_DAY;//一个数据一天
                        // 一天一次的平均数据
                        mData = new QueryHistoryCount.Data();
                        mData.e = mTmpData.e;
//                                / countNumber;
                        mData.s = mTmpData.s;
//                                / countNumber;
                        mCount.mDataArray.add(mData);
                        mCount.day++;

                        if (Debuger.isLogDebug) {

                            Tlog.e(TAG, "isIntervalDay tmpData.e:" + mTmpData.e + " tmpData.s:" + mTmpData.s
                                    + " days:" + countNumber + " add e:" + mData.e + " s:" + mData.s);

                            sbJsLog.append(j).append("-e:").append(mData.e)
                                    .append(",s:").append(mData.s).append("; ");

                        }

                        mTmpData.e = 0;
                        mTmpData.s = 0;

                    }

                } else if (SocketSecureKey.Util.isIntervalHour((byte) mQueryCount.interval)) {

                    int countNumber = CountElectricity.SIZE_ONE_HOUR; // 一个数据一小时
                    mTmpData.e += e;
                    mTmpData.s += s;

                    if (j != 0 && j % countNumber == 0) {
                        // 一小时一次的平均数据

                        mData = new QueryHistoryCount.Data();
                        mData.e = mTmpData.e;
//                                / countNumber;
                        mData.s = mTmpData.s;
//                                / countNumber;
                        mCount.mDataArray.add(mData);
                        mCount.day++;

                        if (Debuger.isLogDebug) {

                            Tlog.e(TAG, "isIntervalHour tmpData.e:" + mTmpData.e + " tmpData.s:" + mTmpData.s
                                    + " days:" + countNumber + " add e:" + mData.e + " s:" + mData.s);

                            sbJsLog.append(j).append("-e:").append(mData.e)
                                    .append(",s:").append(mData.s).append("; ");
                        }

                        mTmpData.e = 0;
                        mTmpData.s = 0;

                    }

                } else if (SocketSecureKey.Util.isIntervalMinute((byte) mQueryCount.interval)) {
                    mData = new QueryHistoryCount.Data(e, s);
                    mCount.mDataArray.add(mData);
                    mCount.day++;

                    if (Debuger.isLogDebug) {
                        sbJsLog.append(j).append("-e:").append(mData.e)
                                .append(",s:").append(mData.s).append("; ");
                    }

                } else {
                    mData = new QueryHistoryCount.Data(e, s);
                    mCount.mDataArray.add(mData);
                    mCount.day++;

                    if (Debuger.isLogDebug) {
                        sbJsLog.append(j).append("-e:").append(mData.e)
                                .append(",s:").append(mData.s).append("; ");
                    }

                }


                if (Debuger.isLogDebug) {
                    sbLog.append(j).append("-e:").append(e).append(",s:").append(s).append(";");

                    if (sbLog.length() >= 1024 * 5) {
                        Tlog.d(TAG, mCount.interval + " QueryHistoryCount myLog: " + sbLog.toString());
                        sbLog = new StringBuilder();
                    }

                    if (sbJsLog.length() >= 1024 * 5) {
                        Tlog.d(TAG, mCount.interval + " QueryHistoryCount jsLog: " + sbJsLog.toString());
                        sbJsLog = new StringBuilder();
                    }

                }

            }

            startTimestamp += DateUtils.ONE_DAY;


            if (Debuger.isLogDebug) {
                Tlog.e(TAG, " QueryHistoryCount one circle finish ,next day: "
                        + dateFormat.format(new Date(startTimestamp)));
            }


            if (startTimestamp >= endTimestamp) {
                Tlog.e(TAG, " QueryHistoryCount break; startTimestamp >= endTimestamp "
                        + dateFormat.format(new Date(startTimestamp)));
                break;
            }

        }

        if (Debuger.isLogDebug) {
            Tlog.d(TAG, mCount.interval + " QueryHistoryCount myLog: " + sbLog.toString());
            Tlog.d(TAG, mCount.interval + " QueryHistoryCount jsLog: " + sbJsLog.toString());
            Tlog.d(TAG, mCount.interval + " QueryHistoryCount jsData: " + mCount.toJsonArrayData());
        }

//        if (mQueryCount.needQueryFromServer && hasQueryFromServer) {
//            scmDevice.sendQueryHistoryCountResult(1000, mCount);
//        } else {
//        }
        scmDevice.sendQueryHistoryCountResult(100, mCount);

    }


    public static void updateHistory(QueryHistoryCount mCount) {


        ArrayList<QueryHistoryCount.Day> mDayArray = mCount.mDayArray;

        if (mDayArray == null) {
            return;
        }


        CountElectricityDao countElectricityDao =
                DBManager.getInstance().getDaoSession().getCountElectricityDao();

        List<CountElectricity> list0 = countElectricityDao.queryBuilder()
                .where(CountElectricityDao.Properties.Mac.eq(mCount.mac),
                        CountElectricityDao.Properties.Timestamp.eq(
                                mCount.startTimeMillis - DateUtils.ONE_DAY)).list();

        long sequence = 0L;
        if (list0.size() > 0) {
            CountElectricity countElectricity = list0.get(0);
            sequence = countElectricity.getSequence();
        }


        long curMillis = DateUtils.fastFormatTsToDayTs(System.currentTimeMillis());

        long startTimestampFromStr = mCount.getStartTimestampFromStr();

//        long startTimestampFromStr = DateUtils.fastFormatTsToDayTs(mCount.startTimeMillis); // 有问题

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        Tlog.v(TAG, " updateHistory  curMillis:" + curMillis + " " + dateFormat.format(new Date(curMillis))
                + " startTimestampFromStr:" + startTimestampFromStr + " " + dateFormat.format(new Date(startTimestampFromStr)));

        for (QueryHistoryCount.Day mData : mDayArray) {

            if (mData.countData == null || mData.countData.length <= 0) {
                Tlog.v(TAG, " QueryHistoryCount.Day.countData ==null ");
                continue;
            }

            List<CountElectricity> list = countElectricityDao.queryBuilder()
                    .where(CountElectricityDao.Properties.Mac.eq(mCount.mac),
                            CountElectricityDao.Properties.Timestamp.eq(mData.startTime)).list();

            CountElectricity countElectricity = null;
            if (list.size() > 0) {
                countElectricity = list.get(0);
            }

            if (countElectricity == null) {
                CountElectricity mCountElectricity = new CountElectricity();
                mCountElectricity.setMac(mCount.mac);
                mCountElectricity.setElectricity(mData.countData);
                mCountElectricity.setTimestamp(mData.startTime);
                mCountElectricity.setSequence(++sequence);
                long insert = countElectricityDao.insert(mCountElectricity);
                Tlog.v(TAG, " HistoryCount insert:" + insert);
            } else {


                if (curMillis == startTimestampFromStr) {

                    byte[] electricity = countElectricity.getElectricity();
                    int length = electricity.length;

                    if (length < CountElectricity.ONE_DAY_BYTES) {

                        byte[] cache = new byte[CountElectricity.ONE_DAY_BYTES];

                        System.arraycopy(electricity, 0, cache, 0, length);

                        byte[] countData = mData.countData;

                        int length1 = countData.length;

                        if (length1 > CountElectricity.ONE_DAY_BYTES) {
                            length1 = CountElectricity.ONE_DAY_BYTES;
                        }

                        System.arraycopy(countData, 0, cache, 0, length1);

                        countElectricity.setElectricity(cache);

                        Tlog.v(TAG, " HistoryCount update oldLength:" + length + " newLength:" + length1);

                    } else {

                        byte[] countData = mData.countData;

                        int length1 = countData.length;

                        if (length1 > CountElectricity.ONE_DAY_BYTES) {
                            length1 = CountElectricity.ONE_DAY_BYTES;
                        }

                        System.arraycopy(countData, 0, electricity, 0, length1);

                        countElectricity.setElectricity(electricity);

                        Tlog.v(TAG, " HistoryCount update oldLength:" + length + " newLength:" + length1);

                    }

                }

                countElectricityDao.update(countElectricity);
                Tlog.v(TAG, " HistoryCount update:" + countElectricity.getId());
            }
        }
    }


    public static void deleteOldHistory(String mac) {

        CountElectricityDao countElectricityDao =
                DBManager.getInstance().getDaoSession().getCountElectricityDao();

        long currentTimeMillis = System.currentTimeMillis();

        long l = DateUtils.fastFormatTsToDayTs(currentTimeMillis - DateUtils.ONE_DAY * 31 * 8);

        List<CountElectricity> list = countElectricityDao.queryBuilder()
                .where(CountElectricityDao.Properties.Mac.eq(mac),
                        CountElectricityDao.Properties.Timestamp.lt(l)).list();

        if (list != null && list.size() > 0) {
            for (CountElectricity mCountElectricity : list) {
                countElectricityDao.delete(mCountElectricity);
            }
        }


    }


}
