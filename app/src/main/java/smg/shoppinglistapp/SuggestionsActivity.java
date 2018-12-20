package smg.shoppinglistapp;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;

import java.util.ArrayList;

import smg.adapters.SuggestionsAdapter;
import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.SuggestionsAdapterInterface;
import smg.models.Suggestion;

public class SuggestionsActivity extends AppCompatActivity implements SuggestionsAdapterInterface, PopupMenu.OnMenuItemClickListener {

    private DatabaseHelper myDb;
    private SuggestionsAdapter mAdapter;
    private ArrayList<Suggestion> suggestions;

    // mode 0 to 3 standing for name, category, amount and price
    private int mode;
    private boolean deleteButtonVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestions);

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.suggestions_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.myDb = new DatabaseHelper(SuggestionsActivity.this);
        this.mode = 0;
        this.suggestions = getNameSuggestionsFromSQL();
        setTitle(R.string.suggestionsAct_nameTitle);
        this.mAdapter = new SuggestionsAdapter(SuggestionsActivity.this, this, suggestions);
        this.deleteButtonVisible = false;

        RecyclerView recyclerView = findViewById(R.id.suggestions_recycler_view);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(SuggestionsActivity.this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_suggestions, menu);
        menu.findItem(R.id.suggestions_action_delete_button).setVisible(deleteButtonVisible);
        return true;
    }

    @Override
    public void refreshToolbar(boolean deleteButtonVisible) {
        this.deleteButtonVisible = deleteButtonVisible;
        invalidateOptionsMenu();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.mode_popup_name:
                mode = 0;
                setTitle(R.string.suggestionsAct_nameTitle);
                suggestions = getNameSuggestionsFromSQL();
                mAdapter.setSuggestions(suggestions);
                mAdapter.refreshRowIndices();
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.mode_popup_category:
                mode = 1;
                setTitle(R.string.suggestionsAct_categoryTitle);
                suggestions = getCategorySuggestionsFromSQL();
                mAdapter.setSuggestions(suggestions);
                mAdapter.refreshRowIndices();
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.mode_popup_amount:
                mode = 2;
                setTitle(R.string.suggestionsAct_amountTitle);
                suggestions = getAmountSuggestionsFromSQL();
                mAdapter.setSuggestions(suggestions);
                mAdapter.refreshRowIndices();
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.mode_popup_price:
                mode = 3;
                setTitle(R.string.suggestionsAct_priceTitle);
                suggestions = getPriceSuggestionsFromSQL();
                mAdapter.setSuggestions(suggestions);
                mAdapter.refreshRowIndices();
                mAdapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showManageSuggestionsPopup(View anchor){
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_popup_mode);
        popup.show();
    }

    public ArrayList<Suggestion> getNameSuggestionsFromSQL(){
        ArrayList<Suggestion> temp = new ArrayList<>();
        Cursor res = myDb.getNamePredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        return temp;
    }

    public ArrayList<Suggestion> getCategorySuggestionsFromSQL(){
        ArrayList<Suggestion> temp = new ArrayList<>();
        Cursor res = myDb.getCategoryPredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        return temp;
    }

    public ArrayList<Suggestion> getAmountSuggestionsFromSQL(){
        ArrayList<Suggestion> temp = new ArrayList<>();
        Cursor res = myDb.getAmountPredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        return temp;
    }

    public ArrayList<Suggestion> getPriceSuggestionsFromSQL(){
        ArrayList<Suggestion> temp = new ArrayList<>();
        Cursor res = myDb.getPricePredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        return temp;
    }

    public void deleteSuggestionFromSQL(String id){
        switch (mode){
            case 0:
                myDb.deleteNamePrediction(id);
                break;

            case 1:
                myDb.deleteCategoryPrediction(id);
                break;

            case 2:
                myDb.deleteAmountPrediction(id);
                break;

            case 3:
                myDb.deletePricePrediction(id);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                Intent intent = NavUtils.getParentActivityIntent(this);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                NavUtils.navigateUpTo(this, intent);
                return true;

            case R.id.suggestions_action_delete_button:
                ArrayList<Suggestion> selectedSuggestions = mAdapter.getSelectedSuggestions();
                if (selectedSuggestions.size() > 0) {
                    for (int i = 0; i < selectedSuggestions.size(); i++) {
                        deleteSuggestionFromSQL(selectedSuggestions.get(i).getId());
                        suggestions.remove(selectedSuggestions.get(i));
                    }
                    mAdapter.deselectAll();
                    mAdapter.notifyDataSetChanged();
                    refreshToolbar(false);
                }
                return true;

            case R.id.suggestions_action_mode_button:
                showManageSuggestionsPopup(findViewById(R.id.suggestions_action_mode_button));
                return true;

        }
        return super.onOptionsItemSelected(menuItem);
    }
}

