package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

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
	
	private static class CountPickerView extends LinearLayout{
		public interface OnCountChangeListener {
			void onCountChange(int count);
		}
		
		public int MinValue = 0;
		public int MaxValue = 0;
		public int Value = 0;
		private LinearLayout mNumberContainer;
		private int mNumberWidth, mNumberHeight;
		private OnCountChangeListener mOnCountChangeListener;
		private boolean mChangeCountThreadStopFlag = false;
		private int mChangeCountThreadModel;
		private boolean mIsChangeCountThreadRunning = false;
		
		@SuppressLint("HandlerLeak")
		private Handler mChangeCountMessageHander = new Handler(){
			@Override
	        public void handleMessage(Message msg){
				switch(msg.what){
				case 0:
					plusCount();
					break;
				case 1:
					minusCount();
					break;
				}
			}
		};
		
		private void startChangeCountThread(int model){ // model=0 plus, model=1 minus
			if (mIsChangeCountThreadRunning){
				return;
			}
			mChangeCountThreadModel = model;
			mChangeCountThreadStopFlag = false;
			Thread worker = new Thread(){
				@Override
				public void run(){
					mIsChangeCountThreadRunning = true;
					while(!mChangeCountThreadStopFlag){
						mChangeCountMessageHander.sendEmptyMessage(mChangeCountThreadModel);
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {}
					}
					mIsChangeCountThreadRunning = false;
				}
			};
			worker.start();
		}
		
		private void minusCount(){
			if (Value<=MinValue){
				return;
			}
			Value--;
			refresh();
			if (mOnCountChangeListener!=null){
				mOnCountChangeListener.onCountChange(Value);
			}
		}
		
		private void plusCount(){
			if (Value>=MaxValue){
				return;
			}
			Value++;
			refresh();
			if (mOnCountChangeListener!=null){
				mOnCountChangeListener.onCountChange(Value);
			}
		}
		
		public CountPickerView(Context context) {
			super(context);
			this.setOrientation(LinearLayout.HORIZONTAL);
			mNumberWidth = (int)(GameResource.Numbers[0].getWidth() * 1.7);
			mNumberHeight = (int)(GameResource.Numbers[0].getHeight() * 1.7);
			LinearLayout.LayoutParams lypButton = new LinearLayout.LayoutParams(mNumberHeight, mNumberHeight);
			// minus button
			ImageView btnMinus = new ImageView(context);
			btnMinus.setImageResource(R.drawable.minusbuttonsel);
			btnMinus.setScaleType(ScaleType.FIT_CENTER);
			btnMinus.setClickable(true);
			btnMinus.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startChangeCountThread(1);
						return true;
					case MotionEvent.ACTION_UP:
						mChangeCountThreadStopFlag = true;
						return true;
					default:
						return false;
					}
				}
			});
			this.addView(btnMinus, lypButton);
			// number container
			mNumberContainer = new LinearLayout(context);
			mNumberContainer.setOrientation(LinearLayout.HORIZONTAL);
			LinearLayout.LayoutParams lypNumberContainer = new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			int margin = (int)(10 * this.getResources().getDisplayMetrics().density);
			lypNumberContainer.leftMargin = lypNumberContainer.rightMargin = margin;
			this.addView(mNumberContainer, lypNumberContainer);
			// plus button
			ImageView btnPlus = new ImageView(context);
			btnPlus.setImageResource(R.drawable.plusbuttonsel);
			btnPlus.setScaleType(ScaleType.FIT_CENTER);
			btnPlus.setClickable(true);
			btnPlus.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						startChangeCountThread(0);
						return true;
					case MotionEvent.ACTION_UP:
						mChangeCountThreadStopFlag = true;
						return true;
					default:
						return false;
					}
				}
			});
			this.addView(btnPlus, lypButton);
			refresh();
		}
		
		public void refresh(){
			mNumberContainer.removeAllViews();
			LinearLayout.LayoutParams lypNumber = new LinearLayout.LayoutParams(mNumberWidth, mNumberHeight);
			String numberStr = String.valueOf(Value);
			for (int i=0;i<numberStr.length();i++){
				ImageView numberView = new ImageView(this.getContext());
				numberView.setImageBitmap(GameResource.MNumbers[numberStr.charAt(i)-48]);
				mNumberContainer.addView(numberView, lypNumber);
			}
		}
		
		public void setOnCountChangeListener(OnCountChangeListener onCountChangeListener){
			mOnCountChangeListener = onCountChangeListener;
		}
	}
	
	private static class ViewStoreItem extends LinearLayout{
		public interface OnSelectListener{
			void onSelected(View v, String relatedGameData);
		}
		
		private ImageView mIcon;
		private TextView mTxtTitle;
		private LinearLayout mStarContainer;
		private boolean mIsLevelItem = false;
		private OnSelectListener mOnSelectListener;
		public boolean IsSelected = false;
		
		public String RelatedGameData;
		
		private View.OnClickListener onClick = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewStoreItem itemView = (ViewStoreItem)v;
				itemView.select();
			}
		};
		
		private void onSelectStatusChange(){
			if (IsSelected){
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
			}else if (RelatedGameData.equals(DataAccess.GameData_ShowAD)){
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
			}else if (RelatedGameData.equals(DataAccess.GameData_ShowAD)){
				return R.drawable.item_removead;
			}
			return 0;
		}
		
		private void renderLevelStar(){
			mStarContainer.removeAllViews();
			int activeStarCnt = (int)DataAccess.getGameDataByKey(RelatedGameData);
			LinearLayout.LayoutParams lypStar = new LinearLayout.LayoutParams(GameResource.StoreItemLevelStarSize, 
					GameResource.StoreItemLevelStarSize);
			for (int i=0;i<DataAccess.GameData_MaxLevel;i++){
				ImageView star = new ImageView(this.getContext());
				if (activeStarCnt>0){
					star.setImageResource(R.drawable.star_active);
					activeStarCnt--;
				}else{
					star.setImageResource(R.drawable.star);
				}
				mStarContainer.addView(star, lypStar);
			}
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
			mTxtTitle = new TextView(context);
			TextView txtTitle = mTxtTitle;
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
				mStarContainer = new LinearLayout(context);
				LinearLayout starContainer = mStarContainer;
				starContainer.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams lypStarContainer = new LinearLayout.LayoutParams(GameResource.StoreItemBgSize, 
						LayoutParams.WRAP_CONTENT);
				starContainer.setGravity(Gravity.CENTER);
				renderLevelStar();
				this.addView(starContainer, lypStarContainer);
			}
			// bind event listener
			this.setOnClickListener(onClick);
		}
		
		public void setOnSelectListener(OnSelectListener listener){
			mOnSelectListener = listener;
		}
		
		public void select(){
			IsSelected = true;
			onSelectStatusChange();
		}
		
		public void unSelect(){
			IsSelected = false;
			onSelectStatusChange();
		}
		
		public void refresh(){
			mTxtTitle.setText(getTitleByRelatedGameData());
			if (mIsLevelItem){
				renderLevelStar();
			}
		}
	}
	
	private static class Order{
		public String RelatedGameData;
		public int Count;
		public boolean IsValidate;
		public SpannableStringBuilder CostStr;
		public boolean IsCountableOrder;
		public BillingManager.Product RelatedBillProduct;
		
		private static HashMap<String, String> OrderDescription = new HashMap<String, String>();
		static{
			OrderDescription.put(DataAccess.GameData_GoldenAppleLevel, 
					"Increase the chance of getting the Golden Apple");
			OrderDescription.put(DataAccess.GameData_GreenAppleLevel, 
					"Increase the chance of getting the Green Apple");
			OrderDescription.put(DataAccess.GameData_GravityAppleLevel, 
					"Increase the chance of getting the Low Gravity Apple");
			OrderDescription.put(DataAccess.GameData_StrongBowCount, 
					"Buy ammunition of power bow, power bow release 3 arrows each time");
			OrderDescription.put(DataAccess.GameData_WeakBowCount, 
					"Buy ammunition of skill bow, score will be double when using skill bow");
			OrderDescription.put(DataAccess.GameData_DollarToGold1, 
					"Get " + String.valueOf(DataAccess.GameData_DollarToGold1Value) + " gold coin");
			OrderDescription.put(DataAccess.GameData_DollarToGold2, 
					"Get " + String.valueOf(DataAccess.GameData_DollarToGold2Value) + " gold coin");
			OrderDescription.put(DataAccess.GameData_DollarToGold3, 
					"Get " + String.valueOf(DataAccess.GameData_DollarToGold3Value) + " gold coin");
			OrderDescription.put(DataAccess.GameData_ShowAD, 
					"Support the developer, remove AD after you buy this");
		}
		
		public Order(String relatedGameData){
			RelatedGameData = relatedGameData;
			refresh();
		}
		
		public String getOrderDescription(){
			if (OrderDescription.containsKey(RelatedGameData)){
				return OrderDescription.get(RelatedGameData);
			}
			return "";
		}
		
		public boolean isBillOrder(){
			if (RelatedGameData.equals(DataAccess.GameData_DollarToGold1)
					|| RelatedGameData.equals(DataAccess.GameData_DollarToGold2)
					|| RelatedGameData.equals(DataAccess.GameData_DollarToGold3)
					|| RelatedGameData.equals(DataAccess.GameData_ShowAD)){
				return true;
			}
			return false;
		}
		
		public void refresh(){
			IsValidate = false;
			IsCountableOrder = false;
			CostStr = new SpannableStringBuilder();
			CostStr.append("Cost: ");
			int cost = 0;
			if (RelatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GreenAppleLevel)
					|| RelatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
				cost = DataAccess.getNextLevelCost(RelatedGameData);
				CostStr.clear();
				CostStr.append("Next Level Cost: ");
			}else if (RelatedGameData.equals(DataAccess.GameData_StrongBowCount)
					|| RelatedGameData.equals(DataAccess.GameData_WeakBowCount)){
				cost = DataAccess.getBowWeaponCost(Count);
				IsCountableOrder = true;
			}
			if (!isBillOrder()){
				int spanStart = CostStr.length() - 1;
				CostStr.append(String.valueOf(cost));
				if (DataAccess.GDGold>=cost && cost>0){
					CostStr.setSpan(new ForegroundColorSpan(Color.YELLOW), spanStart, 
							CostStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					IsValidate = true;
				}else{
					CostStr.setSpan(new ForegroundColorSpan(Color.RED), spanStart, 
							CostStr.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
			}else{
				if (sBillProducts!=null && sBillProducts.size()>0){
					for(BillingManager.Product product : sBillProducts){
						if (product.ProductID.equals(RelatedGameData)){
							CostStr.append(product.ProductPriceStr);
							IsValidate = true;
							break;
						}
					}
				}else{
					CostStr.append("Loading...");
				}
			}
		}
		
		public void executeOrder(final Context context){
			if (IsValidate){
				if (RelatedGameData.equals(DataAccess.GameData_GoldenAppleLevel)
						|| RelatedGameData.equals(DataAccess.GameData_GreenAppleLevel)
						|| RelatedGameData.equals(DataAccess.GameData_GravityAppleLevel)){
					DataAccess.levelUp(RelatedGameData);
				}else if (RelatedGameData.equals(DataAccess.GameData_StrongBowCount)
						|| RelatedGameData.equals(DataAccess.GameData_WeakBowCount)){
					DataAccess.buyBowWeapon(RelatedGameData, Count);
					Count = 0;
				}else if (isBillOrder()){
					RelatedBillProduct = new BillingManager.Product(RelatedGameData);
					BillingManager.purchaseItem(context, RelatedBillProduct, 
					new BillingManager.ResultCallback() {
						// only handle error here
						@Override public void onResult(Exception ex, Object result){
							if (ex==null){
								return;
							}
							String errMessage = "Cann't finish this purchase, please try again later.";
							if (ex instanceof BillingManager.BillingException){
								errMessage = ex.getMessage();
								BillingManager.BillingException billException = (BillingManager.BillingException)ex;
								if (billException.ErrorCode==BillingManager.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED
										&& RelatedGameData.equals(DataAccess.GameData_ShowAD)){
									((actStore)context).removeRemoveADStoreItem();
								}
							}
							Toast.makeText(context, errMessage, Toast.LENGTH_LONG).show();
						}
					});
				}
				refresh();
			}
		}
	}
	
	private AdView mADView;
	private List<ViewStoreItem> mItemViews;
	private TextView mTxtItemDescription;
	private GoldCountView mGoldCntView;
	private ImageView mBtnBuy;
	private TextView mTxtCost;
	private CountPickerView mCountPicker;
	private Order mCurOrder;
	private ViewGroup mStoreItemContainer;
	private ViewStoreItem mRemoveADStoreItem;
	private static List<BillingManager.Product> sBillProducts;
	
	private ViewStoreItem.OnSelectListener mOnItemSelected = new ViewStoreItem.OnSelectListener() {
		@Override
		public void onSelected(View v, String relatedGameData) {
			if (mItemViews!=null && mItemViews.size()>0){
				for (int i=0;i<mItemViews.size();i++){
					if (mItemViews.get(i)!=v){
						mItemViews.get(i).unSelect();
					}
				}
				mCurOrder = new Order(relatedGameData);
				updateDetailPanelInfo(true);
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
	
	private void refreshSelectedStoreItem(){
		if (mItemViews!=null){
			for (int i=0;i<mItemViews.size();i++){
				ViewStoreItem item = mItemViews.get(i);
				if (item.IsSelected){
					item.refresh();
					break;
				}
			}
		}
	}
	
	private void updateDetailPanelInfo(boolean resetCountPicker){
		if (mCurOrder!=null){
			mTxtItemDescription.setText(mCurOrder.getOrderDescription());
			mTxtCost.setText(mCurOrder.CostStr);
			if (mCurOrder.IsValidate){
				mBtnBuy.setImageResource(R.drawable.buybutton_sel);
			}else{
				mBtnBuy.setImageResource(R.drawable.buybutton_disable);
			}
			if (resetCountPicker){
				if (mCurOrder.IsCountableOrder){
					int curCount = 0;
					if (mCurOrder.RelatedGameData.equals(DataAccess.GameData_StrongBowCount)){
						curCount = DataAccess.GDStrongBowCount;
					}else if (mCurOrder.RelatedGameData.equals(DataAccess.GameData_WeakBowCount)){
						curCount = DataAccess.GDWeakBowCount;
					}
					mCountPicker.Value = 0;
					mCountPicker.MaxValue = DataAccess.GameData_MaxWeaponCount - curCount;
					mCountPicker.refresh();
					mCountPicker.setVisibility(View.VISIBLE);
				}else{
					mCountPicker.setVisibility(View.GONE);
				}
			}
		}
	}
	
	private void removeRemoveADStoreItem(){
		if (mRemoveADStoreItem!=null){
			mItemViews.remove(mRemoveADStoreItem);
			mStoreItemContainer.removeView(mRemoveADStoreItem);
			ADManager.removeAD(mADView);
			boolean hasSelectedStoreItem = false;
			for(ViewStoreItem storeItem : mItemViews){
				if (storeItem.IsSelected){
					hasSelectedStoreItem = true;
					break;
				}
			}
			if (!hasSelectedStoreItem){
				mItemViews.get(mItemViews.size()-1).select(); // select last one
			}
		}
	}
	
	private void checkIfOwnedRemoveADItem(){
		BillingManager.isRemovedAD(this, new BillingManager.ResultCallback() {
			@Override public void onResult(Object result){
				if (result!=null && (Boolean)result){
					removeRemoveADStoreItem();
				}
			}
		});
	}
	
	private void queryBillItem(){
		ArrayList<String> billItemIDs = new ArrayList<String>();
		billItemIDs.add(DataAccess.GameData_DollarToGold1);
		billItemIDs.add(DataAccess.GameData_DollarToGold2);
		billItemIDs.add(DataAccess.GameData_DollarToGold3);
		billItemIDs.add(DataAccess.GameData_ShowAD);
		BillingManager.getItemsInfo(this, billItemIDs, new BillingManager.ResultCallback() {
			@SuppressWarnings("unchecked")
			@Override public void onResult(Object result){
				if (result!=null){
					sBillProducts = (List<BillingManager.Product>)result;
					if (mCurOrder!=null && mCurOrder.isBillOrder() && !mCurOrder.IsValidate){
						mCurOrder.refresh();
						updateDetailPanelInfo(false);
					}
				}
			}
		});
	}
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.store);
        checkIfOwnedRemoveADItem();
        queryBillItem();
        RelativeLayout adPanel = (RelativeLayout)this.findViewById(R.id.actStore_ADPanel);
        mADView = ADManager.loadAD(this, adPanel);
        // add close button
        ImageView imgBtnClose = new ImageView(this);
        imgBtnClose.setScaleType(ScaleType.FIT_CENTER);
        imgBtnClose.setImageResource(R.drawable.btnclose);
        RelativeLayout.LayoutParams imgBtnCloseLy = new RelativeLayout.LayoutParams(
                GameResource.SmallBtnSize, GameResource.SmallBtnSize);
        imgBtnCloseLy.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imgBtnCloseLy.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        imgBtnCloseLy.setMargins(0, 5, 10, 0);
        adPanel.addView(imgBtnClose, imgBtnCloseLy);
        imgBtnClose.setOnClickListener(btnClose_Click);
        // Add Store Item
        mStoreItemContainer = (ViewGroup)this.findViewById(R.id.actStore_StoreItem);
        ViewGroup storeItemContainer = mStoreItemContainer;
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
        if (DataAccess.GDShowAD>0){
        	mRemoveADStoreItem = addStoreItem(this, DataAccess.GameData_ShowAD, storeItemContainer, true);
        	mItemViews.add(mRemoveADStoreItem);
        }
        // ====== Init Detail Panel
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
        mTxtItemDescription = (TextView)this.findViewById(R.id.actStore_ItemDesc);
        // Gold View
        mGoldCntView = new GoldCountView(this);
        LinearLayout.LayoutParams lypGoldCntView = new LinearLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lypGoldCntView.gravity = Gravity.BOTTOM;
        lyDetailPanel.addView(mGoldCntView, lypGoldCntView);
        // Count Input
        mCountPicker = new CountPickerView(this);
        mCountPicker.setVisibility(View.GONE);
        LinearLayout.LayoutParams lypCountPicker = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lypCountPicker.leftMargin = (int)(20 * density);
        lyDetailPanel.addView(mCountPicker, lypCountPicker);
        mCountPicker.setOnCountChangeListener(new CountPickerView.OnCountChangeListener() {
			@Override
			public void onCountChange(int count) {
				if (mCurOrder!=null){
					mCurOrder.Count = count;
					mCurOrder.refresh();
					updateDetailPanelInfo(false);
				}
			}
		});
        // Cost Text View
        mTxtCost = new TextView(this);
        TextView txtCost = mTxtCost;
        LinearLayout.LayoutParams lypTxtCost = new LinearLayout.LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lypTxtCost.gravity = Gravity.BOTTOM;
        lypTxtCost.leftMargin = (int)(20 * density);
        txtCost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        txtCost.setTypeface(null, Typeface.BOLD);
        txtCost.setTextColor(Color.YELLOW);
        lyDetailPanel.addView(txtCost, lypTxtCost);
        // ====== Buy button
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
        btnBuy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCurOrder!=null && mCurOrder.IsValidate){
					mCurOrder.executeOrder(actStore.this);
					refreshSelectedStoreItem();
					mGoldCntView.refresh();
					updateDetailPanelInfo(true);
				}
			}
		});
        // default select first item 
        firstItem.select();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
	   if (requestCode == BillingManager.ActivityCode_Purchase) {           
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseDataStr = data.getStringExtra("INAPP_PURCHASE_DATA");
	      if (resultCode==RESULT_OK && responseCode==BillingManager.BILLING_RESPONSE_RESULT_OK) {
	         try {
	            JSONObject purchaseData = new JSONObject(purchaseDataStr);
	            String productID = purchaseData.getString("productId");
	            String productToken = purchaseData.getString("developerPayload");
	            String purchaseToken = purchaseData.getString("purchaseToken");
	            if (mCurOrder!=null && mCurOrder.RelatedBillProduct!=null
	            	&& mCurOrder.RelatedBillProduct.ProductID.equals(productID)
	            	&& mCurOrder.RelatedBillProduct.Token.equals(productToken)){
	            	// purchase success, consume this product
	            	DataAccess.buyBilledData(this, mCurOrder.RelatedGameData);
	            	if (mCurOrder.RelatedGameData.equalsIgnoreCase(DataAccess.GameData_ShowAD)){
	            		removeRemoveADStoreItem();
	            		Toast.makeText(this, "Thanks for your support, AD removed!", Toast.LENGTH_LONG).show();
	            	}
	            	updateDetailPanelInfo(false);
	            	BillingManager.consumeItem(this, purchaseToken, null);
	            }
	          }
	          catch (Exception e) { }
	      }
	   }
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
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		BillingManager.destoryResource(this);
	}
	
	private OnClickListener btnClose_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            actStore.this.finish();
        }
    };
}