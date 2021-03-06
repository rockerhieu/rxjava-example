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

package io.github.rockerhieu.rxjavaexample.data;

import io.github.rockerhieu.rxjavaexample.data.entity.User;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import rx.Observable;

/**
 * Created by rockerhieu on 9/24/16.
 */

@Singleton public class UserCompositeRepository implements UserRepository {
  UserRepository restApi;
  UserRepository dbApi;
  UserRepository memoryApi;

  @Inject public UserCompositeRepository(@Named("rest") UserRepository restApi,
      @Named("db") UserRepository dbApi, @Named("memory") UserRepository memoryApi) {
    this.restApi = restApi;
    this.dbApi = dbApi;
    this.memoryApi = memoryApi;
  }

  @Override public Observable<List<User>> getUsers() {
    return Observable.merge(memoryApi.getUsers(), restApi.getUsers())
        .filter(list -> !list.isEmpty())
        .doOnNext(list -> {
          for (User user : list) memoryApi.saveUser(user);
        })
        .first();
  }

  @Override public Observable<User> getUser(int userId) {
    return Observable.merge(
        restApi.getUser(userId).doOnNext(user -> memoryApi.saveUser(user)),
        memoryApi.getUser(userId)).first();
  }

  @Override public Observable<User> saveUser(User user) {
    throw new UnsupportedOperationException();
  }
}
