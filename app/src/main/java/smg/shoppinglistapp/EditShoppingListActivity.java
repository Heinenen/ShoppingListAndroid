package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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



        editShoppingList();
//        deleteShoppingListButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_shopping_list, menu);
        return true;
    }

    public void editShoppingList(){
        Button editShoppingList = findViewById(R.id.editShoppingListBtn);
        editShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isInserted = myDb.updateSL(slID, shoppingListName.getText().toString());
                if (isInserted) {
                    Toast.makeText(EditShoppingListActivity.this, "Shopping list edited", Toast.LENGTH_LONG).show();
                    Intent shoppingListsActivity = new Intent(EditShoppingListActivity.this, ShoppingListsActivity.class);
                    startActivity(shoppingListsActivity);
                } else {
                    Toast.makeText(EditShoppingListActivity.this, "Editing failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    public void deleteShoppingListButton(){
//        Button deleteShoppingList = findViewById(R.id.deleteShoppingListBtn);
//        deleteShoppingList.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int[] deletedRows = myDb.deleteSL(slID);
//                if (deletedRows[1] > 0) {
//                    Toast.makeText(EditShoppingListActivity.this, "Shopping list and " + deletedRows[1] + " items deleted", Toast.LENGTH_LONG).show();
//                } else if(deletedRows[0] > 0){
//                    Toast.makeText(EditShoppingListActivity.this, "Empty shopping list deleted", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(EditShoppingListActivity.this, "Deleting failed", Toast.LENGTH_LONG).show();
//                }
//                Intent itemsActivity = new Intent(EditShoppingListActivity.this, ShoppingListsActivity.class);
//                startActivity(itemsActivity);
//            }
//        });
//    }

    public void deleteShoppingListFromSQL(){
        int[] deletedRows = myDb.deleteSL(slID);
        if (deletedRows[1] > 0) {
            Toast.makeText(EditShoppingListActivity.this, "Shopping list and " + deletedRows[1] + " items deleted", Toast.LENGTH_LONG).show();
        } else if(deletedRows[0] > 0){
            Toast.makeText(EditShoppingListActivity.this, "Empty shopping list deleted", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditShoppingListActivity.this, "Deleting failed", Toast.LENGTH_LONG).show();
        }
        Intent itemsActivity = new Intent(EditShoppingListActivity.this, ShoppingListsActivity.class);
        startActivity(itemsActivity);
    }

    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_delete_shopping_list:
                deleteShoppingListFromSQL();
                System.out.println("LOOOOOOOOOOOOOOL");
        }

        return super.onOptionsItemSelected(item);
    }
}
