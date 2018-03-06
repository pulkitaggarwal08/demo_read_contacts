package com.demo_read_contacts.pulkit.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.demo_read_contacts.pulkit.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by pulkit on 4/11/17.
 */

public class AllMobileContacts extends AppCompatActivity {

    private ListView listView;
    private Cursor cursor;
    private HashSet<String> contactsHashSet;
    private ArrayList<String> contactsArrayList = new ArrayList<>();

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_mbl_contacts);

        findIds();
        checkPermissions();
        init();
        showContacts();
    }

    private void findIds() {

        listView = (ListView) findViewById(R.id.listView);
    }

    private void checkPermissions() {

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            showContacts();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    PERMISSIONS_REQUEST_READ_CONTACTS);
        }

    }

    private void init() {

        showContacts();

        contactsArrayList.addAll(contactsHashSet);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsArrayList);
        listView.setAdapter(arrayAdapter);
    }

    private void showContacts() {

        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        contactsHashSet = new HashSet<>();

        while (cursor.moveToNext()) {
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phnNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            if ((!contactsHashSet.contains(contactName)) && (!contactsHashSet.contains(phnNumber))) {
                contactsHashSet.add("Name: " + contactName + "\n" + "Phone No.: " + phnNumber);
            }
        }
        cursor.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the contacts", Toast.LENGTH_SHORT).show();
            }

        }

    }


}
