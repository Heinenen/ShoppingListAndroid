package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditShoppingListActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_shopping_list);
        setTitle(R.string.editShoppingListAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        myDb = new DatabaseHelper(this);
        id = getIntent().getStringExtra("smg.SL_ID");

        editShoppingList();
    }


    public void editShoppingList(){
        Button addShoppingList = findViewById(R.id.editShoppingListBtn);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shoppingListName = findViewById(R.id.editShoppingListNameEditText);

                boolean isInserted = myDb.updateSL(id, shoppingListName.getText().toString());
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

    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
