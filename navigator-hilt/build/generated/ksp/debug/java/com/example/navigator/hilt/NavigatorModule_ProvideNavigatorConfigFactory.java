package com.example.navigator.hilt;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("javax.inject.Singleton")
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
    "cast"
})
public final class NavigatorModule_ProvideNavigatorConfigFactory implements Factory<NavigatorConfig> {
  @Override
  public NavigatorConfig get() {
    return provideNavigatorConfig();
  }

  public static NavigatorModule_ProvideNavigatorConfigFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NavigatorConfig provideNavigatorConfig() {
    return Preconditions.checkNotNullFromProvides(NavigatorModule.INSTANCE.provideNavigatorConfig());
  }

  private static final class InstanceHolder {
    private static final NavigatorModule_ProvideNavigatorConfigFactory INSTANCE = new NavigatorModule_ProvideNavigatorConfigFactory();
  }
}
