[![GitHub release](https://img.shields.io/github/v/release/fedeb87/AlarMe.svg?label=release)](https://github.com/fedeb87/AlarMe/releases) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/456966c5bd61499fa7fd69e1798a0452)](https://www.codacy.com/gh/fedeb87/AlarMe/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fedeb87/AlarMe&amp;utm_campaign=Badge_Grade) [![GitHub issues](https://img.shields.io/github/issues/fedeb87/AlarMe)](https://github.com/fedeb87/AlarMe/issues) [![GitHub last commit](https://img.shields.io/github/last-commit/fedeb87/AlarMe?label=last-commit)](https://github.com/fedeb87/AlarMe/commits) [![GitHub license](https://img.shields.io/github/license/fedeb87/AlarMe)](https://github.com/fedeb87/AlarMe/blob/master/LICENSE)

# AlarMeApp

AlarMe is an application that allows you to set alarms with several configurations and personalizations. It allows you to set the horoscope, the daily weather, a realtime speaker and much more.
This app are using many tools and libraries designed to build robust and testable software all together. Also it follow the mvvm architecture, implement RXJava to do asynchronous work, and are fully tested.

## Tech Stack

This project uses [feature modularization architecture](https://proandroiddev.com/intro-to-app-modularization-42411e4c421e) and uses MVVM as software design patter.

## Before you start
This project requires the following

 1. Android Studio 4.2 (stable channel) or higher.
 2. Android SDK 21 or above.
 3. Android SDK build tools 21.0.0 or above.

## Screenshots
The screenshot below shows how the app looks like when it is done.

![](https://i.imgur.com/efNJfuy.png =300x600)   ![Imgur](https://i.imgur.com/ylbZn98.png =300x600)   ![Imgur](https://i.imgur.com/bjWF954.png =300x600)   ![Imgur](https://i.imgur.com/MctQFIE.png =300x600)   ![Imgur](https://i.imgur.com/h1DA11X.png =300x600)

### Libraries

 - Application entirely written in Java.
 - Asynchronous processing using [RXJava](https://reactivex.io/).
 - [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) to schedule alarms and notifications.
 - [Room](https://dagger.dev/) for local data storage.
 - Uses [JUnit4](https://developer.android.com/training/testing/junit-rules), [Espresso](https://developer.android.com/training/testing/espresso), [Robolectric](http://robolectric.org/) among other libraries for unit & instrumented tests.

## Notes
I consumes different APIs to expose several the application's features. Like realtime weather and daily horoscope. Therefore, these functionalities are directly linked to the availability of the corresponding APIs.

On the other hand, you have total freedom to extend the current functionalities and help me to solve any bug that you find :)

## 📃 License

```
Copyright 2022 Federico Beron

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