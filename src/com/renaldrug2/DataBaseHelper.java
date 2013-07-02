package com.renaldrug2;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
//import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static String DB_PATH = null;
	private static String ORIG_DB_NAME = "drugs.db";
	private static String DB_NAME = "drugs2.db";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	//private Map<Integer, String> dosageTypes;
	/*
	 * Constructor
	 */
	public DataBaseHelper(Context context) throws IOException {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/" + context.getPackageName()+"/databases/" + DB_NAME;
		initializeDataBase();
	}
	
	private void initializeDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		
		if(!dbExist){
			myDataBase = this.getReadableDatabase();
			
			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database");
			}
		}
		
		myDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
		//loadDosageTypes();
		//loadDosageSubTypes();
	}
	
	private boolean checkDataBase(){
		 
        SQLiteDatabase checkDB = null;
        boolean retVal = false;
 
        try{
            checkDB = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
            retVal = true;
        }catch(SQLiteException e){
            //database does't exist yet.
        }finally {
	        if(checkDB != null){
	            checkDB.close();
	        }
        }
        return retVal;
    }
	
	private void copyDataBase() throws IOException{
		InputStream myInput = myContext.getAssets().open(ORIG_DB_NAME);
		
		String outFileName = DB_PATH;
		
		OutputStream myOutput = new FileOutputStream(outFileName);
		
		byte[] buffer = new byte[1024];
		int length;
		while((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
		}
		
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}
	
	@Override
		public synchronized void close() {
		if(myDataBase != null)
			myDataBase.close();
		
		super.close();
	}
	
	@Override
		public void onCreate(SQLiteDatabase db) {
		
	}
	
	@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}
	
	/*private void loadDosageTypes() {
		if (this.dosageTypes != null)
			return;
		this.dosageTypes = new HashMap<Integer, String>();
		Cursor c = rawQuery("select _id, description from dosage_type", null);
		while(c.moveToNext()) {
			Integer id = c.getInt(0);
			String description = c.getString(1);
			dosageTypes.put(id, description);
		}
		Log.i("loadDosageTypes", dosageTypes.size() + " loaded.");
		
	}
	
	private void loadDosageSubTypes() {
		//TODO
	}*/
		public Cursor rawQuery(String sql, String[] selectionArgs){
			return myDataBase.rawQuery(sql, selectionArgs);
		}

}
