# NovaLines - TheGuardianNewsApp

This App has been developed as part of the **Udacity Android Basics Nanodegree Course** for the Exercise Project **"News Feed App"**. App connects to the [Guardian News API](https://content.guardianapis.com/) to retrieve the News Feed based on the particular endpoint used and then displays them as a list.

---

## App Compatibility

Android device running with Android OS 4.0.4 (API Level 15) or above. Best experienced on Android Nougat 7.1 and above. Designed for Phones and NOT for Tablets.

---

## Rubric followed for the Project

* App queries the `content.guardianapis.com` API to fetch news stories and properly parses the JSON Response.
* Each List item displayed by the App for the News stories should contain relevant text and information about the story. This includes -
	* Name of the Section.
	* Title of the article.
	* Author Name if present.
	* Published date of the article if present.
* Default text is shown when any of the above parsed information is not available.
* Networking operations are to be done using Loaders.
* Clicking on a story should open the story in the user's browser.
* No external libraries to be used.
* On device rotation
	* The layout remains scrollable.
	* The app saves state and restores the list back to the previously scrolled position.
	* The UI properly adjusts so that all contents of each list item is still visible and not truncated/overlapped.
* When new News data is fetched, the main screen should update properly with the new data.
* Check whether connected to internet or not. Also, validate for any occurrence of bad server response or lack of response.

---

### Things explored/developed in addition to the above defined Rubric

* Used `RecyclerView` in place of `ListView` (to display the News stories) for its advantages in performance and easy placeholders for custom item decoration.
* Custom `RecyclerView.ItemDecoration` for adding space between the News List items.
* `CardView` for displaying the News stories content for each News List items.
* Navigation Drawer Items implemented using `RecyclerView`.
* Explored `FragmentStatePagerAdapter` that displays the Fragments \(retaining their state\) for the `ViewPager`. Also, mocks the dynamic adding of additional tab content to the `ViewPager`.
* Implemented `android.support.v7.preference.Preference` Preferences for the Settings.
* No external libraries are used for communicating with the REST API and also for loading the images. `AsyncTaskLoader` has been used for downloading the data and images in the background thread.
* Most layouts are designed using `ConstraintLayout` to flatten the layout hierarchy as far as possible.
* Spannables for decorating `TextViews` - [TextAppearanceUtility](/app/src/main/java/com/example/kaushiknsanji/novalines/utils/TextAppearanceUtility.java) with image within text, html content in text.
* Custom Fonts for `TextViews` using `ResourceCompat`
* Explored `CoordinatorLayout`
* Used `RecyclerView` in a `SwipeRefreshLayout` to use the integrated Progress/Refresh indicator.
* Used `DiffUtil` in `RecyclerView` to help rebind only the item views that have changed.

---

## Design and Implementation of the App

<!-- Video of the App -->
[![Video of Complete App Flow](https://i.ytimg.com/vi/XzZbe7aYeXU/maxresdefault.jpg)](https://youtu.be/XzZbe7aYeXU)

### The Drawer Layout

App consists of a Drawer for Navigating between News related Fragments. 

<!-- Image for Drawer layout -->
<img src="https://user-images.githubusercontent.com/26028981/38467585-7f1d3cf4-3b58-11e8-9c4a-988f7f68faa3.png" width="40%" />

The Drawer consists of -
* Headlines
* Random News
* Bookmarked News
* Favorited News

Apart from the above which are Fragments by themselves, the Drawer also has a menu for **Settings** and **About** which are Activities.

The Activity that inflates this [Drawer Layout](/app/src/main/res/layout/activity_news.xml) is the [NewsActivity](app/src/main/java/com/example/kaushiknsanji/novalines/NewsActivity.java). The Drawer Layout is custom built using a `RecyclerView` whose items are loaded by the [NavRecyclerAdapter](/app/src/main/java/com/example/kaushiknsanji/novalines/adapters/NavRecyclerAdapter.java). This Drawer is completely managed by the NewsActivity.

### Headlines Fragment

This is the Default Fragment loaded when the App is launched. This [Fragment](/app/src/main/java/com/example/kaushiknsanji/novalines/drawerviews/HeadlinesFragment.java) inflates the [layout](/app/src/main/res/layout/headlines_layout.xml) which consists of a `ViewPager` with [FragmentStatePagerAdapter](/app/src/main/java/com/example/kaushiknsanji/novalines/adapters/HeadlinesPagerAdapter.java) for displaying each of the Subscribed News Sections/Topics. `HeadlinesFragment` acts as the Parent Fragment for all the Child Fragments shown by the ViewPager (i.e., the `ViewPager`'s Adapter is set up using the `ChildFragmentManager`)

<!-- Image for the Headlines Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467590-97648cc2-3b58-11e8-8913-fb3011d00760.png" width="40%" />

The layout used here is a `CoordinatorLayout` which hides the custom [toolbar](/app/src/main/res/layout/toolbar_main.xml) when scrolled to bottom. On launch, this loads the first tab of the `ViewPager` which is the **Highlights** tab shown by the Child Fragment - [HighlightsFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/adapterviews/HighlightsFragment.java). This tab is responsible for showing a list of Subscribed News Topics with the current News Article count on each topic. Three News Topics are always presubscribed as shown -
* **Top Stories**
  - **Top Stories** shows a list of Editor Picked News items. Content is only affected by the start date (`from-date` query parameter) of the News feed mentioned in the Request URL. Results appear only in a single page. Sample URL used is http://content.guardianapis.com/international?show-editors-picks=true&from-date=2017-12-01&show-fields=trailText,byline,thumbnail&api-key=test
  
<!-- Image for the Top Stories Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467626-4c079944-3b59-11e8-96f0-eee446b00ead.png" width="40%" />

* **Most Visited**
  - **Most Visited** shows a list of Most Viewed News Items. Content is only affected by the start date (`from-date` query parameter) of the News feed mentioned in the Request URL. Results appear only in a single page. Sample URL used is http://content.guardianapis.com/international?show-most-viewed=true&from-date=2017-12-01&show-fields=trailText,byline,thumbnail&api-key=test
  
<!-- Image for the Most Visited Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467627-553cc642-3b59-11e8-9aba-ca3a6a021982.png" width="40%" />
  
* **World News**
  - **World News** shows a list of News Items that falls under the **World** section. This is also a type Paginated News as the Results can appear in more than a single page. Content is affected by the change in start date, current page number, number of items in a page and the sort order of the results. Sample URL used is http://content.guardianapis.com/world?from-date=2017-12-01&show-fields=trailText,byline,thumbnail&order-by=relevance&order-date=published&page-size=10&page=1&api-key=test
  
<!-- Image for the World News Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467630-5a9da548-3b59-11e8-9a9f-e81344e2fc73.png" width="40%" />
<img src="https://user-images.githubusercontent.com/26028981/38467633-5bcc4cda-3b59-11e8-9d20-47e6e40bc8b5.png" width="70%" />
  
Clicking on any of the Subscribed News Item in the HighlightsFragment, will take the user to the corresponding `ViewPager` Tab. 

Each of these tabs as described above, displays the parsed News Article Items in a `RecyclerView` of [CardView](/app/src/main/res/layout/news_article_item.xml) item views. These Item Views, inflated by the [ArticlesAdapter](/app/src/main/java/com/example/kaushiknsanji/novalines/adapters/ArticlesAdapter.java), supports Card expand/collapse to reveal more content on expansion as shown below.

<!-- GIF for item expand/collapse -->
![item_expand_collapse](https://user-images.githubusercontent.com/26028981/38467656-72addb08-3b59-11e8-9c2f-aeaeedc0ab64.gif)

Clicking on any of the News Articles Card Items, will navigate the user to the corresponding News Article in the user's selected browser. These News Article Card items also supports Popup Menu options. 

<!-- Image for the Card Item Popup Menu -->
<img src="https://user-images.githubusercontent.com/26028981/38467660-7e924670-3b59-11e8-92bf-8a5321428d80.png" width="40%" />

* **Share News**
	- Uses `ShareCompat`'s Intent Builder for sharing the Web URL of the News Article Item to any app, when clicked.
	
<!-- GIF for Share News -->
![share_news](https://user-images.githubusercontent.com/26028981/38467661-87361752-3b59-11e8-90f3-a6e643993ec1.gif)
	
* **Read Later**
	- On click of this, the corresponding News Article Item will be added to the **"Bookmarked News"** for reading later. At present, this only shows the acknowledgement of the Action but does nothing since this is not implemented yet. The corresponding Action is managed by the Presenter [BookmarkActionPresenter](/app/src/main/java/com/example/kaushiknsanji/novalines/presenters/BookmarkActionPresenter.java)
	
<!-- GIF for Read Later -->
![bookmark_news](https://user-images.githubusercontent.com/26028981/38467665-917c22f6-3b59-11e8-896b-1cce74d9136c.gif)
	
* **Favorite This**
	- On click of this, the corresponding News Article Item will be added to the **"Favorited News"** as your Favorite. At present, this only shows the acknowledgement of the Action but does nothing since this is not implemented yet. The corresponding Action is managed by the Presenter [FavoriteActionPresenter](/app/src/main/java/com/example/kaushiknsanji/novalines/presenters/FavoriteActionPresenter.java)
	
<!-- GIF for Favorite this -->
![favorite_news](https://user-images.githubusercontent.com/26028981/38467669-971b8760-3b59-11e8-8108-e62eeee8f467.gif)

* **Open News Section**
	- Clicking this, will Navigate the user to the Tab of the requested News Topic if already subscribed; if not subscribed then it will show the **"\+"** Tab which is the [MoreNewsFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/adapterviews/MoreNewsFragment.java) that allows the user to subscribe to the requested News Topic.

<!-- GIF for Open News Section -->
![open_news_and_subscribe](https://user-images.githubusercontent.com/26028981/38467675-a5e31e34-3b59-11e8-9b60-e73d6aaf1051.gif)
![open_subscribed_news](https://user-images.githubusercontent.com/26028981/38467676-a712e302-3b59-11e8-8313-8c8cb2fa03e9.gif)

The `MoreNewsFragment` is always the last tab of the `ViewPager` shown by the `HeadlinesFragment`. It allows subscribing to additional News Topics. When Navigated by the **Open News Section**, user can only subscribe to the requested News Topic provided that topic was previously not subscribed. If user wants to subscribe to multiple topics, then the user needs to navigate to the last tab using the "Subscribe More" menu button shown in the **"Highlights** Tab and then use the button to subscribe. 

<!-- Image for More News Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467678-ac3db280-3b59-11e8-8725-3c0ba45803fe.png" width="40%" />

_Subscription to multiple topics is currently not implemented/supported as this will require the use of Database for storing the subscribed list. Also, once a user is subscribed to a particular requested topic, it will stay subscribed only till the lifetime of the `HeadlinesFragment` as this data is maintained in the instance bundle of the Parent Fragment `HeadlinesFragment`._

All additional subscribed News Topics are paginated and are affected by the various parameters, that also affects the **"World News** Topic. All these topics talk to the Guardian News with the Sample URL http://content.guardianapis.com/sectionId?from-date=2017-12-01&show-fields=trailText,byline,thumbnail&order-by=relevance&order-date=published&page-size=10&page=1&api-key=test
where **sectionId** path will be replaced the sectionId of the Subscribed News Topic.

<!-- GIF for Pagination -->
![pagination](https://user-images.githubusercontent.com/26028981/38467681-b2887d5a-3b59-11e8-983a-adf460ee59a9.gif)

Pagination is managed through a custom [panel](/app/src/main/res/layout/pagination_panel_layout.xml) interface that is shown only when the scroll reaches the last few items in the `RecyclerView`. If the items in the `RecyclerView` is very less for scroll, then the panel will be always shown. This is implemented by extending `RecyclerView.OnScrollListener` which is done by the class [BaseRecyclerViewScrollListener](/app/src/main/java/com/example/kaushiknsanji/novalines/observers/BaseRecyclerViewScrollListener.java). This is an abstract class which provides an event callback when the scroll reaches the last 3 items in the list, to reveal the pagination buttons for the user to navigate to different pages in the current News Topic Tab. The Pagination work is always delegated to the Presenter - [PaginationPresenter](/app/src/main/java/com/example/kaushiknsanji/novalines/presenters/PaginationPresenter.java) by the Fragment requiring the Pagination.

### Random News Fragment

<!-- GIF for Random News Fragment -->
![random_news](https://user-images.githubusercontent.com/26028981/38467686-bccb409a-3b59-11e8-9808-234764beaa56.gif)

[RandomNewsFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/drawerviews/RandomNewsFragment.java) is the second fragment shown in the Drawer of `NewsActivity`. It provides the user with the ability to **Search** for any content supported by the Guardian News API. It uses the Search endpoint (https://content.guardianapis.com/search) for retrieving the results on the search query entered by the user. Pagination Panel will always be shown since Search endpoint returns results that support pagination. Content is affected by the Search Query entered, start date of the News, current page number, number of items in a page and the sort order of the results.

News Article card items supports the same set of features as available in `HeadlinesFragment` (since it employs the same `ArticlesAdapter`), with an exception that **"Open News Section"** will be programmatically removed since it is not supported.

### Bookmarked News Fragment

<!-- Image for Bookmarked News Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467694-c9c20284-3b59-11e8-979e-29e51034bda3.png" width="40%" />

[BookmarksFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/drawerviews/BookmarksFragment.java) is the third fragment shown in the Drawer of `NewsActivity`. It is meant for displaying a list of News Article items that were **Bookmarked** by the user, in other News Fragments through the **"Read Later"** Popup menu option on the News Article Item Card. Currently, this is not implemented, hence no items will be shown. This is just a placeholder for future ideas/implementation.

### Favorited News Fragment

<!-- Image for Favorited News Fragment -->
<img src="https://user-images.githubusercontent.com/26028981/38467696-cca04c36-3b59-11e8-9ed1-463667404995.png" width="40%" />

[FavoritesFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/drawerviews/FavoritesFragment.java) is the fourth fragment shown in the Drawer of `NewsActivity`. It is meant for displaying a list of News Article items that were **Favorited** by the user, in other News Fragments through the **"Favorite This"** Popup menu option on the News Article Item Card. Currently, this is not implemented, hence no items will be shown. This is just a placeholder for future ideas/implementation.

### Controlling the Results

The Results shown in the Fragments of `HeadlinesFragment` and `RandomNewsFragment` can be controlled by varying certain parameters embedded in the Request URL. This is defined by the [Guardian News API](http://open-platform.theguardian.com/documentation/) which this application is based on and is acting as a client that receives the data for the request executed.

<!-- Image for the Settings -->
<img src="https://user-images.githubusercontent.com/26028981/38467697-cfb82272-3b59-11e8-81f5-07e9a7a85326.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/38467698-d5182906-3b59-11e8-93b2-cc69ffdfbf22.gif"/>

All the parameters affecting the results can be altered by the **"Settings"** menu item available in each of the News Fragments and also on the Drawer menu. The **Settings** menu item, opens up the [preferences.xml](/app/src/main/res/xml/preferences.xml) loaded by the activity [SettingsActivity](/app/src/main/java/com/example/kaushiknsanji/novalines/settings/SettingsActivity.java). It implements the `android.support.v7.preference.Preference` Preferences. As such all the values are stored in the default `SharedPreferences`. These preferences provides options to override the parameters used in the news feed request, and following are the ones that can be changed -
* `order-by` parameter defines the sorting order of results. This is controlled by the **"Sort by"** ListPreference.
* `order-date` parameter defines on how the sorting needs to be done. This is controlled by the **"Sort based on"** ListPreference.
* `page-size` parameter defines the number of news items to be displayed in a page. This is controlled by the **"News items per page"** NumberPickerPreference. This Preference is a custom [NumberPickerPreference](/app/src/main/java/com/example/kaushiknsanji/novalines/settings/NumberPickerPreference.java) implemented by extending the `DialogPreference`.
* `from-date` parameter defines the start date for the News feed request. This is controlled by the **"Specify Start Period"** DatePickerPreference. This Preference is a custom [DatePickerPreference](/app/src/main/java/com/example/kaushiknsanji/novalines/settings/DatePickerPreference.java) implemented by extending the `DialogPreference`.

The `from-date` parameter is also controlled by other preferences, that provides the user to preset the start date to a particular date based on certain criteria. Unchecking the **"Start Period Preset/Manual"** CheckBoxPreference, will cause the start date to be set automatically based on the other two dependent preferences that gets enabled -
* **"Preset Start Period"** preference is a ListPreference, that allows the user to preset the start date to say 'today'/'start of the week'/'start of the month'.
* **"Buffer to Start Period"** preference is a custom NumberPickerPreference, that allows the user to specify some number of buffer days to the **"Preset Start Period"** specified. Example, if **"Preset Start Period"** is set to "Start of the Week" and the predetermined date falls to (01/01/2018); and if **"Buffer to Start Period"** is set to 4 days buffer, then the final start date calculated will be (28/03/2018). 

The date determined in the above settings will be written to the `from-date` key in the `SharedPreferences`.

There is also another preference setting displayed as **"Reset Settings"**, which resets all the preferences to their default values. This is implemented by [ConfirmationPreference](/app/src/main/java/com/example/kaushiknsanji/novalines/settings/ConfirmationPreference.java).

### Loading of Data

* Loading of Subscribed News Articles' Count is carried out in a background thread using [NewsHighlightsLoader](/app/src/main/java/com/example/kaushiknsanji/novalines/workers/NewsHighlightsLoader.java) that extends `AsyncTaskLoader`.
* Loading of News Articles Data is carried out in a background thread using [NewsArticlesLoader](/app/src/main/java/com/example/kaushiknsanji/novalines/workers/NewsArticlesLoader.java) that extends `AsyncTaskLoader`.
* Request and Response is carried out via a REST call (GET Method) to the Guardian News API.
* The data format used for Response communication is the JSON format.
* Talking to the REST API and parsing the response for the content is done by the utility codes.
	* [JsonUtility](/app/src/main/java/com/example/kaushiknsanji/novalines/utils/JsonUtility.java) for sending request and receiving the response.
	* [NewsSectionInfoParserUtility](/app/src/main/java/com/example/kaushiknsanji/novalines/utils/NewsSectionInfoParserUtility.java) for parsing the response to extract the Subscribed News Articles' Count, as defined by the model [NewsSectionInfo](/app/src/main/java/com/example/kaushiknsanji/novalines/models/NewsSectionInfo.java).
	* [NewsArticleInfoParserUtility](/app/src/main/java/com/example/kaushiknsanji/novalines/utils/NewsArticleInfoParserUtility.java) for parsing the response to extract the News Articles' Data, as defined by the model [NewsArticleInfo](/app/src/main/java/com/example/kaushiknsanji/novalines/models/NewsArticleInfo.java).	

_As per the Rubric, no third party library is used for communicating with the REST API and for parsing the data._

### Loading of Images

Loading of Images for the News Articles are carried out in a background thread through a viewless Fragment [ImageDownloaderFragment](app/src/main/java/com/example/kaushiknsanji/novalines/workers/ImageDownloaderFragment.java). Functioning of this fragment is as follows -
* It first checks whether the image to be loaded is present in the Bitmap Cache, implemented by [BitmapImageCache](/app/src/main/java/com/example/kaushiknsanji/novalines/cache/BitmapImageCache.java)
* If present in the cache, it updates the image to the corresponding `ImageView` passed.
* If not present in cache, then the image is downloaded in a background thread using [ImageDownloader](/app/src/main/java/com/example/kaushiknsanji/novalines/workers/ImageDownloader.java) that extends an `AsyncTaskLoader`. Once successfully downloaded, it updates the image to the corresponding `ImageView` passed, and also saves the same in the Bitmap Cache.

The identifier for the Fragment and its Loader is maintained to be unique to the item being displayed such that each item displays the correct image to be shown, without resulting in any duplication. If duplication still happens, the loader will take care of loading the correct one when the item is being displayed the second time in the screen.

_As per the Rubric, no third party library is used for loading images._

### Information in general, on the entire app

* After scrolling to certain extent in the `RecyclerView` of the `ViewPager` of `HeadlinesFragment` -
	* If the user swipes across the tabs, then the new Fragment being displayed will auto scroll to the topmost item in that Fragment.
	* If the user swipes across the tabs that have paginated content, then the new Paginated Fragment being displayed will reset the current page to first page and auto scrolls to the topmost item in that Fragment.
	* Clicking on the active tab again, will cause the scroll to move over to the topmost item in that Fragment.
	* If the user switches the orientation, the Fragment will auto scroll to the item that was last shown.

* Back button has different behaviors while on different screens -
	* If the user is viewing any of the Tabs other than the **"Highlights** Tab in the `Headlines` Fragment, clicking `back` key will bring the user to the **"Highlights** Tab. Clicking `back` key again, will quit the App.
	* If the user is viewing any of the Drawer Items other than the `Headlines`, clicking `back` key will bring the user to the **"Headlines** Fragment and the **"Highlights** Tab will be shown. Clicking `back` key again, will quit the App.
	
* The `RecyclerView` used in the app is embedded in a [SwipeRefreshLayout](/app/src/main/res/layout/refreshable_recycler_layout.xml). Hence, one can refresh the content with Swipe-To-Pull Refresh.
	
* If the Search entered in the `RandomNewsFragment` did not yield any result, (or) if the request from the `HeadlinesFragment` did not return any News items, then a **"No Feed Available"** page will be shown with several `CardView`s that provide information on how to resolve the issue. This is displayed by the Fragment [NoFeedResolutionFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/errorviews/NoFeedResolutionFragment.java) that inflates the layout [no_feed_layout.xml](/app/src/main/res/layout/no_feed_layout.xml).

<!-- Image/GIF for No Feed Available -->
<img src="https://user-images.githubusercontent.com/26028981/38468457-f9a08778-3b63-11e8-8f40-927426868868.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/38467702-dadad65e-3b59-11e8-9fcf-cf3aba5bd960.gif"/>  <img src="https://user-images.githubusercontent.com/26028981/38467703-dc7b883c-3b59-11e8-81c3-9b44ff2773bd.gif"/>

* While initiating a request, if a network connectivity issue is encountered, then a **"Network Error"** page will be shown, with information on how to resolve the issue. This is displayed by the Fragment [NetworkErrorFragment](/app/src/main/java/com/example/kaushiknsanji/novalines/errorviews/NetworkErrorFragment.java) that inflates the layout [network_error_layout.xml](/app/src/main/res/layout/network_error_layout.xml).

<!-- Image/GIF for Network Error page -->
<img src="https://user-images.githubusercontent.com/26028981/38468459-fc768268-3b63-11e8-9195-2d54143e81ce.png" width="40%" />  <img src="https://user-images.githubusercontent.com/26028981/38467711-f3036282-3b59-11e8-96b6-246806b01a00.gif"/>

### The About Page

This can be viewed by going into the Drawer menu item **"About"** of the `NewsActivity`. This page describes in brief about the app, and has links to my bio and the course details hosted by Udacity. This is shown by the activity [AboutActivity](/app/src/main/java/com/example/kaushiknsanji/novalines/AboutActivity.java) that inflates the layout [activity_about.xml](/app/src/main/res/layout/activity_about.xml).

<!-- Image for About page -->
<img src="https://user-images.githubusercontent.com/26028981/38467715-f911b228-3b59-11e8-9945-e3089e880188.png" width="40%" />

## Review from the Reviewer (Udacity)

![review](https://user-images.githubusercontent.com/26028981/38767016-63bfb7b2-3ff8-11e8-86cf-2930a6fc14fc.PNG)

## Bugs Found

* Any News Article with the Topic **"Global"** when subscribed is currently causing a crash, since **"Global"** is not found to be there in any of the Sections supported by the Guardian News API. We do have something called as **"Global development"**, but this is different.
