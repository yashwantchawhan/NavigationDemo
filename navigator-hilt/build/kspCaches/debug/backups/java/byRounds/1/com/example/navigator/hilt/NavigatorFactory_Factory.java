package com.example.navigator.hilt;

import com.example.navigator.api.NavExecutor;
import com.example.navigator.api.NavLogger;
import com.example.navigator.api.RouteRegistry;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata({
    "com.example.navigator.hilt.FragmentExecutor",
    "com.example.navigator.hilt.ComposeExecutor",
    "com.example.navigator.hilt.ActivityExecutor"
})
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
    "cast"
})
public final class NavigatorFactory_Factory implements Factory<NavigatorFactory> {
  private final Provider<RouteRegistry> registryProvider;

  private final Provider<NavLogger> loggerProvider;

  private final Provider<NavExecutor> fragmentExecutorProvider;

  private final Provider<NavExecutor> composeExecutorProvider;

  private final Provider<NavExecutor> activityExecutorProvider;

  public NavigatorFactory_Factory(Provider<RouteRegistry> registryProvider,
      Provider<NavLogger> loggerProvider, Provider<NavExecutor> fragmentExecutorProvider,
      Provider<NavExecutor> composeExecutorProvider,
      Provider<NavExecutor> activityExecutorProvider) {
    this.registryProvider = registryProvider;
    this.loggerProvider = loggerProvider;
    this.fragmentExecutorProvider = fragmentExecutorProvider;
    this.composeExecutorProvider = composeExecutorProvider;
    this.activityExecutorProvider = activityExecutorProvider;
  }

  @Override
  public NavigatorFactory get() {
    return newInstance(registryProvider.get(), loggerProvider.get(), fragmentExecutorProvider.get(), composeExecutorProvider.get(), activityExecutorProvider.get());
  }

  public static NavigatorFactory_Factory create(Provider<RouteRegistry> registryProvider,
      Provider<NavLogger> loggerProvider, Provider<NavExecutor> fragmentExecutorProvider,
      Provider<NavExecutor> composeExecutorProvider,
      Provider<NavExecutor> activityExecutorProvider) {
    return new NavigatorFactory_Factory(registryProvider, loggerProvider, fragmentExecutorProvider, composeExecutorProvider, activityExecutorProvider);
  }

  public static NavigatorFactory newInstance(RouteRegistry registry, NavLogger logger,
      NavExecutor fragmentExecutor, NavExecutor composeExecutor, NavExecutor activityExecutor) {
    return new NavigatorFactory(registry, logger, fragmentExecutor, composeExecutor, activityExecutor);
  }
}
