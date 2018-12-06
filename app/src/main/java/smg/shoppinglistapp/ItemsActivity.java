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
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import smg.adapters.ItemsAdapter;
import smg.interfaces.ItemsAdapterInterface;
import smg.models.Item;


public class ItemsActivity extends AppCompatActivity implements ItemsAdapterInterface {

    // TODO implement checkboxes: maybe move sort them as last as soon as clicked
    // TODO make item suggestions
    // TODO search thing
    // TODO set spinner as action-Button
    // to do that, use button that opens spinner, if not opened make spinner invisible

    private String slID;
    private String shoppingList;
    private String lastSortedBy;
    private ArrayList<Item> items;
    private ArrayList<Item> allItems;
    private ItemsAdapter mAdapter;
    private DatabaseHelper myDb;
    private boolean deleteButtonVisible;
    private boolean editButtonVisible;

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
        this.deleteButtonVisible = false;
        this.editButtonVisible = false;
        this.lastSortedBy = " ";

        mAdapter = new ItemsAdapter(ItemsActivity.this, this, slID, shoppingList);
        items = mAdapter.getItems();

        sortItems("Name");

        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);

        if(deleteButtonVisible){
            menu.findItem(R.id.action_delete_item).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete_item).setVisible(false);
        }

        if(editButtonVisible){
            menu.findItem(R.id.action_edit_item).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit_item).setVisible(false);
        }

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search_item).getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchName(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchName(newText);
                return false;
            }
        });

//          MenuItem searchItem = menu.findItem(R.id.action_search_item);
//        SearchManager searchManager = (SearchManager) ItemsActivity.this.getSystemService(Context.SEARCH_SERVICE);
//
//        SearchView searchView = null;
//        searchView = (SearchView) searchItem.getActionView();
//        if (searchView != null){
//            searchView.setSearchableInfo(searchManager.getSearchableInfo(ItemsActivity.this.getComponentName()));
//        }

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


    // method that sets visibility of ToolbarButtons and refreshes it
    // Overrides custom interface: AdapterCallActivityMethod
    @Override
    public void refreshToolbar(boolean deleteButtonVisible, boolean editButtonVisible) {
        this.deleteButtonVisible = deleteButtonVisible;
        this.editButtonVisible = editButtonVisible;
        invalidateOptionsMenu();
    }

    @Override
    public void sort() {
        sortItems(lastSortedBy);
    }

    // goes to AddItemActivity
    public void fab(){
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


    // sorts items by Name/Category
    // "important" items always first, checked items always last
    public void sortItems(String string){
        if (string.equals("Name")) {
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.getName().equals("") && o2.getName().equals("")) {
                        return 0;
                    } else if (o1.getName().equals("")) {
                        return 1;
                    } else if (o2.getName().equals("")) {
                        return -1;
                    } else {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                }
            });
            this.lastSortedBy = "Name";
        }
        if (string.equals("Category")) {
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    if (o1.getCategory().equals("") && o2.getCategory().equals("")) {
                        return 0;
                    } else if (o1.getCategory().equals("")) {
                        return 1;
                    } else if (o2.getCategory().equals("")) {
                        return -1;
                    } else {
                        return o1.getCategory().compareToIgnoreCase(o2.getCategory());
                    }
                }
            });
            this.lastSortedBy = "Category";
        }

        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return o2.getPriority().compareTo(o1.getPriority());
            }
        });

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
                    return 0;
                }
            }
        });

        mAdapter.notifyDataSetChanged();
    }

    public void deleteItemFromSQL(String itemID){
        myDb.deleteItem(itemID);
    }


    public void searchName(String searchString){
        if(searchString.equals("")){
            mAdapter.setItems(mAdapter.getItemsFromSQL());
            mAdapter.notifyDataSetChanged();
        }

        ArrayList<Item> searchItems = new ArrayList<>();
        for(Item item: items){
            if(item.getName().contains(searchString)){
                searchItems.add(item);
            }
        }
        mAdapter.setItems(searchItems);
        mAdapter.notifyDataSetChanged();
    }


    // goes to parent activity (ShoppingListsActivity) on backKey
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
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;

            // deletes selected items
            case R.id.action_delete_item:
                ArrayList<Item> selectedItems = mAdapter.getSelectedItems();
                if (selectedItems.size() > 0) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        deleteItemFromSQL(selectedItems.get(i).getId());
                        mAdapter.deleteItemFromList(selectedItems.get(i));
                        mAdapter.deselectAll();
                        mAdapter.notifyDataSetChanged();
                    }
                }
                refreshToolbar(false, false);
                return true;


            // goes to EditItemActivity for selected item
            case R.id.action_edit_item:
                Item selectedItem = mAdapter.getSelectedItems().get(0);
                Intent editItemIntent = new Intent(ItemsActivity.this, EditItemActivity.class);
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
                editItemIntent.putExtra("smg.ITEM_ID", selectedItem.getId());
                startActivity(editItemIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
