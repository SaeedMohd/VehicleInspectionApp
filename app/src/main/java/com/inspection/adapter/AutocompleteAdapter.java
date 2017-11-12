
package com.inspection.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.inspection.inspection.R;

public class AutocompleteAdapter extends BaseAdapter {
    
    private Context activity;
    private static LayoutInflater inflater=null;
    View ll;
    String [] item = null;
    
    public AutocompleteAdapter(Context a,String[] facilityArray) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.item = facilityArray;
    }
    public int getCount() {
        return item.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
        vi = inflater.inflate(R.layout.drawer_list_item, null);
        TextView title = (TextView)vi.findViewById(R.id.title);
        title.setTag(position);
        title.setText(item[position]);
        return vi;
    }
}