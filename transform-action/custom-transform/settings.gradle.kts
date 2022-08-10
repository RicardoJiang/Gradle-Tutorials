pluginManagement {
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
        maven { setUrl("https://maven.zhenguanyu.com/content/repositories/releases") }
        maven { setUrl("https://maven.zhenguanyu.com/content/repositories/snapshots") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)
    repositories {
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/google") }
        maven { setUrl("https://maven.zhenguanyu.com/content/repositories/releases") }
        maven { setUrl("https://maven.zhenguanyu.com/content/repositories/snapshots") }
    }
}

rootProject.name = "transform-action"