package com.trixsearch.hasikit.ui.screens.request;

import com.trixsearch.hasikit.telegram.service.TelegramClientService;
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
public final class RequestContentViewModel_Factory implements Factory<RequestContentViewModel> {
  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private RequestContentViewModel_Factory(
      Provider<TelegramClientService> telegramClientServiceProvider) {
    this.telegramClientServiceProvider = telegramClientServiceProvider;
  }

  @Override
  public RequestContentViewModel get() {
    return newInstance(telegramClientServiceProvider.get());
  }

  public static RequestContentViewModel_Factory create(
      Provider<TelegramClientService> telegramClientServiceProvider) {
    return new RequestContentViewModel_Factory(telegramClientServiceProvider);
  }

  public static RequestContentViewModel newInstance(TelegramClientService telegramClientService) {
    return new RequestContentViewModel(telegramClientService);
  }
}
