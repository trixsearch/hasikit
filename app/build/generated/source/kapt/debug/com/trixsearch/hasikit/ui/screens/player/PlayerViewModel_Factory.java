package com.trixsearch.hasikit.ui.screens.player;

import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramChannelRepository;
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
public final class PlayerViewModel_Factory implements Factory<PlayerViewModel> {
  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<HasikitDownloadManager> downloadManagerProvider;

  private final Provider<TelegramChannelRepository> channelRepositoryProvider;

  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private PlayerViewModel_Factory(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.downloadManagerProvider = downloadManagerProvider;
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.telegramClientServiceProvider = telegramClientServiceProvider;
  }

  @Override
  public PlayerViewModel get() {
    return newInstance(repositoryProvider.get(), downloadManagerProvider.get(), channelRepositoryProvider.get(), telegramClientServiceProvider.get());
  }

  public static PlayerViewModel_Factory create(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider,
      Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    return new PlayerViewModel_Factory(repositoryProvider, downloadManagerProvider, channelRepositoryProvider, telegramClientServiceProvider);
  }

  public static PlayerViewModel newInstance(VideoRepository repository,
      HasikitDownloadManager downloadManager, TelegramChannelRepository channelRepository,
      TelegramClientService telegramClientService) {
    return new PlayerViewModel(repository, downloadManager, channelRepository, telegramClientService);
  }
}
