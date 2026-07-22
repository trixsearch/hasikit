package com.trixsearch.hasikit.telegram.data.repository;

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
public final class TelegramChannelRepositoryImpl_Factory implements Factory<TelegramChannelRepositoryImpl> {
  private final Provider<TelegramClientService> clientServiceProvider;

  private TelegramChannelRepositoryImpl_Factory(
      Provider<TelegramClientService> clientServiceProvider) {
    this.clientServiceProvider = clientServiceProvider;
  }

  @Override
  public TelegramChannelRepositoryImpl get() {
    return newInstance(clientServiceProvider.get());
  }

  public static TelegramChannelRepositoryImpl_Factory create(
      Provider<TelegramClientService> clientServiceProvider) {
    return new TelegramChannelRepositoryImpl_Factory(clientServiceProvider);
  }

  public static TelegramChannelRepositoryImpl newInstance(TelegramClientService clientService) {
    return new TelegramChannelRepositoryImpl(clientService);
  }
}
