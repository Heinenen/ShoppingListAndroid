package smg.databasehelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Main.db";

    private static final String TABLE1_NAME = "sL_table";
    private static final String TABLE2_NAME = "item_table";
    private static final String TABLE4_NAME = "name_p_table";
    private static final String TABLE5_NAME = "category_p_table";
    private static final String TABLE6_NAME = "amount_p_table";
    private static final String TABLE7_NAME = "price_p_table";

    private static final String COL_1_1 = "SL_ID";
    private static final String COL_1_2 = "SL_NAME";
    private static final String COL_1_3 = "SL_COLOR";

    private static final String COL_2_1 = "ITEM_ID";
    private static final String COL_2_2 = "SL";
    private static final String COL_2_3 = "ITEM_NAME";
    private static final String COL_2_4 = "ITEM_CATEGORY";
    private static final String COL_2_5 = "ITEM_AMOUNT";
    private static final String COL_2_6 = "ITEM_PRICE";
    private static final String COL_2_7 = "ITEM_PRIORITY";
    private static final String COL_2_8 = "ITEM_CHECK";

    // P = prediction
    private static final String COL_4_1 = "NAME_P_ID";
    private static final String COL_4_2 = "NAME_P_CONTENT";

    private static final String COL_5_1 = "CATEGORY_P_ID";
    private static final String COL_5_2 = "CATEGORY_P_CONTENT";

    private static final String COL_6_1 = "AMOUNT_P_ID";
    private static final String COL_6_2 = "AMOUNT_P_CONTENT";

    private static final String COL_7_1 = "PRICE_P_ID";
    private static final String COL_7_2 = "PRICE_P_CONTENT";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "
                + TABLE1_NAME + " ("
                + COL_1_1 + " INTEGER PRIMARY KEY, "
                + COL_1_2 + " TEXT, "
                + COL_1_3 + " INTEGER)");
        db.execSQL("CREATE TABLE "
                + TABLE2_NAME + " ("
                + COL_2_1 + " INTEGER PRIMARY KEY, "
                + COL_2_2 + " TEXT, "
                + COL_2_3 + " TEXT, "
                + COL_2_4 + " TEXT, "
                + COL_2_5 + " TEXT, "
                + COL_2_6 + " TEXT, "
                + COL_2_7 + " INTEGER, "
                + COL_2_8 + " INTEGER)");

        db.execSQL("CREATE TABLE "
                + TABLE4_NAME + " ("
                + COL_4_1 + " INTEGER PRIMARY KEY, "
                + COL_4_2 + " TEXT)");

        db.execSQL("CREATE TABLE "
                + TABLE5_NAME + " ("
                + COL_5_1 + " INTEGER PRIMARY KEY, "
                + COL_5_2 + " TEXT)");

        db.execSQL("CREATE TABLE "
                + TABLE6_NAME + " ("
                + COL_6_1 + " INTEGER PRIMARY KEY, "
                + COL_6_2 + " TEXT)");

        db.execSQL("CREATE TABLE "
                + TABLE7_NAME + " ("
                + COL_7_1 + " INTEGER PRIMARY KEY, "
                + COL_7_2 + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE1_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE4_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE5_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE6_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE7_NAME);
        onCreate(db);
    }

    public boolean addSL(String slName, int color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_2, slName);
        contentValues.put(COL_1_3, color);

        long res = db.insert(TABLE1_NAME, null, contentValues);

        return !(res == -1);
    }

    public boolean addItem(String shoppingList, String itemName, String itemCategory, String itemAmount, String itemPrice, int itemPriority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_2, shoppingList);
        contentValues.put(COL_2_3, itemName);
        contentValues.put(COL_2_4, itemCategory);
        contentValues.put(COL_2_5, itemAmount);
        contentValues.put(COL_2_6, itemPrice);
        contentValues.put(COL_2_7, itemPriority);
        contentValues.put(COL_2_8, 0);

        long result = db.insert(TABLE2_NAME, null, contentValues);

        return result != -1;
    }

    public boolean addNamePrediction(String name){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_4_2, name);
        long result = db.insert(TABLE4_NAME, null, contentValues);
        return result != -1;
    }

    public boolean addCategoryPrediction(String category){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_5_2, category);
        long result = db.insert(TABLE5_NAME, null, contentValues);
        return result != -1;
    }

    public boolean addAmountPrediction(String amount){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_6_2, amount);
        long result = db.insert(TABLE6_NAME, null, contentValues);
        return result != -1;
    }

    public boolean addPricePrediction(String price){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_7_2, price);
        long result = db.insert(TABLE7_NAME, null, contentValues);
        return result != -1;
    }


    public Cursor getSL() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE1_NAME, null);
    }


    public Cursor getItem(String itemID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE2_NAME + " WHERE " + COL_2_1 + "='" + itemID + "'", null);
    }


    public Cursor getItems(String shoppingList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE2_NAME + " WHERE " + COL_2_2 + "='" + shoppingList + "'", null);
//        db.close();
        return cursor;
    }

    public Cursor getNamePredictions(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE4_NAME, null);
    }

    public Cursor getCategoryPredictions(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE5_NAME, null);
    }

    public Cursor getAmountPredictions(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE6_NAME, null);
    }

    public Cursor getPricePredictions(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE7_NAME, null);
    }


//    public Cursor getAllItems() {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.rawQuery("SELECT * FROM " + TABLE2_NAME, null);
//    }


    public boolean updateSL(String id, String shoppingListName, int color){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_1, id);
        contentValues.put(COL_1_2, shoppingListName);
        contentValues.put(COL_1_3, color);

        db.update(TABLE1_NAME, contentValues, COL_1_1 + "= ?", new String[]{id});
        return true;
    }


    public boolean updateItem(String itemID, String shoppingList, String itemName, String itemCategory, String itemAmount, String itemPrice, int itemPriority) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_1, itemID);
        contentValues.put(COL_2_2, shoppingList);
        contentValues.put(COL_2_3, itemName);
        contentValues.put(COL_2_4, itemCategory);
        contentValues.put(COL_2_5, itemAmount);
        contentValues.put(COL_2_6, itemPrice);
        contentValues.put(COL_2_7, itemPriority);

        db.update(TABLE2_NAME, contentValues, COL_2_1 + "= ?", new String[]{itemID});
        return true;
    }

    public boolean updateItemCheck(String itemID, int itemCheck){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2_8, itemCheck);

        db.update(TABLE2_NAME, contentValues, COL_2_1 + "= ?", new String[]{itemID});
        return true;
    }

    public int[] deleteSL(String slID){
        SQLiteDatabase db = this.getWritableDatabase();

        return new int[]{db.delete(TABLE1_NAME, COL_1_1 + "= ?", new String[]{slID}),
                db.delete(TABLE2_NAME, COL_2_2 + "= ?", new String[]{slID})};
    }

    public int deleteItem(String itemID){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE2_NAME, COL_2_1 + "= ?", new String[]{itemID});
    }

    public int deleteNamePrediction(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE4_NAME, COL_4_1 + "= ?", new String[]{id});
    }

    public int deleteCategoryPrediction(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE5_NAME, COL_5_1 + "= ?", new String[]{id});
    }

    public int deleteAmountPrediction(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE6_NAME, COL_6_1 + "= ?", new String[]{id});
    }

    public int deletePricePrediction(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE7_NAME, COL_7_1 + "= ?", new String[]{id});
    }
}