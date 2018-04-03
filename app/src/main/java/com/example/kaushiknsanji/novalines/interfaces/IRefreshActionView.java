package com.example.kaushiknsanji.novalines.interfaces;

/**
 * Interface to be implemented by Activity/Fragment
 * hosting/inflating the layout 'R.layout.refreshable_recycler_layout',
 * that requires the contract defined.
 *
 * @author Kaushik N Sanji
 */
public interface IRefreshActionView {
    /**
     * Method that triggers a content refresh
     */
    void triggerRefresh();
}
