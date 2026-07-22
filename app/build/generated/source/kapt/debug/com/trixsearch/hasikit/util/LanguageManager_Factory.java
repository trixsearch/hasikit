package com.trixsearch.hasikit.util;

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
public final class LanguageManager_Factory implements Factory<LanguageManager> {
  private final Provider<Context> contextProvider;

  private LanguageManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public LanguageManager get() {
    return newInstance(contextProvider.get());
  }

  public static LanguageManager_Factory create(Provider<Context> contextProvider) {
    return new LanguageManager_Factory(contextProvider);
  }

  public static LanguageManager newInstance(Context context) {
    return new LanguageManager(context);
  }
}
