package com.trixsearch.hasikit.telegram.data.repository;

import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TelegramMediaRepositoryImpl_Factory implements Factory<TelegramMediaRepositoryImpl> {
  private final Provider<TelegramClientService> clientServiceProvider;

  private final Provider<TelegramSourceConfig> configProvider;

  private TelegramMediaRepositoryImpl_Factory(Provider<TelegramClientService> clientServiceProvider,
      Provider<TelegramSourceConfig> configProvider) {
    this.clientServiceProvider = clientServiceProvider;
    this.configProvider = configProvider;
  }

  @Override
  public TelegramMediaRepositoryImpl get() {
    return newInstance(clientServiceProvider.get(), configProvider.get());
  }

  public static TelegramMediaRepositoryImpl_Factory create(
      Provider<TelegramClientService> clientServiceProvider,
      Provider<TelegramSourceConfig> configProvider) {
    return new TelegramMediaRepositoryImpl_Factory(clientServiceProvider, configProvider);
  }

  public static TelegramMediaRepositoryImpl newInstance(TelegramClientService clientService,
      TelegramSourceConfig config) {
    return new TelegramMediaRepositoryImpl(clientService, config);
  }
}
