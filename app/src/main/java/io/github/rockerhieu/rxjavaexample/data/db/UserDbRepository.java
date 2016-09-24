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

package io.github.rockerhieu.rxjavaexample.data.db;

import android.util.Log;
import io.github.rockerhieu.rxjavaexample.data.UserRepository;
import io.github.rockerhieu.rxjavaexample.data.entity.User;
import io.realm.Realm;
import java.util.List;
import rx.Observable;

/**
 * Created by rockerhieu on 9/24/16.
 */
public class UserDbRepository implements UserRepository {
  private static final String TAG = "UserDbRepository";
  Realm realm;

  public UserDbRepository() {
    realm = Realm.getDefaultInstance();
  }

  @Override public Observable<List<User>> getUsers() {
    Log.d(TAG, "getUsers - start");
    return realm.where(User.class)
        .findAllAsync()
        .asObservable()
        .map(result -> realm.copyFromRealm(result))
        .doOnEach(o -> Log.d(TAG, "getUsers - onNext"))
        .doOnError(e -> Log.d(TAG, "getUsers - onError"))
        .doOnCompleted(() -> Log.d(TAG, "getUsers - onComplete"));
  }

  @Override public Observable<User> getUser(int userId) {
    Log.d(TAG, "getUser - start");
    Observable<User> observable =
        realm.where(User.class).equalTo(User.Fields.ID, userId).findFirstAsync().asObservable();
    return observable.doOnEach(o -> Log.d(TAG, "getUser - onNext"))
        .doOnError(e -> Log.d(TAG, "getUser - onError"))
        .doOnCompleted(() -> Log.d(TAG, "getUser - onComplete"));
  }

  @Override public Observable<User> saveUser(User user) {
    Log.d(TAG, "saveUser " + user.getId());
    return Observable.just(user)
        .filter(u -> u != null)
        .doOnNext(u -> realm.insertOrUpdate(u))
        .doOnEach(o -> Log.d(TAG, "saveUser " + user.getId()))
        .doOnError(e -> Log.d(TAG, "saveUser " + user.getId()))
        .doOnCompleted(() -> Log.d(TAG, "saveUser " + user.getId()));
  }
}
