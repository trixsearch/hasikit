package com.trixsearch.hasikit.ui.screens.settings;

import com.trixsearch.hasikit.util.LanguageManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
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
public final class LanguageViewModel_Factory implements Factory<LanguageViewModel> {
  private final Provider<LanguageManager> languageManagerProvider;

  private LanguageViewModel_Factory(Provider<LanguageManager> languageManagerProvider) {
    this.languageManagerProvider = languageManagerProvider;
  }

  @Override
  public LanguageViewModel get() {
    return newInstance(languageManagerProvider.get());
  }

  public static LanguageViewModel_Factory create(
      Provider<LanguageManager> languageManagerProvider) {
    return new LanguageViewModel_Factory(languageManagerProvider);
  }

  public static LanguageViewModel newInstance(LanguageManager languageManager) {
    return new LanguageViewModel(languageManager);
  }
}
