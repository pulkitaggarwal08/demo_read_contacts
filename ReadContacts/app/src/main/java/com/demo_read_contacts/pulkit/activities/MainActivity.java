package com.demo_read_contacts.pulkit.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.demo_read_contacts.pulkit.R;

/**
 * Created by pulkit on 4/11/17.
 */

public class MainActivity extends AppCompatActivity{

    private Button btn_all_contacts, btn_all_mbl_contacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findIds();
        init();
    }

    private void findIds() {

        btn_all_contacts = (Button) findViewById(R.id.btn_all_contacts);
        btn_all_mbl_contacts = (Button) findViewById(R.id.btn_all_mbl_contacts);
    }

    private void init() {

        btn_all_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AllContacts.class);
                startActivity(intent);
            }
        });

        btn_all_mbl_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AllMobileContacts.class);
                startActivity(intent);
            }
        });

    }


}
