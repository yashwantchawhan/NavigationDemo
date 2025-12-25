package com.example.navigator.hilt;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata("dagger.hilt.android.scopes.ActivityScoped")
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
public final class ActivityNavigatorModule_ProvideNavigatorControllersFactory implements Factory<NavigatorControllers> {
  @Override
  public NavigatorControllers get() {
    return provideNavigatorControllers();
  }

  public static ActivityNavigatorModule_ProvideNavigatorControllersFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static NavigatorControllers provideNavigatorControllers() {
    return Preconditions.checkNotNullFromProvides(ActivityNavigatorModule.INSTANCE.provideNavigatorControllers());
  }

  private static final class InstanceHolder {
    private static final ActivityNavigatorModule_ProvideNavigatorControllersFactory INSTANCE = new ActivityNavigatorModule_ProvideNavigatorControllersFactory();
  }
}
