package com.trixsearch.hasikit.telegram.data.session;

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
public final class TelegramSessionManager_Factory implements Factory<TelegramSessionManager> {
  private final Provider<Context> contextProvider;

  private TelegramSessionManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public TelegramSessionManager get() {
    return newInstance(contextProvider.get());
  }

  public static TelegramSessionManager_Factory create(Provider<Context> contextProvider) {
    return new TelegramSessionManager_Factory(contextProvider);
  }

  public static TelegramSessionManager newInstance(Context context) {
    return new TelegramSessionManager(context);
  }
}
