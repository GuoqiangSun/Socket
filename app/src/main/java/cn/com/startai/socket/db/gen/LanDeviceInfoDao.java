package cn.com.startai.socket.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import cn.com.startai.socket.mutual.js.bean.WiFiDevice.LanDeviceInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LAN_DEVICE_INFO".
*/
public class LanDeviceInfoDao extends AbstractDao<LanDeviceInfo, Long> {

    public static final String TABLENAME = "LAN_DEVICE_INFO";

    /**
     * Properties of entity LanDeviceInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Model = new Property(1, int.class, "model", false, "MODEL");
        public final static Property MainVersion = new Property(2, int.class, "mainVersion", false, "MAIN_VERSION");
        public final static Property SubVersion = new Property(3, int.class, "subVersion", false, "SUB_VERSION");
        public final static Property HasAdmin = new Property(4, boolean.class, "hasAdmin", false, "HAS_ADMIN");
        public final static Property IsAdmin = new Property(5, boolean.class, "isAdmin", false, "IS_ADMIN");
        public final static Property HasRemote = new Property(6, boolean.class, "hasRemote", false, "HAS_REMOTE");
        public final static Property BindNeedPwd = new Property(7, boolean.class, "bindNeedPwd", false, "BIND_NEED_PWD");
        public final static Property HasActivate = new Property(8, boolean.class, "hasActivate", false, "HAS_ACTIVATE");
        public final static Property IsLanBind = new Property(9, boolean.class, "isLanBind", false, "IS_LAN_BIND");
        public final static Property IsWanBind = new Property(10, boolean.class, "isWanBind", false, "IS_WAN_BIND");
        public final static Property State = new Property(11, boolean.class, "state", false, "STATE");
        public final static Property DeviceID = new Property(12, String.class, "deviceID", false, "DEVICE_ID");
        public final static Property Mac = new Property(13, String.class, "mac", false, "MAC");
        public final static Property Name = new Property(14, String.class, "name", false, "NAME");
        public final static Property Ip = new Property(15, String.class, "ip", false, "IP");
        public final static Property Port = new Property(16, int.class, "port", false, "PORT");
        public final static Property Ssid = new Property(17, String.class, "ssid", false, "SSID");
        public final static Property Rssi = new Property(18, int.class, "rssi", false, "RSSI");
        public final static Property RelayState = new Property(19, boolean.class, "relayState", false, "RELAY_STATE");
        public final static Property CpuInfo = new Property(20, String.class, "cpuInfo", false, "CPU_INFO");
        public final static Property NightLightShake = new Property(21, boolean.class, "nightLightShake", false, "NIGHT_LIGHT_SHAKE");
    }


    public LanDeviceInfoDao(DaoConfig config) {
        super(config);
    }
    
    public LanDeviceInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LAN_DEVICE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MODEL\" INTEGER NOT NULL ," + // 1: model
                "\"MAIN_VERSION\" INTEGER NOT NULL ," + // 2: mainVersion
                "\"SUB_VERSION\" INTEGER NOT NULL ," + // 3: subVersion
                "\"HAS_ADMIN\" INTEGER NOT NULL ," + // 4: hasAdmin
                "\"IS_ADMIN\" INTEGER NOT NULL ," + // 5: isAdmin
                "\"HAS_REMOTE\" INTEGER NOT NULL ," + // 6: hasRemote
                "\"BIND_NEED_PWD\" INTEGER NOT NULL ," + // 7: bindNeedPwd
                "\"HAS_ACTIVATE\" INTEGER NOT NULL ," + // 8: hasActivate
                "\"IS_LAN_BIND\" INTEGER NOT NULL ," + // 9: isLanBind
                "\"IS_WAN_BIND\" INTEGER NOT NULL ," + // 10: isWanBind
                "\"STATE\" INTEGER NOT NULL ," + // 11: state
                "\"DEVICE_ID\" TEXT," + // 12: deviceID
                "\"MAC\" TEXT," + // 13: mac
                "\"NAME\" TEXT," + // 14: name
                "\"IP\" TEXT," + // 15: ip
                "\"PORT\" INTEGER NOT NULL ," + // 16: port
                "\"SSID\" TEXT," + // 17: ssid
                "\"RSSI\" INTEGER NOT NULL ," + // 18: rssi
                "\"RELAY_STATE\" INTEGER NOT NULL ," + // 19: relayState
                "\"CPU_INFO\" TEXT," + // 20: cpuInfo
                "\"NIGHT_LIGHT_SHAKE\" INTEGER NOT NULL );"); // 21: nightLightShake
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LAN_DEVICE_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LanDeviceInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getModel());
        stmt.bindLong(3, entity.getMainVersion());
        stmt.bindLong(4, entity.getSubVersion());
        stmt.bindLong(5, entity.getHasAdmin() ? 1L: 0L);
        stmt.bindLong(6, entity.getIsAdmin() ? 1L: 0L);
        stmt.bindLong(7, entity.getHasRemote() ? 1L: 0L);
        stmt.bindLong(8, entity.getBindNeedPwd() ? 1L: 0L);
        stmt.bindLong(9, entity.getHasActivate() ? 1L: 0L);
        stmt.bindLong(10, entity.getIsLanBind() ? 1L: 0L);
        stmt.bindLong(11, entity.getIsWanBind() ? 1L: 0L);
        stmt.bindLong(12, entity.getState() ? 1L: 0L);
 
        String deviceID = entity.getDeviceID();
        if (deviceID != null) {
            stmt.bindString(13, deviceID);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(14, mac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(15, name);
        }
 
        String ip = entity.getIp();
        if (ip != null) {
            stmt.bindString(16, ip);
        }
        stmt.bindLong(17, entity.getPort());
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(18, ssid);
        }
        stmt.bindLong(19, entity.getRssi());
        stmt.bindLong(20, entity.getRelayState() ? 1L: 0L);
 
        String cpuInfo = entity.getCpuInfo();
        if (cpuInfo != null) {
            stmt.bindString(21, cpuInfo);
        }
        stmt.bindLong(22, entity.getNightLightShake() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LanDeviceInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getModel());
        stmt.bindLong(3, entity.getMainVersion());
        stmt.bindLong(4, entity.getSubVersion());
        stmt.bindLong(5, entity.getHasAdmin() ? 1L: 0L);
        stmt.bindLong(6, entity.getIsAdmin() ? 1L: 0L);
        stmt.bindLong(7, entity.getHasRemote() ? 1L: 0L);
        stmt.bindLong(8, entity.getBindNeedPwd() ? 1L: 0L);
        stmt.bindLong(9, entity.getHasActivate() ? 1L: 0L);
        stmt.bindLong(10, entity.getIsLanBind() ? 1L: 0L);
        stmt.bindLong(11, entity.getIsWanBind() ? 1L: 0L);
        stmt.bindLong(12, entity.getState() ? 1L: 0L);
 
        String deviceID = entity.getDeviceID();
        if (deviceID != null) {
            stmt.bindString(13, deviceID);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(14, mac);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(15, name);
        }
 
        String ip = entity.getIp();
        if (ip != null) {
            stmt.bindString(16, ip);
        }
        stmt.bindLong(17, entity.getPort());
 
        String ssid = entity.getSsid();
        if (ssid != null) {
            stmt.bindString(18, ssid);
        }
        stmt.bindLong(19, entity.getRssi());
        stmt.bindLong(20, entity.getRelayState() ? 1L: 0L);
 
        String cpuInfo = entity.getCpuInfo();
        if (cpuInfo != null) {
            stmt.bindString(21, cpuInfo);
        }
        stmt.bindLong(22, entity.getNightLightShake() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LanDeviceInfo readEntity(Cursor cursor, int offset) {
        LanDeviceInfo entity = new LanDeviceInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // model
            cursor.getInt(offset + 2), // mainVersion
            cursor.getInt(offset + 3), // subVersion
            cursor.getShort(offset + 4) != 0, // hasAdmin
            cursor.getShort(offset + 5) != 0, // isAdmin
            cursor.getShort(offset + 6) != 0, // hasRemote
            cursor.getShort(offset + 7) != 0, // bindNeedPwd
            cursor.getShort(offset + 8) != 0, // hasActivate
            cursor.getShort(offset + 9) != 0, // isLanBind
            cursor.getShort(offset + 10) != 0, // isWanBind
            cursor.getShort(offset + 11) != 0, // state
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12), // deviceID
            cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13), // mac
            cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14), // name
            cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15), // ip
            cursor.getInt(offset + 16), // port
            cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17), // ssid
            cursor.getInt(offset + 18), // rssi
            cursor.getShort(offset + 19) != 0, // relayState
            cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20), // cpuInfo
            cursor.getShort(offset + 21) != 0 // nightLightShake
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LanDeviceInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setModel(cursor.getInt(offset + 1));
        entity.setMainVersion(cursor.getInt(offset + 2));
        entity.setSubVersion(cursor.getInt(offset + 3));
        entity.setHasAdmin(cursor.getShort(offset + 4) != 0);
        entity.setIsAdmin(cursor.getShort(offset + 5) != 0);
        entity.setHasRemote(cursor.getShort(offset + 6) != 0);
        entity.setBindNeedPwd(cursor.getShort(offset + 7) != 0);
        entity.setHasActivate(cursor.getShort(offset + 8) != 0);
        entity.setIsLanBind(cursor.getShort(offset + 9) != 0);
        entity.setIsWanBind(cursor.getShort(offset + 10) != 0);
        entity.setState(cursor.getShort(offset + 11) != 0);
        entity.setDeviceID(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
        entity.setMac(cursor.isNull(offset + 13) ? null : cursor.getString(offset + 13));
        entity.setName(cursor.isNull(offset + 14) ? null : cursor.getString(offset + 14));
        entity.setIp(cursor.isNull(offset + 15) ? null : cursor.getString(offset + 15));
        entity.setPort(cursor.getInt(offset + 16));
        entity.setSsid(cursor.isNull(offset + 17) ? null : cursor.getString(offset + 17));
        entity.setRssi(cursor.getInt(offset + 18));
        entity.setRelayState(cursor.getShort(offset + 19) != 0);
        entity.setCpuInfo(cursor.isNull(offset + 20) ? null : cursor.getString(offset + 20));
        entity.setNightLightShake(cursor.getShort(offset + 21) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LanDeviceInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LanDeviceInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(LanDeviceInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
