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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import java.util.ArrayList;

import smg.databasehelpers.DatabaseHelper;
import smg.models.Item;

public class EditItemActivity extends AppCompatActivity {

    private DatabaseHelper myDb;
    private String itemID;
    private String slID;
    private String shoppingList;
    private CheckBox itemPriority;
    private Item item;

    private ArrayList<String> namePredictions;
    private ArrayList<String> categoryPredictions;
    private ArrayList<String> amountPredictions;
    private ArrayList<String> pricePredictions;
    private AutoCompleteTextView itemName;
    private AutoCompleteTextView itemCategory;
    private AutoCompleteTextView itemAmount;
    private AutoCompleteTextView itemPrice;

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
        this.namePredictions = getNamePredictions();
        this.categoryPredictions = getCategoryPredictions();
        this.amountPredictions = getAmountPredictions();
        this.pricePredictions = getPricePredictions();


        // set default text and set cursor to last position
        itemName = findViewById(R.id.edit_item_name_auto_complete);
        itemName.setText(item.getName());
        itemName.setSelection(item.getName().length());
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namePredictions);
        itemName.setAdapter(nameAdapter);

        itemCategory = findViewById(R.id.edit_item_category_auto_complete);
        itemCategory.setText(item.getCategory());
        itemCategory.setSelection(item.getCategory().length());
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryPredictions);
        itemCategory.setAdapter(categoryAdapter);

        itemAmount = findViewById(R.id.edit_item_amount_auto_complete);
        itemAmount.setText(item.getAmount());
        itemAmount.setSelection(item.getAmount().length());
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, amountPredictions);
        itemAmount.setAdapter(amountAdapter);

        itemPrice = findViewById(R.id.edit_item_price_auto_complete);
        itemPrice.setText(item.getPrice().replace("€", ""));
        if(item.getPrice().equals(" ")){
            itemPrice.setText("");
        }
        itemPrice.setSelection(item.getPrice().length() - 1);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pricePredictions);
        itemPrice.setAdapter(priceAdapter);


        itemPriority = findViewById(R.id.editItemPriorityCheckBox);
        if(item.isPriority()) itemPriority.setChecked(true);

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
                confirmEdit();
            }
        });
    }

    public void confirmEdit(){
        // takes previous values of the item as default values if nothing is typed into EditText
        String[] itemAttributes = new String[4];

        // default value for itemName
        if(itemName.getText().toString().equals("")){
            itemAttributes[0] = item.getName();
        } else {
            itemAttributes[0] = itemName.getText().toString();
            if(!namePredictions.contains(itemAttributes[0])) {
                myDb.addNamePrediction(itemAttributes[0]);
            }
        }

        itemAttributes[1] = itemCategory.getText().toString();
        itemAttributes[2] = itemAmount.getText().toString();

        if(!(itemCategory.getText().toString().equals("") || categoryPredictions.contains(itemAttributes[1]))){
            myDb.addCategoryPrediction(itemAttributes[1]);
        }

        if(!(itemAmount.getText().toString().equals("") || amountPredictions.contains(itemAttributes[2]))){
            myDb.addAmountPrediction(itemAttributes[2]);
        }

        if(itemPrice.getText().toString().equals("") || itemPrice.getText().toString().contains(" ")){
            itemAttributes[3] = " ";
        } else {
            itemAttributes[3] = itemPrice.getText().toString() + "€";
            if(!categoryPredictions.contains(itemAttributes[3])) {
                myDb.addPricePrediction(itemPrice.getText().toString());
            }
        }

        // default value for itemPriority
        int itemPriorityInt;
        if(itemPriority.isChecked()){
            itemPriorityInt = 1;
        } else {
            itemPriorityInt = 0;
        }

        // edits item in SQL
        boolean isInserted = myDb.updateItem(itemID, slID, itemAttributes[0], itemAttributes[1], itemAttributes[2], itemAttributes[3], itemPriorityInt);
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
                res.getInt(6),
                res.getInt(7));
    }

    public ArrayList<String> getNamePredictions(){
        Cursor res = myDb.getNamePredictions();
        ArrayList<String> namePredictions = new ArrayList<>();
        while (res.moveToNext()){
            namePredictions.add(res.getString(1));
        }
        return namePredictions;
    }

    public ArrayList<String> getCategoryPredictions(){
        Cursor res = myDb.getCategoryPredictions();
        ArrayList<String> categoryPredictions = new ArrayList<>();
        while (res.moveToNext()){
            categoryPredictions.add(res.getString(1));
        }
        return categoryPredictions;
    }

    public ArrayList<String> getAmountPredictions(){
        Cursor res = myDb.getAmountPredictions();
        ArrayList<String> amountPredictions = new ArrayList<>();
        while (res.moveToNext()){
            amountPredictions.add(res.getString(1));
        }
        return amountPredictions;
    }

    public ArrayList<String> getPricePredictions(){
        Cursor res = myDb.getPricePredictions();
        ArrayList<String> pricePredictions = new ArrayList<>();
        while (res.moveToNext()){
            pricePredictions.add(res.getString(1));
        }
        return pricePredictions;
    }

    public void deleteFromSQL(String itemID){
        myDb.deleteItem(itemID);
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

            case R.id.edit_item_save:
                confirmEdit();
                return true;

            case R.id.edit_item_delete:
                deleteFromSQL(itemID);
                Intent parentActivityIntent = NavUtils.getParentActivityIntent(this);
                parentActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, parentActivityIntent);
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
