package com.trixsearch.hasikit;

import androidx.hilt.work.HiltWorkerFactory;
import com.trixsearch.hasikit.telegram.service.TelegramClientService;
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
public final class HasikitApp_MembersInjector implements MembersInjector<HasikitApp> {
  private final Provider<TelegramClientService> telegramClientServiceProvider;

  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private HasikitApp_MembersInjector(Provider<TelegramClientService> telegramClientServiceProvider,
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.telegramClientServiceProvider = telegramClientServiceProvider;
    this.workerFactoryProvider = workerFactoryProvider;
  }

  @Override
  public void injectMembers(HasikitApp instance) {
    injectTelegramClientService(instance, telegramClientServiceProvider.get());
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  public static MembersInjector<HasikitApp> create(
      Provider<TelegramClientService> telegramClientServiceProvider,
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new HasikitApp_MembersInjector(telegramClientServiceProvider, workerFactoryProvider);
  }

  @InjectedFieldSignature("com.trixsearch.hasikit.HasikitApp.telegramClientService")
  public static void injectTelegramClientService(HasikitApp instance,
      TelegramClientService telegramClientService) {
    instance.telegramClientService = telegramClientService;
  }

  @InjectedFieldSignature("com.trixsearch.hasikit.HasikitApp.workerFactory")
  public static void injectWorkerFactory(HasikitApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
