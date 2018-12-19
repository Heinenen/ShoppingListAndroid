package smg.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import smg.interfaces.SuggestionsAdapterInterface;
import smg.models.Suggestion;
import smg.shoppinglistapp.R;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder>{

    private Context context;
    private SuggestionsAdapterInterface parentActivity;
    private ArrayList<Suggestion> suggestions;
    private ArrayList<Suggestion> selectedSuggestions;
    private int[] rowIndices;


    // TODO cleanup, remove unnecessary code
    public SuggestionsAdapter(Context context, SuggestionsAdapterInterface parentActivity, ArrayList<Suggestion> suggestions){
        this.context = context;
        this.parentActivity = parentActivity;
        this.suggestions = suggestions;
        this.selectedSuggestions = new ArrayList<>();
        this.rowIndices = new int[suggestions.size()];
        Arrays.fill(rowIndices, -1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_suggestion, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        // sets value(s) to shown shopping list
        final Suggestion suggestion = suggestions.get(position);
        final String id = suggestion.getId();
        final String content = suggestion.getContent();
        holder.contentTextView.setText(content);

        // deSelects SL on longClick
        holder.parentView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deSelectSuggestion(holder.getAdapterPosition(), suggestion);
                return true;
            }
        });

        // deSelects SL on click if at least one is already selected
        // if none selected, goes to corresponding ItemsActivity
        holder.parentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean suggestionSelectedChecker = false;
                for(int i = 0; i < rowIndices.length; i++){
                    if(rowIndices[i] != -1){
                        suggestionSelectedChecker = true;
                        break;
                    }
                }

                if(suggestionSelectedChecker && holder.getAdapterPosition() != -1){
                    deSelectSuggestion(holder.getAdapterPosition(), suggestion);
                }
            }
        });

        // sets colors depending on marked/not marked
        if(rowIndices[holder.getAdapterPosition()] == holder.getAdapterPosition()){
            // TODO change color resource
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.shoppingListMarked));
            holder.contentTextView.setTextColor(ContextCompat.getColor(context, R.color.shoppingListMarkedText));
        } else {
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.contentTextView.setTextColor(ContextCompat.getColor(context, R.color.shoppingListDefaultText));
        }

    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }


    // method for deSelecting SLs
    private void deSelectSuggestion(int position, Suggestion suggestion){
        if (rowIndices[position] == -1){
            rowIndices[position] = position;
            selectedSuggestions.add(suggestion);
        } else {
            rowIndices[position] = -1;
            selectedSuggestions.remove(suggestion);
        }
        notifyDataSetChanged();

        boolean[] booleans = checkForToolbarButtonVisibility();
        parentActivity.refreshToolbar(booleans[0]);
    }

    // deselects all SLs
    public void deselectAll(){
        refreshRowIndices();
        selectedSuggestions.clear();
    }

    public void refreshRowIndices(){
        this.rowIndices = new int[suggestions.size()];
        Arrays.fill(rowIndices, -1);
    }

    // method that checks which ToolbarButtons should be shown
    // depending on (how many) items selected
    public boolean[] checkForToolbarButtonVisibility(){
        int suggestionsSelectedCounter = 0;

        for(int i = 0; i < rowIndices.length; i++){
            if(rowIndices[i] != -1){
                suggestionsSelectedCounter = suggestionsSelectedCounter + 1;
                if(suggestionsSelectedCounter > 1) break;
            }
        }

        if(suggestionsSelectedCounter == 1){
            return new boolean[]{true, true};
        } else if(suggestionsSelectedCounter > 1){
            return new boolean[]{true, false};
        } else {
            return  new boolean[]{false, false};
        }
    }

    // getters
    public ArrayList<Suggestion> getSelectedSuggestions(){
        return this.selectedSuggestions;
    }

    public void setSuggestions(ArrayList<Suggestion> suggestions) {
        this.suggestions = suggestions;
    }

    public int[] getRowIndices() {
        return rowIndices;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView contentTextView;
        private View parentView;

        public ViewHolder (@NonNull View view){
            super(view);
            this.parentView = view;
            this.contentTextView = view.findViewById(R.id.suggestions_text_view);

        }
    }
}
