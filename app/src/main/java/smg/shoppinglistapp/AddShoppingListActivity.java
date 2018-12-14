package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import smg.databasehelpers.DatabaseHelper;
import uz.shift.colorpicker.LineColorPicker;

public class AddShoppingListActivity extends AppCompatActivity {

    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);
        setTitle(R.string.addShoppingListAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.add_shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        myDb = new DatabaseHelper(this);

        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_shopping_list, menu);
        return true;
    }

    // adds SL
    public void fab(){
        FloatingActionButton addShoppingList = findViewById(R.id.addShoppingListFAB);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveShoppingList();
            }
        });
    }

    public void saveShoppingList(){
        // receives text given by EditText
        EditText shoppingListName = findViewById(R.id.addShoppingListNameEditText);

        // adds SL to SQL
        boolean isInserted = myDb.addSL(shoppingListName.getText().toString(), colorPicker());
        if (isInserted) {
            Toast.makeText(AddShoppingListActivity.this, R.string.toast_shoppingListAdded, Toast.LENGTH_SHORT).show();
            Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
            startActivity(shoppingListsActivity);
        } else {
            Toast.makeText(AddShoppingListActivity.this, R.string.toast_addingFailed, Toast.LENGTH_SHORT).show();
        }
    }

    public int colorPicker(){
        LineColorPicker colorPicker = findViewById(R.id.add_shopping_list_color_picker);
        return colorPicker.getColor();
    }


    // goes to parent activity (ShoppingListsActivity) on backKey
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
//                Intent intent = new Intent(EditItemActivity.this, ItemsActivity.class);
//                intent.putExtra("smg.SL_ID", slID);
//                intent.putExtra("smg.SHOPPING_LIST", shoppingList);
//                startActivity(intent);
//                return true;
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;

            case R.id.add_shopping_list_save:
                saveShoppingList();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}