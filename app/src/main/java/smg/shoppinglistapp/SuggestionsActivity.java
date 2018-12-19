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

import java.util.ArrayList;

import smg.adapters.SuggestionsAdapter;
import smg.databasehelpers.DatabaseHelper;
import smg.interfaces.SuggestionsAdapterInterface;
import smg.models.Suggestion;

public class SuggestionsActivity extends AppCompatActivity implements SuggestionsAdapterInterface {

    // TODO use mode, till now only deleting name suggestions works

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

        setTitle("Suggestions");

        android.support.v7.widget.Toolbar mToolbar = findViewById(R.id.suggestions_toolbar);
        setSupportActionBar(mToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        this.myDb = new DatabaseHelper(SuggestionsActivity.this);
        this.suggestions = getSuggestionsFromSQL();
        this.mAdapter = new SuggestionsAdapter(SuggestionsActivity.this, this, suggestions);
        this.deleteButtonVisible = false;
        this.mode = 0;

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

    public ArrayList<Suggestion> getSuggestionsFromSQL(){
        ArrayList<Suggestion> temp = new ArrayList<>();
        Cursor res = myDb.getNamePredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        res = myDb.getCategoryPredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        res = myDb.getAmountPredictions();
        while(res.moveToNext()){
            temp.add(new Suggestion(res.getString(0), res.getString(1)));
        }
        res = myDb.getPricePredictions();
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
        }
        return super.onOptionsItemSelected(menuItem);
    }
}

