package smg.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import java.util.ArrayList;

import smg.shoppinglistapp.R;

public class ActionBarAdapter implements SearchView.OnCloseListener {
    public interface Listener {
        public abstract class Action {
            public static final int CHANGE_SEARCH_QUERY = 0;
            public static final int START_SEARCH_MODE = 1;
            public static final int START_SELECTION_MODE = 2;
            public static final int STOP_SEARCH_AND_SELECTION_MODE = 3;
            public static final int BEGIN_STOPPING_SEARCH_AND_SELECTION_MODE = 4;
        }
        void onAction(int action);
        void onUpButtonPressed();
    }

    private static final String EXTRA_KEY_SEARCH_MODE = "navBar.searchMode";
    private static final String EXTRA_KEY_QUERY = "navBar.query";
    private static final String EXTRA_KEY_SELECTED_MODE = "navBar.selectionMode";
    private boolean mSelectionMode;
    private boolean mSearchMode;
    private String mQueryString;
    private EditText mSearchView;
    private View mClearSearchView;
    private View mSearchContainer;
    private View mSelectionContainer;
    private int mMaxToolbarContentInsetStart;

    private Activity mActivity;
    private ActionBarAdapter.Listener mListener;
    private ActionBar mActionBar;
    private Toolbar mToolbar;

    private FrameLayout mToolBarFrame;
    private boolean mShowHomeIcon;
    private boolean mShowHomeAsUp;
    private int mSearchHintResId;


    public ActionBarAdapter(Activity activity, ActionBarAdapter.Listener listener, ActionBar actionBar,
                                  Toolbar toolbar) {
        this(activity, listener, actionBar, toolbar, R.string.addItemAct_defaultItemName);
    }
    public ActionBarAdapter(Activity activity, ActionBarAdapter.Listener listener, ActionBar actionBar,
                                  Toolbar toolbar, int searchHintResId) {
        mActivity = activity;
        mListener = listener;
        mActionBar = actionBar;
        mToolbar = toolbar;
        mToolBarFrame = (FrameLayout) mToolbar.getParent();
        mMaxToolbarContentInsetStart = mToolbar.getContentInsetStart();
        mSearchHintResId = searchHintResId;
//        mActionBarAnimationDuration =
//                mActivity.getResources().getInteger(R.integer.action_bar_animation_duration);
        setupSearchAndSelectionViews();
    }
    private void setupSearchAndSelectionViews() {
        final LayoutInflater inflater = (LayoutInflater) mToolbar.getContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        // Setup search bar
        mSearchContainer = inflater.inflate(R.layout.search_bar_expanded, mToolbar,
                /* attachToRoot = */ false);
        mSearchContainer.setVisibility(View.VISIBLE);
        mToolbar.addView(mSearchContainer);
        mSearchContainer.setBackgroundColor(mActivity.getResources().getColor(
                R.color.colorPrimary));
        mSearchView = (EditText) mSearchContainer.findViewById(R.id.search_view);
        mSearchView.setHint(mActivity.getString(mSearchHintResId));
        mSearchView.addTextChangedListener(new ActionBarAdapter.SearchTextWatcher());
        final ImageButton searchBackButton = (ImageButton) mSearchContainer
                .findViewById(R.id.search_back_button);
        searchBackButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onUpButtonPressed();
                        }
                    }
                });
//        searchBackButton.getDrawable().setAutoMirrored(true);
        mClearSearchView = mSearchContainer.findViewById(R.id.search_close_button);
        mClearSearchView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setQueryString(null);
                    }
                });
        // Setup selection bar
        mSelectionContainer = inflater.inflate(R.layout.selection_bar, mToolbar,
                /* attachToRoot = */ false);
        // Insert the selection container into mToolBarFrame behind the Toolbar, so that
        // the Toolbar's MenuItems can appear on top of the selection container.
        mToolBarFrame.addView(mSelectionContainer, 0);
        mSelectionContainer.findViewById(R.id.selection_close).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mListener != null) {
                            mListener.onUpButtonPressed();
                        }
                    }
                });
    }

    public void initialize(Bundle savedState) {
        if (savedState == null) {
            mSearchMode = true;
            mQueryString = "";
            mSelectionMode = false;
        } else {
            mSearchMode = savedState.getBoolean(EXTRA_KEY_SEARCH_MODE);
            mSelectionMode = savedState.getBoolean(EXTRA_KEY_SELECTED_MODE);
            mQueryString = savedState.getString(EXTRA_KEY_QUERY);
        }
        // Show tabs or the expanded {@link SearchView}, depending on whether or not we are in
        // search mode.
        update(true /* skipAnimation */);
        // Expanding the {@link SearchView} clears the query, so set the query from the
        // {@link ContactsRequest} after it has been expanded, if applicable.
        if (mSearchMode && !TextUtils.isEmpty(mQueryString)) {
            setQueryString(mQueryString);
        }
    }

    private void update(boolean skipAnimation) {
        updateOverflowButtonColor();
        final boolean isSelectionModeChanging
                = (mSelectionContainer.getParent() == null) == mSelectionMode;
        final boolean isSwitchingFromSearchToSelection =
                mSearchMode && isSelectionModeChanging || mSearchMode && mSelectionMode;
        final boolean isSearchModeChanging
                = (mSearchContainer.getParent() == null) == mSearchMode;
        final boolean isTabHeightChanging = isSearchModeChanging || isSelectionModeChanging;
        // Update toolbar and status bar color.
        mToolBarFrame.setBackgroundColor(Color.parseColor("#000000"));
//        updateStatusBarColor(isSelectionModeChanging && !isSearchModeChanging);
        // When skipAnimation=true, it is possible that we will switch from search mode
        // to selection mode directly. So we need to remove the undesired container in addition
        // to adding the desired container.

        skipAnimation = true;
        if (skipAnimation || isSwitchingFromSearchToSelection) {
            if (isTabHeightChanging || isSwitchingFromSearchToSelection) {
                mToolbar.removeView(mSearchContainer);
                mToolBarFrame.removeView(mSelectionContainer);
                if (mSelectionMode) {
                    addSelectionContainer();
                } else if (mSearchMode) {
                    addSearchContainer();
                }
                updateDisplayOptions(isSearchModeChanging);
            }
            return;
        }
        // Handle a switch to/from selection mode, due to UI interaction.
//        if (isSelectionModeChanging) {
//            if (mSelectionMode) {
//                addSelectionContainer();
//                mSelectionContainer.setAlpha(0);
////                mSelectionContainer.animate().alpha(1).setDuration(mActionBarAnimationDuration);
//                updateDisplayOptions(isSearchModeChanging);
//            } else {
//                if (mListener != null) {
//                    mListener.onAction(Listener.Action.BEGIN_STOPPING_SEARCH_AND_SELECTION_MODE);
//                }
//                mSelectionContainer.setAlpha(1);
////                mSelectionContainer.animate().alpha(0).setDuration(mActionBarAnimationDuration)
////                        .withEndAction(new Runnable() {
////                            @Override
////                            public void run() {
////                                updateDisplayOptions(isSearchModeChanging);
////                                mToolBarFrame.removeView(mSelectionContainer);
////                            }
////                        });
//            }
//        }
        // Handle a switch to/from search mode, due to UI interaction.
        if (isSearchModeChanging) {
            if (mSearchMode) {
                addSearchContainer();
                mSearchContainer.setAlpha(0);
//                mSearchContainer.animate().alpha(1).setDuration(mActionBarAnimationDuration);
                updateDisplayOptions(isSearchModeChanging);
            } // else {
//                mSearchContainer.setAlpha(1);
//                mSearchContainer.animate().alpha(0).setDuration(mActionBarAnimationDuration)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                updateDisplayOptions(isSearchModeChanging);
//                                mToolbar.removeView(mSearchContainer);
//                            }
//                        });
//            }
        }
    }

    private void addSearchContainer() {
        mToolbar.removeView(mSearchContainer);
        mToolbar.addView(mSearchContainer);
        mSearchContainer.setAlpha(1);
    }

    private void addSelectionContainer() {
        mToolBarFrame.removeView(mSelectionContainer);
        mToolBarFrame.addView(mSelectionContainer, 0);
        mSelectionContainer.setAlpha(1);
    }

    private void updateDisplayOptions(boolean isSearchModeChanging) {
        if (mSearchMode && !mSelectionMode) {
            setFocusOnSearchView();
            // Since we have the {@link SearchView} in a custom action bar, we must manually handle
            // expanding the {@link SearchView} when a search is initiated. Note that a side effect
            // of this method is that the {@link SearchView} query text is set to empty string.
            if (isSearchModeChanging) {
                final CharSequence queryText = mSearchView.getText();
                if (!TextUtils.isEmpty(queryText)) {
                    mSearchView.setText(queryText);
                }
            }
        }
        if (mListener != null) {
            if (mSearchMode) {
                mListener.onAction(Listener.Action.START_SEARCH_MODE);
            }
            if (mSelectionMode) {
                mListener.onAction(Listener.Action.START_SELECTION_MODE);
            }
            if (!mSearchMode && !mSelectionMode) {
                mListener.onAction(Listener.Action.STOP_SEARCH_AND_SELECTION_MODE);
            }
        }
        updateDisplayOptionsInner();
    }



    private class SearchTextWatcher implements TextWatcher {
        @Override
        public void onTextChanged(CharSequence queryString, int start, int before, int count) {
            if (queryString.equals(mQueryString)) {
                return;
            }
            mQueryString = queryString.toString();
            if (!mSearchMode) {
                if (!TextUtils.isEmpty(queryString)) {
                    setSearchMode(true);
                }
            } else if (mListener != null) {
                mListener.onAction(Listener.Action.CHANGE_SEARCH_QUERY);
            }
            mClearSearchView.setVisibility(
                    TextUtils.isEmpty(queryString) ? View.GONE : View.VISIBLE);
        }
        @Override
        public void afterTextChanged(Editable s) {}
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    }

    /**
     * @return Whether in search mode, i.e. if the search view is visible/expanded.
     *
     * Note even if the action bar is in search mode, if the query is empty, the search fragment
     * will not be in search mode.
     */
    public boolean isSearchMode() {
        return mSearchMode;
    }

    /**
     * @return Whether in selection mode, i.e. if the selection view is visible/expanded.
     */
    public boolean isSelectionMode() {
        return mSelectionMode;
    }

    public void setSearchMode(boolean flag) {
        if (mSearchMode != flag) {
            mSearchMode = flag;
            update(false /* skipAnimation */);
            if (mSearchView == null) {
                return;
            }
            if (mSearchMode) {
                mSearchView.setEnabled(true);
//                mSearchView.setVisibility(View.VISIBLE);
                setFocusOnSearchView();
            } else {
                // Disable search view, so that it doesn't keep the IME visible.
                mSearchView.setEnabled(false);
//                mSearchView.setVisibility(View.INVISIBLE);
            }
            setQueryString(null);
        } else if (flag) {
            // Everything is already set up. Still make sure the keyboard is up
            if (mSearchView != null) setFocusOnSearchView();
        }
    }

    public void setSelectionMode(boolean flag) {
        if (mSelectionMode != flag) {
            mSelectionMode = flag;
            update(false /* skipAnimation */);
        }
    }

    public String getQueryString() {
        return mSearchMode ? mQueryString : null;
    }

    public void setQueryString(String query) {
        mQueryString = query;
        if (mSearchView != null) {
            mSearchView.setText(query);
            // When programmatically entering text into the search view, the most reasonable
            // place for the cursor is after all the text.
            mSearchView.setSelection(mSearchView.getText() == null ?
                    0 : mSearchView.getText().length());
        }
    }

    /** @return true if the "UP" icon is showing. */
    public boolean isUpShowing() {
        return mSearchMode; // Only shown on the search mode.
    }
    private void updateDisplayOptionsInner() {
        // All the flags we may change in this method.
        final int MASK = ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_HOME_AS_UP;
        // The current flags set to the action bar.  (only the ones that we may change here)
        final int current = mActionBar.getDisplayOptions() & MASK;
        final boolean isSearchOrSelectionMode = mSearchMode || mSelectionMode;
        // Build the new flags...
        int newFlags = 0;
        if (mShowHomeIcon && !isSearchOrSelectionMode) {
            newFlags |= ActionBar.DISPLAY_SHOW_HOME;
            if (mShowHomeAsUp) {
                newFlags |= ActionBar.DISPLAY_HOME_AS_UP;
            }
        }
        if (mSearchMode && !mSelectionMode) {
            // The search container is placed inside the toolbar. So we need to disable the
            // Toolbar's content inset in order to allow the search container to be the width of
            // the window.
            mToolbar.setContentInsetsRelative(0, mToolbar.getContentInsetEnd());
        }
        if (!isSearchOrSelectionMode) {
            newFlags |= ActionBar.DISPLAY_SHOW_TITLE;
            mToolbar.setContentInsetsRelative(mMaxToolbarContentInsetStart,
                    mToolbar.getContentInsetEnd());
            mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        } else {
            mToolbar.setNavigationIcon(null);
        }
        if (mSelectionMode) {
            // Minimize the horizontal width of the Toolbar since the selection container is placed
            // behind the toolbar and its left hand side needs to be clickable.
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolbar.getLayoutParams();
            params.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.END;
            mToolbar.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mToolbar.getLayoutParams();
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.END;
            mToolbar.setLayoutParams(params);
        }
        if (current != newFlags) {
            // Pass the mask here to preserve other flags that we're not interested here.
            mActionBar.setDisplayOptions(newFlags, MASK);
        }
    }

    public void updateOverflowButtonColor() {
        final String overflowDescription = mActivity.getResources().getString(
                R.string.abc_action_menu_overflow_description);
        final ViewGroup decorView = (ViewGroup) mActivity.getWindow().getDecorView();
        final ViewTreeObserver viewTreeObserver = decorView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        // Find the overflow ImageView.
                        final ArrayList<View> outViews = new ArrayList<>();
                        decorView.findViewsWithText(outViews, overflowDescription,
                                View.FIND_VIEWS_WITH_CONTENT_DESCRIPTION);
                        for (View view : outViews) {
                            if (!(view instanceof ImageView)) {
                                continue;
                            }
                            final ImageView overflow = (ImageView) view;
                            // Update the overflow image color.
                            final int iconColor;
                            if (mSelectionMode) {
                                iconColor = mActivity.getResources().getColor(
                                        R.color.colorAccent);
                            } else {
                                iconColor = mActivity.getResources().getColor(
                                        R.color.white);
                            }
//                            overflow.setImageTintList(ColorStateList.valueOf(iconColor));
                        }
                        // We're done, remove the listener.
//                        decorView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                });
    }

    public void setFocusOnSearchView() {
        mSearchView.requestFocus();
        showInputMethod(mSearchView); // Workaround for the "IME not popping up" issue.
    }

    private void showInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    @Override
    public boolean onClose() {
        setSearchMode(false);
        return false;
    }
}
