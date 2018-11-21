package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddShoppingListActivity extends AppCompatActivity {

    private DatabaseHelper myDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);
        setTitle(R.string.addShoppingListAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        myDb = new DatabaseHelper(this);

        addShoppingList();
    }


    public void addShoppingList(){
        Button addShoppingList = findViewById(R.id.addShoppingListBtn);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shoppingListName = findViewById(R.id.shoppingListNameEditText);

                boolean isInserted = myDb.addSL(shoppingListName.getText().toString());
                if (isInserted) {
                    Toast.makeText(AddShoppingListActivity.this, "Shopping list added", Toast.LENGTH_LONG).show();
                    Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
                    startActivity(shoppingListsActivity);
                } else {
                    Toast.makeText(AddShoppingListActivity.this, "Adding failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}