/*
 * Copyright (c) 2016 Hieu Rocker
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

package io.github.rockerhieu.rxjavaexample.di;

import android.app.Application;
import android.util.LruCache;
import dagger.Module;
import dagger.Provides;
import io.github.rockerhieu.rxjavaexample.data.UserRepository;
import io.github.rockerhieu.rxjavaexample.data.db.UserDbRepository;
import io.github.rockerhieu.rxjavaexample.data.entity.User;
import io.github.rockerhieu.rxjavaexample.data.http.UserApi;
import io.github.rockerhieu.rxjavaexample.data.http.UserRestRepository;
import io.github.rockerhieu.rxjavaexample.data.memory.UserMemoryRepository;
import javax.inject.Named;
import javax.inject.Singleton;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module public class DataModule {
  String mBaseUrl;

  // Constructor needs one parameter to instantiate.
  public DataModule(String baseUrl) {
    this.mBaseUrl = baseUrl;
  }

  @Provides @Singleton Cache providesOkHttpCache(Application application) {
    int cacheSize = 10 * 1024 * 1024; // 10 MiB
    Cache cache = new Cache(application.getCacheDir(), cacheSize);
    return cache;
  }

  @Provides @Singleton OkHttpClient providesOkHttpClient(Cache cache) {
    return new OkHttpClient.Builder().cache(cache).build();
  }

  @Provides @Singleton Retrofit providesRetrofit(OkHttpClient okHttpClient) {
    Retrofit retrofit = new Retrofit.Builder()
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .baseUrl(mBaseUrl)
        .client(okHttpClient)
        .build();
    return retrofit;
  }

  @Provides @Singleton UserApi providesUserApi(Retrofit retrofit) {
    return retrofit.create(UserApi.class);
  }

  @Provides @Singleton @Named("rest") UserRepository providesUserRestRepository(UserApi userApi) {
    return new UserRestRepository(userApi);
  }

  @Provides @Singleton @Named("db") UserRepository providesUserDbRepository() {
    return new UserDbRepository();
  }

  @Provides @Singleton @Named("memory") UserRepository providesUserMemoryRepository(
      LruCache<Integer, User> cache) {
    return new UserMemoryRepository(cache);
  }

  @Provides @Singleton LruCache<Integer, User> providesMemoryCache() {
    return new LruCache<>(5);
  }
}