package com.beixiyo.expofinderdemo

import android.app.Application
import android.content.res.Configuration

import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.ReactHost
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.load
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader

import expo.modules.ApplicationLifecycleDispatcher
import expo.modules.ReactNativeHostWrapper

import com.bytedance.applog.AppLog
import com.bytedance.applog.ILogger
import com.bytedance.applog.InitConfig
import com.bytedance.applog.UriConfig

class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost = ReactNativeHostWrapper(
        this,
        object : DefaultReactNativeHost(this) {
          override fun getPackages(): List<ReactPackage> {
            // Packages that cannot be autolinked yet can be added manually here, for example:
            // packages.add(new MyReactNativePackage());
            return PackageList(this).packages
          }

          override fun getJSMainModuleName(): String = ".expo/.virtual-metro-entry"

          override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

          override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED
          override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
      }
  )

  override val reactHost: ReactHost
    get() = ReactNativeHostWrapper.createReactHost(applicationContext, reactNativeHost)

  override fun onCreate() {
    super.onCreate()
    SoLoader.init(this, false)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      // If you opted-in for the New Architecture, we load the native entry point for this app.
      load()
    }
    ApplicationLifecycleDispatcher.onApplicationCreate(this)
    initDataFinder()
  }

  /**
    * 初始化火山引擎数据埋点 sass-云原生
    */
    private fun initDataFinder() {
      /* 初始化SDK */
      // 第一个参数APPID: 参考2.1节获取
      // 第二个参数CHANNEL: 填写渠道信息，请注意不能为空
      val config = InitConfig("20004290", "test").apply {
          // 设置数据上送地址
          uriConfig = UriConfig.createByDomain("https://gator.volces.com", null)
          // 是否 init 后自动 start 可改为 false，并请在用户授权后调用 start 开启采集
          setAutoStart(false)
          // 全埋点开关，true开启，false关闭
          isAutoTrackEnabled = true
          // true:开启日志，参考4.3节设置logger，false:关闭日志
          isLogEnable = false
      }
      // 加密开关，true开启，false关闭
      AppLog.setEncryptAndCompress(true)
      // 初始化一次即可
      // 在 Applition 中初始化建议使用该方法
      AppLog.init(this, config)
      // 在 Activity 中初始化建议使用该方法
      //AppLog.init(this, config, activity)

      // 请在用户授权后调用如下方法，start 开始实际采集用户信息+上报：
      AppLog.start()
    }

  override fun onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
    ApplicationLifecycleDispatcher.onConfigurationChanged(this, newConfig)
  }
}
