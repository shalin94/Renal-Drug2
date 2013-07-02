package com.renaldrug2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TwoItemListAdapter extends ArrayAdapter<ImpairmentGFR>{
private ArrayList<ImpairmentGFR> items;
public TwoItemListAdapter(Context context, int textViewResourceId, ArrayList<ImpairmentGFR> items) {
	super(context, textViewResourceId, items);
	this.items = items;
}
@Override 
public View getView(int position,View convertView, ViewGroup parent){
	View v = convertView;
	if(v==null) {
		LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		v = vi.inflate(R.layout.two_item_view, null); 
	}
	ImpairmentGFR o = items.get(position);
	if(o != null) {
		TextView r = (TextView) v.findViewById(R.id.right);
		TextView l = (TextView) v.findViewById(R.id.left);
		if(r != null) {
			r.setText(o.getDescription());
		}
		if(l != null) {
			l.setText(o.getDosageSubType());
		}
	}
	return v;
}
}
