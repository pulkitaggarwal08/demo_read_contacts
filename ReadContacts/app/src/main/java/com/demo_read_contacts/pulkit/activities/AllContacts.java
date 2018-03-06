package com.demo_read_contacts.pulkit.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.demo_read_contacts.pulkit.ContactVO;
import com.demo_read_contacts.pulkit.R;
import com.demo_read_contacts.pulkit.adapters.AllContactsAdapter;

import java.util.ArrayList;
import java.util.List;

public class AllContacts extends AppCompatActivity {

    RecyclerView rvContacts;
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    private ProgressDialog pDialog;
    private Handler updateBarHandler;
    int counter;

    Cursor cursor;
    List<ContactVO> contactVOList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_contacts);

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);
        updateBarHandler = new Handler();

        rvContacts.setLayoutManager(new LinearLayoutManager(this));

        showContacts();

    }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Reading contacts...");
            pDialog.setCancelable(false);
            pDialog.show();

            new Thread(new Runnable() {

                @Override
                public void run() {
                    getAllContacts();
                }
            }).start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAllContacts() {
        contactVOList = new ArrayList();
        ContactVO contactVO;

        Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
        String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
        String DATA = ContactsContract.CommonDataKinds.Email.DATA;

        StringBuffer output = null;

        ContentResolver contentResolver = getContentResolver();
        cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

//        ContentResolver contentResolver = getContentResolver();
//        Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {

            counter = 0;
            while (cursor.moveToNext()) {

                updateBarHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.setMessage("Reading contacts : " + counter++ + "/" + cursor.getCount());
                    }
                });

                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));

                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

//                    output.append("\n First Name:" + name);

                    contactVO = new ContactVO();
                    contactVO.setContactName(name);


                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

                    while (phoneCursor.moveToNext()) {

                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
//                        output.append("\n Phone number:" + phoneNumber);

                        contactVO.setContactNumber(phoneNumber);
                    }
                    phoneCursor.close();


//                    Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);
//
//                    while (emailCursor.moveToNext()) {
//
//                        String email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
//                        output.append("\n Email:" + email);
//
//                    }
//                    emailCursor.close();

                    contactVOList.add(contactVO);
                }
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AllContactsAdapter contactAdapter = new AllContactsAdapter(contactVOList, getApplicationContext());
                    rvContacts.setAdapter(contactAdapter);
                }
            });


            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            },500);


        }
    }
}