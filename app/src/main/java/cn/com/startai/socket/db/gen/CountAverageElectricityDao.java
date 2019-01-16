package cn.com.startai.socket.db.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import cn.com.startai.socket.mutual.js.bean.CountAverageElectricity;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "COUNT_AVERAGE_ELECTRICITY".
*/
public class CountAverageElectricityDao extends AbstractDao<CountAverageElectricity, Long> {

    public static final String TABLENAME = "COUNT_AVERAGE_ELECTRICITY";

    /**
     * Properties of entity CountAverageElectricity.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");
        public final static Property Timestamp = new Property(2, long.class, "timestamp", false, "TIMESTAMP");
        public final static Property Electricity = new Property(3, float.class, "electricity", false, "ELECTRICITY");
        public final static Property Price = new Property(4, float.class, "price", false, "PRICE");
        public final static Property Interval = new Property(5, int.class, "interval", false, "INTERVAL");
    }


    public CountAverageElectricityDao(DaoConfig config) {
        super(config);
    }
    
    public CountAverageElectricityDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"COUNT_AVERAGE_ELECTRICITY\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MAC\" TEXT," + // 1: mac
                "\"TIMESTAMP\" INTEGER NOT NULL ," + // 2: timestamp
                "\"ELECTRICITY\" REAL NOT NULL ," + // 3: electricity
                "\"PRICE\" REAL NOT NULL ," + // 4: price
                "\"INTERVAL\" INTEGER NOT NULL );"); // 5: interval
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"COUNT_AVERAGE_ELECTRICITY\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CountAverageElectricity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
        stmt.bindLong(3, entity.getTimestamp());
        stmt.bindDouble(4, entity.getElectricity());
        stmt.bindDouble(5, entity.getPrice());
        stmt.bindLong(6, entity.getInterval());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CountAverageElectricity entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
        stmt.bindLong(3, entity.getTimestamp());
        stmt.bindDouble(4, entity.getElectricity());
        stmt.bindDouble(5, entity.getPrice());
        stmt.bindLong(6, entity.getInterval());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public CountAverageElectricity readEntity(Cursor cursor, int offset) {
        CountAverageElectricity entity = new CountAverageElectricity( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // mac
            cursor.getLong(offset + 2), // timestamp
            cursor.getFloat(offset + 3), // electricity
            cursor.getFloat(offset + 4), // price
            cursor.getInt(offset + 5) // interval
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CountAverageElectricity entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setTimestamp(cursor.getLong(offset + 2));
        entity.setElectricity(cursor.getFloat(offset + 3));
        entity.setPrice(cursor.getFloat(offset + 4));
        entity.setInterval(cursor.getInt(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(CountAverageElectricity entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(CountAverageElectricity entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(CountAverageElectricity entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}