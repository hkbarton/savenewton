package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class actStore extends FragmentActivity{
	private static class ViewStoreItem extends LinearLayout{
		private ImageView mIcon;
		
		public String RelatedGameData;
		public boolean IsSelected = false;
		
		private View.OnClickListener onClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				IsSelected = !IsSelected;
				ViewStoreItem itemView = (ViewStoreItem)v;
				if (IsSelected){
					itemView.mIcon.setBackgroundResource(R.drawable.storeitembg_hover);
				}else{
					itemView.mIcon.setBackgroundResource(R.drawable.storeitembg);
				}
			}
		};
		
		private String getTitleByRelatedGameData(){
			if (RelatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GreenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
				return "Level " + String.valueOf((int)DataAccess.getGameDataByKey(RelatedGameData));
			}else{
				return String.valueOf((int)DataAccess.getGameDataByKey(RelatedGameData));
			}
		}
		
		
		
		public ViewStoreItem(Context context, String relatedGameData) {
			super(context);
			RelatedGameData = relatedGameData;
			// init layout by game data type
			this.setOrientation(LinearLayout.VERTICAL);
			// Title
			TextView txtTitle = new TextView(context);
			LinearLayout.LayoutParams lypTitle = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
					LayoutParams.WRAP_CONTENT);
			lypTitle.topMargin = GameResource.StoreItemTopMargin;
			this.addView(txtTitle, lypTitle);
			txtTitle.setTextColor(Color.YELLOW);
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			txtTitle.setTypeface(null, Typeface.BOLD);
			txtTitle.setGravity(Gravity.CENTER);
			txtTitle.setText(getTitleByRelatedGameData());
			// Icon
			mIcon = new ImageView(context);
			LinearLayout.LayoutParams lyIcon = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
					GameResource.StoreItemBgSize);
			this.addView(mIcon, lyIcon);
			mIcon.setBackgroundResource(R.drawable.storeitembg);
			mIcon.setImageBitmap(GameResource.GoldenApple[1]);// TODO test
			// bind event listener
			this.setOnClickListener(onClick);
		}
	}
	
	private static ViewStoreItem addStoreItem(Context context, String relatedGameData, ViewGroup parent){
		ViewStoreItem itemView = new ViewStoreItem(context, relatedGameData);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
				LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = GameResource.StoreItemIconLeftMargin;
		parent.addView(itemView, layoutParams);
		return itemView;
	}
	
	private AdView mADView;
	private List<ViewStoreItem> mItemViews;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        mADView = ADManager.loadAD(this, (ViewGroup)(this.findViewById(R.id.actStore_ADPanel)));
        // Add Store Item
        ViewGroup storeItemContainer = (ViewGroup)this.findViewById(R.id.actStore_StoreItem);
        mItemViews = new ArrayList<ViewStoreItem>();
        mItemViews.add(addStoreItem(this, DataAccess.GameData_GoldenAppleLevel, storeItemContainer));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_GreenAppleLevel, storeItemContainer));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_GravityAppleLevel, storeItemContainer));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_StrongBowCount, storeItemContainer));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_WeakBowCount, storeItemContainer));
	}
	
	@Override
	public void onStart(){
		super.onStart();
		ADManager.refreshAD(mADView);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		DataAccess.saveAllGameData(this);
	}
}