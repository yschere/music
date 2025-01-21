/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.music

import android.app.Application
//import coil.ImageLoader
//import coil.ImageLoaderFactory
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.util.DebugLogger
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application which sets up our dependency [Graph] with a context.
 */
/*
@HiltAndroidApp
class MusicApplication : Application(){//, ImageLoaderFactory {

    //@Inject lateinit var imageLoader: ImageLoader

    //override fun newImageLoader(): ImageLoader = imageLoader
}*/

/*
/Users/cherre/AndroidStudioProjects/music/app/build/generated/hilt/component_sources/debug/com/example/music/MusicApplication_HiltComponents.java:142: error: [Dagger/MissingBinding] coil.ImageLoader cannot be provided without an @Provides-annotated method.
  public abstract static class SingletonC implements MusicApplication_GeneratedInjector,
                         ^

      coil.ImageLoader is injected at
          [com.example.music.MusicApplication_HiltComponents.SingletonC] com.example.music.MusicApplication.imageLoader
      com.example.music.MusicApplication is injected at
          [com.example.music.MusicApplication_HiltComponents.SingletonC] com.example.music.MusicApplication_GeneratedInjector.injectMusicApplication(com.example.music.MusicApplication)

addressed this by updating all imports to use coil3
changed MusicApplication to use SingletonImageLoader.Factory and removed the lateinit var for imageLoader to just use direct override fun imageLoader constructor
 */

@HiltAndroidApp
class MusicApplication : Application(), SingletonImageLoader.Factory {

    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader(context)
            .newBuilder()
            .logger(DebugLogger())
            .build()
}
