package com.example.android.popularmovies.data;

/**
 * POJO Class that represents the position (actual page) in a set of pages.
 */
public class Page {
    private static int mPage = 1;
    private static int mTotalPages;

    /**
     * Gets the actual page
     *
     * @return the actual page
     */
    public static int getPage() {
        return mPage;
    }

    /**
     * Sets the actual page
     *
     * @param page the actual page
     */
    public static void setPage(int page) {
        mPage = page;
    }

    /**
     * Get the total number of pages available
     *
     * @return the total number of pages available
     */
    public static int getTotalPages() {
        return mTotalPages;
    }

    /**
     * Sets the total number of pages available
     *
     * @param pages the total number of pages available
     */
    public static void setTotalPages(int pages) {
        mTotalPages = pages;
    }
}
