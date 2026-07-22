package com.trixsearch.hasikit.telegram.data.repository;

import android.content.Context;
import com.trixsearch.hasikit.telegram.data.session.TelegramSessionManager;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
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
public final class TelegramAuthRepositoryImpl_Factory implements Factory<TelegramAuthRepositoryImpl> {
  private final Provider<Context> contextProvider;

  private final Provider<TelegramClientService> clientServiceProvider;

  private final Provider<TelegramSessionManager> sessionManagerProvider;

  private TelegramAuthRepositoryImpl_Factory(Provider<Context> contextProvider,
      Provider<TelegramClientService> clientServiceProvider,
      Provider<TelegramSessionManager> sessionManagerProvider) {
    this.contextProvider = contextProvider;
    this.clientServiceProvider = clientServiceProvider;
    this.sessionManagerProvider = sessionManagerProvider;
  }

  @Override
  public TelegramAuthRepositoryImpl get() {
    return newInstance(contextProvider.get(), clientServiceProvider.get(), sessionManagerProvider.get());
  }

  public static TelegramAuthRepositoryImpl_Factory create(Provider<Context> contextProvider,
      Provider<TelegramClientService> clientServiceProvider,
      Provider<TelegramSessionManager> sessionManagerProvider) {
    return new TelegramAuthRepositoryImpl_Factory(contextProvider, clientServiceProvider, sessionManagerProvider);
  }

  public static TelegramAuthRepositoryImpl newInstance(Context context,
      TelegramClientService clientService, TelegramSessionManager sessionManager) {
    return new TelegramAuthRepositoryImpl(context, clientService, sessionManager);
  }
}
