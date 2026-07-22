package com.trixsearch.hasikit.ui.screens.auth;

import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<TelegramAuthRepository> authRepositoryProvider;

  private AuthViewModel_Factory(Provider<TelegramAuthRepository> authRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get());
  }

  public static AuthViewModel_Factory create(
      Provider<TelegramAuthRepository> authRepositoryProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider);
  }

  public static AuthViewModel newInstance(TelegramAuthRepository authRepository) {
    return new AuthViewModel(authRepository);
  }
}
