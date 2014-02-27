package ml.game.android.SaveNewton;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.*;

public class ADManager {
	private static class HideADAction implements Runnable{
		private AdView _adView;
		public HideADAction(AdView adView){
			_adView = adView;
		}
		@Override
		public void run() {
			_adView.setVisibility(View.GONE);
		}
	}
	
	private static AdRequest buildRequest(Context context){
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		String keyWordStr = context.getString(R.string.ad_keywords);
		String[] keyWords = keyWordStr.split(",");
		for(int i=0;i<keyWords.length;i++){
			adRequestBuilder.addKeyword(keyWords[i]);
		}
		return adRequestBuilder.build();
	}
	
	public static AdView loadAD(Context context, ViewGroup adContainer){
		if (DataAccess.GDShowAD<1){
			return null;
		}
		AdView adView = new AdView(context);
		adView.setAdUnitId(context.getString(R.string.adUnitID));
		adView.setAdSize(AdSize.BANNER);
		RelativeLayout.LayoutParams adViewLy =  new RelativeLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	    adViewLy.addRule(RelativeLayout.ALIGN_PARENT_TOP);
	    adViewLy.addRule(RelativeLayout.CENTER_HORIZONTAL);
	    adContainer.addView(adView, adViewLy);
		adView.loadAd(buildRequest(context));
		return adView;
	}
	
	public static AdView loadAD(Activity activity, int adViewID){
		if (DataAccess.GDShowAD<1){
			return null;
		}
		AdView adView = (AdView)activity.findViewById(adViewID);
		adView.loadAd(buildRequest(activity));
		return adView;
	}
	
	public static void hideAdDelay(final AdView adView, int delaySeconds){
		if (DataAccess.GDShowAD<1){
			return;
		}
		HideADAction hideAction = new HideADAction(adView);
		adView.setTag(hideAction);
		adView.postDelayed(hideAction, delaySeconds * 1000);
	}
	
	public static void refreshAD(AdView adView){
		if (DataAccess.GDShowAD<1 || adView==null){
			return;
		}
		Object tagAction = adView.getTag();
		if (tagAction!=null){
			HideADAction hideAction = (HideADAction)tagAction;
			adView.removeCallbacks(hideAction);
			hideAction = null;
		}
		adView.loadAd(buildRequest(adView.getContext()));
		adView.setVisibility(View.VISIBLE);
	}
}
