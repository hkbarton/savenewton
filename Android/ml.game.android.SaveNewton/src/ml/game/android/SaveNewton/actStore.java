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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

public class actStore extends FragmentActivity{
	private static class GoldCountView extends LinearLayout{
		public GoldCountView(Context context) {
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			refresh();
		}
		
		public void refresh(){
			this.removeAllViews();
			// Icon
			Context context = this.getContext();
			ImageView iconView = new ImageView(context);
			LinearLayout.LayoutParams lypIconView = new LinearLayout.LayoutParams(GameResource.GoldCoin.getWidth(), 
					GameResource.GoldCoin.getHeight());
			lypIconView.rightMargin = 3;
			iconView.setImageResource(R.drawable.goldcoin);
			this.addView(iconView, lypIconView);
			// Number
			LinearLayout.LayoutParams lypNumber = new LinearLayout.LayoutParams(GameResource.Numbers[0].getWidth(), 
					GameResource.Numbers[0].getHeight());
			String numberStr = String.valueOf(DataAccess.GDGold);
			for (int i=0;i<numberStr.length();i++){
				ImageView numberView = new ImageView(context);
				numberView.setImageBitmap(GameResource.Numbers[numberStr.charAt(i)-48]);
				this.addView(numberView, lypNumber);
			}
		}
	}
	
	private static class ViewStoreItem extends LinearLayout{
		public interface OnSelectListener{
			void onSelected(View v, String relatedGameData);
		}
		
		private ImageView mIcon;
		private boolean mIsLevelItem = false;
		private OnSelectListener mOnSelectListener;
		public boolean mIsSelected = false;
		
		public String RelatedGameData;
		
		private View.OnClickListener onClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewStoreItem itemView = (ViewStoreItem)v;
				itemView.select();
			}
		};
		
		private void onSelectStatusChange(){
			if (mIsSelected){
				mIcon.setBackgroundResource(R.drawable.storeitembg_hover);
				if (mOnSelectListener!=null){
					mOnSelectListener.onSelected(this, RelatedGameData);
				}
			}else{
				mIcon.setBackgroundResource(R.drawable.storeitembg);
			}
		}
		
		private String getTitleByRelatedGameData(){
			if (RelatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GreenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
				return "Level " + String.valueOf((int)DataAccess.getGameDataByKey(RelatedGameData));
			}else if (RelatedGameData.equals(DataAccess.GameData_StrongBowCount)
					|| RelatedGameData.equals(DataAccess.GameData_WeakBowCount)){
				return String.valueOf((int)DataAccess.getGameDataByKey(RelatedGameData));
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold1)){
				return "G + " + String.valueOf(DataAccess.GameData_DollarToGold1Value);
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold2)){
				return "G + " + String.valueOf(DataAccess.GameData_DollarToGold2Value);
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold3)){
				return "G + " + String.valueOf(DataAccess.GameData_DollarToGold3Value);
			}else if (RelatedGameData.equals(DataAccess.GameData_RemoveAD)){
				return "Remove AD";
			}
			return "";
		}
		
		private int getIconResourceIDByGameData(){
			if (RelatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)){
				return R.drawable.gapple1;
			}else if (RelatedGameData.equals(DataAccess.GameData_GreenAppleLevel)){
				return R.drawable.wapple1;
			}else if (RelatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
				return R.drawable.sapple1;
			}else if (RelatedGameData.equals(DataAccess.GameData_StrongBowCount)){
				return R.drawable.ic_weapon_strongbow;
			}else if (RelatedGameData.equals(DataAccess.GameData_WeakBowCount)){
				return R.drawable.ic_weapon_weakbow;
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold1)){
				return R.drawable.gold1;
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold2)){
				return R.drawable.gold2;
			}else if (RelatedGameData.equals(DataAccess.GameData_DollarToGold3)){
				return R.drawable.gold3;
			}else if (RelatedGameData.equals(DataAccess.GameData_RemoveAD)){
				return 0; // TODO
			}
			return 0;
		}
		
		public ViewStoreItem(Context context, String relatedGameData) {
			super(context);
			RelatedGameData = relatedGameData;
			if (relatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)
					|| relatedGameData.equals(DataAccess.GameData_GreenAppleLevel)
					|| relatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
				mIsLevelItem = true;
			}
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
			mIcon.setPadding(GameResource.StoreItemIconPadding, GameResource.StoreItemIconPadding, 
					GameResource.StoreItemIconPadding, GameResource.StoreItemIconPadding);
			mIcon.setImageResource(getIconResourceIDByGameData());
			// level star if need
			if (mIsLevelItem){
				LinearLayout starContainer = new LinearLayout(context);
				starContainer.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams lypStarContainer = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
						LayoutParams.WRAP_CONTENT);
				starContainer.setGravity(Gravity.CENTER);
				int activeStarCnt = (int)DataAccess.getGameDataByKey(relatedGameData);
				LinearLayout.LayoutParams lypStar = new LinearLayout.LayoutParams(GameResource.StoreItemLevelStarSize, 
						GameResource.StoreItemLevelStarSize);
				for (int i=0;i<DataAccess.GameData_MaxLevel;i++){
					ImageView star = new ImageView(context);
					if (activeStarCnt>0){
						star.setImageResource(R.drawable.star_active);
						activeStarCnt--;
					}else{
						star.setImageResource(R.drawable.star);
					}
					starContainer.addView(star, lypStar);
				}
				this.addView(starContainer, lypStarContainer);
			}
			// bind event listener
			this.setOnClickListener(onClick);
		}
		
		public void setOnSelectListener(OnSelectListener listener){
			mOnSelectListener = listener;
		}
		
		public void select(){
			mIsSelected = true;
			onSelectStatusChange();
		}
		
		public void unSelect(){
			mIsSelected = false;
			onSelectStatusChange();
		}
	}
	
	
	private AdView mADView;
	private List<ViewStoreItem> mItemViews;
	private ImageView mBtnBuy;
	private ViewStoreItem.OnSelectListener mOnItemSelected = new ViewStoreItem.OnSelectListener() {
		@Override
		public void onSelected(View v, String relatedGameData) {
			if (mItemViews!=null && mItemViews.size()>0){
				for (int i=0;i<mItemViews.size();i++){
					if (mItemViews.get(i)!=v){
						mItemViews.get(i).unSelect();
					}
				}
				// TODO
			}
		}
	};
	
	private ViewStoreItem addStoreItem(Context context, String relatedGameData, ViewGroup parent, boolean isLastOne){
		ViewStoreItem itemView = new ViewStoreItem(context, relatedGameData);
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
				LayoutParams.WRAP_CONTENT);
		layoutParams.leftMargin = GameResource.StoreItemIconLeftMargin;
		if (isLastOne){
			layoutParams.rightMargin = GameResource.StoreItemIconLeftMargin;
		}
		parent.addView(itemView, layoutParams);
		itemView.setOnSelectListener(mOnItemSelected);
		return itemView;
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        mADView = ADManager.loadAD(this, (ViewGroup)(this.findViewById(R.id.actStore_ADPanel)));
        // Add Store Item
        ViewGroup storeItemContainer = (ViewGroup)this.findViewById(R.id.actStore_StoreItem);
        mItemViews = new ArrayList<ViewStoreItem>();
        ViewStoreItem firstItem = addStoreItem(this, DataAccess.GameData_GoldenAppleLevel, storeItemContainer, false);
        mItemViews.add(firstItem);
        mItemViews.add(addStoreItem(this, DataAccess.GameData_GreenAppleLevel, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_GravityAppleLevel, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_StrongBowCount, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_WeakBowCount, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_DollarToGold1, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_DollarToGold2, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_DollarToGold3, storeItemContainer, false));
        mItemViews.add(addStoreItem(this, DataAccess.GameData_RemoveAD, storeItemContainer, true));
        // Init Detail Panel
        RelativeLayout opPanel = (RelativeLayout)this.findViewById(R.id.actStore_OpPanel);
        float density = this.getResources().getDisplayMetrics().density;
        LinearLayout lyDetailPanel = new LinearLayout(this);
        RelativeLayout.LayoutParams lypDetailPanel = new RelativeLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lypDetailPanel.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lypDetailPanel.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int panelPadding = (int)(7 * density);
        lyDetailPanel.setPadding(panelPadding, panelPadding, panelPadding, panelPadding);
        opPanel.addView(lyDetailPanel, lypDetailPanel);
        GoldCountView goldCntView = new GoldCountView(this);
        RelativeLayout.LayoutParams lypGoldCntView = new RelativeLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lyDetailPanel.addView(goldCntView, lypGoldCntView);
        // Buy button
        mBtnBuy = new ImageView(this);
        ImageView btnBuy = mBtnBuy;
        btnBuy.setClickable(true);
        btnBuy.setScaleType(ScaleType.FIT_CENTER);
        btnBuy.setImageResource(R.drawable.buybutton_sel);
        RelativeLayout.LayoutParams lypBtnBuy = new RelativeLayout.LayoutParams(
        		GameResource.BuyButtonWidth, GameResource.BuyButtonHeight);
        lypBtnBuy.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lypBtnBuy.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lypBtnBuy.rightMargin = (int)(20 * density);
        opPanel.addView(btnBuy, lypBtnBuy);
        // default select first item 
        firstItem.select();
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