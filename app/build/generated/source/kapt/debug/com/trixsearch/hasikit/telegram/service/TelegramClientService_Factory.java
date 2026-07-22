package com.trixsearch.hasikit.telegram.service;

import android.content.Context;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class TelegramClientService_Factory implements Factory<TelegramClientService> {
  private final Provider<Context> contextProvider;

  private final Provider<TelegramSourceConfig> configProvider;

  private TelegramClientService_Factory(Provider<Context> contextProvider,
      Provider<TelegramSourceConfig> configProvider) {
    this.contextProvider = contextProvider;
    this.configProvider = configProvider;
  }

  @Override
  public TelegramClientService get() {
    return newInstance(contextProvider.get(), configProvider.get());
  }

  public static TelegramClientService_Factory create(Provider<Context> contextProvider,
      Provider<TelegramSourceConfig> configProvider) {
    return new TelegramClientService_Factory(contextProvider, configProvider);
  }

  public static TelegramClientService newInstance(Context context, TelegramSourceConfig config) {
    return new TelegramClientService(context, config);
  }
}
