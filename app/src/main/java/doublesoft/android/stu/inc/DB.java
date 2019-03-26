package doublesoft.android.stu.inc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import doublesoft.android.stu.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

public class DB {
	// private final static byte[] _writeLock = new byte[0];// 写需要加锁
	private final int BUFFER_SIZE = 10 * 1024;
	public static final String DB_NAME = "db2.2.sqlite"; // 保存的数据库文件名
	public static final String PACKAGE_NAME = "doublesoft.android.stu";// 包名
	public static final String DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/"
			+ PACKAGE_NAME; // 在手机里存放数据库的位置
	private SQLiteDatabase mdb = null;
	private Context context;

	// private static DB db = null;

	// 获取静态数据库对象
	public static DB getDB(Context context) {
		return new DB(context);
		// if (db == null) {
		// db = new DB(context);
		// }
		//
		// return db;
	}

	// 构造函数
	private DB(Context context) {
		this.context = context;
	}

	// 打开数据库
	public Boolean openDB() {
		boolean result = false;
		try {
			// synchronized (_writeLock) {

			if (this.mdb == null) {
				this.mdb = this.openDatabase(DB_PATH + "/" + DB_NAME);
			}
			result = this.mdb != null;

			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private SQLiteDatabase openDatabase(String dbfile) {
		try {
			if (!(new File(dbfile).exists())) {
				// 断定数据库文件是否存在，若不存在则履行导入，不然直接打开数据库
				InputStream inputStream = this.context.getResources().openRawResource(R.raw.db_sqlite); // 欲导入的数据库压缩包

				// 读取压缩包
				ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
				zipInputStream.getNextEntry();
				BufferedInputStream sqliteInputStream = new BufferedInputStream(zipInputStream);

				FileOutputStream outputStream = new FileOutputStream(dbfile);
				byte[] buffer = new byte[BUFFER_SIZE];
				int count = 0;
				while ((count = sqliteInputStream.read(buffer)) > 0) {
					outputStream.write(buffer, 0, count);
				}
				outputStream.close();
				sqliteInputStream.close();
				zipInputStream.close();
				inputStream.close();
			}
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
			return db;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// 关闭数据库
	public void closeDB() {
		try {
			// synchronized (_writeLock) {
			if (this.mdb != null) {
				this.mdb.close();
				this.mdb = null;
			}
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 插入行
	public long insertRow(String table, ContentValues values) {
		long result = 0;
		try {
			// synchronized (_writeLock) {
			result = this.mdb.insert(table, null, values);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 获取行
	public ContentValues getRowBySQL(String sql) {
		ContentValues values = null;

		Cursor rs = null;
		try {
			rs = this.mdb.rawQuery(sql, null);
			while (rs != null && rs.moveToNext()) {
				values = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					values.put(rs.getColumnName(i), rs.getString(i));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
				}
			}
		}

		return values;
	}

	public ContentValues getRow(String table, int id) {
		ContentValues values = null;
		Cursor rs = null;
		try {
			rs = this.mdb.query(table, new String[] { "*" }, "ID = ?", new String[] { "" + id }, null, null, null, "1");
			while (rs != null && rs.moveToNext()) {
				values = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					values.put(rs.getColumnName(i), rs.getString(i));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
				}
			}
		}
		return values;
	}

	public ContentValues getRow(String table) {
		ContentValues values = null;
		Cursor rs = null;
		try {
			rs = this.mdb.query(table, new String[] { "*" }, null, null, null, null, "", "1");
			while (rs != null && rs.moveToNext()) {
				values = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					values.put(rs.getColumnName(i), rs.getString(i));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
				}
			}
		}

		return values;
	}

	public ContentValues getRow(String table, String where) {
		ContentValues values = null;
		Cursor rs = null;
		try {
			rs = this.mdb.query(table, new String[] { "*" }, where, null, null, null, null, "1");
			while (rs != null && rs.moveToNext()) {
				values = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					values.put(rs.getColumnName(i), rs.getString(i));
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
				}
			}
		}

		return values;
	}

	// 获取结构
	public List<ContentValues> getStruct(String table) {
		// 查询
		ArrayList<ContentValues> resultList = new ArrayList<ContentValues>();
		Cursor rs = null;
		try {
			rs = this.mdb.rawQuery("PRAGMA table_info([" + table + "])", null);
			while (rs != null && rs.moveToNext()) {
				ContentValues values = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					values.put(rs.getColumnName(i), rs.getString(i));
				}
				resultList.add(values);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
					rs = null;
				} catch (Exception e) {
				}
			}
		}

		return resultList;
	}

	public List<String> getFields(String table) {

		List<String> resultList = new ArrayList<String>();
		try {
			List<ContentValues> structList = this.getStruct(table);

			for (int i = 0; i < structList.size(); i++) {
				ContentValues rowContentValue = structList.get(i);
				resultList.add(rowContentValue.getAsString("name"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	// 修改行
	public int updateRow(String table, ContentValues values, String where) {
		int result = 0;
		try {
			// synchronized (_writeLock) {
			result = this.mdb.update(table, values, where, null);
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// 删除行
	public int deleteRow(String table, String where) {
		int result = 0;
		try {
			// synchronized (_writeLock) {
			result = this.mdb.delete(table, where, null);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// 查询列表 最后一个节点的ContentValues为分页信息
	public List<ContentValues> list(ContentValues values) {
		List<ContentValues> recordList = new ArrayList<ContentValues>();
		int recordCount = 0;
		try {
			// Limit
			String limit = null;
			if (values.getAsString("Limit") != null) {
				limit = values.getAsString("Limit");
			}

			// 分页
			if (values.getAsString("PageSize") != null && values.getAsString("NowPage") != null) {
				limit = ((values.getAsInteger("NowPage") - 1) * values.getAsInteger("PageSize")) + ","
						+ values.getAsString("PageSize");
			}

			Cursor rs = null;

			rs = this.mdb.query(values.getAsString("Tables"),
					new String[] { values.getAsString("Fields") == null ? "*" : values.getAsString("Fields") },
					values.getAsString("Where"), null, null, null, values.getAsString("OrderBy"), limit);

			while (rs != null && rs.moveToNext()) {
				recordCount++;
				ContentValues rowValues = new ContentValues();
				for (int i = 0; i < rs.getColumnCount(); i++) {
					rowValues.put(rs.getColumnName(i), rs.getString(i));
				}
				recordList.add(rowValues);
			}
			rs.close();
			rs = null;

			// 分页信息
			ContentValues pageValues = new ContentValues();
			if (values.getAsString("PageSize") != null && values.getAsString("NowPage") != null) {
				// 查询总数量
				Cursor rsPage = null;
				try {
					rsPage = this.mdb.query(values.getAsString("Tables"), new String[] { "COUNT(*) AS TotalCount" },
							values.getAsString("Where"), null, null, null, null, "1");

					if (rsPage.moveToNext()) {
						recordCount = rsPage.getInt(0);
					}
					rsPage.close();
					rsPage = null;
				} catch (Exception e) {
				}
			}

			if (values.getAsString("PageSize") != null) {
				pageValues.put("PageSize", values.getAsInteger("PageSize"));
			} else {
				pageValues.put("PageSize", 99999);
			}

			if (values.getAsString("NowPage") != null) {
				pageValues.put("NowPage", values.getAsInteger("NowPage"));
			} else {
				pageValues.put("NowPage", 1);
			}

			pageValues.put("RecordCount", recordCount);

			int pageSize = pageValues.getAsInteger("PageSize");
			int totalPage = recordCount / pageSize;
			if (recordCount % pageSize != 0) {
				totalPage++;
			}
			pageValues.put("TotalPage", totalPage);
			recordList.add(pageValues);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return recordList;
	}

	// 执行SQL语句
	public void execSQL(String sql) {
		try {
			this.mdb.execSQL(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}