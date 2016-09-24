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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.squareup.phrase.Phrase;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import io.github.rockerhieu.rxjavaexample.data.UserCompositeRepository;
import io.github.rockerhieu.rxjavaexample.data.entity.User;
import io.github.rockerhieu.rxjavaexample.util.ToastUtil;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UserActivity extends RxAppCompatActivity {
  public static final String EXTRA_USER_ID = "user_id";

  public static Intent getCallingIntent(Context context, int userId) {
    Intent intent = new Intent(context, UserActivity.class);
    intent.putExtra(EXTRA_USER_ID, userId);
    return intent;
  }

  private int xUserId;

  @BindView(R.id.swipe_refresh_layout) SwipeRefreshLayout vSwipeRefreshLayout;
  @BindView(R.id.id) TextView vId;
  @BindView(R.id.fullname) TextView vFullname;
  @BindView(R.id.followers) TextView vFollowers;

  @Inject UserCompositeRepository userCompositeRepository;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.user_activity);
    ButterKnife.bind(this);
    xUserId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
    ((AndroidApplication) getApplication()).getDataComponent().inject(this);
  }

  @Override protected void onPostCreate(@Nullable Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    vSwipeRefreshLayout.setOnRefreshListener(() -> getUser(xUserId));
    getUser(xUserId);
  }

  private void getUser(int userId) {
    vSwipeRefreshLayout.setRefreshing(true);
    userCompositeRepository.getUser(userId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .compose(bindUntilEvent(ActivityEvent.DESTROY))
        .doOnCompleted(() -> vSwipeRefreshLayout.setRefreshing(false))
        .subscribe(user -> renderUser(user), e -> {
          ToastUtil.shortToast(this, "Error: " + e.getMessage());
          vSwipeRefreshLayout.setRefreshing(false);
        });
  }

  private void renderUser(User user) {
    vId.setText(Phrase.from(getString(R.string.user_id)).put("id", user.getId()).format());

    vFullname.setText(Phrase.from(getString(R.string.user_fullname))
        .put("fullname", user.getFullName())
        .format());

    vFollowers.setText(Phrase.from(getString(R.string.user_followers))
        .put("followers", user.getFollowers())
        .format());
  }
}
