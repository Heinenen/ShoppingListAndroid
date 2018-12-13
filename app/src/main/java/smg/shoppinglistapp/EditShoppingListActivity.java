package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import smg.databasehelpers.DatabaseHelper;

public class EditShoppingListActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String slID;
    private EditText shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);
        setTitle(R.string.editShoppingListAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.edit_shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        this.myDb = new DatabaseHelper(this);
        this.slID = getIntent().getStringExtra("smg.SL_ID");

        // set default text and set cursor to last position
        String shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        shoppingListName = findViewById(R.id.editShoppingListNameEditText);
        shoppingListName.setText(shoppingList);
        shoppingListName.setSelection(shoppingList.length());



        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_shopping_list, menu);
        return true;
    }


    // edits SL
    public void fab(){
        FloatingActionButton editShoppingList = findViewById(R.id.editShoppingListFAB);
        editShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // edits SL in SQL
                boolean isInserted = myDb.updateSL(slID, shoppingListName.getText().toString(), getResources().getColor(R.color.white)+ "");
                if (isInserted) {
                    Toast.makeText(EditShoppingListActivity.this, R.string.toast_shoppingListEdited, Toast.LENGTH_SHORT).show();
                    Intent shoppingListsActivity = new Intent(EditShoppingListActivity.this, ShoppingListsActivity.class);
                    startActivity(shoppingListsActivity);
                } else {
                    Toast.makeText(EditShoppingListActivity.this, R.string.toast_editingFailed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // goes to parent activity (ShoppingListsActivity) on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
