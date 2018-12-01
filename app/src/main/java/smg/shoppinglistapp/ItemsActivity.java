package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import smg.adapters.ItemsAdapter;
import smg.models.Item;


public class ItemsActivity extends AppCompatActivity {

    // TODO implement checkboxes: maybe move sort them as last as soon as clicked
    // TODO change longPress behaviour (s. SL todo)

    private String slID;
    private String shoppingList;
    private ArrayList<Item> items;
    private ItemsAdapter mAdapter;
    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        slID = getIntent().getStringExtra("smg.SL_ID");
        shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.items_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(shoppingList);

        this.myDb = new DatabaseHelper(ItemsActivity.this);

        mAdapter = new ItemsAdapter(ItemsActivity.this, slID, shoppingList);
        items = mAdapter.getItems();

        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
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
                sortItems(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return true;
    }


    public void addItemActivity(){
        FloatingActionButton fab = findViewById(R.id.itemsFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                onSaveInstanceState(new Bundle());
                addItemActivity.putExtra("smg.SL_ID", slID);
                addItemActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
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

        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.isCheck() == o2.isCheck()) {
                    return 0;
                } else if (o1.isCheck()) {
                    return 1;
                } else if (o2.isCheck()) {
                    return -1;
                } else {
                    return o1.getCategory().compareToIgnoreCase(o2.getCategory());
                }
            }
        });

        mAdapter.notifyDataSetChanged();
    }

    public void deleteItemFromSQL(String itemID){
        myDb.deleteItem(itemID);
    }


    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = new Intent(ItemsActivity.this, ShoppingListsActivity.class);
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

            case R.id.action_delete_items:
                ArrayList<Item> selectedItems = mAdapter.getSelectedItems();
                if (selectedItems.size() > 1) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        deleteItemFromSQL(selectedItems.get(i).getId());
                        mAdapter.deleteItemFromList(selectedItems.get(i));
                        mAdapter.deselectAll();
                        mAdapter.notifyDataSetChanged();
                    }
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
