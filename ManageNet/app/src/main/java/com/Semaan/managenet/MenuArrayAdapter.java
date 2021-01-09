package com.Semaan.managenet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

import DataClasses.MenuObject;

public class MenuArrayAdapter<T> extends ArrayAdapter implements Serializable {

    Context mContext;
    int resource;
    List<T> objects;

    public MenuArrayAdapter(Context context, int resource, List<T> objects){
        super(context, resource,objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.menu_object_list_row, parent, false);
        }

        TextView name=(TextView)convertView.findViewById(R.id.menu_object_name);
        TextView desc=(TextView)convertView.findViewById(R.id.menu_object_description);
        TextView price=(TextView)convertView.findViewById(R.id.menu_object_price);
        name.setText(((MenuObject)getItem(position)).getName());
        desc.setText(((MenuObject)getItem(position)).getDescritpion());
        price.setText(((MenuObject)getItem(position)).getPrice()+"");
        return convertView;
    }
}
