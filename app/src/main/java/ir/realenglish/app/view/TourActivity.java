package ir.realenglish.app.view;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import ir.realenglish.app.R;
import ir.realenglish.app.app.PrefKey;
import ir.realenglish.app.utils.Utils;


public class TourActivity extends AppCompatActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    int pageCount = 5;

    private ViewPager mViewPager;
    ImageButton mNextBtn;
    Button mSkipBtn, mFinishBtn;

    ImageView zero, one, two, three, four;
    ImageView[] indicators;
    int colorUpdate;


    int lastLeftValue = 0;

    CoordinatorLayout mCoordinator;


    int page = 0;   //  to track page position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black_trans80));
        }

        setContentView(R.layout.activity_pager);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mNextBtn = (ImageButton) findViewById(R.id.intro_btn_next);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP)
            mNextBtn.setImageDrawable(
                    Utils.tintMyDrawable(ContextCompat.getDrawable(this, R.drawable.ic_chevron_right_24dp), Color.WHITE)
            );

        mSkipBtn = (Button) findViewById(R.id.intro_btn_skip);
        mFinishBtn = (Button) findViewById(R.id.intro_btn_finish);

        zero = (ImageView) findViewById(R.id.intro_indicator_0);
        one = (ImageView) findViewById(R.id.intro_indicator_1);
        two = (ImageView) findViewById(R.id.intro_indicator_2);
        three = (ImageView) findViewById(R.id.intro_indicator_3);
        four = (ImageView) findViewById(R.id.intro_indicator_4);

        mCoordinator = (CoordinatorLayout) findViewById(R.id.main_content);


        indicators = new ImageView[]{zero, one, two, three, four};

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(page);
        updateIndicators(page);

        final int color1 = ContextCompat.getColor(this, R.color.indigo);
        final int color2 = ContextCompat.getColor(this, R.color.light_blue);
        final int color3 = ContextCompat.getColor(this, R.color.green);
        final int color4 = ContextCompat.getColor(this, R.color.orange);
        final int color5 = ContextCompat.getColor(this, R.color.deep_orange);
        final int color6 = ContextCompat.getColor(this, R.color.blue_grey);

        final int[] colorList = new int[]{color1, color2, color3, color4, color5, color6};

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (Build.VERSION.SDK_INT >= 11) {
                    ArgbEvaluator evaluator = new ArgbEvaluator();
                    colorUpdate = (Integer) evaluator.evaluate(positionOffset, colorList[position], colorList[position == 2 ? position : position + 1]);
                    mViewPager.setBackgroundColor(colorUpdate);
                } else {
                    mViewPager.setBackgroundColor(colorList[position]);
                }
            }

            @Override
            public void onPageSelected(int position) {

                page = position;

                updateIndicators(page);

                switch (position) {
                    case 0:
                        mViewPager.setBackgroundColor(color1);
                        break;
                    case 1:
                        mViewPager.setBackgroundColor(color2);
                        break;
                    case 2:
                        mViewPager.setBackgroundColor(color3);
                        break;
                    case 4:
                        mViewPager.setBackgroundColor(color4);
                        break;
                    case 5:
                        mViewPager.setBackgroundColor(color5);
                        break;
                }


                mNextBtn.setVisibility(position == pageCount - 1 ? View.GONE : View.VISIBLE);
                mFinishBtn.setVisibility(position == pageCount - 1 ? View.VISIBLE : View.GONE);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                mViewPager.setCurrentItem(page, true);
            }
        });

        mSkipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mFinishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefKey.setSeenTour();
                finish();
            }
        });

    }

    void updateIndicators(int position) {
        for (int i = 0; i < pageCount; i++) {
            indicators[i].setBackgroundResource(
                    i == position ? R.drawable.indicator_selected : R.drawable.indicator_unselected
            );
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        ImageView img;

        int[] bgs = new int[]{R.mipmap.tour_a, R.mipmap.tour_b, R.mipmap.tour_c, R.mipmap.tour_d, R.mipmap.tour_e};

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_pager, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            TextView txtDesc = (TextView) rootView.findViewById(R.id.txtDesc);

            String text = "";
            String desc = "";
            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    text = "Real English";
                    desc = "";
                    break;
                case 2:
                    text = "Cool Lessons";
                    desc = "Fun English lessons with persian explanation";
                    break;
                case 3:
                    text = "Quiz";
                    desc = "Test yourself and get score on each lesson";
                    break;
                case 4:
                    text = "Top Users";
                    desc = "You will find users by high scores";
                    break;
                case 5:
                    text = "Chatting";
                    desc = "Improve your writing skills through chatting with users";
                    break;
            }
            textView.setText(text);
            txtDesc.setText(desc);
            img = (ImageView) rootView.findViewById(R.id.section_img);
            img.setBackgroundResource(bgs[getArguments().getInt(ARG_SECTION_NUMBER) - 1]);
            return rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);

        }

        @Override
        public int getCount() {
            return pageCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "title";
        }

    }


}