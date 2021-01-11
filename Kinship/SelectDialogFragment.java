package com.example.hossain.relationship;

import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hossain on 7/2/2020.
 */

public class SelectDialogFragment extends DialogFragment{

    public static String ADD_NAME_OR_DELETE = "AddNameOrDelete";
    public static String CATEGORY_NAME = "Name";
    public static String CATEGORY_FATHER = "Father";
    public static String CATEGORY_MOTHER = "Mother";
    public static String CATEGORY_SPOUSE = "Spouse";

    ArrayList<String> itemList;
    ArrayList<Integer> idList;
    int selectedItem;
    String selectedName;
    int id;
    int viewID;

    public SelectDialogFragment(){

    }
    public static SelectDialogFragment newInstance(String name, String category, int id){
        SelectDialogFragment sdf = new SelectDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString("name", name);
        bundle.putString("category", category);
        bundle.putInt("id", id);
        sdf.setArguments(bundle);

        return sdf;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        selectedItem = 0;
        selectedName = getArguments().getString("name");
        if(selectedName.equals(ADD_NAME_OR_DELETE)){
            String category = getArguments().getString("category");
            if(category.equals(CATEGORY_NAME)){
                builder.setMessage("Add Name to Home.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(getContext());
                                helper.updatePriority(getArguments().getInt("id"), 1);
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Ok", CATEGORY_NAME, -1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Cancel", CATEGORY_NAME, -1);
                            }
                        });
            }
            else if(category.equals(CATEGORY_FATHER)){
                builder.setMessage("Delete Father Name.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(getContext());
                                helper.updateInfo(getArguments().getInt("id"), DBHelper.FATHER, "");
                                helper.updateInfoID(getArguments().getInt("id"), DBHelper.FATHERID, 0);
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Delete", CATEGORY_FATHER, -1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Cancel", CATEGORY_FATHER, -1);
                            }
                        });
            }
            else if(category.equals(CATEGORY_MOTHER)){
                builder.setMessage("Delete Mother Name.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(getContext());
                                helper.updateInfo(getArguments().getInt("id"), DBHelper.MOTHER, "");
                                helper.updateInfoID(getArguments().getInt("id"), DBHelper.MOTHERID, 0);
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Delete", CATEGORY_MOTHER, -1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Cancel", CATEGORY_MOTHER, -1);
                            }
                        });
            }
            else if(category.equals(CATEGORY_SPOUSE)){
                builder.setMessage("Delete Spouse Name.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                DBHelper helper = new DBHelper(getContext());
                                helper.updateInfo(getArguments().getInt("id"), DBHelper.SPOUSE, "");
                                helper.updateInfoID(getArguments().getInt("id"), DBHelper.SPOUSEID, 0);
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Delete", CATEGORY_SPOUSE, -1);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                                listener.onFinishDialog("Cancel", CATEGORY_SPOUSE, -1);
                            }
                        });
            }
        }
        else{
            itemList = new ArrayList<String>();
            idList = new ArrayList<>();

            DBHelper db = new DBHelper(getContext());
            Cursor cursor = db.getQueryInfo(selectedName);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false){
                itemList.add(cursor.getString(cursor.getColumnIndex(DBHelper.NAME)));
                itemList.add(cursor.getString(cursor.getColumnIndex(DBHelper.FATHER)));
                itemList.add(cursor.getString(cursor.getColumnIndex(DBHelper.MOTHER)));
                itemList.add(cursor.getString(cursor.getColumnIndex(DBHelper.SPOUSE)));
                idList.add(cursor.getInt(cursor.getColumnIndex(DBHelper.ID)));
                cursor.moveToNext();
            }

            final String[] list = new String[idList.size() + 1];
            int i = 0;
            int idx = 0;
            while (i < itemList.size()){
                list[idx] = "Name : " + itemList.get(i) + "\nFather : " + itemList.get(i+1)
                        + "\nMother : " + itemList.get(i+2) + "\nSpouse : " + itemList.get(i+3);
                i += 4;
                idx += 1;
            }
            list[idList.size()] = "New entry";
            //adapter = new ArrayAdapter<String>(this, )

            builder.setSingleChoiceItems(list, 0, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selectedItem = which;
                }
            })
                    .setTitle(R.string.add_dialog_title)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            id = getArguments().getInt("id");
                            saveToDatabase(getArguments().getString("category"));
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();
                            listener.onFinishDialog("Cancel", getArguments().getString("category"), -1);
                        }
                    });

        }

        return builder.create();
    }

    public void saveToDatabase(String category){
        DBHelper db = new DBHelper(getContext());
        SelectDialogFragmentListener listener = (SelectDialogFragmentListener) getActivity();

        if(category.equals("Father")){ // for father
            if(selectedItem == idList.size()){ // new entry
                int entryID = (int) db.insertInfo(selectedName,"","","Male","","","",null,null,null,0);
                db.updateInfo(id, DBHelper.FATHER, selectedName);
                db.updateInfoID(id, DBHelper.FATHERID, entryID);
                listener.onFinishDialog("Save", category, entryID);
            }
            else {// for existing one
                db.updateInfo(id, DBHelper.FATHER, selectedName);
                db.updateInfoID(id, DBHelper.FATHERID, idList.get(selectedItem));
                listener.onFinishDialog("Save", category, idList.get(selectedItem));
            }
        }
        else if(category.equals("Mother")){ // for mother
            if(selectedItem == idList.size()){ // new entry
                int entryID = (int) db.insertInfo(selectedName,"","","Female","","","",null,null,null,0);
                db.updateInfo(id, DBHelper.MOTHER, selectedName);
                db.updateInfoID(id, DBHelper.MOTHERID, entryID);
                listener.onFinishDialog("Save", category, entryID);
            }
            else {// for existing one
                db.updateInfo(id, DBHelper.MOTHER, selectedName);
                db.updateInfoID(id, DBHelper.MOTHERID, idList.get(selectedItem));
                listener.onFinishDialog("Save", category, idList.get(selectedItem));
            }
        }
        else if(category.equals("Spouse")){ // for spouse
            if(selectedItem == idList.size()){ // new entry
                String gender = db.getGender(id);
                String namesp = db.getName(id);
                String gdr = "Male";
                if(gender.equals("Male"))
                    gdr = "Female";
                int entryID = (int) db.insertInfo(selectedName,"","",gdr,namesp,"","",null,null,id,0);
                db.updateInfo(id, DBHelper.SPOUSE, selectedName);
                db.updateInfoID(id, DBHelper.SPOUSEID, entryID);
                listener.onFinishDialog("Save", category, entryID);
            }
            else {// for existing one
                db.updateInfo(id, DBHelper.SPOUSE, selectedName);
                db.updateInfoID(id, DBHelper.SPOUSEID, idList.get(selectedItem));
                String namesp = db.getName(id);
                db.updateInfo(idList.get(selectedItem), DBHelper.SPOUSE, namesp);
                db.updateInfoID(idList.get(selectedItem), DBHelper.SPOUSEID, id);
                listener.onFinishDialog("Save", category, idList.get(selectedItem));
            }
        }
        else if (category.equals("Boy")){ // for boy
            String father = "";
            String mother = "";
            Integer fatherID = null;
            Integer motherID = null;

            Cursor cursor = db.getInfo(id);
            cursor.moveToFirst();
            String gender = cursor.getString(cursor.getColumnIndex(DBHelper.GENDER));
            if(gender.equals("Male")){
                father = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
                fatherID = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                mother = cursor.getString(cursor.getColumnIndex(DBHelper.SPOUSE));
                motherID = cursor.getInt(cursor.getColumnIndex(DBHelper.SPOUSEID));
            }
            else{
                mother = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
                motherID = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                father = cursor.getString(cursor.getColumnIndex(DBHelper.SPOUSE));
                fatherID = cursor.getInt(cursor.getColumnIndex(DBHelper.SPOUSEID));
            }

            if(selectedItem == idList.size()){ // new entry
                int entryID = (int) db.insertInfo(selectedName,father,mother,"Male","","","",fatherID,motherID,null,0);
                listener.onFinishDialog("Save", category, entryID);
            }
            else {// for existing one
                db.updateInfo(idList.get(selectedItem), DBHelper.FATHER, father);
                db.updateInfo(idList.get(selectedItem), DBHelper.MOTHER, mother);
                db.updateInfoID(idList.get(selectedItem), DBHelper.FATHERID, fatherID);
                db.updateInfoID(idList.get(selectedItem), DBHelper.MOTHERID, motherID);
                listener.onFinishDialog("Save", category, idList.get(selectedItem));
            }
        }
        else if (category.equals("Girl")){ // for girl
            String father = "";
            String mother = "";
            Integer fatherID = null;
            Integer motherID = null;

            Cursor cursor = db.getInfo(id);
            cursor.moveToFirst();
            String gender = cursor.getString(cursor.getColumnIndex(DBHelper.GENDER));
            if(gender.equals("Male")){
                father = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
                fatherID = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                mother = cursor.getString(cursor.getColumnIndex(DBHelper.SPOUSE));
                motherID = cursor.getInt(cursor.getColumnIndex(DBHelper.SPOUSEID));
            }
            else{
                mother = cursor.getString(cursor.getColumnIndex(DBHelper.NAME));
                motherID = cursor.getInt(cursor.getColumnIndex(DBHelper.ID));
                father = cursor.getString(cursor.getColumnIndex(DBHelper.SPOUSE));
                fatherID = cursor.getInt(cursor.getColumnIndex(DBHelper.SPOUSEID));
            }

            if(selectedItem == idList.size()){ // new entry
                int entryID = (int) db.insertInfo(selectedName,father,mother,"Female","","","",fatherID,motherID,null,0);
                listener.onFinishDialog("Save", category, entryID);
            }
            else {// for existing one
                db.updateInfo(idList.get(selectedItem), DBHelper.FATHER, father);
                db.updateInfo(idList.get(selectedItem), DBHelper.MOTHER, mother);
                db.updateInfoID(idList.get(selectedItem), DBHelper.FATHERID, fatherID);
                db.updateInfoID(idList.get(selectedItem), DBHelper.MOTHERID, motherID);
                listener.onFinishDialog("Save", category, idList.get(selectedItem));
            }
        }
    }

    public void test(int pos){
        if(pos == 10)
            Toast.makeText(getContext(), "Cancel: "+Integer.toString(pos), Toast.LENGTH_LONG).show();
        else if(pos == 20)
            Toast.makeText(getContext(), "Save : "+Integer.toString(pos), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "Checked : "+Integer.toString(pos), Toast.LENGTH_LONG).show();
    }

    public interface SelectDialogFragmentListener{
        void onFinishDialog(String action, String category, int entryID);
    }
}
