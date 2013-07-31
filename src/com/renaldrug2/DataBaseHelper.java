package com.renaldrug2;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.os.StrictMode;
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
	private Map<Integer, DosageSubType> dosageSubTypes;
	private Map<Medicine, MedicineDosage> medicineDosages;

	/*
	 * Constructor
	 */
	public DataBaseHelper(Context context) throws IOException {
		super(context, DB_NAME, null, 1);
		this.myContext = context;
		DB_PATH = "/data/data/" + context.getPackageName()+"/databases/" + DB_NAME;
		initializeDataBase();
		/*String hash = getLatestDBChecksum();
		String phoneDBHash = getCurrentDBChecksum();
		if (hash.equals(phoneDBHash)){
			//Do Nothing
			String update = "They're the same";
			Log.i("Checking if Hashs are equal",update);
		}
		else {
			downloadDatabase(DB_PATH);
		}*/
		
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
		//not relevant
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//not relevant
	}

	private void loadDosageTypes() {
		if (this.dosageTypes != null)
			return;
		this.dosageTypes = new HashMap<Integer, DosageType>();
		Cursor c = rawQuery("select _id, description from dosage_types", null);
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
	
	private String getLatestDBChecksum(){
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
			        new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			}
		DefaultHttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://10.0.2.2:8080/RenalDrug-Backend/Downloads?checksum");
		String response = "";
		try {
		  HttpResponse execute = client.execute(httpGet);
		  InputStream content = execute.getEntity().getContent();
		  BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
		  String s = "";
		  while ((s = buffer.readLine()) != null) {
		    response += s;
		  }
		} catch (Exception e) {
		  Log.i("getHash","Failed : " + e);
		};
		return response;
	}
	
	private String getCurrentDBChecksum(){
		byte [] buffer = new byte[1024];
		String checksum = "";
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			InputStream in = myContext.getAssets().open(ORIG_DB_NAME);
			int numRead;	
			do {
				numRead = in.read(buffer);
				if (numRead > 0) {
					md.update(buffer,0,numRead);
				}
			} while (numRead != -1);
			in.close();
			byte [] md5 = md.digest();
			for (int i =0;i<md5.length;i++){
				checksum += Integer.toString((md5[i] & 0xff) + 0x100, 16).substring(1);
			}
		} catch (Exception e) {
			System.err.println(e);
		}
		return checksum;
	}
	
	private void downloadDatabase(String pathToSave) {
		try {
	        File f = new File(pathToSave);
	        int count;
	        URL url = new URL("http://10.0.2.2:8080/RenalDrug-Backend/Downloads?checksum=false");
	        URLConnection connection = url.openConnection();
	        connection.connect();
	        InputStream input = new BufferedInputStream(url.openStream());
	        OutputStream output = new FileOutputStream(f);
	        byte data[] = new byte[1024];
	        while ((count = input.read(data)) != -1) {
	            output.write(data, 0, count);
	        }
	        output.flush();
	        output.close();
	        input.close();
	    } catch (Exception e) {
	        Log.i("Download Error: ", e.toString());
	    }
	}
}
