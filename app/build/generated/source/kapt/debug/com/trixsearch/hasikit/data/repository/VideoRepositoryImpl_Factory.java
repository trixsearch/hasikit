package com.trixsearch.hasikit.data.repository;

import com.trixsearch.hasikit.data.local.dao.VideoDao;
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
public final class VideoRepositoryImpl_Factory implements Factory<VideoRepositoryImpl> {
  private final Provider<VideoDao> videoDaoProvider;

  private VideoRepositoryImpl_Factory(Provider<VideoDao> videoDaoProvider) {
    this.videoDaoProvider = videoDaoProvider;
  }

  @Override
  public VideoRepositoryImpl get() {
    return newInstance(videoDaoProvider.get());
  }

  public static VideoRepositoryImpl_Factory create(Provider<VideoDao> videoDaoProvider) {
    return new VideoRepositoryImpl_Factory(videoDaoProvider);
  }

  public static VideoRepositoryImpl newInstance(VideoDao videoDao) {
    return new VideoRepositoryImpl(videoDao);
  }
}
