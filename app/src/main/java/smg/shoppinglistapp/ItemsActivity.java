package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import smg.adapters.ActionBarAdapter;
import smg.adapters.ItemsAdapter;
import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.ItemsAdapterInterface;
import smg.models.Item;


public class ItemsActivity extends AppCompatActivity implements ItemsAdapterInterface, PopupMenu.OnMenuItemClickListener, ActionBarAdapter.Listener {

    // TODO make item suggestions in addItem
    // TODO make item suggestions in Search

    private static final String KEY_SEARCH_MODE = "searchMode";
    private static final String KEY_SHOPPING_LIST = "shoppingList";
    private static final String KEY_SL_ID = "slID";
    private static final String KEY_SL_COLOR = "slColor";

    private static final int SORT_BY_NAME = 0;
    private static final int SORT_BY_CATEGORY = 1;

    private String slID;
    private String shoppingList;
    private int slColor;
    private int lastSortedBy;
    private String lastSearchedBy;
    private ArrayList<Item> items;
    private ItemsAdapter mAdapter;
    private DatabaseHelper myDb;
    private boolean deleteButtonVisible;
    private boolean editButtonVisible;
    private boolean sortButtonVisible;


//    private int mActionCode = -1;
    private Toolbar mToolbar;
    private ActionBarAdapter mActionBarAdapter;
    private boolean mIsSearchMode;
    private boolean mIsSelectionMode;
    private int mSelectedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);

        mToolbar = findViewById(R.id.items_toolbar);
        setSupportActionBar(mToolbar);

        this.slID = getIntent().getStringExtra("smg.SL_ID");
        this.shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        this.slColor = getIntent().getIntExtra("smg.COLOR", 16777215);

        if (savedInstanceState != null) {
//            mActionCode = savedInstanceState.getInt(KEY_ACTION_CODE);
            mIsSearchMode = savedInstanceState.getBoolean(KEY_SEARCH_MODE);
            slID = savedInstanceState.getString(KEY_SL_ID);
            shoppingList = savedInstanceState.getString(KEY_SHOPPING_LIST);
            slColor = savedInstanceState.getInt(KEY_SL_COLOR);
        }

        setTitle(shoppingList);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#" + Integer.toHexString(slColor)));

        this.myDb = new DatabaseHelper(ItemsActivity.this);
        this.deleteButtonVisible = false;
        this.editButtonVisible = false;
        this.lastSortedBy = 0;
        this.lastSearchedBy = "";
        this.items = getItemsFromSQL();

//        ActionBarAdapter actionBarAdapter = new ActionBarAdapter(ItemsActivity.this, this,getSupportActionBar(), mToolbar, R.string.actAddItem_categoryHint);
//        actionBarAdapter.initialize(null);
        mAdapter = new ItemsAdapter(ItemsActivity.this, this, items, slColor);

        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        fab();
        prepareSearchViewAndActionBar(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_items, menu);

        menu.findItem(R.id.action_delete_item).setVisible(deleteButtonVisible);
        menu.findItem(R.id.action_edit_item).setVisible(editButtonVisible);
        menu.findItem(R.id.action_sort_items).setVisible(sortButtonVisible);

        MenuItem searchButton = menu.findItem(R.id.action_search_button);
        searchButton.setVisible(!mIsSearchMode && !mIsSelectionMode);
        return true;
    }


    // method that sets visibility of ToolbarButtons and refreshes it
    // Overrides custom interface: AdapterCallActivityMethod
    @Override
    public void setSelectedItemsCount(int count) {
        mSelectedItems = count;
        refreshToolbar();
    }

    @Override
    public void sort() {
        sortItems(lastSortedBy);
    }

    public void refreshToolbar(){
        mIsSelectionMode = (mSelectedItems > 0);
        if(mIsSearchMode && mIsSelectionMode){
            deleteButtonVisible = true;
            editButtonVisible = mSelectedItems == 1;
            sortButtonVisible = false;
            mActionBarAdapter.setSearchMode(true);
            mActionBarAdapter.setSelectionMode(true);
            mActionBarAdapter.setSelectionCount(mSelectedItems);
        } else if(mIsSearchMode){
            deleteButtonVisible = false;
            editButtonVisible = false;
            sortButtonVisible = false;
            mActionBarAdapter.setSearchMode(true);
            mActionBarAdapter.setSelectionMode(false);
        } else if(mIsSelectionMode){
            deleteButtonVisible = true;
            editButtonVisible = mSelectedItems == 1;
            sortButtonVisible = false;
            mActionBarAdapter.setSearchMode(false);
            mActionBarAdapter.setSelectionMode(true);
            mActionBarAdapter.setSelectionCount(mSelectedItems);
        } else {
            deleteButtonVisible = false;
            editButtonVisible = false;
            sortButtonVisible = true;
            mActionBarAdapter.setSearchMode(false);
            mActionBarAdapter.setSelectionMode(false);
        }
//        configureSelectionMode();
//        configureSearchMode();
        invalidateOptionsMenu();
    }


//    public void configureSearchMode(){
//        mActionBarAdapter.setSearchMode(mIsSearchMode);
//        sortButtonVisible = !mIsSearchMode;
//        invalidateOptionsMenu();
//    }
//
//    public void configureSelectionMode(){
//        mActionBarAdapter.setSelectionMode(mIsSelectionMode);
//        mActionBarAdapter.setSelectionCount(mSelectedItems);
//        deleteButtonVisible = mIsSelectionMode;
//        editButtonVisible = mSelectedItems == 1;
//        sortButtonVisible = !mIsSelectionMode;
//        invalidateOptionsMenu();
//    }

    private void prepareSearchViewAndActionBar(Bundle savedState) {
        mToolbar = findViewById(R.id.items_toolbar);
        setSupportActionBar(mToolbar);
        // Add a shadow under the toolbar.
//        ViewUtils.addRectangularOutlineProvider(findViewById(R.id.toolbar_parent), getResources());
        mActionBarAdapter = new ActionBarAdapter(ItemsActivity.this, this, getSupportActionBar(), mToolbar,
                R.string.itemAct_searchHint);
//        mActionBarAdapter.setShowHomeIcon(true);
//        mActionBarAdapter.setShowHomeAsUp(true);
        mActionBarAdapter.initialize(savedState);
        // Postal address pickers (and legacy pickers) don't support search, so just show
        // "HomeAsUp" button and title.
//        mIsSearchSupported = mRequest.getActionCode() != ContactsRequest.ACTION_PICK_POSTAL
//                && mRequest.getActionCode() != ContactsRequest.ACTION_PICK_EMAILS
//                && mRequest.getActionCode() != ContactsRequest.ACTION_PICK_PHONES
//                && !mRequest.isLegacyCompatibilityMode();
//        configureSearchMode();
        refreshToolbar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putInt(KEY_ACTION_CODE, mActionCode);
        outState.putBoolean(KEY_SEARCH_MODE, mIsSearchMode);
        outState.putString(KEY_SL_ID, slID);
        outState.putString(KEY_SHOPPING_LIST, shoppingList);
        outState.putInt(KEY_SL_COLOR, slColor);
        if (mActionBarAdapter != null) {
            mActionBarAdapter.onSaveInstanceState(outState);
        }
    }

//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        System.out.println("restored items activity");
//        mIsSearchMode = savedInstanceState.getBoolean(KEY_SEARCH_MODE);
//        shoppingList = savedInstanceState.getString(KEY_SHOPPING_LIST);
//        slID = savedInstanceState.getString(KEY_SL_ID);
//    }


    // goes to AddItemActivity
    public void fab(){
        FloatingActionButton fab = findViewById(R.id.itemsFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                addItemActivity.putExtra("smg.SL_ID", slID);
                addItemActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
                startActivity(addItemActivity);
            }
        });
    }


    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.sort_name:
                sortItems(SORT_BY_NAME);
                return true;

            case R.id.sort_category:
                sortItems(SORT_BY_CATEGORY);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onAction(int action) {
        switch (action) {
            case ActionBarAdapter.Listener.Action.START_SEARCH_MODE:
                mIsSearchMode = true;
//                configureSearchMode();
                refreshToolbar();
                break;
            case ActionBarAdapter.Listener.Action.CHANGE_SEARCH_QUERY:
                final String queryString = mActionBarAdapter.getQueryString();
//                mListFragment.setQueryString(queryString, /* delaySelection */ false);
                searchName(queryString);
                break;
//            case ActionBarAdapter.Listener.Action.START_SELECTION_MODE:
//                if (getMultiSelectListFragment() != null) {
//                    getMultiSelectListFragment().displayCheckBoxes(true);
//                }
//                invalidateOptionsMenu();
//                break;
            case ActionBarAdapter.Listener.Action.STOP_SEARCH_AND_SELECTION_MODE:
                searchName("");
                mActionBarAdapter.setSearchMode(false);
//                if (getMultiSelectListFragment() != null) {
//                    getMultiSelectListFragment().displayCheckBoxes(false);
//                }
                invalidateOptionsMenu();
                break;
        }
    }

    @Override
    public void onUpButtonPressed() {
        onBackPressed();
    }

    public void showSortPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_popup_sort);
        popup.show();
    }


    public void sortItems(int id){
        if (id == SORT_BY_NAME) {
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item o1, Item o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
            this.lastSortedBy = SORT_BY_NAME;
        }
        if (id == SORT_BY_CATEGORY) {
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
            this.lastSortedBy = SORT_BY_CATEGORY;
        }

        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                if (o1.isPriority() == o2.isPriority()) {
                    return 0;
                } else if (o1.isPriority()) {
                    return -1;
                } else if (o2.isPriority()) {
                    return 1;
                } else {
                    return 0;
                }
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
        searchString = searchString.toLowerCase();
        this.lastSearchedBy = searchString;
        System.out.println(searchString);
        ArrayList<Item> searchItems = new ArrayList<>();
        if(searchString.equals("")){
            mAdapter.setItems(items);
        } else {
            for (Item item : items) {
                String name = item.getName().toLowerCase();
                if (name.contains(searchString)) {
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
                    res.getInt(6),
                    res.getInt(7)));

        }

        return list;
    }

    @Override
    public void onBackPressed() {
        if(mActionBarAdapter.isSelectionMode()){
            mAdapter.deselectAll();
            mAdapter.notifyDataSetChanged();
            mIsSelectionMode = false;
//            configureSelectionMode();
            refreshToolbar();
//            if(getMultiSelectListFragment() != null) {
//                getMultiselectListFragment().displayCheckBoxes(false);
//            }
        } else if (mIsSearchMode) {
            mIsSearchMode = false;
            refreshToolbar();
//            configureSearchMode();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.items = getItemsFromSQL();
        mAdapter.setItems(items);
        sortItems(SORT_BY_NAME);
        mAdapter.refreshRowIndices();
        mIsSelectionMode = false;
        refreshToolbar();
//        configureSelectionMode();
//        configureSearchMode();
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
            case R.id.action_search_button:
                mIsSearchMode = !mIsSearchMode;
                refreshToolbar();
//                configureSearchMode();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            // deletes selected items
            case R.id.action_delete_item:
                ArrayList<Item> selectedItems = mAdapter.getSelectedItems();
                int[] rowIndices = mAdapter.getRowIndices();
                if (selectedItems.size() > 0) {
                    for (int i = 0; i < selectedItems.size(); i++) {
                        deleteItemFromSQL(selectedItems.get(i).getId());
                        items.remove(selectedItems.get(i));
                    }
                    for(int i = 0; i < rowIndices.length; i++){
                        if(rowIndices[i] != -1){
                            mAdapter.notifyItemRemoved(i);
                            mAdapter.notifyItemRangeChanged(i, mAdapter.getItemCount());
                        }
                    }
                    mAdapter.deselectAll();
//                    mAdapter.notifyDataSetChanged();
                }
//                if (searchName(lastSearchedBy).isEmpty()) {
//                    mAdapter.setItems(items);
//                    lastSearchedBy = "";
//                    mIsSearchMode = false;
//                    configureSearchMode();
//                } else {
//                    searchName(lastSearchedBy);
//                }
                mSelectedItems = 0;
                mIsSelectionMode = false;
//                configureSelectionMode();
                refreshToolbar();
                return true;

            // goes to EditItemActivity for selected item
            case R.id.action_edit_item:
                Item selectedItem = mAdapter.getSelectedItems().get(0);
                Intent editItemIntent = new Intent(ItemsActivity.this, EditItemActivity.class);
                editItemIntent.putExtra("smg.SL_ID", slID);
                editItemIntent.putExtra("smg.SHOPPING_LIST", shoppingList);
                editItemIntent.putExtra("smg.ITEM_ID", selectedItem.getId());
                startActivity(editItemIntent);
                mAdapter.deselectAll();
                return true;

            case R.id.action_sort_items:
                showSortPopup(findViewById(R.id.action_sort_items));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
