package com.Semaan.managenet;

import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;


public class ManagerUserListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_user_list);

        ListView lv = (ListView) findViewById(R.id.manager_user_list);

        /*Cursor c = DatabaseTask.query("SELECT * FROM user");
        c.moveToFirst();

        CursorAdapter ca = new SimpleCursorAdapter(this,R.layout.list_view_row_layout,c,new String[]{"username"},new int[]{R.id.list_view_row_text_view});
        lv.setAdapter(ca);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = ((TextView)view.findViewById(R.id.list_view_row_text_view)).getText().toString();
                Snackbar.make(view,name,Snackbar.LENGTH_SHORT).show();
            }
        });*/
    }
}
