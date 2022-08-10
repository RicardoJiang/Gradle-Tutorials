plugins {
    id("java-gradle-plugin")
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
}

gradlePlugin {
    plugins {
        //插件名
        register("TransformActionPlugin") {
            id = "com.transform.action"
            implementationClass = "com.zj.android.transform.action.TransformActionPlugin"
        }
    }
}

group = "com.zj.transform.action"
version = "1.0.0"

