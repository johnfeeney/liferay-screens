package com.liferay.mobile.screens.bankofwesteros.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.View;
import android.widget.Button;

import com.liferay.mobile.screens.bankofwesteros.R;
import com.liferay.mobile.screens.bankofwesteros.fragments.TourPageFragment;

/**
 * @author Javier Gamarra
 */
public class TourActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

	public static final String TOUR_VISITED = "TOUR_VISITED";
	public static final String WESTEROS_PREFERENCES = "WESTEROS_PREFERENCES";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour);

		_tourButton = (Button) findViewById(R.id.tour_button);
		_tourButton.setOnClickListener(this);

		_viewPager = (ViewPager) findViewById(R.id.pager);
		_viewPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
		_viewPager.setOnPageChangeListener(this);
	}

	@Override
	public void onClick(View v) {
		boolean lastPage = _viewPager.getCurrentItem() == NUM_PAGES - 1;

		if (lastPage) {
			SharedPreferences preferences = getSharedPreferences(WESTEROS_PREFERENCES, MODE_PRIVATE);
			preferences.edit().putBoolean(TOUR_VISITED, true).apply();

			startActivity(new Intent(this, MainActivity.class));
		}
		else {
			_viewPager.setCurrentItem(_viewPager.getCurrentItem() + 1, true);
		}
	}

	@Override
	public void onBackPressed() {
		if (_viewPager.getCurrentItem() == 0) {
			super.onBackPressed();
		}
		else {
			_viewPager.setCurrentItem(_viewPager.getCurrentItem() - 1);
		}
	}

	@Override
	public void onPageSelected(int position) {
		if (position == NUM_PAGES - 1) {
			_tourButton.setText(getString(R.string.start));
		}
	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

	}
	private ViewPager _viewPager;
	private Button _tourButton;
	private static final int NUM_PAGES = 3;

	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TourPageFragment.newInstance(getLayout(position));
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}

		@Override
		public CharSequence getPageTitle(int position) {

			int drawableId = _viewPager.getCurrentItem() == position ? R.drawable.pagination_on : R.drawable.pagination_off;
			Drawable drawable = getResources().getDrawable(drawableId);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
			ImageSpan imageSpan = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);

			SpannableStringBuilder ssb = new SpannableStringBuilder(" ");
			ssb.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			return ssb;
		}

		private int getLayout(int position) {
			if (position == 0) {
				return R.layout.tour0;
			}
			else if (position == 1) {
				return R.layout.tour1;
			}
			else {
				return R.layout.tour2;
			}
		}
	}

}
