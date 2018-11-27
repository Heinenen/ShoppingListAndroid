package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class ShoppingListsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_lists);
        setTitle(R.string.shoppingListsAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.shopping_list_toolbar);
        setSupportActionBar(mToolbar);

        RecyclerView recyclerView = findViewById(R.id.mainRecyclerView);
        recyclerView.setAdapter(new ShoppingListsAdapter(ShoppingListsActivity.this));
        recyclerView.setLayoutManager(new LinearLayoutManager(ShoppingListsActivity.this));

        addShoppingListActivity();
    }


    public void addShoppingListActivity(){
        Button addShoppingListActivityBtn = findViewById(R.id.addShoppingListActivityBtn);
        addShoppingListActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addShoppingList = new Intent(ShoppingListsActivity.this, AddShoppingListActivity.class);
                startActivity(addShoppingList);
            }
        });

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
}
