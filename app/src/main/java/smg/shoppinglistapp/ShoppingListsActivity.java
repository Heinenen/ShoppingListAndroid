package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import smg.adapters.ShoppingListsAdapter;
import smg.interfaces.AdapterCallActivityMethod;
import smg.models.ShoppingList;


public class ShoppingListsActivity extends AppCompatActivity implements AdapterCallActivityMethod {


    // TODO reimplement editing of SLs and items
    // TODO ask whether one REALLY wants to delete the SL
    // TODO change color theme
    // TODO make deleteButton invisible if no SL is selected
    // TODO make a case for only one SL getting deleted (-> so that there is a nice animation)

    private DatabaseHelper myDb;
    private ShoppingListsAdapter mAdapter;
    private boolean deleteButtonVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        setTitle(R.string.shoppingListsAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        this.myDb = new DatabaseHelper(ShoppingListsActivity.this);
        this.mAdapter = new ShoppingListsAdapter(ShoppingListsActivity.this, this);
        this.deleteButtonVisible = false;


        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListsActivity.this));

        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_lists, menu);
        if(deleteButtonVisible){
            menu.findItem(R.id.action_delete_shopping_lists).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete_shopping_lists).setVisible(false);
        }
        return true;
    }


    @Override
    public void refreshToolbar(boolean deleteButtonVisible) {
        this.deleteButtonVisible = deleteButtonVisible;
        invalidateOptionsMenu();
    }

    public void fab(){
        FloatingActionButton fab = findViewById(R.id.shoppingListsFAB);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addShoppingList = new Intent(ShoppingListsActivity.this, AddShoppingListActivity.class);
                startActivity(addShoppingList);
            }
        });
    }

    public void deleteShoppingListFromSQL(String slID){
        myDb.deleteSL(slID);
//        int[] deletedRows = myDb.deleteSL(slID);
//        if (deletedRows[1] > 0) {
//            Toast.makeText(ShoppingListsActivity.this, "Shopping list and " + deletedRows[1] + " items deleted", Toast.LENGTH_SHORT).show();
//        } else if(deletedRows[0] > 0){
//            Toast.makeText(ShoppingListsActivity.this, "Empty shopping list deleted", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(ShoppingListsActivity.this, "Deleting failed", Toast.LENGTH_SHORT).show();
//        }
    }


    // minimizes app on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.action_delete_shopping_lists:
                // if only one SL gets deleted, tell adapter to only delete/refresh one
                // -> nice animation
                ArrayList<ShoppingList> selectedShoppingLists = mAdapter.getSelectedShoppingLists();

                // commented code doesn't work
//                if (selectedShoppingLists.size() == 1) {
//                    deleteShoppingListFromSQL(selectedShoppingLists.get(0).getPosition());
//                    mAdapter.deleteSLFromList(selectedShoppingLists.get(0));
//                    mAdapter.notifyItemRemoved(Integer.parseInt(selectedShoppingLists.get(0).getPosition()));
//                    mAdapter.deselectAll();
//                    mAdapter.notifyItemChanged(Integer.parseInt(selectedShoppingLists.get(0).getPosition()));
//                }
                if (selectedShoppingLists.size() > 0) {
                    for (int i = 0; i < selectedShoppingLists.size(); i++) {
                        deleteShoppingListFromSQL(selectedShoppingLists.get(i).getPosition());
                        mAdapter.deleteSLFromList(selectedShoppingLists.get(i));
                        mAdapter.deselectAll();
                        mAdapter.notifyDataSetChanged();
                    }
                    deleteButtonVisible = false;
                    invalidateOptionsMenu();
                }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}