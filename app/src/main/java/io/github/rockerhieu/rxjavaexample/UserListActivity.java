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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import io.github.rockerhieu.rxjavaexample.data.UserCompositeRepository;
import io.github.rockerhieu.rxjavaexample.data.entity.User;
import io.github.rockerhieu.rxjavaexample.util.ToastUtil;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserListActivity extends RxAppCompatActivity {

  @Inject UserCompositeRepository userCompositeRepository;
  @BindView(android.R.id.list) ListView vListView;
  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout vSwipeRefreshLayout;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_list_activity);
    ButterKnife.bind(this);
    ((AndroidApplication) getApplication()).getDataComponent().inject(this);
  }

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    vSwipeRefreshLayout.setOnRefreshListener(() -> getUsers());
    vListView.setOnItemClickListener((parent, view, position, id) -> startActivity(
        UserActivity.getCallingIntent(this,
            ((User) parent.getAdapter().getItem(position)).getId())));
    getUsers();
  }

  private void getUsers() {
    vSwipeRefreshLayout.setRefreshing(true);
    userCompositeRepository.getUsers()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(ActivityEvent.DESTROY))
        .doOnCompleted(() -> vSwipeRefreshLayout.setRefreshing(false))
        .subscribe(users -> vListView.setAdapter(
            new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users)), e -> {
          ToastUtil.shortToast(this, "Error: " + e.getMessage());
          vSwipeRefreshLayout.setRefreshing(false);
        });
  }
}
