package com.zj.android.transform.action

import org.gradle.api.Plugin
import org.gradle.api.Project

class TransformActionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("使用模板创建的插件")
    }
}