package com.example.galaxycoffie3;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

/**
 * the adapter for the viewPager in which we set the viewpager images and instantiate the
 * actual viewpager
 */
class ViewPagerAdapter extends PagerAdapter {

    /*=============================================================================*/
    /*                                Members                                      */
    /*=============================================================================*/

    /* the context of the activity using the adapter*/
    private Context mContext;
    /* the adapter images*/
    private int[] images;
    private int state;

    /*=============================================================================*/
    /*                                  Other                                      */
    /*=============================================================================*/

    ViewPagerAdapter(Context context, int[] array, int state) {
        this.mContext = context;
        images = array;
        this.state = state;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert layoutInflater != null;
        @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.custom_layout, null);

        // not replaced
        final ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* empty, here we can add click action to our slider*/
            }
        });


        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}