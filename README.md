An android report library that allows to collect data like logcat, preferences, databases and then share it,
by default it creates a zip file that can be shared via email for example.

Please be sure to use this library only on the **development mode** if you won't violate your users' privacy.


## How to use

To use all the prebuilt module, you have to create the Reporto in your Application class like this
```kotlin
        Reporto.Builder()
            .addLogcatModule()
            .addLogcatModule(LogcatModule.LogParams(bufferName = LogcatModule.BUFFER_EVENTSLOG))
            .addPreferencesModule()
            .addDatabaseModule()
            .showNotification(true)
            .create(this)
```

`showNotification(true)` : set to true if you want add a notification that create a report when click on
`addDatabaseModule()` : for add the databases in the report
`addPreferencesModule()` : for add the preferences in their xml format
`addLogcatModule()` : add the last device's log
`addLogcatModule(LogcatModule.LogParams(bufferName = LogcatModule.BUFFER_EVENTSLOG))` : maybe you want the events logs like the activities transition

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
