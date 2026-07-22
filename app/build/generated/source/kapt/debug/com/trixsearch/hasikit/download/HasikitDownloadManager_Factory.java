package com.trixsearch.hasikit.download;

import android.content.Context;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
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
public final class HasikitDownloadManager_Factory implements Factory<HasikitDownloadManager> {
  private final Provider<Context> contextProvider;

  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private HasikitDownloadManager_Factory(Provider<Context> contextProvider,
      Provider<VideoRepository> repositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.telegramClientServiceProvider = telegramClientServiceProvider;
  }

  @Override
  public HasikitDownloadManager get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), telegramClientServiceProvider.get());
  }

  public static HasikitDownloadManager_Factory create(Provider<Context> contextProvider,
      Provider<VideoRepository> repositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    return new HasikitDownloadManager_Factory(contextProvider, repositoryProvider, telegramClientServiceProvider);
  }

  public static HasikitDownloadManager newInstance(Context context, VideoRepository repository,
      TelegramClientService telegramClientService) {
    return new HasikitDownloadManager(context, repository, telegramClientService);
  }
}
