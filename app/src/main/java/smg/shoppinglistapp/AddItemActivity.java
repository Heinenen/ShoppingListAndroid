package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
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

public class AddItemActivity extends AppCompatActivity {


    private String slID;
    private String shoppingList;
    private DatabaseHelper myDb;
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
        setContentView(R.layout.activity_add_item);
        setTitle(R.string.addItemAct_title);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.add_item_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }

        this.slID = getIntent().getStringExtra("smg.SL_ID");
        this.shoppingList = getIntent().getStringExtra("smg.SHOPPING_LIST");
        this.myDb = new DatabaseHelper(this);
        this.namePredictions = getNamePredictions();
        this.categoryPredictions = getCategoryPredictions();
        this.amountPredictions = getAmountPredictions();
        this.pricePredictions = getPricePredictions();


        itemName = findViewById(R.id.add_item_name_auto_complete);
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, namePredictions);
        itemName.setAdapter(nameAdapter);

        itemCategory = findViewById(R.id.add_item_category_auto_complete);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoryPredictions);
        itemCategory.setAdapter(categoryAdapter);

        itemAmount = findViewById(R.id.add_item_amount_auto_complete);
        ArrayAdapter<String> amountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, amountPredictions);
        itemAmount.setAdapter(amountAdapter);

        itemPrice = findViewById(R.id.add_item_price_auto_complete);
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, pricePredictions);
        itemPrice.setAdapter(priceAdapter);



        fab();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_item, menu);
        return true;
    }

    // adds item
    public void fab(){
        FloatingActionButton addItem = findViewById(R.id.addItemFAB);
        // receives text given into EditTexts
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveItem();
            }
        });
    }

    public void saveItem(){
//        EditText itemName = findViewById(R.id.addItemNameEditText);
//        EditText itemCategory = findViewById(R.id.addItemCategoryEditText);
//        EditText itemAmount = findViewById(R.id.addItemAmountEditText);
//        EditText itemPrice = findViewById(R.id.addItemPriceEditText);
        CheckBox itemPriority = findViewById(R.id.addItemPriorityCheckBox);


        // show alert dialog if no name is given, proceed if name is given
        String itemNameString;
        if(itemName.getText().toString().equals("")) {
            showAlertDialog();
        } else {
            // Item parameters
            itemNameString = itemName.getText().toString();
            if(!namePredictions.contains(itemNameString)){
                myDb.addNamePrediction(itemNameString);
            }

            // default value for category (-> "")
            String itemCategoryString;
            if (itemCategory.getText().toString().equals("")) {
                itemCategoryString = getString(R.string.addItemAct_defaultCategoryName);
            } else {
                itemCategoryString = itemCategory.getText().toString();
                if(!categoryPredictions.contains(itemCategoryString)){
                    myDb.addCategoryPrediction(itemCategoryString);
                }
            }

            // default value for amount (-> null)
            String itemAmountString;
            if (itemAmount.getText().toString().equals("")) {
                itemAmountString = getString(R.string.addItemAct_defaultAmount);
            } else {
                itemAmountString = itemAmount.getText().toString();
                if(!amountPredictions.contains(itemAmountString)){
                    myDb.addAmountPrediction(itemAmountString);
                }
            }

            // default value for amount (-> "")
            String itemPriceString = itemPrice.getText().toString();
            if (itemPrice.getText().toString().equals("")) {
                itemPriceString = " ";
            } else {
                if(!pricePredictions.contains(itemPriceString)){
                    myDb.addPricePrediction(itemPriceString);
                }
                itemPriceString = itemPriceString + "€";

            }

            // default value for priority (-> 0)
            int itemPriorityInt;
            if (itemPriority.isChecked()) {
                itemPriorityInt = 1;
            } else {
                itemPriorityInt = 0;
            }

            // adds Item to SQL
            boolean isInserted = myDb.addItem(slID, itemNameString, itemCategoryString, itemAmountString, itemPriceString, itemPriorityInt);
            if (isInserted) {
                Toast.makeText(AddItemActivity.this, R.string.toast_itemAdded, Toast.LENGTH_SHORT).show();
//                        Intent itemsActivity = new Intent(AddItemActivity.this, ItemsActivity.class);
//                        itemsActivity.putExtra("smg.SL_ID", slID);
//                        itemsActivity.putExtra("smg.SHOPPING_LIST", shoppingList);
//                        startActivity(itemsActivity);
                Intent intent = NavUtils.getParentActivityIntent(AddItemActivity.this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(AddItemActivity.this, intent);

            } else {
                Toast.makeText(AddItemActivity.this, R.string.toast_addingFailed, Toast.LENGTH_SHORT).show();
            }
        }
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


    // method for showing an AlertDialog when no name is given
    public void showAlertDialog(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(R.string.actAddItem_alertTitle);
        alert.create().show();
    }


    // goes to ItemActivity on backKey
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            Intent intent = new Intent(AddItemActivity.this, ItemsActivity.class);
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

            case R.id.add_item_save:
                saveItem();
                return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
