package smg.shoppinglistapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class ItemsActivity extends AppCompatActivity {

    String slID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items);
        setTitle(R.string.itemsAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        slID = getIntent().getStringExtra("smg.SL_ID");

        if(savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        RecyclerView recyclerView = findViewById(R.id.secondRecyclerView);
        recyclerView.setAdapter(new ItemsAdapter(ItemsActivity.this, slID));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        addItemActivity();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

        outState.putString("smg.SL_ID", slID);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (slID == null) {
            slID = savedInstanceState.getString("smg.SL_ID");
        }
    }

    public void callOnSaveInstanceState(Bundle outState){
        onSaveInstanceState(outState);
    }

    public void addItemActivity(){
        Button addItemActivityBtn = findViewById(R.id.addItemActivityBtn);
        addItemActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemActivity = new Intent(ItemsActivity.this, AddItemActivity.class);
                onSaveInstanceState(new Bundle());
                addItemActivity.putExtra("smg.SL_ID", slID);
                startActivity(addItemActivity);
                finish();
            }
        });
    }

    public void sortItems(){
        // do stuff
    }


    // goes to parent activity on backKey-press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Intent intent = NavUtils.getParentActivityIntent(this);
            intent.putExtra("smg.SL_ID", slID);
            NavUtils.navigateUpTo(this, intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

            case R.id.action_sort:
                sortItems();
        }
        return super.onOptionsItemSelected(item);
    }
}
