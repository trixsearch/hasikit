package com.trixsearch.hasikit.download;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.trixsearch.hasikit.domain.repository.VideoRepository;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
import dagger.internal.DaggerGenerated;
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
public final class DownloadWorker_Factory {
  private final Provider<VideoRepository> repositoryProvider;

  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private DownloadWorker_Factory(Provider<VideoRepository> repositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    this.repositoryProvider = repositoryProvider;
    this.telegramClientServiceProvider = telegramClientServiceProvider;
  }

  public DownloadWorker get(Context context, WorkerParameters params) {
    return newInstance(context, params, repositoryProvider.get(), telegramClientServiceProvider.get());
  }

  public static DownloadWorker_Factory create(Provider<VideoRepository> repositoryProvider,
      Provider<TelegramClientService> telegramClientServiceProvider) {
    return new DownloadWorker_Factory(repositoryProvider, telegramClientServiceProvider);
  }

  public static DownloadWorker newInstance(Context context, WorkerParameters params,
      VideoRepository repository, TelegramClientService telegramClientService) {
    return new DownloadWorker(context, params, repository, telegramClientService);
  }
}
