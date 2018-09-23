package com.mattfein.iamcp;


import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment {


    public Events() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);
        String eventURL = "https://www.iamcp-us.org/events/event_list.asp";
        WebView view = (WebView) rootView.findViewById(R.id.eventswebview);
        TypedValue tv = new TypedValue();
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.constraint);
        ViewGroup.MarginLayoutParams layoutParam =  (ViewGroup.MarginLayoutParams)  layout.getLayoutParams();
        if (getActivity().getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
        {
            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());
            layoutParam.setMargins(0, actionBarHeight, 0, 0);
            layout.setLayoutParams(layoutParam);


        }
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(eventURL);


        return rootView;

    }

}
