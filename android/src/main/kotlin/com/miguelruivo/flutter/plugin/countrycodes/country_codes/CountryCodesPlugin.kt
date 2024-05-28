package com.miguelruivo.flutter.plugin.countrycodes.country_codes

import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.Locale

/** CountryCodesPlugin */
class CountryCodesPlugin : FlutterPlugin, MethodCallHandler {
  private lateinit var channel: MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "country_codes")
    channel.setMethodCallHandler(this)
  }

  companion object {
    @JvmStatic
    fun registerWith(registrar: Registrar) {
      val channel = MethodChannel(registrar.messenger(), "country_codes")
      channel.setMethodCallHandler(CountryCodesPlugin())
    }
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {
      "getLocale" -> result.success(listOf(Locale.getDefault().language, Locale.getDefault().country, getLocalizedCountryNames(call.arguments as? String)))
      "getRegion" -> result.success(Locale.getDefault().country)
      "getLanguage" -> result.success(Locale.getDefault().language)
      else -> result.notImplemented()
    }
  }

  private fun getLocalizedCountryNames(localeTag: String?): HashMap<String, String> {
    val localizedCountries = HashMap<String, String>()
    val deviceCountry = Locale.getDefault().toLanguageTag()

    for (countryCode in Locale.getISOCountries()) {
      val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        Locale.forLanguageTag(localeTag ?: deviceCountry)
      } else {
        Locale(localeTag ?: deviceCountry, countryCode)
      }
      val countryName = locale.getDisplayCountry(Locale.forLanguageTag(localeTag ?: deviceCountry))
      localizedCountries[countryCode.uppercase()] = countryName ?: ""
    }
    return localizedCountries
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
