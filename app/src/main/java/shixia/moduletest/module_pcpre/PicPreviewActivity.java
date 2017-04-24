package shixia.moduletest.module_pcpre;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import shixia.moduletest.R;

public class PicPreviewActivity extends AppCompatActivity
{
	private ViewPager mViewPager;
	private int[] mImgs = new int[] { R.drawable.tbug, R.drawable.a,
			R.drawable.xx };
	private ImageView[] mImageViews = new ImageView[mImgs.length];

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.vp);
		
		mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
		mViewPager.setAdapter(new PagerAdapter()
		{

			@Override
			public Object instantiateItem(ViewGroup container, int position)
			{
				ZoomImageView imageView = new ZoomImageView(getApplicationContext());
				imageView.setImageResource(mImgs[position]);
				container.addView(imageView);
				mImageViews[position] = imageView;
				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object)
			{
				container.removeView(mImageViews[position]);
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1)
			{
				return arg0 == arg1;
			}

			@Override
			public int getCount()
			{
				return mImgs.length;
			}
		});

	}
}
