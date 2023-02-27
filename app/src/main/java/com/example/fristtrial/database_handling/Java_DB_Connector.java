package com.example.fristtrial.database_handling;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.room.Room;


import com.example.test_database_kotlin2.R;

import java.util.ArrayList;
import java.util.List;


public class Java_DB_Connector extends AppCompatActivity {

    TimeDB_Implementation implemnt = new TimeDB_Implementation();
    EditText firstName, lastName;
    Time_Database database;
    Time_DAO userDao;
    Button insert, getAll, update, delete;
    ArrayList<User_Time> details;
    ArrayAdapter<User_Time> adapter;
    ArrayList<String> forStringList;
    ArrayAdapter<String> forStringAdapt;
    ListView listView;
    LifecycleOwner owner = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.database_connector_layout);
//        ask_DB.onCreate(null);

        database = Room.databaseBuilder(this, UDatabase.class, "UserDB").build();
        userDao = database.userDao();
        implemnt.init(database, userDao);

        insert = findViewById(R.id.insert);
        insert.setOnClickListener(listener);
        delete = findViewById(R.id.delete);
        delete.setOnClickListener(listener);
        update = findViewById(R.id.update);
        update.setOnClickListener(listener);
        getAll = findViewById(R.id.getAll);
        getAll.setOnClickListener(listener);

        details = new ArrayList<>();
        forStringList = new ArrayList<>();
        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, details);
        forStringAdapt = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, forStringList);
//        ArrayAdapter<String> lapAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, details);
        listView = findViewById(R.id.displayList);

        listView.setAdapter(forStringAdapt);
    }

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            firstName = findViewById(R.id.firstName);
            lastName = findViewById(R.id.lastName);
            String first = firstName.getText().toString();
            String last = lastName.getText().toString();

            if (view.equals(insert))
                implemnt.insertDataThis(first, last);
            else if (view.equals(delete))
                implemnt.deleteAllUser();
            else if(view.equals(update)) {
                Intent i = new Intent(getApplicationContext(), Theme_Service.class);
                startActivity(i);
            }
//                implemnt.update();
            else if (view.equals(getAll))
                displayAllUsers();



        }
    };

    private void displayAllUsers() {
        userDao.getAllUsers().observe(owner, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                String thisbeFirst;
                String thisbeLast;
                forStringAdapt.clear();
                int count = users.size();
                for (int i = 0; i < count; i++) {
                    thisbeFirst = users.get(i).getFirstName();
                    thisbeLast = users.get(i).getLastName();
                    forStringList.add("First name:" + thisbeFirst + " Last Name:" + thisbeLast);
                    Log.d("Thisbe", " display this for chekcing " + thisbeFirst);
                }


                forStringAdapt.notifyDataSetChanged();
                userDao.getAllUsers().removeObserver(this);

            }
        });

    }
}
