package smg.shoppinglistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
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
import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.ShoppingListsAdapterInterface;
import smg.models.ShoppingList;


public class ShoppingListsActivity extends AppCompatActivity implements ShoppingListsAdapterInterface {

    // TODO change color theme for every SL
    // TODO make a case for only one SL getting deleted (-> so that there is a nice animation)

    private DatabaseHelper myDb;
    private ShoppingListsAdapter mAdapter;
    private ArrayList<ShoppingList> shoppingLists;

    private boolean deleteButtonVisible;
    private boolean editButtonVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        setTitle(R.string.shoppingListsAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        this.myDb = new DatabaseHelper(ShoppingListsActivity.this);
        this.shoppingLists = getSLFromSQL();
        this.mAdapter = new ShoppingListsAdapter(ShoppingListsActivity.this, this, shoppingLists);
        this.deleteButtonVisible = false;
        this.editButtonVisible = false;


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
            menu.findItem(R.id.action_delete_shopping_list).setVisible(true);
        } else {
            menu.findItem(R.id.action_delete_shopping_list).setVisible(false);
        }

        if(editButtonVisible){
            menu.findItem(R.id.action_edit_shopping_list).setVisible(true);
        } else {
            menu.findItem(R.id.action_edit_shopping_list).setVisible(false);
        }

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


    // goes to AddShoppingListActivity
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


    // gets SLs from SQL
    public ArrayList<ShoppingList> getSLFromSQL(){
        Cursor res = myDb.getSL();
        ArrayList<ShoppingList> list = new ArrayList<>();

        while (res.moveToNext()){
            list.add(new ShoppingList(res.getString(0), res.getString(1)));
        }

        return list;
    }


    // method for deleting SL fromSQL
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
            // deletes selected SLs
            case R.id.action_delete_shopping_list:
                // if only one SL gets deleted, tell adapter to only delete/refresh one
                // -> nice animation
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(R.string.shoppingListAct_reallyDeleteSL);
                alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayList<ShoppingList> selectedShoppingLists = mAdapter.getSelectedShoppingLists();
                        int[] rowIndices = mAdapter.getRowIndices();
                        if (selectedShoppingLists.size() > 0) {
                            for (int i = 0; i < selectedShoppingLists.size(); i++) {
                                deleteShoppingListFromSQL(selectedShoppingLists.get(i).getSlID());
                                shoppingLists.remove(selectedShoppingLists.get(i));
                            }
                            for(int i = 0; i< rowIndices.length; i++){
                                if(rowIndices[i] != -1){
                                    mAdapter.notifyItemRemoved(i);
                                    mAdapter.notifyItemRangeChanged(i, mAdapter.getItemCount());
                                }
                            }
                            mAdapter.deselectAll();
//                            mAdapter.notifyDataSetChanged();
                            refreshToolbar(false, false);
                        }
                    }
                });
                alert.setNegativeButton(R.string.no, null);
                alert.create().show();
                return true;

            // goes to EditShoppingListActivity for selected items
            case R.id.action_edit_shopping_list:
                ShoppingList selectedShoppingList = mAdapter.getSelectedShoppingLists().get(0);
                Intent editSLIntent = new Intent(ShoppingListsActivity.this, EditShoppingListActivity.class);
                editSLIntent.putExtra("smg.SL_ID", selectedShoppingList.getSlID());
                editSLIntent.putExtra("smg.SHOPPING_LIST", selectedShoppingList.getName());
                startActivity(editSLIntent);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}