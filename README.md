An android report library that allows to collect the app data like logcat, preferences, databases
and then share it. With this library, when the QA or even the developer detect and issue/bug, he can
provide more information about it that helps to fix it, so we can avoid the "unable to reproduce"
state

By default it creates a zip file that can be shared via email for example.

Please be sure to use this library only on the **development mode** if you dont want violate your
users' privacy.

## How to use

Add maven central to your project build gradle

```gradle
allprojects {
    repositories {
        //...
        mavenCentral()
    }
}
```

Add this to your module build.gradle

```gradle
implementation 'io.github.ohoussein:reportoandroid:1.0.4'
```

and in your manifest file, under the application tag, add

```xml

<provider android:name="androidx.core.content.FileProvider"
    android:authorities="dev.ohoussein.reportoandroid.provider"
    android:exported="false"
    android:grantUriPermissions="true">

    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/reporto_file_provider" />

</provider>
```
This is necessary when you use the default result handler (the ZipResultHandler) in order to share the zip file.

To use all the prebuilt module, you have to create the Reporto in your Application class like this
```kotlin
        Reporto.Builder()
            .addLogcatModule()
            .addLogcatModule(LogcatModule.LogParams(bufferName = LogcatModule.BUFFER_EVENTSLOG))
            .addPreferencesModule()
            .addDatabaseModule()
            .showNotification()
            .create(this)
```

* `showNotification()` : add a notification that create a report when click on
* `addDatabaseModule()` : for add the databases in the report
* `addPreferencesModule()` : for add the preferences in their xml format
* `addLogcatModule()` : add the last device's log
* `addLogcatModule(LogcatModule.LogParams(bufferName = LogcatModule.BUFFER_EVENTSLOG))` : maybe you want the events logs like the activities transition
* `addModule(myModule)` : to add custom report data

The Reports can be customized, for example you can add a screenshot or a http interceptor to your reports data

You can Generate a report manually by calling the report method: `Reporto.instance.report(myActivity)`


## Customisation

When we set showNotification to true, it creates a notification that launches the `ReportActivity`. You can start this activity by yourself with
`startActivity(Intent(context, ReportActivity::class.java))`

By default, Reporto use the `ZipFileHandler` to collects all the data (logs, databases, ...) put them in a zip file and shares it,
so you can send it via e-mail, store it in the device...

This behavior can be customized by implementing the `ResultHandler` and set it in the Reporto Builder like this
```kotlin
        Reporto
            .Builder()
             //...
            .resultHandler(MyCustomHandler())
            .create(this)
```
