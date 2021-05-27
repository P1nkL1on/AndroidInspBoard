# Android Kotlin Inspiration Board App

## Видео

Работа приложения 

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/Wb77MXDokv8/0.jpg)](https://www.youtube.com/watch?v=Wb77MXDokv8)

Разбор кода приложения

[![IMAGE ALT TEXT HERE](https://img.youtube.com/vi/uZoVA13Y2os/0.jpg)](https://www.youtube.com/watch?v=uZoVA13Y2os)

## Использованные библиотеки и сервисы

* [firebase](https://console.firebase.google.com/) для бд и хранения картинок
* [glide v4](https://bumptech.github.io/glide/) для всех пайплайнов с картинками
* [BottomNavigationViewEx](https://github.com/ittianyu/BottomNavigationViewEx) для нижнего тулбара
* [CircleImageView](https://github.com/hdodenhof/CircleImageView) для круглых ImageView

## Ссылка на APK

Можно скачать с [Google Disk'a](https://drive.google.com/file/d/1ms0Hud_zd0nHrbItXFoSMndbmWUQN9vG/view?usp=sharing), архив без пароля содержит `insp-board-app-debug.apk` 

Исользовался [ProGuard](https://developer.android.com/studio/build/shrink-code):
```gradle
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
```
