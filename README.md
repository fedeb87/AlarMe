

# SimpleRemindMeApp

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

![](https://imgur.com/efNJfuy)   ![Imgur](https://imgur.com/ylbZn98)   ![Imgur](https://imgur.com/bjWF954)   ![Imgur](https://imgur.com/MctQFIE)   ![Imgur](https://imgur.com/h1DA11X)

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