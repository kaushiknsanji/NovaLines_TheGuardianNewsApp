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

import android.view.View;

/**
 * A View Interface for the Popup Menu actions shown
 * on the News Article Card items 'R.layout.news_article_item'
 *
 * @author Kaushik N Sanji
 */
public interface IArticleActionView extends IGenericView {
    /**
     * Method that returns the Root {@link View}
     * of the Activity/Fragment implementing {@link IArticleActionView}
     *
     * @return The Root {@link View} of the implementing Activity/Fragment
     */
    View getRootView();
}
