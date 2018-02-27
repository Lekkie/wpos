package com.avantir.wpos.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.avantir.wpos.model.ReversalInfo;
import com.avantir.wpos.model.TransInfo;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by luyq on 2014/3/26.
 */
public class DataHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "wpos.db";
	private static final int DATABASE_VERSION = 1;

	public DataHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase,
			ConnectionSource connectionSource) {
		try {
			Log.e(DataHelper.class.getName(), "Start creating the database");
			TableUtils.createTable(connectionSource, TransInfo.class);
			TableUtils.createTable(connectionSource, ReversalInfo.class);
			Log.e(DataHelper.class.getName(), "Create a database successfully");

		} catch (SQLException e) {

			Log.e(DataHelper.class.getName(), "Failed to create database", e);

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
                          ConnectionSource connectionSource, int i, int i2) {
		try {

			//TableUtils.dropTable(connectionSource, SystemParam.class, true);
			TableUtils.dropTable(connectionSource, TransInfo.class, true);
			TableUtils.dropTable(connectionSource, ReversalInfo.class, true);

			onCreate(sqLiteDatabase, connectionSource);

			Log.e(DataHelper.class.getName(), "Update the database successfully");

		} catch (SQLException e) {

			Log.e(DataHelper.class.getName(), "Failed to update the database", e);

		}
	}

	@Override
	public void close() {
		super.close();
	}
}
