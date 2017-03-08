package jucaipen.bluetooth.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import jucaipen.bluetooth.R;
/**
 * Created by Administrator on 2017/3/8.
 */

public class BlueAdapter extends BaseAdapter {

    private final List<BluetoothDevice> devices;
    private final Context context;

    public BlueAdapter(List<BluetoothDevice> devices, Context context) {
        this.devices=devices;
        this.context=context;
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.activity_item,null);
        }
        TextView address= (TextView) convertView.findViewById(R.id.address);
        TextView name= (TextView) convertView.findViewById(R.id.name);
        name.setText(devices.get(position).getName());
        address.setText(devices.get(position).getAddress());
        return convertView;
    }
}
