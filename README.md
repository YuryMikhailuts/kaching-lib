[![](https://jitpack.io/v/YuryMikhailuts/kaching-lib.svg)](https://jitpack.io/#YuryMikhailuts/kaching-lib)

# KachingLib

Библиотека для Kotlin, посвящённая кешированию объектов. Предназначена, в основном, для уменьшения количества
повторяющихся запросов куда бы то ни было.

<p>
<a rel="license" href="http://creativecommons.org/publicdomain/mark/1.0/">
<img src="https://licensebuttons.net/p/mark/1.0/88x31.png" style="border-style: none;" alt="Public Domain Mark" />
</a>
<br />
Это произведение (<a href="https://gitflic.ru/project/mikhaylutsyury/kaching-lib" rel="dct:creator"><span property="dct:title">KachingLib</span></a>, автор <a href="https://gitflic.ru/user/mikhaylutsyury" rel="dct:creator"><span property="dct:title">Михайлуц Юрий Вячеславович</span></a>), заверенное <a href="https://gitflic.ru/user/mikhaylutsyury" rel="dct:publisher"><span property="dct:title">Михайлуц Юрий Вячеславович</span></a>, свободно от известных лицензионных ограничений.
</p>


## Скачать библиотеку в Maven или Gradle можно из репозитория JitPack:

### groovy
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

### kotlin
```kotlin
allprojects {
	repositories {
		maven("https://jitpack.io")
	}
}
```

## Последняя версия подключается в проект так:

### groovy

```groovy
dependencies {
    // Kotlin multiplatform common
    implementation 'com.github.YuryMikhailuts.kaching-lib:KachingLib:1.0.2-SNAPSHOT'
    // Kotlin multiplatform JS
    implementation 'com.github.YuryMikhailuts.kaching-lib:KachingLib-js:1.0.2-SNAPSHOT'
    // Kotlin multiplatform JVM
    implementation 'com.github.YuryMikhailuts.kaching-lib:KachingLib-jvm:1.0.2-SNAPSHOT'
}
```

### kotlin

```kotlin
dependencies {
    // Kotlin multiplatform common
    implementation("com.github.YuryMikhailuts.kaching-lib:KachingLib:1.0.2-SNAPSHOT")
    // Kotlin multiplatform JS
    implementation("com.github.YuryMikhailuts.kaching-lib:KachingLib-js:1.0.2-SNAPSHOT")
    // Kotlin multiplatform JVM
    implementation("com.github.YuryMikhailuts.kaching-lib:KachingLib-jvm:1.0.2-SNAPSHOT")
}
```
