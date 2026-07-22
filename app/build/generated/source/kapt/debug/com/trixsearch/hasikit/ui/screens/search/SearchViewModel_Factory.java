package com.trixsearch.hasikit.ui.screens.search;

import com.trixsearch.hasikit.telegram.config.TelegramSourceConfig;
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<TelegramChannelRepository> channelRepositoryProvider;

  private final Provider<TelegramSourceConfig> sourceConfigProvider;

  private SearchViewModel_Factory(Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramSourceConfig> sourceConfigProvider) {
    this.channelRepositoryProvider = channelRepositoryProvider;
    this.sourceConfigProvider = sourceConfigProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(channelRepositoryProvider.get(), sourceConfigProvider.get());
  }

  public static SearchViewModel_Factory create(
      Provider<TelegramChannelRepository> channelRepositoryProvider,
      Provider<TelegramSourceConfig> sourceConfigProvider) {
    return new SearchViewModel_Factory(channelRepositoryProvider, sourceConfigProvider);
  }

  public static SearchViewModel newInstance(TelegramChannelRepository channelRepository,
      TelegramSourceConfig sourceConfig) {
    return new SearchViewModel(channelRepository, sourceConfig);
  }
}
