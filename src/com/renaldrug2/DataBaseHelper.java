package com.renaldrug2;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Map<Integer, DosageType> dosageTypes;
	private List<Medicine> medicines = null;
	private Map<Integer, DosageSubType> dosageSubTypes = null;
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
		loadDosageTypes();
		//loadDosageSubTypes();
	}
	
	public List<Medicine> getMedicines() {
		if(this.medicines == null)
			this.medicines = loadMedicines();
		
		return medicines;
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
	
	private void loadDosageTypes() {
		if (this.dosageTypes != null)
			return;
		this.dosageTypes = new HashMap<Integer, DosageType>();
		Cursor c = rawQuery("select _id, description from dosage_type", null);
		while(c.moveToNext()) {
			Integer id = c.getInt(0);
			String description = c.getString(1);
			DosageType dt = new DosageType(id, description);
			dosageTypes.put(id, dt);
		}
		Log.i("loadDosageTypes", dosageTypes.size() + " loaded.");
		
	}
	private List<Medicine> loadMedicines(){
		Cursor cursor = rawQuery("select * from medicines",null);
		List<Medicine> temp = new ArrayList<Medicine>();
		while(cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex("medicine_name"));
					Medicine m = new Medicine(name);
					temp.add(m);
		}
	return temp;
	}
	
	private void loadDosageSubTypes() {
		if (this.dosageSubTypes != null)
			return;
		this.dosageSubTypes = new HashMap<Integer, DosageSubType>();
		Cursor c = rawQuery("select dosage_type, description from dosage_sub_types", null);
		while(c.moveToNext()) {
			Integer id = c.getInt(0);
			Integer dt = c.getInt(1);
			String description = c.getString(2);
			DosageSubType dst = new DosageSubType(dt,description);
			dosageSubTypes.put(id,dst);
		}
		Log.i("loadDosageSubTypes", dosageSubTypes.size() + " loaded.");
		
	}
		public Cursor rawQuery(String sql, String[] selectionArgs){
			return myDataBase.rawQuery(sql, selectionArgs);
		}

}
