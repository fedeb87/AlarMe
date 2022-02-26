[![GitHub release](https://img.shields.io/github/v/release/fedeb87/SimpleRemindMe.svg?label=release)](https://github.com/fedeb87/SimpleRemindMe/releases) [![Codacy Badge](https://app.codacy.com/project/badge/Grade/84f9abc9cb294d38aabfcb33a2c90c47)](https://www.codacy.com/gh/fedeb87/SimpleRemindMe/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=fedeb87/SimpleRemindMe&amp;utm_campaign=Badge_Grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/84f9abc9cb294d38aabfcb33a2c90c47)](https://www.codacy.com/gh/fedeb87/SimpleRemindMe/dashboard?utm_source=github.com&utm_medium=referral&utm_content=fedeb87/SimpleRemindMe&utm_campaign=Badge_Coverage) [![GitHub issues](https://img.shields.io/github/issues/fedeb87/SimpleRemindMe?label=issues)](https://github.com/fedeb87/SimpleRemindMe/issues) [![GitHub last commit](https://img.shields.io/github/last-commit/fedeb87/SimpleRemindMe?label=last-commit)](https://github.com/fedeb87/SimpleRemindMe/commits) [![GitHub license](https://img.shields.io/github/license/fedeb87/SimpleRemindMe?label=license)](https://github.com/fedeb87/SimpleRemindMe/blob/master/LICENSE)

# SimpleRemindMeApp

Simple application that allows you to save events or milestones and set alarms for it.
EscuelApp is a simple android application that show how to use many of tools and libraries designed to build robust and testable software all together.
The main idea is to show how to design an app following the mvvm architecture. Implementing RXJava to do asynchronous work. And do UI tests with Espresso.

## Tech Stack

This project uses [feature modularization architecture](https://proandroiddev.com/intro-to-app-modularization-42411e4c421e) and uses MVVM as software design patter.

## Before you start
This project requires the following

 1. Android Studio 4.2 (stable channel) or higher.
 2. Android SDK 23 or above.
 3. Android SDK build tools 23.0.3 or above.

## Screenshots
The screenshot below shows how the app looks like when it is done.

![](https://i.imgur.com/3zxh1y6l.png)   ![Imgur](https://i.imgur.com/ED9sTXKl.png)   ![Imgur](https://i.imgur.com/zldJNrwl.png)   ![Imgur](https://i.imgur.com/6yEWOKSl.png)

### Libraries

 - Application entirely written in Java.
 - Asynchronous processing using [RXJava](https://reactivex.io/).
 - [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) to schedule alarms and notifications.
 - [Room](https://dagger.dev/) for local data storage.
 - Uses [JUnit4](https://developer.android.com/training/testing/junit-rules), [Espresso](https://developer.android.com/training/testing/espresso), [Robolectric](http://robolectric.org/) among other libraries for unit & instrumented tests.

## Notes
The application does not have all the functionalities developed. Since the main idea is to expose a structure on which to later work on extending.
I think it is not necessary to emphasize that the project is open to contributions, improvement proposals and corrections ;)
The notes below describe some features that are not currently being developed that you could help implement.

 1. About screen are not developer
 2. Validations over input forms are not developed
 3. Update created Event are not developed
 4. Following de same structure you can develop the Event types management. Allowing users to define her owns types.

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