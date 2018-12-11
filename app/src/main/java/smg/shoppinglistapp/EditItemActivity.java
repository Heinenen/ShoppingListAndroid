package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import smg.databasehelpers.DatabaseHelper;
import smg.models.Item;

public class EditItemActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String itemID;
    private String slID;
    private String shoppingList;
    private CheckBox itemPriority;
    private Item item;

    private EditText itemName;
    private EditText itemCategory;
    private EditText itemAmount;
    private EditText itemPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        setTitle(R.string.editItemAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.edit_item_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        this.myDb = new DatabaseHelper(this);
        this.itemID = getIntent().getStringExtra("smg.ITEM_ID");
        this.slID = getIntent().getStringExtra("smg.SL_ID");
        this.shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        this.item = getItemFromSQL(itemID);


        // set default text and set cursor to last position
        itemName = findViewById(R.id.editItemNameEditText);
        itemName.setText(item.getName());
        itemName.setSelection(item.getName().length());
        itemCategory = findViewById(R.id.editItemCategoryEditText);
        itemCategory.setText(item.getCategory());
        itemCategory.setSelection(item.getCategory().length());
        itemAmount = findViewById(R.id.editItemAmountEditText);
        itemAmount.setText(item.getAmount());
        itemAmount.setSelection(item.getAmount().length());
        itemPrice = findViewById(R.id.editItemPriceEditText);
        itemPrice.setText(item.getPrice().replace("€", ""));
        itemPrice.setSelection(item.getPrice().length() - 1);


        itemPriority = findViewById(R.id.editItemPriorityCheckBox);
        if(item.getPriority().equals("1")) itemPriority.setChecked(true);

        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_item, menu);
        return true;
    }


    // edits item
    public void fab(){
        FloatingActionButton editItem = findViewById(R.id.editItemFAB);
        editItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // takes previous values of the item as default values if nothing is typed into EditText
                String[] itemAttributes = new String[5];

                // default value for itemName
                if(itemName.getText().toString().equals("")){
                    itemAttributes[0] = item.getName();
                } else {
                    itemAttributes[0] = itemName.getText().toString();
                }

                // default value for itemCategory
                if(itemCategory.getText().toString().equals("")){
                    itemAttributes[1] = item.getCategory();
                } else {
                    itemAttributes[1] = itemCategory.getText().toString();
                }

                // default value for itemAmount
                if(itemAmount.getText().toString().equals("")){
                    itemAttributes[3] = item.getAmount();
                } else {
                    itemAttributes[3] = itemAmount.getText().toString();
                }

                if(itemPrice.getText().toString().equals("") || itemPrice.getText().toString().equals(" ")){
                    itemAttributes[4] = item.getPrice();
                } else {
                    itemAttributes[4] = itemPrice.getText().toString() + "€";
                }

                // default value for itemPriority
                int itemPriorityInt;
                if(itemPriority.isChecked()){
                    itemPriorityInt = 1;
                } else {
                    itemPriorityInt = 0;
                }

                // edits item in SQL
                boolean isInserted = myDb.updateItem(itemID, slID, itemAttributes[0], itemAttributes[1], itemAttributes[3], itemPriorityInt, itemAttributes[4]);
                if (isInserted) {
                    Toast.makeText(EditItemActivity.this, R.string.toast_itemEdited, Toast.LENGTH_SHORT).show();
//                    Intent itemsActivity = new Intent(EditItemActivity.this, ItemsActivity.class);
//                    itemsActivity.putExtra("smg.SL_ID", slID);
//                    itemsActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
//                    startActivity(itemsActivity);
                    Intent intent = NavUtils.getParentActivityIntent(EditItemActivity.this);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    NavUtils.navigateUpTo(EditItemActivity.this, intent);
                } else {
                    Toast.makeText(EditItemActivity.this, R.string.toast_editingFailed, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // gets the item which is being edited from SQL
    public Item getItemFromSQL(String itemID){
        Cursor res = myDb.getItem(itemID);

        res.moveToNext();
        return new Item(
                res.getString(0),
                res.getString(2),
                res.getString(3),
                res.getString(4),
                res.getString(5),
                res.getString(6),
                res.getInt(7));
    }


    // goes to ItemActivity on backKey
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent(EditItemActivity.this, ItemsActivity.class);
//            intent.putExtra("smg.SL_ID", slID);
//            intent.putExtra("smg.SHOPPING_LIST", shoppingList);
//            startActivity(intent);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);

        Intent intent = NavUtils.getParentActivityIntent(this);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        NavUtils.navigateUpTo(this, intent);
        return true;
    }

    // goes to ItemActivity on .home
    @Override
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
        }
        return super.onOptionsItemSelected(menuItem);


    }
}
