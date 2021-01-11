package com.example.hossain.relationship;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hossain on 7/5/2020.
 */

public class ActionDialogFragment extends DialogFragment{

    //action names
    public static String DELETE_REMOVE = "DeleteRemove";
    public static String DELETE_REMOVE_ADD = "DeleteRemoveAdd";

    public ActionDialogFragment(){

    }
    public static ActionDialogFragment newInstance(String category, int pos, int id){
        ActionDialogFragment sdf = new ActionDialogFragment();
        Bundle bundle = new Bundle();

        bundle.putString("category", category);
        bundle.putInt("position", pos);
        bundle.putInt("id", id);
        sdf.setArguments(bundle);

        return sdf;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String category = getArguments().getString("category");

        final int pos = getArguments().getInt("position");
        final int id = getArguments().getInt("id");
        final ActionDialogFragmentListener listener = (ActionDialogFragmentListener) getActivity();
        final DBHelper db = new DBHelper(getContext());

        if(category.equals(DELETE_REMOVE)){
            builder.setTitle("Delete or Remove from home.")
                    .setMessage("What do you want?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String gender = db.getGender(id);
                            db.updateInfo(id, gender);
                            db.deleteInfo(id);
                            listener.onFinishDialog("Delete", DELETE_REMOVE, pos);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listener.onFinishDialog("Cancel", DELETE_REMOVE, pos);
                        }
                    })
                    .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.updatePriority(id, 0);
                            listener.onFinishDialog("Remove", DELETE_REMOVE, pos);
                        }
                    });
        }
        else if(category.equals(DELETE_REMOVE_ADD)){
            int home = db.getPriority(id);
            if(home == 1){
                builder.setTitle("Delete or Remove from home.")
                        .setMessage("What do you want?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String gender = db.getGender(id);
                                db.updateInfo(id, gender);
                                db.deleteInfo(id);
                                listener.onFinishDialog("Delete", DELETE_REMOVE_ADD, pos);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onFinishDialog("Cancel", DELETE_REMOVE_ADD, pos);
                            }
                        })
                        .setNegativeButton("Remove", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updatePriority(id, 0);
                                listener.onFinishDialog("Remove", DELETE_REMOVE_ADD, pos);
                            }
                        });
            }
            else{
                builder.setTitle("Delete or Add to home.")
                        .setMessage("What do you want?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String gender = db.getGender(id);
                                db.updateInfo(id, gender);
                                db.deleteInfo(id);
                                listener.onFinishDialog("Delete", DELETE_REMOVE_ADD, pos);
                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                listener.onFinishDialog("Cancel", DELETE_REMOVE_ADD, pos);
                            }
                        })
                        .setNegativeButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.updatePriority(id, 1);
                                listener.onFinishDialog("Add", DELETE_REMOVE_ADD, pos);
                            }
                        });
            }
        }
        else{
            builder.setMessage("Select action")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
        }


        return builder.create();
    }


    public void test(int pos){
        if(pos == 10) {
            Toast.makeText(getContext(), "Cancel: " + Integer.toString(pos), Toast.LENGTH_LONG).show();
        }
        else if(pos == 20)
            Toast.makeText(getContext(), "Save : "+Integer.toString(pos), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(getContext(), "Checked : "+Integer.toString(pos), Toast.LENGTH_LONG).show();
    }

    public interface ActionDialogFragmentListener{
        void onFinishDialog(String action, String category, int position);
    }
}
