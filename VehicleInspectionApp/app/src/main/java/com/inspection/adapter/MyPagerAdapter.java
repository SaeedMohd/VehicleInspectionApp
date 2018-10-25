/**
 *
 */
package com.inspection.adapter;
 
import java.util.List;
 
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
 
/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 * @author mwho
 */
public class MyPagerAdapter extends FragmentPagerAdapter {
 
    private List<Fragment> fragments;
    /**
     * @param fm
     * @param fragments
     */
    public MyPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }
    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
     */
    @Override
    public Fragment getItem(int position) {
        return this.fragments.get(position);
    }
 
    /* (non-Javadoc)
     * @see android.support.v4.view.PagerAdapter#getCount()
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
    
    
}