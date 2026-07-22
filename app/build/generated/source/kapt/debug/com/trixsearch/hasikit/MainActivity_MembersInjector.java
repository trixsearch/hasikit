package com.trixsearch.hasikit;

import com.trixsearch.hasikit.player.HasikitPlayer;
import com.trixsearch.hasikit.telegram.domain.repository.TelegramAuthRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<HasikitPlayer> playerProvider;

  private final Provider<TelegramAuthRepository> telegramAuthRepositoryProvider;

  private MainActivity_MembersInjector(Provider<HasikitPlayer> playerProvider,
      Provider<TelegramAuthRepository> telegramAuthRepositoryProvider) {
    this.playerProvider = playerProvider;
    this.telegramAuthRepositoryProvider = telegramAuthRepositoryProvider;
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectPlayer(instance, playerProvider.get());
    injectTelegramAuthRepository(instance, telegramAuthRepositoryProvider.get());
  }

  public static MembersInjector<MainActivity> create(Provider<HasikitPlayer> playerProvider,
      Provider<TelegramAuthRepository> telegramAuthRepositoryProvider) {
    return new MainActivity_MembersInjector(playerProvider, telegramAuthRepositoryProvider);
  }

  @InjectedFieldSignature("com.trixsearch.hasikit.MainActivity.player")
  public static void injectPlayer(MainActivity instance, HasikitPlayer player) {
    instance.player = player;
  }

  @InjectedFieldSignature("com.trixsearch.hasikit.MainActivity.telegramAuthRepository")
  public static void injectTelegramAuthRepository(MainActivity instance,
      TelegramAuthRepository telegramAuthRepository) {
    instance.telegramAuthRepository = telegramAuthRepository;
  }
}
