package com.trixsearch.hasikit.ui.screens.library;

import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.download.HasikitDownloadManager;
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
public final class LibraryViewModel_Factory implements Factory<LibraryViewModel> {
  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<HasikitDownloadManager> downloadManagerProvider;

  private LibraryViewModel_Factory(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.downloadManagerProvider = downloadManagerProvider;
  }

  @Override
  public LibraryViewModel get() {
    return newInstance(repositoryProvider.get(), downloadManagerProvider.get());
  }

  public static LibraryViewModel_Factory create(Provider<VideoRepository> repositoryProvider,
      Provider<HasikitDownloadManager> downloadManagerProvider) {
    return new LibraryViewModel_Factory(repositoryProvider, downloadManagerProvider);
  }

  public static LibraryViewModel newInstance(VideoRepository repository,
      HasikitDownloadManager downloadManager) {
    return new LibraryViewModel(repository, downloadManager);
  }
}
