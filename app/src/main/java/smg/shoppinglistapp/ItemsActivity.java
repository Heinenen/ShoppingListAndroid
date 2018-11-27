package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import smg.logic.Item;


public class ItemsActivity extends AppCompatActivity {

    String slID;
    ArrayList<Item> items;
    ItemsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        setTitle(R.string.itemsAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.items_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        slID = getIntent().getStringExtra("smg.SL_ID");

        if(savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        mAdapter = new ItemsAdapter(ItemsActivity.this, slID);
        items = mAdapter.getItems();

        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        addItemActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        Spinner spinner = (Spinner) menu.findItem(R.id.action_sort).getActionView();
        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(ItemsActivity.this,
                R.layout.layout_spinner,
                getResources().getStringArray(R.array.sortBys));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(parent.getSelectedItem());
                sortItems(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("smg.SL_ID", slID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (slID == null) {
            slID = savedInstanceState.getString("smg.SL_ID");
        }
    }

    public void callOnSaveInstanceState(Bundle outState){
        onSaveInstanceState(outState);
    }

    public void addItemActivity(){
        Button addItemActivityBtn = findViewById(R.id.addItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                onSaveInstanceState(new Bundle());
                addItemActivity.putExtra("smg.SL_ID", slID);
                startActivity(addItemActivity);
                finish();
            }
        });
    }

    public void sortItems(String string){
        if (string.equals("Name")) {
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if(o1.getName().equals("") && o2.getName().equals("")){
                        return 0;
                    } else if(o1.getName().equals("")){
                        return 1;
                    } else if(o2.getName().equals("")){
                        return -1;
                    } else {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                }
            });
        }

        if (string.equals("Category")){
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if(o1.getCategory().equals("") && o2.getCategory().equals("")){
                        return 0;
                    } else if (o1.getCategory().equals("")){
                        return 1;
                    } else if (o2.getCategory().equals("")){
                        return -1;
                    } else {
                        return o1.getCategory().compareToIgnoreCase(o2.getCategory());
                    }
                }
            });
        }

        mAdapter.notifyDataSetChanged();
    }


    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.putExtra("smg.SL_ID", slID);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
