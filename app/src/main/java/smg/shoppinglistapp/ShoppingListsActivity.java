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
import smg.models.ShoppingList;

public class ShoppingListsActivity extends AppCompatActivity {


    // TODO change longPress to choseMultipleItems instead of goToEdit
    // TODO ask whether one REALLY wants to delete the SL
    // TODO change color theme
    // TODO make deleteButton invisible if no SL is selected
    // TODO make a case for only one SL getting deleted (-> so that there is a nice animation)

    private DatabaseHelper myDb;
    private ShoppingListsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        setTitle(R.string.shoppingListsAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        this.myDb = new DatabaseHelper(ShoppingListsActivity.this);
        this.mAdapter = new ShoppingListsAdapter((ShoppingListsActivity.this));


        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListsActivity.this));

        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopping_lists, menu);
        return true;
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
        int[] deletedRows = myDb.deleteSL(slID);
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
                ArrayList<ShoppingList> selectedShoppingLists = mAdapter.getSelectedShoppingLists();

                for (int i = 0; i < selectedShoppingLists.size(); i++){
                    deleteShoppingListFromSQL(selectedShoppingLists.get(i).getPosition());
                    mAdapter.deleteSLFromList(selectedShoppingLists.get(i));
                }
                mAdapter.unselectAll();
                mAdapter.notifyDataSetChanged();

        }
        return super.onOptionsItemSelected(menuItem);
    }
}
