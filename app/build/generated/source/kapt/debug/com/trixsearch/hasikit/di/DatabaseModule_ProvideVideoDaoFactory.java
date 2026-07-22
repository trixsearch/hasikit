package com.trixsearch.hasikit.di;

import com.trixsearch.hasikit.data.local.HasikitDatabase;
import com.trixsearch.hasikit.data.local.dao.VideoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideVideoDaoFactory implements Factory<VideoDao> {
  private final Provider<HasikitDatabase> databaseProvider;

  private DatabaseModule_ProvideVideoDaoFactory(Provider<HasikitDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public VideoDao get() {
    return provideVideoDao(databaseProvider.get());
  }

  public static DatabaseModule_ProvideVideoDaoFactory create(
      Provider<HasikitDatabase> databaseProvider) {
    return new DatabaseModule_ProvideVideoDaoFactory(databaseProvider);
  }

  public static VideoDao provideVideoDao(HasikitDatabase database) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideVideoDao(database));
  }
}
