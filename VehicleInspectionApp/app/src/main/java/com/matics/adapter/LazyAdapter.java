
package com.matics.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.matics.R;
import com.matics.model.UserAccountModel;

public class LazyAdapter extends BaseAdapter {
    
    private Context activity;
    private static LayoutInflater inflater=null;
    View ll;
    ArrayList<UserAccountModel>items = new ArrayList<UserAccountModel>();
    
    public LazyAdapter(Context a, ArrayList<UserAccountModel> items) {
        activity = a;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.items = items;
    }
    public int getCount() {
        return items.size();
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
        vi = inflater.inflate(R.layout.row, null);
        TextView name = (TextView)vi.findViewById(R.id.textViewBluetoothName);
        name.setText(items.get(0).getAccountFullName());
        return vi;
    }
}