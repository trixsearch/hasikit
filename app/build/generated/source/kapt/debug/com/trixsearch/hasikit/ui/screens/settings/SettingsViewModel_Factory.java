package com.trixsearch.hasikit.ui.screens.settings;

import android.content.Context;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<HasikitDownloadManager> downloadManagerProvider;

  private final Provider<TelegramAuthRepository> telegramAuthRepositoryProvider;

  private final Provider<TelegramSourceConfig> telegramSourceConfigProvider;

  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramAuthRepository> telegramAuthRepositoryProvider,
      Provider<TelegramSourceConfig> telegramSourceConfigProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    this.contextProvider = contextProvider;
    this.repositoryProvider = repositoryProvider;
    this.downloadManagerProvider = downloadManagerProvider;
    this.telegramAuthRepositoryProvider = telegramAuthRepositoryProvider;
    this.telegramSourceConfigProvider = telegramSourceConfigProvider;
    this.telegramClientServiceProvider = telegramClientServiceProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), repositoryProvider.get(), downloadManagerProvider.get(), telegramAuthRepositoryProvider.get(), telegramSourceConfigProvider.get(), telegramClientServiceProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramAuthRepository> telegramAuthRepositoryProvider,
      Provider<TelegramSourceConfig> telegramSourceConfigProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    return new SettingsViewModel_Factory(contextProvider, repositoryProvider, downloadManagerProvider, telegramAuthRepositoryProvider, telegramSourceConfigProvider, telegramClientServiceProvider);
  }

  public static SettingsViewModel newInstance(Context context, VideoRepository repository,
      HasikitDownloadManager downloadManager, TelegramAuthRepository telegramAuthRepository,
      TelegramSourceConfig telegramSourceConfig, TelegramClientService telegramClientService) {
    return new SettingsViewModel(context, repository, downloadManager, telegramAuthRepository, telegramSourceConfig, telegramClientService);
  }
}
