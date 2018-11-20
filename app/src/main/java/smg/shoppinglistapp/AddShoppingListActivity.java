package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.util.ArrayList;

import smg.logic.ShoppingList;

public class AddShoppingListActivity extends AppCompatActivity implements Serializable {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shopping_list);
        setTitle(R.string.addShoppingListAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        Button addShoppingList = findViewById(R.id.addShoppingListBtn);
        addShoppingList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText shoppingListName = findViewById(R.id.shoppingListNameEditText);
                ArrayList<ShoppingList> shoppingLists = (ArrayList) getIntent().getSerializableExtra("smg.SHOPPING_LISTS");
                shoppingLists.add(new ShoppingList(shoppingListName.getText().toString()));

                Intent shoppingListsActivity = new Intent(AddShoppingListActivity.this, ShoppingListsActivity.class);
                shoppingListsActivity.putExtra("smg.SHOPPING_LISTS", shoppingLists);
                startActivity(shoppingListsActivity);
            }
        });

    }
}
