package com.example.hossain.relationship;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SearchedInfoActivity extends AppCompatActivity {

    ListView nameListView;
    private DBHelper db;

    ArrayAdapter resultAdapter;
    private ArrayList<String> resultNameList;
    private ArrayList<Integer> resultIdList;
    TextView resultEmpty;

    String queryName;
    Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_results);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        db = new DBHelper(getApplicationContext());
        resultIdList = new ArrayList<>();
        resultNameList = new ArrayList<>();

        queryName = getIntent().getStringExtra("name");
        Cursor cursor = db.getQueryInfo(queryName);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            resultNameList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            resultIdList.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
            cursor.moveToNext();
        }

        nameListView = (ListView) findViewById(R.id.mainListView);
        resultAdapter = new ArrayAdapter<String>(this, R.layout.listview_main, resultNameList);
        nameListView.setAdapter(resultAdapter);

        resultEmpty = (TextView) findViewById(R.id.resultEmpty);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddInfoActivity.class));
            }
        });

        if(!resultNameList.isEmpty()){
            resultEmpty.setVisibility(View.GONE);
        }

        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", resultIdList.get(position));
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other, menu);
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        mainMenu = menu;

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                //searchView.clearFocus();
                Intent intent = new Intent(getApplicationContext(), SearchedInfoActivity.class);
                intent.putExtra("name", query);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                resultAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                return true;

            case R.id.action_refresh:
                Intent intent1 = new Intent(getApplicationContext(), SearchedInfoActivity.class);
                intent1.putExtra("name", queryName);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}

