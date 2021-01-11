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

public class MainActivity extends AppCompatActivity implements ActionDialogFragment.ActionDialogFragmentListener{

    ListView nameListView;
    ArrayAdapter arrayAdapter;
    TextView txtEmpty;
    private ArrayList<String> nameList;
    private ArrayList<Integer> idList;
    private DBHelper db;

    ArrayAdapter resultAdapter;
    private ArrayList<String> resultNameList;
    private ArrayList<Integer> resultIdList;
    TextView resultEmpty;

    private boolean isResultInfo;
    Menu mainMenu;
    int ff = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        nameList = new ArrayList<String>();
        idList = new ArrayList<Integer>();
        isResultInfo = false;
        db = new DBHelper(getApplicationContext());

        Cursor cursor = db.getPriorityInfo();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            nameList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            idList.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
            ff += 1;
            cursor.moveToNext();
        }

        nameListView = (ListView) findViewById(R.id.mainListView);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.listview_main, nameList);
        nameListView.setAdapter(arrayAdapter);

        txtEmpty = (TextView) findViewById(R.id.txtEmpty);
        resultEmpty = (TextView) findViewById(R.id.resultEmpty);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Add action" + Integer.toString(ff), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                startActivity(new Intent(getApplicationContext(), AddInfoActivity.class));

            }
        });

        if(!nameList.isEmpty()){
            txtEmpty.setVisibility(View.GONE);
        }

        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getBaseContext(), "Position : "+String.format("%d",position)+", ID : "+String.format("%d",id),
                         //Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", idList.get(position));
                startActivity(intent);
            }
        });

        nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ActionDialogFragment dialog = ActionDialogFragment.newInstance(ActionDialogFragment.DELETE_REMOVE,
                        position, idList.get(position));
                dialog.show(getSupportFragmentManager(), "DeleteRemoveHome");

                return true;
            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {
        //Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
    }


    public void resultInfo(String name){
        resultNameList = new ArrayList<String>();
        resultIdList = new ArrayList<Integer>();

        //(db == null)
        //DBHelper dbHelper = new DBHelper(getApplicationContext());
        Cursor cursor = db.getQueryInfo(name);
        if(cursor==null)
            return;

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            resultNameList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            resultIdList.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
            cursor.moveToNext();
        }

        //nameListView = (ListView) findViewById(R.id.mainListView);
        resultAdapter = new ArrayAdapter<String>(this, R.layout.listview_main, resultNameList);
        nameListView.setAdapter(resultAdapter);
        isResultInfo = true;

        txtEmpty.setVisibility(View.GONE);
        if(resultNameList.isEmpty()){
            resultEmpty.setVisibility(View.VISIBLE);
        }else
            resultEmpty.setVisibility(View.GONE);

        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", resultIdList.get(position));
                startActivity(intent);
            }
        });
        nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ActionDialogFragment dialog = ActionDialogFragment.newInstance(ActionDialogFragment.DELETE_REMOVE_ADD,
                        position, resultIdList.get(position));
                dialog.show(getSupportFragmentManager(), "DeleteRemoveAdd");

                return true;
            }
        });
    }

    public void showAllInfo(){
        resultNameList = new ArrayList<String>();
        resultIdList = new ArrayList<Integer>();

        Cursor cursor = db.getAllInfo();
        if(cursor==null)
            return;

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            resultNameList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
            resultIdList.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
            cursor.moveToNext();
        }

        resultAdapter = new ArrayAdapter<String>(this, R.layout.listview_main, resultNameList);
        nameListView.setAdapter(resultAdapter);
        isResultInfo = true;

        resultEmpty.setVisibility(View.GONE);
        if(resultNameList.isEmpty()){
            txtEmpty.setVisibility(View.VISIBLE);
        }else
            txtEmpty.setVisibility(View.GONE);

        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", resultIdList.get(position));
                startActivity(intent);
            }
        });
        nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ActionDialogFragment dialog = ActionDialogFragment.newInstance(ActionDialogFragment.DELETE_REMOVE_ADD,
                        position, resultIdList.get(position));
                dialog.show(getSupportFragmentManager(), "DeleteRemoveAdd");

                return true;
            }
        });
    }

    public void showPriorityInfo(){
        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_main, nameList);
        nameListView.setAdapter(arrayAdapter);

        resultEmpty.setVisibility(View.GONE);
        if(nameList.isEmpty())
            txtEmpty.setVisibility(View.VISIBLE);
        else
            txtEmpty.setVisibility(View.GONE);
        isResultInfo = false;

        nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", idList.get(position));
                startActivity(intent);
            }
        });
        nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                ActionDialogFragment dialog = ActionDialogFragment.newInstance(ActionDialogFragment.DELETE_REMOVE,
                        position, idList.get(position));
                dialog.show(getSupportFragmentManager(), "DeleteRemove");

                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        mainMenu = menu;

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                //searchView.clearFocus();
                resultInfo(query);
                //Toast.makeText(getApplicationContext(), "onQueryTextSubmit : "+query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
                if(isResultInfo)
                    resultAdapter.getFilter().filter(newText);
                else
                    arrayAdapter.getFilter().filter(newText);
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener(){
            @Override
            public boolean onClose(){
                //Toast.makeText(getApplicationContext(), "onClose() : ", Toast.LENGTH_LONG).show();
                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_main, nameList);
                nameListView.setAdapter(arrayAdapter);

                resultEmpty.setVisibility(View.GONE);
                if(nameList.isEmpty())
                    txtEmpty.setVisibility(View.VISIBLE);
                else
                    txtEmpty.setVisibility(View.GONE);
                isResultInfo = false;

                nameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                        intent.putExtra("id", idList.get(position));
                        startActivity(intent);
                    }
                });
                nameListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        ActionDialogFragment dialog = ActionDialogFragment.newInstance(ActionDialogFragment.DELETE_REMOVE,
                                position, idList.get(position));
                        dialog.show(getSupportFragmentManager(), "DeleteRemove");

                        return true;
                    }
                });
                return true;
            }
        });

/*
        searchView.setOnQueryTextFocusChangeListener(new SearchView.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View view, boolean bool){
                Toast.makeText(getApplicationContext(), "onFocusChange() : ", Toast.LENGTH_LONG).show();
            }
        });
        searchView.setOnSearchClickListener(new SearchView.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "onClick() : ", Toast.LENGTH_LONG).show();
            }
        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Toast.makeText(getApplicationContext(), "onSuggestionSelect() : ", Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Toast.makeText(getApplicationContext(), "onSuggestionClick() : ", Toast.LENGTH_LONG).show();
                return false;
            }
        });
*/

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

            case R.id.dropAndCreateTable:
                DBHelper db = new DBHelper(getApplicationContext());
                //db.dropAndCreate();

                Toast.makeText(getApplicationContext(), "Dropped and created new table.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.action_showAll:
                MenuItem showPriority = mainMenu.findItem(R.id.action_showPriority);
                item.setVisible(false);
                showPriority.setVisible(true);
                showAllInfo();
                break;

            case R.id.action_refresh:
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;

            case R.id.action_showPriority:
                MenuItem showAll = mainMenu.findItem(R.id.action_showAll);
                item.setVisible(false);
                showAll.setVisible(true);
                showPriorityInfo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFinishDialog(String action, String category, int position) {
        if(category.equals(ActionDialogFragment.DELETE_REMOVE)){
            if(action.equals("Delete")){
                Toast.makeText(getApplicationContext(), nameList.get(position)+" is deleted.", Toast.LENGTH_LONG).show();
                //arrayAdapter.remove(position);
                nameList.remove(position);
                idList.remove(position);
                arrayAdapter.notifyDataSetChanged();
            }
            else if(action.equals("Remove")){
                Toast.makeText(getApplicationContext(), nameList.get(position)+" is removed from home.",
                        Toast.LENGTH_LONG).show();
                nameList.remove(position);
                idList.remove(position);
                arrayAdapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
        else if(category.equals(ActionDialogFragment.DELETE_REMOVE_ADD)){
            if(action.equals("Delete")){
                Toast.makeText(getApplicationContext(), resultNameList.get(position)+" is deleted.", Toast.LENGTH_LONG).show();
                resultNameList.remove(position);
                resultIdList.remove(position);
                resultAdapter.notifyDataSetChanged();
            }
            else if(action.equals("Remove")){
                Toast.makeText(getApplicationContext(), resultNameList.get(position)+" is removed from home.",
                        Toast.LENGTH_LONG).show();
                resultNameList.remove(position);
                resultIdList.remove(position);
                resultAdapter.notifyDataSetChanged();
            }
            else if(action.equals("Add")){
                nameList.add(resultNameList.get(position));
                idList.add(resultIdList.get(position));
                Toast.makeText(getApplicationContext(), resultNameList.get(position)+" is added to home.",
                        Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}



    /*// for animation
        /*if (!opened) {
                    //linearLayout.setVisibility(View.VISIBLE);
                    TranslateAnimation animate = new TranslateAnimation(0, 0, linearLayout.getHeight(), 0);
                    animate.setDuration(500);
                    animate.setFillAfter(true);
                    linearLayout.startAnimation(animate);
                    //mainContentSV.smoothScrollTo(0, mainContentSV.getChildAt(0).getBottom());
                    //mainContentSV.fullScroll(View.FOCUS_DOWN);
                }*/
