# NovaLines - v1.0

![GitHub](https://img.shields.io/github/license/kaushiknsanji/NovaLines_TheGuardianNewsApp) ![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/kaushiknsanji/NovaLines_TheGuardianNewsApp) ![GitHub repo size](https://img.shields.io/github/repo-size/kaushiknsanji/NovaLines_TheGuardianNewsApp) ![GitHub Releases](https://img.shields.io/github/downloads/kaushiknsanji/NovaLines_TheGuardianNewsApp/v1.0/total)

This is the Release version 1.0 of the **NovaLines** Guardian News App.

## Changes done in this Release

* Used `ConstrainedWidth`/`ConstrainedHeight` to enforce `WRAP_CONTENT` constraints for certain Item Views and Dialog Layouts - ([commit](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/commit/d2557a64a9f13112326c3fc1d224ca9ffc259c58)).
* "Handling of Null Image links", and "Reading from and Writing Bitmaps to Memory cache" is now taken care by the `ImageDownloader` AsyncTaskLoader - ([commit](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/commit/3687cdcea82a311b15218a7003c47a8d54c00bc1)).
* Enabled logging for debuggable build types only, through the use of custom [Logger](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/blob/release_v1.0/app/src/main/java/com/example/kaushiknsanji/novalines/utils/Logger.java) which is a wrapper to the `android.util.Log` - ([commit](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/commit/7122e6f9d86aba9539b875c41301dc812ac84bfb)).
* Changed certain inner classes to static nested classes to prevent memory leaks - ([commit](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/commit/8239456ae7fb9121330a8e4b8431544056e1efb7)).
* Configured an Activity Alias to launch the `NewsActivity` - ([commit](https://github.com/kaushiknsanji/NovaLines_TheGuardianNewsApp/commit/145f01a3449a10dd4bd74e3871e564123ae75871)).

## License

```
Copyright 2018 Kaushik N. Sanji

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0
   
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
