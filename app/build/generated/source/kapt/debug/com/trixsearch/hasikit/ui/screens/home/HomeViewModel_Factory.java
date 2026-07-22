package com.trixsearch.hasikit.ui.screens.home;

import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<HasikitDownloadManager> downloadManagerProvider;

  private final Provider<TelegramChannelRepository> channelRepositoryProvider;

  private final Provider<TelegramAuthRepository> authRepositoryProvider;

  private final Provider<TelegramSourceConfig> sourceConfigProvider;

  private HomeViewModel_Factory(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramAuthRepository> authRepositoryProvider,
      Provider<TelegramSourceConfig> sourceConfigProvider) {
    this.repositoryProvider = repositoryProvider;
    this.downloadManagerProvider = downloadManagerProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.sourceConfigProvider = sourceConfigProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(repositoryProvider.get(), downloadManagerProvider.get(), channelRepositoryProvider.get(), authRepositoryProvider.get(), sourceConfigProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramAuthRepository> authRepositoryProvider,
      Provider<TelegramSourceConfig> sourceConfigProvider) {
    return new HomeViewModel_Factory(repositoryProvider, downloadManagerProvider, channelRepositoryProvider, authRepositoryProvider, sourceConfigProvider);
  }

  public static HomeViewModel newInstance(VideoRepository repository,
      HasikitDownloadManager downloadManager, TelegramChannelRepository channelRepository,
      TelegramAuthRepository authRepository, TelegramSourceConfig sourceConfig) {
    return new HomeViewModel(repository, downloadManager, channelRepository, authRepository, sourceConfig);
  }
}
