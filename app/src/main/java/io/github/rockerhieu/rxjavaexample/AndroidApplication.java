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

package io.github.rockerhieu.rxjavaexample;

import android.app.Application;
import io.github.rockerhieu.rxjavaexample.di.AppModule;
import io.github.rockerhieu.rxjavaexample.di.DaggerDataComponent;
import io.github.rockerhieu.rxjavaexample.di.DataComponent;
import io.github.rockerhieu.rxjavaexample.di.DataModule;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by rockerhieu on 9/24/16.
 */
public class AndroidApplication extends Application {
  private static final String API_HOST = "http://192.168.1.100:3000";
  private DataComponent dataComponent;

  @Override public void onCreate() {
    super.onCreate();
    RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
    Realm.setDefaultConfiguration(realmConfiguration);

    dataComponent = DaggerDataComponent.builder()
        // list of modules that are part of this component need to be created here too
        .appModule(new AppModule(
            this)) // This also corresponds to the name of your module: %component_name%Module
        .dataModule(new DataModule(API_HOST)).build();
  }

  public DataComponent getDataComponent() {
    return dataComponent;
  }
}
