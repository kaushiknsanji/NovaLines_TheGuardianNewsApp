package com.example.kaushiknsanji.novalines.interfaces;

/**
 * A Presenter Interface (as in MVP) with a generic parameter for the View(V)
 *
 * @author Kaushik N Sanji
 */
public interface IGenericPresenter<V> {
    /**
     * Registers the Presenter {@link IGenericPresenter}
     * with the {@link android.view.View}
     *
     * @param view Instance of the {@link android.view.View}
     *             to be associated with this Presenter {@link IGenericPresenter}
     */
    void attachView(V view);

    /**
     * Unregisters the previously registered {@link android.view.View}
     * by the Presenter {@link IGenericPresenter}
     */
    void detachView();
}
