package com.example.hossain.relationship;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.hossain.relationship.helperClass.NameProvider;

/**
 * Created by hossain on 6/15/2020.
 */

public class AddInfoActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    private String[] fruits = {"Apple", "Appy", "Banana", "Chery", "Grape"};
    private AppCompatAutoCompleteTextView autoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info);

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_other, menu);

        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query){
                searchView.clearFocus();
                Intent intent = new Intent(getApplicationContext(), SearchedInfoActivity.class);
                intent.putExtra("name", query);
                startActivity(intent);
                //Toast.makeText(getApplicationContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText){
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

        switch (id){
            case R.id.action_settings:
                return true;

            case R.id.action_refresh:
                Intent intent1 = new Intent(getApplicationContext(), AddInfoActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickAddInfo(View view) {
        // Add a new info
        //ContentValues values = new ContentValues();

        // get view id
        EditText name = (EditText) findViewById(R.id.txtName);
        EditText father = (EditText) findViewById(R.id.txtFather);
        EditText mother = (EditText) findViewById(R.id.txtMother);
        EditText spouse = (EditText) findViewById(R.id.txtSpouse);
        EditText mobile = (EditText) findViewById(R.id.txtMobileNo);
        EditText email = (EditText) findViewById(R.id.txtEmail);
        int priority = 0;

        if(name.getText() == null || name.getText().toString().trim().length() == 0){
            Toast.makeText(getBaseContext(), "Please add Name", Toast.LENGTH_LONG).show();
            return;
        }

        // get checked radio button
        RadioGroup genderrg = (RadioGroup) findViewById(R.id.genderBtn);
        int genderId = genderrg.getCheckedRadioButtonId();
        RadioButton genderButton = (RadioButton) findViewById(genderId);

        if(genderId == -1){
            Toast.makeText(getBaseContext(), "Please select gender", Toast.LENGTH_LONG).show();
            return;
        }
        RadioGroup prg = (RadioGroup) findViewById(R.id.showHomeBtn);
        int pId = prg.getCheckedRadioButtonId();
        RadioButton pButton = (RadioButton) findViewById(pId);

        // get txt
        if(pButton.getText().toString().equals("Yes"))
            priority = 1;
        String txtName = name.getText().toString().trim();
        String txtFather = father.getText().toString().trim();
        String txtMother = mother.getText().toString().trim();
        String txtGender = genderButton.getText().toString().trim();
        String txtSpouse = spouse.getText().toString().trim();
        String txtMobileNo = mobile.getText().toString().trim();
        String txtEmail = email.getText().toString().trim();

        // add to database
        DBHelper db = new DBHelper(getApplicationContext());
        long id = 0;

        /* create entry for father, mother, spouse and get their id */
        long fatherID = 0;
        long motherID = 0;
        long spouseID = 0;

        if(txtFather.length() !=0 )
            fatherID = db.insertInfo(txtFather,"","","Male","","","",null,null,null,0);
        if(txtMother.length() !=0 )
            motherID = db.insertInfo(txtMother,"","","Female","","","",null,null,null,0);
        if(txtSpouse.length() !=0 ){
            if(txtGender.equals("Male"))
                spouseID = db.insertInfo(txtSpouse,"","","Female","","","",null,null,null,0);
            else
                spouseID = db.insertInfo(txtSpouse,"","","Male","","","",null,null,null,0);
        }

        Integer fid = null;
        Integer mid = null;
        Integer sid = null;
        if(fatherID > 0) fid = (int)fatherID;
        if(motherID > 0) mid = (int)motherID;
        if(spouseID > 0) sid = (int)spouseID;

        // insert entry
        id = db.insertInfo(txtName, txtFather, txtMother, txtGender, txtSpouse, txtMobileNo, txtEmail, fid, mid, sid, priority);

        // update name and id of rest entries
        if(sid != null){
            db.updateInfo(sid, db.SPOUSE, txtName);
            db.updateInfoID(sid, db.SPOUSEID, (int)id);
        }
        if(fid != null){
            if(mid != null){
                db.updateInfo(fid, db.SPOUSE, txtMother);
                db.updateInfoID(fid, db.SPOUSEID, mid);

                db.updateInfo(mid, db.SPOUSE, txtFather);
                db.updateInfoID(mid, db.SPOUSEID, fid);
            }
        }

        // for another one or go home
        LinearLayout addInfo = (LinearLayout) findViewById(R.id.addInfoLinearLayout);
        addInfo.setVisibility(View.GONE);

        RelativeLayout addAnother = (RelativeLayout)findViewById(R.id.addAnother);
        addAnother.setVisibility(View.VISIBLE);

        Toast.makeText(getApplicationContext(), "Information added successfully."
                , Toast.LENGTH_LONG).show();

    }

    public void addAnotherInfo(View view){
        LinearLayout addInfo = (LinearLayout) findViewById(R.id.addInfoLinearLayout);
        addInfo.setVisibility(View.VISIBLE);

        RelativeLayout addAnother = (RelativeLayout)findViewById(R.id.addAnother);
        addAnother.setVisibility(View.GONE);

        EditText name = (EditText) findViewById(R.id.txtName);
        name.setText("");

        EditText father = (EditText) findViewById(R.id.txtFather);
        father.setText("");

        EditText mother = (EditText) findViewById(R.id.txtMother);
        mother.setText("");

        EditText spouse = (EditText) findViewById(R.id.txtSpouse);
        spouse.setText("");

        EditText mobile = (EditText) findViewById(R.id.txtMobileNo);
        mobile.setText("");

        /*RadioButton genderMale = (RadioButton) findViewById(R.id.radioMale);
        genderMale.setChecked(false);
        RadioButton genderFemale = (RadioButton) findViewById(R.id.radioFemale);
        genderFemale.setChecked(false);*/
        RadioGroup genderrg = (RadioGroup) findViewById(R.id.genderBtn);
        genderrg.clearCheck();
    }
    public void goHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
