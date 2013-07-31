package com.renaldrug2;

import java.util.ArrayList;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class SingleListItem extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.single_list_item);

		TextView txtMed = (TextView) findViewById(R.id.medicine_label);

		Intent i = getIntent();
		String medicine = i.getStringExtra("medicine");
		txtMed.setText(medicine);
		final ListView normalList = (ListView) findViewById(R.id.normal_dose_list);
		final ListView GFRList = (ListView) findViewById(R.id.gfr_list);
		final ListView ReplacementList = (ListView) findViewById(R.id.replacement_list);
		final ArrayList<String> list1 = new ArrayList<String>();
		DataBaseHelper myDbHelper = null;
		ArrayList<ImpairmentGFR> list2 = new ArrayList<ImpairmentGFR>();
		ArrayList<ImpairmentGFR> list3 = new ArrayList<ImpairmentGFR>();
		try {
			myDbHelper = new DataBaseHelper(SingleListItem.this);
			Cursor c1 = myDbHelper
					.rawQuery(
							"select md.dosage_value from medicine_dosage md, medicines m, dosage_types d "
									+ "where md.medicine_id = m._id and md.dosage_type_id = d._id"
									+ " and m.medicine_name = '" + medicine
									+ "' and d.description='Normal'", null);
			if (c1.moveToFirst()) {
				while (c1.isAfterLast() == false) {
					String name = c1.getString(c1
							.getColumnIndex("dosage_value"));
					list1.add(name);
					c1.moveToNext();
				}
			}

			Cursor c2 = myDbHelper
					.rawQuery(
							"select dst.dosage_sub_type_description,md.dosage_value from medicine_dosage md, medicines m, dosage_types d, dosage_sub_types dst "
									+ "where md.medicine_id = m._id and md.dosage_type_id = d._id and d.description='Impairment GFR' "
									+ "and md.dosage_sub_type_id = dst._id and m.medicine_name = '"
									+ medicine + "'", null);
			c2.moveToFirst();
			do {
				ImpairmentGFR gfr = new ImpairmentGFR();
				String description = c2.getString(c2
						.getColumnIndex("dosage_value"));
				String subtype = c2.getString(c2
						.getColumnIndex("dosage_sub_type_description"));
				gfr.setDescription(description);
				gfr.setDosageSubType(subtype);
				list2.add(gfr);
				// TwoItemListAdapter adapter2 = new
				// TwoItemListAdapter(this,R.layout.two_item_view,list2);
				// GFRList.setAdapter(adapter2);
			} while (c2.moveToNext());
			
			Cursor c3 = myDbHelper
					.rawQuery(
							"select dst.dosage_sub_type_description,md.dosage_value from medicine_dosage md, medicines m, dosage_types d, dosage_sub_types dst "
									+ "where md.medicine_id = m._id and md.dosage_type_id = d._id and d.description='Replacement' "
									+ "and md.dosage_sub_type_id = dst._id and m.medicine_name = '"
									+ medicine + "'", null);
			c3.moveToFirst();
			do {
				ImpairmentGFR replacement = new ImpairmentGFR();
				String description = c3.getString(c3
						.getColumnIndex("dosage_value"));
				String subtype = c3.getString(c3
						.getColumnIndex("dosage_sub_type_description"));
				replacement.setDescription(description);
				replacement.setDosageSubType(subtype);
				list3.add(replacement);
				// TwoItemListAdapter adapter2 = new
				// TwoItemListAdapter(this,R.layout.two_item_view,list2);
				// GFRList.setAdapter(adapter2);
			} while (c3.moveToNext());

		} catch (Exception e) {
			Log.e(this.getClass().getName(), "Failed to run query", e);
		} finally {
			myDbHelper.close();
		}
		final StableArrayAdapter adapter = new StableArrayAdapter(this,
				R.layout.simplelist1, list1);
		normalList.setAdapter(adapter);
		TwoItemListAdapter adapter2 = new TwoItemListAdapter(this,
				R.layout.two_item_view, list2);
		GFRList.setAdapter(adapter2);
		TwoItemListAdapter adapter3 = new TwoItemListAdapter(this,
				R.layout.two_item_view, list3);
		ReplacementList.setAdapter(adapter3);
		//setListViewHeightBasedOnChildren(normalList);
		//setListViewHeightBasedOnChildren(GFRList);
		//setListViewHeightBasedOnChildren(ReplacementList);

	}
	public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter(); 
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
