package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.PopupMenu;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import smg.adapters.ItemsAdapter;
import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.ItemsAdapterInterface;
import smg.models.Item;


public class ItemsActivity extends AppCompatActivity implements ItemsAdapterInterface, PopupMenu.OnMenuItemClickListener{

    // TODO make item suggestions in addItem
    // TODO make item suggestions in Search
    // TODO save what was typed into searchBar

    private String slID;
    private String shoppingList;
    private int lastSortedBy;
    private String lastSearchedBy;
    private ArrayList<Item> items;
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
        this.lastSortedBy = 0;
        this.lastSearchedBy = "";
        this.items = getItemsFromSQL();

        mAdapter = new ItemsAdapter(ItemsActivity.this, this, items);
//        items = mAdapter.getItems();

        sortItems(0);

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


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sort_name:
                sortItems(0);
                return true;

            case R.id.sort_category:
                sortItems(1);
                return true;

            default:
                return false;
        }
    }

    public void showSortPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_popup_sort);
        popup.show();
    }


    public void sortItems(int id){
        if (id == 0) {
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            this.lastSortedBy = 0;
        }
        if (id == 1) {
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
            this.lastSortedBy = 1;
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


    public ArrayList<Item> searchName(String searchString){
        this.lastSearchedBy = searchString;
        System.out.println(searchString);
        ArrayList<Item> searchItems = new ArrayList<>();
        if(searchString.equals("")){
            mAdapter.setItems(items);
        } else {
            for (Item item : items) {
                if (item.getName().contains(searchString)) {
                    searchItems.add(item);
                }
            }
            mAdapter.setItems(searchItems);
        }
        mAdapter.notifyDataSetChanged();
        return searchItems;
    }


    public ArrayList<Item> getItemsFromSQL() {
        Cursor res = myDb.getItems(slID);
        ArrayList<Item> list = new ArrayList<>();

        while (res.moveToNext()) {
            list.add(new Item(
                    res.getString(0),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4),
                    res.getString(5),
                    res.getString(6),
                    res.getInt(7)));

        }

        return list;
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
                        items.remove(selectedItems.get(i));
                    }
                    mAdapter.deselectAll();
                    mAdapter.notifyDataSetChanged();
                }
                if (searchName(lastSearchedBy).isEmpty()){
                    mAdapter.setItems(items);
                } else {
                    searchName(lastSearchedBy);
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

            case R.id.action_sort:
                showSortPopup(findViewById(R.id.action_sort));
        }
        return super.onOptionsItemSelected(item);
    }
}
