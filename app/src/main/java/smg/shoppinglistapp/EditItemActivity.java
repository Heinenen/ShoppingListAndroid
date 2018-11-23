package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditItemActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String itemID;
    private String slID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setTitle(R.string.editItemAct_title);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);

        this.myDb = new DatabaseHelper(this);
        this.itemID = getIntent().getStringExtra("smg.ITEM_ID");
        this.slID = getIntent().getStringExtra("smg.SL_ID");

        editItem();
    }


    public void editItem(){
        Button editItem = findViewById(R.id.editItemBtn);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText itemName = findViewById(R.id.editItemNameEditText);
                EditText itemCategory = findViewById(R.id.editItemCategoryEditText);
                EditText itemPriority = findViewById(R.id.editItemPriorityEditText);
                EditText itemAmount = findViewById(R.id.editItemAmountEditText);

                // takes previous values of the item as default values if nothing is typed into EditText
                String[] strings = getItem(itemID);
                String[] itemAttributes = new String[4];

                // default value for itemName
                if(itemName.getText().toString().equals("")){
                    itemAttributes[0] = strings[2];
                } else {
                    itemAttributes[0] = itemName.getText().toString();
                }

                // default value for itemCategory
                if(itemCategory.getText().toString().equals("")){
                    itemAttributes[1] = strings[3];
                } else {
                    itemAttributes[1] = itemCategory.getText().toString();
                }

                // default value for itemPriority
                if(itemPriority.getText().toString().equals("")){
                    itemAttributes[2] = "1";
                } else {
                    itemAttributes[2] = itemPriority.getText().toString();
                }

                // default value for itemAmount
                if(itemAmount.getText().toString().equals("")){
                    itemAttributes[3] = strings[5];
                } else {
                    itemAttributes[3] = itemAmount.getText().toString();
                }


//                boolean isInserted = myDb.updateItem(itemID, slID,
//                        itemName.getText().toString(),
//                        itemCategory.getText().toString(),
//                        Integer.parseInt(itemPriority.getText().toString()),
//                        itemAmount.getText().toString());

                boolean isInserted = myDb.updateItem(itemID, slID, itemAttributes[0], itemAttributes[1], Integer.parseInt(itemAttributes[2]), itemAttributes[3]);

                if (isInserted) {
                    Toast.makeText(EditItemActivity.this, "Item edited", Toast.LENGTH_LONG).show();
                    Intent itemsActivity = new Intent(EditItemActivity.this, ItemsActivity.class);
                    itemsActivity.putExtra("smg.SL_ID", slID);
                    startActivity(itemsActivity);
                } else {
                    Toast.makeText(EditItemActivity.this, "Editing failed", Toast.LENGTH_LONG).show();
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


    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home){
            Intent intent = new Intent(EditItemActivity.this, ItemsActivity.class);
            intent.putExtra("smg.SL_ID", slID);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(menuItem);
    }

    public String[] getItem(String itemID){
        Cursor res = myDb.getItem(itemID);
        String [] strings = new String[6];

        res.moveToNext();
        for(int i = 0; i < 5; i++){
            strings[i] = res.getString(i);
        }
        // String itemID, String shoppingList, String itemName, String itemCategory, int itemPriority, String itemAmount
        return strings;
    }
}
