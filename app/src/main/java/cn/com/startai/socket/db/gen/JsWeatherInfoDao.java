package cn.com.startai.socket.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import cn.com.startai.socket.mutual.js.bean.JsWeatherInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "JS_WEATHER_INFO".
*/
public class JsWeatherInfoDao extends AbstractDao<JsWeatherInfo, Long> {

    public static final String TABLENAME = "JS_WEATHER_INFO";

    /**
     * Properties of entity JsWeatherInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Userid = new Property(1, String.class, "userid", false, "USERID");
        public final static Property Lat = new Property(2, String.class, "lat", false, "LAT");
        public final static Property Lng = new Property(3, String.class, "lng", false, "LNG");
        public final static Property Province = new Property(4, String.class, "province", false, "PROVINCE");
        public final static Property City = new Property(5, String.class, "city", false, "CITY");
        public final static Property District = new Property(6, String.class, "district", false, "DISTRICT");
        public final static Property Qlty = new Property(7, String.class, "qlty", false, "QLTY");
        public final static Property Tmp = new Property(8, String.class, "tmp", false, "TMP");
        public final static Property Weather = new Property(9, String.class, "weather", false, "WEATHER");
        public final static Property WeatherPic = new Property(10, String.class, "weatherPic", false, "WEATHER_PIC");
        public final static Property Timestamp = new Property(11, long.class, "timestamp", false, "TIMESTAMP");
    }


    public JsWeatherInfoDao(DaoConfig config) {
        super(config);
    }
    
    public JsWeatherInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"JS_WEATHER_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"USERID\" TEXT," + // 1: userid
                "\"LAT\" TEXT," + // 2: lat
                "\"LNG\" TEXT," + // 3: lng
                "\"PROVINCE\" TEXT," + // 4: province
                "\"CITY\" TEXT," + // 5: city
                "\"DISTRICT\" TEXT," + // 6: district
                "\"QLTY\" TEXT," + // 7: qlty
                "\"TMP\" TEXT," + // 8: tmp
                "\"WEATHER\" TEXT," + // 9: weather
                "\"WEATHER_PIC\" TEXT," + // 10: weatherPic
                "\"TIMESTAMP\" INTEGER NOT NULL );"); // 11: timestamp
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"JS_WEATHER_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, JsWeatherInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(2, userid);
        }
 
        String lat = entity.getLat();
        if (lat != null) {
            stmt.bindString(3, lat);
        }
 
        String lng = entity.getLng();
        if (lng != null) {
            stmt.bindString(4, lng);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(5, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(6, city);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(7, district);
        }
 
        String qlty = entity.getQlty();
        if (qlty != null) {
            stmt.bindString(8, qlty);
        }
 
        String tmp = entity.getTmp();
        if (tmp != null) {
            stmt.bindString(9, tmp);
        }
 
        String weather = entity.getWeather();
        if (weather != null) {
            stmt.bindString(10, weather);
        }
 
        String weatherPic = entity.getWeatherPic();
        if (weatherPic != null) {
            stmt.bindString(11, weatherPic);
        }
        stmt.bindLong(12, entity.getTimestamp());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, JsWeatherInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String userid = entity.getUserid();
        if (userid != null) {
            stmt.bindString(2, userid);
        }
 
        String lat = entity.getLat();
        if (lat != null) {
            stmt.bindString(3, lat);
        }
 
        String lng = entity.getLng();
        if (lng != null) {
            stmt.bindString(4, lng);
        }
 
        String province = entity.getProvince();
        if (province != null) {
            stmt.bindString(5, province);
        }
 
        String city = entity.getCity();
        if (city != null) {
            stmt.bindString(6, city);
        }
 
        String district = entity.getDistrict();
        if (district != null) {
            stmt.bindString(7, district);
        }
 
        String qlty = entity.getQlty();
        if (qlty != null) {
            stmt.bindString(8, qlty);
        }
 
        String tmp = entity.getTmp();
        if (tmp != null) {
            stmt.bindString(9, tmp);
        }
 
        String weather = entity.getWeather();
        if (weather != null) {
            stmt.bindString(10, weather);
        }
 
        String weatherPic = entity.getWeatherPic();
        if (weatherPic != null) {
            stmt.bindString(11, weatherPic);
        }
        stmt.bindLong(12, entity.getTimestamp());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public JsWeatherInfo readEntity(Cursor cursor, int offset) {
        JsWeatherInfo entity = new JsWeatherInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // userid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // lat
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // lng
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // province
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // city
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // district
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // qlty
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // tmp
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // weather
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // weatherPic
            cursor.getLong(offset + 11) // timestamp
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, JsWeatherInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUserid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLat(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLng(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setProvince(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setCity(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setDistrict(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setQlty(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setTmp(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setWeather(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setWeatherPic(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setTimestamp(cursor.getLong(offset + 11));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(JsWeatherInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(JsWeatherInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(JsWeatherInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
