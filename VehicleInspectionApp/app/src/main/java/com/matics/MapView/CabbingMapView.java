package com.matics.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;

public class CabbingMapView extends MapView {

	public CabbingMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CabbingMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CabbingMapView(Context context, GoogleMapOptions options) {
		super(context, options);
		// TODO Auto-generated constructor stub
	}

	public CabbingMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
//		switch (action) {
//		case MotionEvent.ACTION_DOWN:
//			// Disallow ScrollView to intercept touch events.
//			this.getParent().requestDisallowInterceptTouchEvent(true);
//			break;
//		case MotionEvent.ACTION_UP:
//		case MotionEvent.ACTION_CANCEL:
//			this.getParent().requestDisallowInterceptTouchEvent(false);
//			break;
//		}

		// Handle MapView's touch events.
		super.onTouchEvent(ev);
		return true;
	}

}
