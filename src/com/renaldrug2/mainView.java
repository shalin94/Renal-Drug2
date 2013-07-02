package com.renaldrug2;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;

import com.renaldrug2.DataBaseHelper;


import android.app.*;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.*;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
public class mainView extends Activity{
	private TextView myText = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listview);
		final ListView listview = (ListView) findViewById(R.id.listview);
		final ArrayList<String> list = new ArrayList<String>();
		DataBaseHelper myDbHelper = null;
		try {
			myDbHelper = new DataBaseHelper(mainView.this);
			//Cursor c = myDbHelper.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
			List<String> tables = new ArrayList<String>();
			Cursor c = myDbHelper.rawQuery("SELECT COUNT(*) FROM medicines",null);
			while(c.moveToNext()) {
				String t = c.getString(0);
				String count = t;
				Resources res = getResources();
				String text = String.format(res.getString(R.string.count1), count);
				//ListView lview = new ListView(this);
				myText = new TextView(this);
				myText.setText(text);
				//lview.addView(myText);
				//setContentView(lview);
				Log.i(this.getClass().getName(), "table = " + t);
				tables.add(t);
			}
			Cursor cursor = myDbHelper.rawQuery("select * from medicines",null);
			if(cursor.moveToFirst()) {
				while (cursor.isAfterLast() == false){
					String name = cursor.getString(cursor.getColumnIndex("medicine_name"));
						list.add(name);
						
	                cursor.moveToNext();
				}
			}
		}catch (Exception e){
			Log.e(this.getClass().getName(), "Failed to run query", e);
		} finally {
			myDbHelper.close();
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,android.R.layout.simple_list_item_1, list);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, final View view, int position,long id){
				String medicine = ((TextView) view).getText().toString();
				Intent i = new Intent(getApplicationContext(), SingleListItem.class);
				i.putExtra("medicine",medicine);
				startActivity(i);
			}
		});
		//select dosage_value from medicine_dosage where 
		//	medicine_id = (select _id from medicines where medicine_name='Aztreonam') 
		//  and dosage_type_id = (select _id from dosage_types where description='Normal')
		
		// using join
		//select md.dosage_value from medicine_dosage md, medicines m, dosage_types d 
		//	where md.medicine_id = m._id and md.dosage_type_id = d._id 
		//	and m.medicine_name = 'Aztreonam' and d.description='Normal'
		//
		//select dst.dosage_sub_type_description,md.dosage_value from medicine_dosage md, medicines m,
		//dosage_types d, dosage_sub_types dst 
		//where md.medicine_id = m._id and md.dosage_type_id = d._id 
		//and d.description='Impairment GFR' and md.dosage_sub_type_id = dst._id 
		//and m.medicine_name = 'Aztreonam' 
	}

}

