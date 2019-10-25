/*
 * Copyright 2018 Kaushik N. Sanji
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
