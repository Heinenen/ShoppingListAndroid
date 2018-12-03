package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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


    // adds SL
    public void fab(){
        FloatingActionButton addShoppingList = findViewById(R.id.addShoppingListFAB);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // receives text given by EditText
                EditText shoppingListName = findViewById(R.id.addShoppingListNameEditText);

                // adds SL to SQL
                boolean isInserted = myDb.addSL(shoppingListName.getText().toString());
                if (isInserted) {
                    Toast.makeText(AddShoppingListActivity.this, R.string.toast_shoppingListAdded, Toast.LENGTH_SHORT).show();
                    Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
                    startActivity(shoppingListsActivity);
                } else {
                    Toast.makeText(AddShoppingListActivity.this, R.string.toast_addingFailed, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}