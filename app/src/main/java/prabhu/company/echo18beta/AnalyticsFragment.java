package prabhu.company.echo18beta;


import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyticsFragment extends Fragment {


    public AnalyticsFragment() {
        // Required empty public constructor
    }


    String url = "",gum="";
    WebView webView;
    String strtext,strtext2,gum2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_analytics, container,
                false);

        //FirebaseDatabase.getInstance().getReference().child("misc").child("url").setValue("file:///assets/SIH2018/index.html");
        //url="https://ranjanbalappa.github.io/SIH2018/";

        //url="file:///assets/SIH2018/index.html";


        if ( !isNetworkAvailable() ) {
            Toast.makeText(getActivity(), "No Internet!", Toast.LENGTH_SHORT).show();
            //Snackbar.make(coordinatorLayout , "No Internet!", Snackbar.LENGTH_SHORT).show();

        }

        webView = (WebView) view.findViewById(R.id.webView);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        //webView.getSettings().setSupportZoom(true);
        //webView.getSettings().setBuiltInZoomControls(true);
        //webView.getSettings().setDisplayZoomControls(false);

        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setJavaScriptEnabled(true);

        FirebaseDatabase.getInstance().getReference().child("misc").child("url").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                url=dataSnapshot.getValue().toString();
                webView.loadUrl(url);
                //Toast.makeText(getActivity(),url,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        webView.loadUrl(url);

        webView.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
                    if (webView.getUrl() == url){
                        return false;
                    }
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });



        return view;
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService( Activity.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap
                favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String
                url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url)
        {
            super.onPageFinished(view, url);
        }
    }
}
