package com.hiveview.dianshang.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;


/**
 * {@link HiveGridLayoutManager} extension which introduces workaround for focus finding bug when
 * navigating with dpad.
 *
 * @see <a href="http://stackoverflow.com/questions/31596801/recyclerview-focus-scrolling">http://stackoverflow.com/questions/31596801/recyclerview-focus-scrolling</a>
 */
public class HiveGridLayoutManager extends android.support.v7.widget.GridLayoutManager {

    private static final String TAG = HiveGridLayoutManager.class.getSimpleName();

    private int mSpanCount = 4;

    public HiveGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }

    public HiveGridLayoutManager(Context context, int spanCount, int orientation,
                                 boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }


    /**
     *　...........[No function].........
     * @param focused
     * @param focusDirection
     * @param recycler
     * @param state
     * @return
     */
    @Override
    public View onFocusSearchFailed(View focused, int focusDirection,
                                    RecyclerView.Recycler recycler, RecyclerView.State state) {

        View nextFocus = super.onFocusSearchFailed(focused, focusDirection, recycler, state);
        if (nextFocus == null) {
            //Logger.i(TAG, "onFocusSearchFailed nextFocus--> null = ");
            return null;
        }
        int fromPos = getPosition(focused);
        //Logger.i(TAG, "onFocusSearchFailed fromPos-->" + fromPos);
        int nextPos = getNextViewPos(fromPos, focusDirection);
        //Logger.i(TAG, "onFocusSearchFailed nextPos-->" + nextPos);
        return findViewByPosition(nextPos);
    }

    /**
     * Manually detect next view to focus.
     *
     * @param fromPos   from what position start to seek.
     * @param direction in what direction start to seek. Your regular {@code View.FOCUS_*}.
     * @return adapter position of next view to focus. May be equal to {@code fromPos}.
     */
    protected int getNextViewPos(int fromPos, int direction) {

        int offset = calcOffsetToNextView(direction);
        if (hitBorder(fromPos, offset)) {
            return fromPos;
        }
        return fromPos + offset;
    }

    /**
     * Calculates position offset.
     *
     * @param direction regular {@code View.FOCUS_*}.
     * @return position offset according to {@code direction}.
     */
    protected int calcOffsetToNextView(int direction) {
        int spanCount = getSpanCount();
        int orientation = getOrientation();

        if (orientation == VERTICAL) {
            switch (direction) {
                case View.FOCUS_DOWN:
                    return spanCount;
                case View.FOCUS_UP:
                    return -spanCount;
                case View.FOCUS_RIGHT:
                    return 1;
                case View.FOCUS_LEFT:
                    return -1;
            }
        } else if (orientation == HORIZONTAL) {
            switch (direction) {
                case View.FOCUS_DOWN:
                    return 1;
                case View.FOCUS_UP:
                    return -1;
                case View.FOCUS_RIGHT:
                    return spanCount;
                case View.FOCUS_LEFT:
                    return -spanCount;
            }
        }

        return 0;
    }

    /**
     * Checks if we hit borders.
     *
     * @param from   from what position.
     * @param offset offset to new position.
     * @return {@code true} if we hit border.
     */
    private boolean hitBorder(int from, int offset) {
        //Logger.i(TAG, "hitBorder-->offset->" + offset);
        int spanCount = getSpanCount();
        if (Math.abs(offset) == 1) {
            int spanIndex = from % spanCount;
            int newSpanIndex = spanIndex + offset;
            return newSpanIndex < 0 || newSpanIndex >= spanCount;
        } else {
            int newPos = from + offset;
            return newPos < 0 && newPos >= spanCount;
        }
    }


    @Override
    public View onInterceptFocusSearch(View focused, int direction) {
         try {
             if (direction == View.FOCUS_DOWN) {
                 int pos = getPosition(focused);
                 int size = getChildCount();
                 int count = getItemCount();
                 Log.v(TAG,"pos=="+pos+",size="+size+",count="+count);
                 if (size > 0) {
                     int startIndex = 0;
                     if (size >= mSpanCount) {
                         startIndex = size - mSpanCount;
                     }
                     View view;
                     for (int i = startIndex; i < size; i++) {
                         view = getChildAt(i);
                         if (view == focused) {
                             int lastVisibleItemPos = findLastCompletelyVisibleItemPosition();
                             Log.v(TAG,"lastVisibleItemPos="+lastVisibleItemPos);
                             if (pos > lastVisibleItemPos) { //lastVisibleItemPos==-1 ||
                                 return focused;
                             } else {
                                 int lastLineStartIndex = 0;
                                 if (count >= mSpanCount) {
                                     lastLineStartIndex = count - mSpanCount;
                                 }
                                 if (pos >= lastLineStartIndex && pos < count) { //最后一排的可见view时,返回当前view
                                     return focused;
                                 }
                                 break;
                             }
                         }
                     }
                 } else {
                     return focused;
                 }
             }
         } catch (Exception e) {
             e.printStackTrace();
         }
        return super.onInterceptFocusSearch(focused, direction);
    }



}