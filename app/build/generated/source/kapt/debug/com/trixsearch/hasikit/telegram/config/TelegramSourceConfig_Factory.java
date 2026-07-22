package com.trixsearch.hasikit.telegram.config;

import android.content.Context;
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
public final class TelegramSourceConfig_Factory implements Factory<TelegramSourceConfig> {
  private final Provider<Context> contextProvider;

  private TelegramSourceConfig_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TelegramSourceConfig get() {
    return newInstance(contextProvider.get());
  }

  public static TelegramSourceConfig_Factory create(Provider<Context> contextProvider) {
    return new TelegramSourceConfig_Factory(contextProvider);
  }

  public static TelegramSourceConfig newInstance(Context context) {
    return new TelegramSourceConfig(context);
  }
}
