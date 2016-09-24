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

package io.github.rockerhieu.rxjavaexample.data.memory;

import android.util.LruCache;
import io.github.rockerhieu.rxjavaexample.data.UserRepository;
import io.github.rockerhieu.rxjavaexample.data.entity.User;
import java.util.List;
import rx.Observable;

/**
 * Created by rockerhieu on 9/24/16.
 */
public class UserMemoryRepository implements UserRepository {
  LruCache<Integer, User> cache;

  public UserMemoryRepository(LruCache<Integer, User> cache) {
    this.cache = cache;
  }

  @Override public Observable<List<User>> getUsers() {
    return Observable.just(cache)
        .map(cache -> cache.snapshot())
        .map(map -> map.values())
        .flatMap(collection -> Observable.from(collection))
        .filter(user -> user != null)
        .sorted((user1, user2) -> (user1.getId() < user2.getId()) ? -1
            : ((user1.getId() == user2.getId()) ? 0 : 1))
        .toList();
  }

  @Override public Observable<User> getUser(int userId) {
    return Observable.just(cache).map(cache -> cache.get(userId)).filter(user -> user != null);
  }
}
