package org.upennapo.app.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import org.upennapo.app.R;
import org.upennapo.app.model.Brother;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


/**
 * An extension of DirectoryFragment that loads directory from all directory sources, using
 * ActionItems to toggle which source is displayed.
 * <p/>
 * QuickReturnBar code is based on the work of Dandré Allison:
 * https://gist.github.com/imminent/4576886
 *
 * @author Ronald Martin
 */
public class AlumDirectoryFragment extends DirectoryFragment implements View.OnClickListener,
        AbsListView.OnScrollListener {

    /* Tag for debugging */
    public static final String TAG = "AlumDirectoryFragment";
    /* Keys allow storage and retrieval of data during Fragment lifecycle. */
    private static final String LIST_BROS = "LIST_BROS";
    private static final String LIST_ALUM = "LIST_ALUM";

    /* Store entries independently of directory list to allow toggling by user. */
    private ArrayList<Brother> mStudentList, mAlumList;
    /* Toggle buttons for switching directory lists. */
    private Button mShowAlumBtn, mShowStudentBtn;
    /**
     * The current state of the quick return bar
     */
    private QuickReturnState mQuickReturnState = QuickReturnState.ON_SCREEN;
    /**
     * Index of the last seen first visible child
     */
    private int mLastFirstVisibleItemIndex;
    /**
     * Tracks the last seen y-position of the first visible child
     */
    private int mLastFirstVisibleChildYPos;
    /**
     * Animates the quick return bar off of the screen
     */
    private ObjectAnimator mQuickReturnBarReturnAnimator;
    /**
     * Animates the quick return bar onto the screen
     */
    private ObjectAnimator mQuickReturnBarHideAnimator;

    /**
     * Returns new instance of AlumDirectoryFragment with arguments set.
     *
     * @param context - context used to access String resources
     * @return primed instance of AlumDirectoryFragment
     */
    public static AlumDirectoryFragment newInstance(Context context) {
        AlumDirectoryFragment instance = new AlumDirectoryFragment();

        final String sheetKey = context.getString(R.string.alumni_directory_sheet_key);
        Bundle args = new Bundle();
        args.putString(SHEET_KEY, sheetKey);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If possible, retrieve saved data. Otherwise, initialize new lists.
        if (savedInstanceState == null) {
            mAlumList = new ArrayList<Brother>();
            mStudentList = new ArrayList<Brother>();
        } else {
            mAlumList = savedInstanceState.getParcelableArrayList(LIST_ALUM);
            mStudentList = savedInstanceState.getParcelableArrayList(LIST_BROS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alumni_directory, container, false);
        init(savedInstanceState, view);
        return view;
    }

    @Override
    protected void init(Bundle savedInstanceState, View view) {
        super.init(savedInstanceState, view);
        initQuickReturnBar(view);
        initButtons(view);
    }

    /**
     * Attach the buttons.
     *
     * @param rootView the root view inflated by the fragment.
     */
    private void initButtons(View rootView) {
        mShowAlumBtn = (Button) rootView.findViewById(R.id.btn_show_alum);
        mShowStudentBtn = (Button) rootView.findViewById(R.id.btn_show_students);

        mShowAlumBtn.setOnClickListener(this);
        mShowStudentBtn.setOnClickListener(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(LIST_BROS, mStudentList);
        outState.putParcelableArrayList(LIST_ALUM, mAlumList);
    }

    @Override
    protected void loadData(final boolean forceDownload) {
        // Populate list with alumni data and save a reference to it.
        new AsyncBrotherLoader() {
            @Override
            protected void onPostExecute(Brother[] result) {
                // Use dependency injection to save a reference to the alumni list.
                super.onPostExecute(result);
                mAlumList = getDirectoryList();
            }
        }.execute(getString(R.string.alumni_directory_sheet_key), "" + forceDownload);

        // Load and save brother and pledge data, too.
        mStudentList.clear();
        loadDataInBackground(getString(R.string.brother_directory_sheet_key), forceDownload);
        loadDataInBackground(getString(R.string.pledge_directory_sheet_key), forceDownload);
        Collections.sort(mStudentList);
    }

    /**
     * Asynchronously load directory data and save it to the secondary (student directory) list.
     *
     * @param sheetKey      for the data to download
     * @param forceDownload whether or not to force downloading over the Internet
     */
    private void loadDataInBackground(String sheetKey, boolean forceDownload) {
        // Use dependency injection to override default AsyncBrotherLoader behavior.
        new AsyncBrotherLoader() {
            @Override
            protected void onPostExecute(Brother[] result) {
                if (getActivity() != null && result == null) {
                    Toast.makeText(getActivity(),
                            R.string.no_internet_toast_msg,
                            Toast.LENGTH_LONG)
                            .show();
                } else if (result != null) {
                    mStudentList.addAll(Arrays.asList(result));
                    Collections.sort(mStudentList);
                }
            }
        }.execute(sheetKey, "" + forceDownload);
    }

    @Override
    protected void updateListView() {
        super.updateListView();
        View view = getView();
        if (view != null) {
            View content = view.findViewById(R.id.content);
            if (content.getVisibility() == View.GONE) {
                getProgressBar().setVisibility(View.GONE);
                content.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        // Refresh the index for the QuickReturnBar
        mLastFirstVisibleItemIndex = getListView().getFirstVisiblePosition();
        final View firstItem = getListView().getChildAt(mLastFirstVisibleItemIndex);
        mLastFirstVisibleChildYPos = firstItem == null ? 0 : firstItem.getTop();

        switch (view.getId()) {
            case R.id.btn_show_alum:
                switchToAlumList();
                break;
            case R.id.btn_show_students:
                switchToStudentList();
                break;
        }
    }

    /**
     * If we are currently displaying the student directory, switch to show the alumni directory and
     * change button colors accordingly.
     */
    private void switchToAlumList() {
        if (getAdapter() != null && getAdapter().getCount() != mAlumList.size()) {
            setDirectoryList(mAlumList);
            updateListView();
            mShowAlumBtn.setTextColor(getResources().getColor(R.color.accent_fallback_light));
            mShowStudentBtn.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    /**
     * If we are currently displaying the alumni directory, switch to show the student directory and
     * change button colors accordingly.
     */
    private void switchToStudentList() {
        if (getAdapter() != null && getAdapter().getCount() != mStudentList.size()) {
            setDirectoryList(mStudentList);
            updateListView();
            mShowStudentBtn.setTextColor(getResources().getColor(R.color.accent_fallback_light));
            mShowAlumBtn.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    /**
     * Set up the Quick Return Bar's animations.
     *
     * @param rootView the view holding the quick return bar
     */
    private void initQuickReturnBar(View rootView) {
        final AbsListView listView = (AbsListView) rootView.findViewById(R.id.name_list);

        // Record the index and y-position of the first visible child.
        mLastFirstVisibleItemIndex = listView.getFirstVisiblePosition();
        final View firstItem = listView.getChildAt(mLastFirstVisibleItemIndex);
        mLastFirstVisibleChildYPos = firstItem == null ? 0 : firstItem.getTop();

        // Set up quick return animations.
        final View quickReturnBar = rootView.findViewById(R.id.quick_return_bar);
        final String animationPropertyName = "translationY";

        mQuickReturnBarHideAnimator =
                ObjectAnimator.ofFloat(quickReturnBar, animationPropertyName, 0)
                        .setDuration(210);

        mQuickReturnBarHideAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mQuickReturnState = QuickReturnState.RETURNING;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mQuickReturnState = QuickReturnState.ON_SCREEN;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mQuickReturnState = QuickReturnState.OFF_SCREEN;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        mQuickReturnBarReturnAnimator =
                ObjectAnimator.ofFloat(quickReturnBar,
                        animationPropertyName,
                        getResources().getDimension(R.dimen.quick_return_bar_height))
                        .setDuration(128);
        mQuickReturnBarReturnAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                mQuickReturnState = QuickReturnState.HIDING;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mQuickReturnState = QuickReturnState.OFF_SCREEN;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                mQuickReturnState = QuickReturnState.ON_SCREEN;
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

        listView.setOnScrollListener(this);
    }

    /** Quick Return Bar **/

    /**
     * Checks if the quick return bar is transitioning back onto the screen.
     *
     * @return {@code true} indicates that the quick return bar is returning
     */
    private boolean quickReturnBarIsReturning() {
        return (mQuickReturnBarHideAnimator.isRunning()
                || mQuickReturnBarHideAnimator.isStarted());
    }

    /**
     * Checks if the quick return bar is transitioning off of the screen.
     *
     * @return {@code true} indicates that the quick return bar is going away
     */
    private boolean quickReturnBarIsGoingAway() {
        return mQuickReturnBarReturnAnimator.isRunning()
                || mQuickReturnBarReturnAnimator.isStarted();
    }

    @Override
    public void onScroll(AbsListView listView, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        final View firstChild = listView.getChildAt(firstVisibleItem);
        final int firstChildY = firstChild == null ? 0 : firstChild.getTop();

        switch (mQuickReturnState) {
            case OFF_SCREEN:
                if (!quickReturnBarIsReturning()
                        && (firstVisibleItem == mLastFirstVisibleItemIndex && firstChildY > mLastFirstVisibleChildYPos)
                        || firstVisibleItem < mLastFirstVisibleItemIndex) {
                    mQuickReturnBarHideAnimator.start();
                    Log.d(TAG, "Hide animation started.");
                }
                break;

            case ON_SCREEN:
                if (!quickReturnBarIsGoingAway()
                        && (firstVisibleItem == mLastFirstVisibleItemIndex && firstChildY < mLastFirstVisibleChildYPos)
                        || firstVisibleItem > mLastFirstVisibleItemIndex) {
                    mQuickReturnBarReturnAnimator.start();
                    Log.d(TAG, "Return animation started.");
                }
                break;

            case RETURNING:
                if ((firstVisibleItem == mLastFirstVisibleItemIndex && firstChildY < mLastFirstVisibleChildYPos)
                        || firstVisibleItem > mLastFirstVisibleItemIndex) {
                    mQuickReturnBarHideAnimator.cancel();
                    mQuickReturnBarReturnAnimator.start();
                    Log.d(TAG, "Return cancelled, hide animation started.");
                }
                break;

            case HIDING:
                if ((firstVisibleItem == mLastFirstVisibleItemIndex && firstChildY > mLastFirstVisibleChildYPos)
                        || firstVisibleItem < mLastFirstVisibleItemIndex) {
                    mQuickReturnBarReturnAnimator.cancel();
                    mQuickReturnBarHideAnimator.start();
                    Log.d(TAG, "Hide cancelled, return animation started.");
                }
                break;
        }
        mLastFirstVisibleItemIndex = firstVisibleItem;
        mLastFirstVisibleChildYPos = firstChildY;
    }

    @Override
    public void onScrollStateChanged(AbsListView listView, int scrollState) {
    }

    /**
     * Quick Return Bar **
     */
    private static enum QuickReturnState {
        /**
         * Stable state indicating that the quick return bar is visible on screen
         */
        ON_SCREEN,
        /**
         * Stable state indicating that the quick return bar is hidden off screen
         */
        OFF_SCREEN,
        /**
         * Transitive state indicating that the quick return bar is coming onto the screen
         */
        RETURNING,
        /**
         * Transitive state indicating that the quick return bar is going off of the screen
         */
        HIDING
    }
}
