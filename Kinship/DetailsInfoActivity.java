package com.example.hossain.relationship;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hossain on 6/19/2020.
 */

public class DetailsInfoActivity extends AppCompatActivity implements SelectDialogFragment.SelectDialogFragmentListener{
    //LinearLayout linearLayout;
    private int num_boys = 0;
    private int num_girls = 0;

    private Integer id;
    private Integer fatherId;
    private Integer motherId;
    private Integer spouseId;
    private String txtGender = "";

    private ArrayList<String> maleList;
    private ArrayList<String> femaleList;

    private AppCompatAutoCompleteTextView autoTextFather;
    private AppCompatAutoCompleteTextView autoTextSpouse;
    private AppCompatAutoCompleteTextView autoTextMother;

    private AppCompatAutoCompleteTextView autoTextBoy;
    private AppCompatAutoCompleteTextView autoTextGirl;

    private static String selectedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_info);

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

        selectedName = "";
        // get name and id
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);

        // get views
        TextView name = (TextView) findViewById(R.id.txtName);
        TextView father = (TextView) findViewById(R.id.txtFather);
        TextView mother = (TextView) findViewById(R.id.txtMother);
        TextView spouse = (TextView) findViewById(R.id.txtSpouse);
        TextView mobile = (TextView) findViewById(R.id.txtMobileNo);
        TextView email = (TextView) findViewById(R.id.txtEmail);
        TextView gender = (TextView) findViewById(R.id.txtGender);

        // set onLongClickAction
        name.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SelectDialogFragment dialog = SelectDialogFragment.newInstance(SelectDialogFragment.ADD_NAME_OR_DELETE,
                        SelectDialogFragment.CATEGORY_NAME, id);
                dialog.show(getSupportFragmentManager(), "AddNameHome");
                return true;
            }
        });
        father.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(fatherId > 0) {
                    SelectDialogFragment dialog = SelectDialogFragment.newInstance(SelectDialogFragment.ADD_NAME_OR_DELETE,
                            SelectDialogFragment.CATEGORY_FATHER, id);
                    dialog.show(getSupportFragmentManager(), "DeleteFather");
                    return true;
                }
                return false;
            }
        });
        mother.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(motherId > 0) {
                    SelectDialogFragment dialog = SelectDialogFragment.newInstance(SelectDialogFragment.ADD_NAME_OR_DELETE,
                            SelectDialogFragment.CATEGORY_MOTHER, id);
                    dialog.show(getSupportFragmentManager(), "DeleteMother");
                    return true;
                }
                return false;
            }
        });
        spouse.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(spouseId > 0) {
                    SelectDialogFragment dialog = SelectDialogFragment.newInstance(SelectDialogFragment.ADD_NAME_OR_DELETE,
                            SelectDialogFragment.CATEGORY_SPOUSE, id);
                    dialog.show(getSupportFragmentManager(), "DeleteSpouse");
                    return true;
                }
                return false;
            }
        });

        // get values from database
        DBHelper db = new DBHelper(getApplicationContext());
        Cursor cursor = db.getInfo(id);

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            // set value to view text
            name.setText(cursor.getString(cursor.getColumnIndex(db.NAME)));
            father.setText(cursor.getString(cursor.getColumnIndex(db.FATHER)));
            mother.setText(cursor.getString(cursor.getColumnIndex(db.MOTHER)));
            spouse.setText(cursor.getString(cursor.getColumnIndex(db.SPOUSE)));
            mobile.setText(cursor.getString(cursor.getColumnIndex(db.MOBILENO)));
            email.setText(cursor.getString(cursor.getColumnIndex(db.EMAIL)));
            gender.setText(cursor.getString(cursor.getColumnIndex(db.GENDER)));

            fatherId = cursor.getInt(cursor.getColumnIndex(db.FATHERID));
            motherId= cursor.getInt(cursor.getColumnIndex(db.MOTHERID));
            spouseId = cursor.getInt(cursor.getColumnIndex(db.SPOUSEID));
            txtGender = cursor.getString(cursor.getColumnIndex(db.GENDER));

            cursor.moveToNext();
        }

        // get boys / girls (Children)
        if(txtGender.equals("Male"))
            cursor = db.getChildren(name.getText().toString(), "Father");
        else
            cursor = db.getChildren(name.getText().toString(), "Mother");

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false){
            String nametxt = cursor.getString(cursor.getColumnIndex(db.NAME));
            int idx = cursor.getInt(cursor.getColumnIndex(db.ID));
            String gend = cursor.getString(cursor.getColumnIndex(db.GENDER));
            childrenView(gend, nametxt, idx);

            cursor.moveToNext();
        }

        // for suggestion
        maleList = db.getAllName("Male");
        femaleList = db.getAllName("Female");

        ArrayAdapter<String> adapterMale = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, maleList);
        ArrayAdapter<String> adapterFemale = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, femaleList);

        autoTextFather = (AppCompatAutoCompleteTextView) findViewById(R.id.txtFatherAdd);
        autoTextFather.setThreshold(1);
        autoTextFather.setAdapter(adapterMale);

        autoTextMother = (AppCompatAutoCompleteTextView) findViewById(R.id.txtMotherAdd);
        autoTextMother.setThreshold(1);
        autoTextMother.setAdapter(adapterFemale);

        if(txtGender.equals("Male")) {
            autoTextSpouse = (AppCompatAutoCompleteTextView) findViewById(R.id.txtSpouseeAdd);
            autoTextSpouse.setThreshold(1);
            autoTextSpouse.setAdapter(adapterFemale);
        }
        else {
            autoTextSpouse = (AppCompatAutoCompleteTextView) findViewById(R.id.txtSpouseeAdd);
            autoTextSpouse.setThreshold(1);
            autoTextSpouse.setAdapter(adapterMale);
        }
        // for boys/girls
        autoTextBoy = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAddBoy);
        autoTextBoy.setThreshold(1);
        autoTextBoy.setAdapter(adapterMale);

        autoTextGirl = (AppCompatAutoCompleteTextView) findViewById(R.id.txtAddGirl);
        autoTextGirl.setThreshold(1);
        autoTextGirl.setAdapter(adapterFemale);
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
        int idx = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (idx){
            case R.id.action_settings:
                return true;

            case R.id.action_refresh:
                Intent intent1 = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent1.putExtra("id", id);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent1);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    //test
    public void show(int pos){
        if(pos == 1)
            Toast.makeText(this, "Item selected..", Toast.LENGTH_LONG).show();
        else if(pos == 2)
            Toast.makeText(this, "Nothing selected..", Toast.LENGTH_LONG).show();
        else if(pos == 3)
            Toast.makeText(this, "Item click..", Toast.LENGTH_LONG).show();
        else if(pos == 4)
            Toast.makeText(this, "Focus changed..", Toast.LENGTH_LONG).show();
    }
    // edit or cancel edit
    public void detailsEditCancel(View view){
        // for edit
        ImageView name = (ImageView) findViewById(R.id.nameEditBtn);
        ImageView father = (ImageView) findViewById(R.id.fatherEditBtn);
        ImageView mother = (ImageView) findViewById(R.id.motherEditBtn);
        ImageView spouse = (ImageView) findViewById(R.id.spouseEditBtn);
        ImageView mobile = (ImageView) findViewById(R.id.mobileNoEditBtn);
        ImageView email = (ImageView) findViewById(R.id.emailEditBtn);
        ImageView gender = (ImageView) findViewById(R.id.genderEditBtn);

        TextView edit = (TextView) findViewById(R.id.detailsEdit);
        TextView cancel = (TextView) findViewById(R.id.detailsEditCancel);

        //for add
        ImageView fatherAdd = (ImageView) findViewById(R.id.fatherAddBtn);
        ImageView motherAdd = (ImageView) findViewById(R.id.motherAddBtn);
        ImageView spouseAdd = (ImageView) findViewById(R.id.spouseAddBtn);

        switch (view.getId()){
            case R.id.detailsEdit:
                name.setVisibility(View.VISIBLE);
                mobile.setVisibility(View.VISIBLE);
                email.setVisibility(View.VISIBLE);
                gender.setVisibility(View.VISIBLE);
                if(fatherId > 0)
                    father.setVisibility(View.VISIBLE);
                else
                    fatherAdd.setVisibility(View.VISIBLE);

                if(motherId > 0)
                    mother.setVisibility(View.VISIBLE);
                else
                    motherAdd.setVisibility(View.VISIBLE);

                if(spouseId > 0)
                    spouse.setVisibility(View.VISIBLE);
                else
                    spouseAdd.setVisibility(View.VISIBLE);

                edit.setVisibility(View.GONE);
                cancel.setVisibility(View.VISIBLE);
                break;
            case R.id.detailsEditCancel:
                name.setVisibility(View.GONE);
                father.setVisibility(View.GONE);
                mother.setVisibility(View.GONE);
                spouse.setVisibility(View.GONE);
                mobile.setVisibility(View.GONE);
                email.setVisibility(View.GONE);
                gender.setVisibility(View.GONE);

                fatherAdd.setVisibility(View.GONE);
                motherAdd.setVisibility(View.GONE);
                spouseAdd.setVisibility(View.GONE);

                edit.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.GONE);
                break;
        }
    }

    // for edit existing entry
    public void edit(View view){
        TextView text;
        EditText editText;
        ImageView editBtn;
        LinearLayout ll;

        switch (view.getId()){
            case R.id.nameEditBtn:
                text = (TextView) findViewById(R.id.txtName);
                editText = (EditText) findViewById(R.id.txtNameEdit);
                editBtn = (ImageView) findViewById(R.id.nameEditBtn);
                ll = (LinearLayout) findViewById(R.id.editNameLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.fatherEditBtn:
                text = (TextView) findViewById(R.id.txtFather);
                editText = (EditText) findViewById(R.id.txtFatherEdit);
                editBtn = (ImageView) findViewById(R.id.fatherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editFatherLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.motherEditBtn:
                text = (TextView) findViewById(R.id.txtMother);
                editText = (EditText) findViewById(R.id.txtMotherEdit);
                editBtn = (ImageView) findViewById(R.id.motherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMotherLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.spouseEditBtn:
                text = (TextView) findViewById(R.id.txtSpouse);
                editText = (EditText) findViewById(R.id.txtSpouseeEdit);
                editBtn = (ImageView) findViewById(R.id.spouseEditBtn);
                ll = (LinearLayout) findViewById(R.id.editSpouseLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.mobileNoEditBtn:
                text = (TextView) findViewById(R.id.txtMobileNo);
                editText = (EditText) findViewById(R.id.txtMobileNoEdit);
                editBtn = (ImageView) findViewById(R.id.mobileNoEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMobileNoLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.emailEditBtn:
                text = (TextView) findViewById(R.id.txtEmail);
                editText = (EditText) findViewById(R.id.txtNameEdit);
                editBtn = (ImageView) findViewById(R.id.emailEditBtn);
                ll = (LinearLayout) findViewById(R.id.editEmailLayout);

                text.setVisibility(View.GONE);
                editText.setText(text.getText());
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.genderEditBtn:
                text = (TextView) findViewById(R.id.txtGender);
                editBtn = (ImageView) findViewById(R.id.genderEditBtn);
                ll = (LinearLayout) findViewById(R.id.editGenderLayout);

                text.setVisibility(View.GONE);
                editBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);

                RadioButton radioButton;
                if(text.getText().toString().equals("Male")){
                    radioButton = (RadioButton) findViewById(R.id.radioMale);
                    radioButton.setChecked(true);
                }
                else{
                    radioButton = (RadioButton) findViewById(R.id.radioFemale);
                    radioButton.setChecked(true);
                }
        }
    }
    public void editSave(View view){
        TextView text;
        EditText editText;
        ImageView editBtn;
        LinearLayout ll;

        String val;
        DBHelper db = new DBHelper(getApplicationContext());

        switch (view.getId()){
            case R.id.editNameSave:
                text = (TextView) findViewById(R.id.txtName);
                editText = (EditText) findViewById(R.id.txtNameEdit);
                editBtn = (ImageView) findViewById(R.id.nameEditBtn);
                ll = (LinearLayout) findViewById(R.id.editNameLayout);

                val = editText.getText().toString().trim();
                if(val.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Name can't be empty.", Toast.LENGTH_LONG).show();
                    break;
                }
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                db.updateInfo(id, DBHelper.NAME, val);
                break;

            case R.id.editFatherSave:
                text = (TextView) findViewById(R.id.txtFather);
                editText = (EditText) findViewById(R.id.txtFatherEdit);
                editBtn = (ImageView) findViewById(R.id.fatherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editFatherLayout);

                val = editText.getText().toString().trim();
                if(val.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Father name can't be empty.", Toast.LENGTH_LONG).show();
                    break;
                }
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);

                db.updateInfo(id, DBHelper.FATHER, val);
                db.updateInfo(fatherId, DBHelper.NAME, val);

                break;

            case R.id.editMotherSave:
                text = (TextView) findViewById(R.id.txtMother);
                editText = (EditText) findViewById(R.id.txtMotherEdit);
                editBtn = (ImageView) findViewById(R.id.motherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMotherLayout);

                val = editText.getText().toString().trim();
                if(val.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Mother name can't be empty.", Toast.LENGTH_LONG).show();
                    break;
                }
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);

                db.updateInfo(id, DBHelper.MOTHER, val);
                db.updateInfo(motherId, DBHelper.NAME, val);
                break;

            case R.id.editSpouseSave:
                text = (TextView) findViewById(R.id.txtSpouse);
                editText = (EditText) findViewById(R.id.txtSpouseeEdit);
                editBtn = (ImageView) findViewById(R.id.spouseEditBtn);
                ll = (LinearLayout) findViewById(R.id.editSpouseLayout);

                val = editText.getText().toString().trim();
                if(val.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Spouse name can't be empty.", Toast.LENGTH_LONG).show();
                    break;
                }
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);

                db.updateInfo(id, DBHelper.SPOUSE, val);
                db.updateInfo(spouseId, DBHelper.NAME, val);
                break;

            case R.id.editMobileNoSave:
                text = (TextView) findViewById(R.id.txtMobileNo);
                editText = (EditText) findViewById(R.id.txtMobileNoEdit);
                editBtn = (ImageView) findViewById(R.id.mobileNoEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMobileNoLayout);

                val = editText.getText().toString().trim();
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                db.updateInfo(id, DBHelper.MOBILENO, val);
                break;

            case R.id.editEmailSave:
                text = (TextView) findViewById(R.id.txtEmail);
                editText = (EditText) findViewById(R.id.txtEmailEdit);
                editBtn = (ImageView) findViewById(R.id.emailEditBtn);
                ll = (LinearLayout) findViewById(R.id.editEmailLayout);

                val = editText.getText().toString().trim();
                text.setVisibility(View.VISIBLE);
                text.setText(val);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                db.updateInfo(id, DBHelper.EMAIL, val);
                break;

            case R.id.editGenderSave:
                text = (TextView) findViewById(R.id.txtGender);
                editBtn = (ImageView) findViewById(R.id.genderEditBtn);
                ll = (LinearLayout) findViewById(R.id.editGenderLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);

                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.editGenderGroup);
                RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                text.setText(radioButton.getText());

                db.updateInfo(id, DBHelper.GENDER, radioButton.getText().toString());
        }
    }
    public void editCancel(View view){
        TextView text;
        ImageView editBtn;
        LinearLayout ll;

        switch (view.getId()){
            case R.id.editNameCancel:
                text = (TextView) findViewById(R.id.txtName);
                editBtn = (ImageView) findViewById(R.id.nameEditBtn);
                ll = (LinearLayout) findViewById(R.id.editNameLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editFatherCancel:
                text = (TextView) findViewById(R.id.txtFather);
                editBtn = (ImageView) findViewById(R.id.fatherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editFatherLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editMotherCancel:
                text = (TextView) findViewById(R.id.txtMother);
                editBtn = (ImageView) findViewById(R.id.motherEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMotherLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editSpouseCancel:
                text = (TextView) findViewById(R.id.txtSpouse);
                editBtn = (ImageView) findViewById(R.id.spouseEditBtn);
                ll = (LinearLayout) findViewById(R.id.editSpouseLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editMobileNoCancel:
                text = (TextView) findViewById(R.id.txtMobileNo);
                editBtn = (ImageView) findViewById(R.id.mobileNoEditBtn);
                ll = (LinearLayout) findViewById(R.id.editMobileNoLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editEmailCancel:
                text = (TextView) findViewById(R.id.txtEmail);
                editBtn = (ImageView) findViewById(R.id.emailEditBtn);
                ll = (LinearLayout) findViewById(R.id.editEmailLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.editGenderCancel:
                text = (TextView) findViewById(R.id.txtGender);
                editBtn = (ImageView) findViewById(R.id.genderEditBtn);
                ll = (LinearLayout) findViewById(R.id.editGenderLayout);

                text.setVisibility(View.VISIBLE);
                editBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
        }
    }

    //add father, mother or spouse ( new entry )
    public void add(View view){
        ImageView addBtn;
        LinearLayout ll;

        switch (view.getId()) {
            case R.id.fatherAddBtn:
                addBtn = (ImageView) findViewById(R.id.fatherAddBtn);
                ll = (LinearLayout) findViewById(R.id.addFatherLayout);

                addBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.motherAddBtn:
                addBtn = (ImageView) findViewById(R.id.motherAddBtn);
                ll = (LinearLayout) findViewById(R.id.addMotherLayout);

                addBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;

            case R.id.spouseAddBtn:
                addBtn = (ImageView) findViewById(R.id.spouseAddBtn);
                ll = (LinearLayout) findViewById(R.id.addSpouseLayout);

                addBtn.setVisibility(View.GONE);
                ll.setVisibility(View.VISIBLE);
                break;
        }
    }
    public void addSave(View view){
        AppCompatAutoCompleteTextView text;
        SelectDialogFragment dialog;

        switch (view.getId()){
            case R.id.addFatherSave:
                text = (AppCompatAutoCompleteTextView) findViewById(R.id.txtFatherAdd);
                selectedName = text.getText().toString().trim();
                if(selectedName.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please add father name.", Toast.LENGTH_LONG).show();
                    break;
                }
                dialog = SelectDialogFragment.newInstance(selectedName, "Father", id);
                dialog.show(getSupportFragmentManager(), "addFather");
                break;

            case R.id.addMotherSave:
                text = (AppCompatAutoCompleteTextView) findViewById(R.id.txtMotherAdd);
                selectedName = text.getText().toString().trim();
                if(selectedName.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please add mother name.", Toast.LENGTH_LONG).show();
                    break;
                }
                dialog = SelectDialogFragment.newInstance(selectedName, "Mother", id);
                dialog.show(getSupportFragmentManager(), "addMother");
                break;

            case R.id.addSpouseSave:
                text = (AppCompatAutoCompleteTextView) findViewById(R.id.txtSpouseeAdd);
                selectedName = text.getText().toString().trim();
                if(selectedName.length() == 0){
                    Toast.makeText(getApplicationContext(), "Please add spouse name.", Toast.LENGTH_LONG).show();
                    break;
                }
                dialog = SelectDialogFragment.newInstance(selectedName, "Spouse", id);
                dialog.show(getSupportFragmentManager(), "addSpouse");
                break;

        }
    }
    public void addCancel(View view){
        ImageView addBtn;
        LinearLayout ll;

        switch (view.getId()) {
            case R.id.addFatherCancel:
                addBtn = (ImageView) findViewById(R.id.fatherAddBtn);
                ll = (LinearLayout) findViewById(R.id.addFatherLayout);

                addBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.addMotherCancel:
                addBtn = (ImageView) findViewById(R.id.motherAddBtn);
                ll = (LinearLayout) findViewById(R.id.addMotherLayout);

                addBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;

            case R.id.addSpouseCancel:
                addBtn = (ImageView) findViewById(R.id.spouseAddBtn);
                ll = (LinearLayout) findViewById(R.id.addSpouseLayout);

                addBtn.setVisibility(View.VISIBLE);
                ll.setVisibility(View.GONE);
                break;
        }
    }

    // called when dialog finished
    @Override
    public void onFinishDialog(String action, String category, int entryID) {
        TextView text;
        LinearLayout layout;
        AppCompatAutoCompleteTextView actv;
        ImageView editBtn;
        ImageView addBtn;
        TextView detailsEdit;

        if(action.equals("Save")){
            if(category.equals("Father")){
                fatherId = entryID;
                text = (TextView) findViewById(R.id.txtFather);
                layout = (LinearLayout) findViewById(R.id.addFatherLayout);
                actv = (AppCompatAutoCompleteTextView) findViewById(R.id.txtFatherAdd);
                editBtn = (ImageView) findViewById(R.id.fatherEditBtn);

                editBtn.setVisibility(View.VISIBLE);
                text.setText(actv.getText());
                layout.setVisibility(View.GONE);
            }
            else if(category.equals("Mother")){
                motherId = entryID;
                text = (TextView) findViewById(R.id.txtMother);
                layout = (LinearLayout) findViewById(R.id.addMotherLayout);
                actv = (AppCompatAutoCompleteTextView) findViewById(R.id.txtMotherAdd);
                editBtn = (ImageView) findViewById(R.id.motherEditBtn);

                editBtn.setVisibility(View.VISIBLE);
                text.setText(actv.getText());
                layout.setVisibility(View.GONE);
            }
            else if(category.equals("Spouse")){
                spouseId = entryID;
                text = (TextView) findViewById(R.id.txtSpouse);
                layout = (LinearLayout) findViewById(R.id.addSpouseLayout);
                actv = (AppCompatAutoCompleteTextView) findViewById(R.id.txtSpouseeAdd);
                editBtn = (ImageView) findViewById(R.id.spouseEditBtn);

                editBtn.setVisibility(View.VISIBLE);
                text.setText(actv.getText());
                layout.setVisibility(View.GONE);
            }
            else if(category.equals("Boy")){
                Toast.makeText(getApplicationContext(), "Boy added.", Toast.LENGTH_LONG).show();
                addBoyGirlText(category, entryID);
            }
            else if(category.equals("Girl")){
                Toast.makeText(getApplicationContext(), "Girl added.", Toast.LENGTH_LONG).show();
                addBoyGirlText(category, entryID);
            }
        }
        else if(action.equals("Ok")){
            Toast.makeText(getApplicationContext(), "Added Name to Home", Toast.LENGTH_LONG).show();
        }
        else if(action.equals("Delete")){
            detailsEdit = (TextView) findViewById(R.id.detailsEditCancel);

            if(category.equals(SelectDialogFragment.CATEGORY_FATHER)){
                TextView txt = (TextView) findViewById(R.id.txtFather);
                txt.setText("");
                fatherId = 0;
                if(detailsEdit.isShown()){
                    editBtn = (ImageView) findViewById(R.id.fatherEditBtn);
                    addBtn = (ImageView) findViewById(R.id.fatherAddBtn);
                    editBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), "Father Name is Deleted.", Toast.LENGTH_LONG).show();
            }
            else if(category.equals(SelectDialogFragment.CATEGORY_MOTHER)){
                TextView txt = (TextView) findViewById(R.id.txtMother);
                txt.setText("");
                motherId = 0;
                if(detailsEdit.isShown()){
                    editBtn = (ImageView) findViewById(R.id.motherEditBtn);
                    addBtn = (ImageView) findViewById(R.id.motherAddBtn);
                    editBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), "Mother Name is Deleted.", Toast.LENGTH_LONG).show();
            }
            else if(category.equals(SelectDialogFragment.CATEGORY_SPOUSE)){
                TextView txt = (TextView) findViewById(R.id.txtSpouse);
                txt.setText("");
                spouseId = 0;
                if(detailsEdit.isShown()){
                    editBtn = (ImageView) findViewById(R.id.spouseEditBtn);
                    addBtn = (ImageView) findViewById(R.id.spouseAddBtn);
                    editBtn.setVisibility(View.GONE);
                    addBtn.setVisibility(View.VISIBLE);
                }
                Toast.makeText(getApplicationContext(), "Spouse Name is Deleted.", Toast.LENGTH_LONG).show();
            }
        }
        else if(action.equals("Cancel")){
            Toast.makeText(getApplicationContext(), "Cancelled", Toast.LENGTH_LONG).show();
        }
    }
    // show children
    public void childrenView(String gender, final String name, final int id){
        if(gender.equals("Male")){
            // views
            LinearLayout parent = (LinearLayout) findViewById(R.id.boyLayout);
            LinearLayout newLayout = new LinearLayout(this);
            TextView left = new TextView(this);
            final TextView right = new TextView(this);

            // params
            Resources r = getResources();
            LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // dp to px
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());

            // set attributes
            params_1.setMargins(0, px, 0, 0);
            newLayout.setWeightSum(1.0f);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setLayoutParams(params_1);

            params_2.weight = 0.3f;
            left.setLayoutParams(params_2);

            num_boys += 1;
            params_3.weight = 0.6f;
            right.setLayoutParams(params_3);
            right.setTextColor(r.getColor(R.color.black));
            right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

            // set action
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            //Toast.makeText(getApplicationContext(), String.format("%d : ",px)+String.format("%d",txtSize), Toast.LENGTH_LONG).show();

            right.setText(name);

            newLayout.addView(left);
            newLayout.addView(right);

            parent.addView(newLayout);
        }
        else {
            // views
            LinearLayout parent = (LinearLayout) findViewById(R.id.girlLayout);
            LinearLayout newLayout = new LinearLayout(this);
            TextView left = new TextView(this);
            final TextView right = new TextView(this);

            // params
            Resources r = getResources();
            LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            // dp to px
            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());

            // set attributes
            params_1.setMargins(0, px, 0, 0);
            newLayout.setWeightSum(1.0f);
            newLayout.setOrientation(LinearLayout.HORIZONTAL);
            newLayout.setLayoutParams(params_1);

            params_2.weight = 0.3f;
            left.setLayoutParams(params_2);

            num_girls += 1;
            params_3.weight = 0.6f;
            right.setLayoutParams(params_3);
            right.setTextColor(r.getColor(R.color.black));
            right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

            // set action
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                    intent.putExtra("name", name);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            });
            //Toast.makeText(getApplicationContext(), String.format("%d : ",px)+String.format("%d",txtSize), Toast.LENGTH_LONG).show();

            right.setText(name);

            newLayout.addView(left);
            newLayout.addView(right);

            parent.addView(newLayout);
        }
    }

    public void addBoy(View view){
        LinearLayout addBoyLayout = (LinearLayout) findViewById(R.id.addBoyLayout);
        addBoyLayout.setVisibility(View.VISIBLE);

        ImageView addBoyBtn = (ImageView) findViewById(R.id.addBoyBtn);
        addBoyBtn.setVisibility(View.GONE);
    }
    public void addBoyCancel(View view){
        LinearLayout addBoyLayout = (LinearLayout) findViewById(R.id.addBoyLayout);
        addBoyLayout.setVisibility(View.GONE);

        ImageView addBoyBtn = (ImageView) findViewById(R.id.addBoyBtn);
        addBoyBtn.setVisibility(View.VISIBLE);
    }
    public void addBoySave(View view){
        EditText txtAddBoy = (EditText) findViewById(R.id.txtAddBoy);
        selectedName = txtAddBoy.getText().toString().trim();
        if(selectedName.length() == 0){
            Toast.makeText(getApplicationContext(), "Please add name of boy.", Toast.LENGTH_LONG).show();
            return;
        }
        SelectDialogFragment dialog = SelectDialogFragment.newInstance(selectedName, "Boy", id);
        dialog.show(getSupportFragmentManager(), "addBoy");
    }

    public void addGirl(View view){
        LinearLayout addGirlLayout = (LinearLayout) findViewById(R.id.addGirlLayout);
        addGirlLayout.setVisibility(View.VISIBLE);

        ImageView addGirlBtn = (ImageView) findViewById(R.id.addGirlBtn);
        addGirlBtn.setVisibility(View.GONE);
    }
    public void addGirlCancel(View view){
        LinearLayout addGirlLayout = (LinearLayout) findViewById(R.id.addGirlLayout);
        addGirlLayout.setVisibility(View.GONE);

        ImageView addGirlBtn = (ImageView) findViewById(R.id.addGirlBtn);
        addGirlBtn.setVisibility(View.VISIBLE);
    }
    public void addGirlSave(View view){
        EditText txtAddGirl = (EditText) findViewById(R.id.txtAddGirl);
        if(txtAddGirl.getText().toString().trim().length() == 0){
            Toast.makeText(getApplicationContext(), "Please add name of girl.", Toast.LENGTH_LONG).show();
            return;
        }
        LinearLayout addGirlLayout = (LinearLayout) findViewById(R.id.addGirlLayout);
        addGirlLayout.setVisibility(View.GONE);

        ImageView addGirlBtn = (ImageView) findViewById(R.id.addGirlBtn);
        addGirlBtn.setVisibility(View.VISIBLE);
        // views
        LinearLayout parent = (LinearLayout) findViewById(R.id.girlLayout);
        LinearLayout newLayout = new LinearLayout(this);
        TextView left = new TextView(this);
        final TextView right = new TextView(this);

        // params
        Resources r = getResources();
        LinearLayout.LayoutParams params_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params_2 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params_3 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // dp to px
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());

        // set attributes
        params_1.setMargins(0, px, 0, 0);
        newLayout.setWeightSum(1.0f);
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLayout.setLayoutParams(params_1);
        //newLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        params_2.weight = 0.3f;
        left.setLayoutParams(params_2);

        num_girls += 1;
        params_3.weight = 0.6f;
        right.setLayoutParams(params_3);
        //right.setId();
        right.setTextColor(r.getColor(R.color.black));
        //int txtSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, r.getDisplayMetrics());
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        // save to database and get id
        String father = "";
        String mother = "";
        Integer fid = 0;
        Integer mid = 0;
        String name = txtAddGirl.getText().toString().trim();
        TextView nameView = (TextView) findViewById(R.id.txtName);
        TextView spouseView = (TextView) findViewById(R.id.txtSpouse);
        if(txtGender.equals("Male")){
            father = nameView.getText().toString();
            fid = id;
            mid = spouseId;
            mother = spouseView.getText().toString();
        }
        else{
            mother = nameView.getText().toString();
            mid = id;
            fid = spouseId;
            father = spouseView.getText().toString();
        }
        DBHelper db = new DBHelper(getApplicationContext());
        final int girlId = (int)db.insertInfo(name,father,mother,"Female","","","",fid,mid,null,0);

        // set action
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), right.getText().toString(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("name", right.getText().toString().trim());
                intent.putExtra("id", girlId);
                startActivity(intent);
            }
        });
        right.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                right.setBackgroundColor(getResources().getColor(R.color.ash));
            }
        });
        //Toast.makeText(getApplicationContext(), String.format("%d : ",px)+String.format("%d",txtSize), Toast.LENGTH_LONG).show();

        right.setText(txtAddGirl.getText().toString().trim());

        newLayout.addView(left);
        newLayout.addView(right);

        parent.addView(newLayout);
    }

    public void addBoyGirlText(String category, final int entryID){
        EditText txtAdd ;
        LinearLayout addLayout;
        ImageView addBtn;
        LinearLayout parent;
        LinearLayout newLayout;
        TextView left;
        TextView right;

        Resources r = getResources();
        LinearLayout.LayoutParams params_1;
        LinearLayout.LayoutParams params_2;
        LinearLayout.LayoutParams params_3;


        newLayout = new LinearLayout(this);
        left = new TextView(this);
        right = new TextView(this);

        // params
        params_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params_2 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params_3 = new LinearLayout.LayoutParams((int)r.getDimension(R.dimen.zero_width),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // dp to px
        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 15, r.getDisplayMetrics());

        // set attributes
        params_1.setMargins(0, px, 0, 0);
        newLayout.setWeightSum(1.0f);
        newLayout.setOrientation(LinearLayout.HORIZONTAL);
        newLayout.setLayoutParams(params_1);
        //newLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        params_2.weight = 0.3f;
        left.setLayoutParams(params_2);
        params_3.weight = 0.6f;
        right.setLayoutParams(params_3);
        //right.setId();
        right.setTextColor(r.getColor(R.color.black));
        //int txtSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 6, r.getDisplayMetrics());
        right.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);

        // set action
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
                intent.putExtra("id", entryID);
                startActivity(intent);
            }
        });

        newLayout.addView(left);
        newLayout.addView(right);

        if(category.equals("Boy")){
            txtAdd = (EditText) findViewById(R.id.txtAddBoy);
            addLayout = (LinearLayout) findViewById(R.id.addBoyLayout);
            addLayout.setVisibility(View.GONE);

            addBtn = (ImageView) findViewById(R.id.addBoyBtn);
            addBtn.setVisibility(View.VISIBLE);
            // views
            parent = (LinearLayout) findViewById(R.id.boyLayout);

            right.setText(txtAdd.getText().toString().trim());
            parent.addView(newLayout);
        }
        else if(category.equals("Girl")){
            txtAdd = (EditText) findViewById(R.id.txtAddGirl);
            addLayout = (LinearLayout) findViewById(R.id.addGirlLayout);
            addLayout.setVisibility(View.GONE);

            addBtn = (ImageView) findViewById(R.id.addGirlBtn);
            addBtn.setVisibility(View.VISIBLE);
            // views
            parent = (LinearLayout) findViewById(R.id.girlLayout);

            right.setText(txtAdd.getText().toString().trim());
            parent.addView(newLayout);
        }
    }

    public void viewDetailsFather(View view){
        TextView text = (TextView) findViewById(R.id.txtFather);
        if(fatherId > 0){
            Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
            intent.putExtra("id", fatherId);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Please add father name.", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), text.getText().toString()+" : "+Integer.toString(fatherId), Toast.LENGTH_LONG).show();
    }
    public void viewDetailsMother(View view){
        TextView text = (TextView) findViewById(R.id.txtMother);
        if(motherId > 0){
            Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
            intent.putExtra("id", motherId);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Please add mother name.", Toast.LENGTH_LONG).show();
         //Toast.makeText(getApplicationContext(), text.getText().toString()+" : "+Integer.toString(motherId), Toast.LENGTH_LONG).show();
    }
    public void viewDetailsSpouse(View view){
        TextView text = (TextView) findViewById(R.id.txtSpouse);
        if(spouseId > 0){
            Intent intent = new Intent(getApplicationContext(), DetailsInfoActivity.class);
            intent.putExtra("id", spouseId);
            startActivity(intent);
        }
        else
            Toast.makeText(getApplicationContext(), "Please add spouse name.", Toast.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), text.getText().toString()+" : "+Integer.toString(spouseId), Toast.LENGTH_LONG).show();
    }

    public void onLongClick(View view){

    }
}
