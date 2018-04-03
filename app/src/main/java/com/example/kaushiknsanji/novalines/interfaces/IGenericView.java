package com.example.kaushiknsanji.novalines.interfaces;

import android.content.Context;

/**
 * A View Interface as in MVP
 *
 * @author Kaushik N Sanji
 */
public interface IGenericView {
    /**
     * This method returns the {@link Context}
     * of the Activity/Fragment implementing {@link IGenericView}
     *
     * @return {@link Context} of the Activity/Fragment
     */
    Context getViewContext();
}
