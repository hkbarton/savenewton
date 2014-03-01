package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.json.JSONObject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.android.vending.billing.IInAppBillingService;

public class BillingManager{
	// Google Billing Result
	public static final int BILLING_RESPONSE_RESULT_OK = 0;
	public static final int BILLING_RESPONSE_RESULT_USER_CANCELED = 1;
	public static final int BILLING_RESPONSE_RESULT_BILLING_UNAVAILABLE = 3;
	public static final int BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE = 4;
	public static final int BILLING_RESPONSE_RESULT_DEVELOPER_ERROR = 5;
	public static final int BILLING_RESPONSE_RESULT_ERROR = 6;
	public static final int BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED = 7;
	public static final int BILLING_RESPONSE_RESULT_ITEM_NOT_OWNED = 8;
	// Activity Result code for purchase Action
	public static final int ActivityCode_Purchase = 1000;
	
	public static class BillingException extends Exception{
		private static final long serialVersionUID = 1L;
		public int ErrorCode;
		public BillingException(int code){
			ErrorCode = code;
		}
		
		@Override public String getMessage(){
			// only return user friendly message here
			switch(ErrorCode){
			case BILLING_RESPONSE_RESULT_USER_CANCELED:
				return "You've canceled this purchase.";
			case BILLING_RESPONSE_RESULT_ITEM_UNAVAILABLE:
				return "This item is not available for purchase now.";
			case BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED:
				return "You have already puchased this item";
			default:
				return "Cann't finish this purchase, please try again later.";
			}
		}
	}
	
	public static class Product{
		public String ProductID;
		public String ProductPriceStr;
		public String Token = "";
		
		public Product(String productID, String productPriceStr){
			ProductID = productID;
			ProductPriceStr = productPriceStr;
			// no token here, used by query item info
		}
		
		public Product(String productID){
			ProductID = productID;
			// generate token for purchase action
			Token = SecueUtil.encryptData(productID, SecueUtil.getRandomEncryptKey());
		}
	}
	
	private static final int WorkType_IsRemoveAD = 0;
	private static final int WorkType_GetItemsInfo = 1;
	private static final int WorkType_PurchaseItem = 2;
	private static final int WorkType_ConsumeItem = 3;
	
	public static abstract class ResultCallback{
		public void onResult(Object result){};
		public void onResult(Exception ex, Object result){};
	}
	
	private static class Work{
		public int WorkType;
		public Object WorkParams;
		public Object Result;
		public ResultCallback Callback;
		public Context Context;
		public Exception Exception;
		
		public Work(Context context, int workType, ResultCallback callback){
			WorkType = workType;
			Callback = callback;
			Context = context;
		}
		
		public Work(Context context, int workType, Object workParams, ResultCallback callback){
			WorkType = workType;
			WorkParams = workParams;
			Callback = callback;
			Context = context;
		}
		
		public void callbackResult(){
			if (Callback!=null){
				Callback.onResult(Result);
				Callback.onResult(Exception, Result);
			}
		}
	}

	
	private static IInAppBillingService sBillingService;
	private static Thread sWorkThread;
	private static Queue<Work> sWorkQueue = new LinkedList<Work>();
	private static boolean sForceStopWorkThreadFlag = false;
	
	// make sure all callback are happen in UI thread
	private static Handler sResultMessageHandler = new Handler(){
		@Override public void handleMessage(Message msg){
			Work work = (Work)msg.obj;
			work.callbackResult();
		}
	};
	
	private static ServiceConnection sBillingServiceCnn = new ServiceConnection() {
		@Override public void onServiceDisconnected(ComponentName name) {
			sBillingService = null;
		}
		
		@Override public void onServiceConnected(ComponentName name, IBinder service) {
			sBillingService = IInAppBillingService.Stub.asInterface(service);
		}
	};
	
	private static void bindBillingService(Context context){
		if (sBillingService==null){
			context.bindService(new Intent("com.android.vending.billing.InAppBillingService.BIND"),
					sBillingServiceCnn, Context.BIND_AUTO_CREATE);
		}
	}
	
	private static void startWork(Work work){
		sWorkQueue.add(work);
		sForceStopWorkThreadFlag = false;
		if (sWorkThread==null){
			sWorkThread = new Thread(){
				@SuppressWarnings("unchecked")
				@Override public void run(){
					while(sWorkQueue.size()>0){
						Work work = sWorkQueue.poll();
						// check billing service available
						while(sBillingService==null && !sForceStopWorkThreadFlag){
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {}
						}
						if (!sForceStopWorkThreadFlag){
							// do work
							try{
								if (work.WorkType==WorkType_IsRemoveAD){
									Bundle puchasedItem = sBillingService.getPurchases(3, work.Context.getPackageName(), 
											"inapp", null);
									int responseCode = puchasedItem.getInt("RESPONSE_CODE");
									if (responseCode==BILLING_RESPONSE_RESULT_OK){
										ArrayList<String> ownedSkus =
												puchasedItem.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
										if (ownedSkus.contains(DataAccess.GameData_ShowAD)){ 
											DataAccess.GDShowAD = 0;
											work.Result = true;
										}else{
											work.Result = false;
										}
									}else{
										work.Exception = new BillingException(responseCode);
										work.Result = null;
									}
								}else if (work.WorkType==WorkType_GetItemsInfo){
									ArrayList<String> queryIDs = (ArrayList<String>)work.WorkParams;
									Bundle querySkus = new Bundle();
									querySkus.putStringArrayList("ITEM_ID_LIST", queryIDs);
									Bundle skuDetails = sBillingService.getSkuDetails(3, work.Context.getPackageName(), 
											"inapp", querySkus);
									int responseCode = skuDetails.getInt("RESPONSE_CODE");
									if (responseCode==BILLING_RESPONSE_RESULT_OK){
										ArrayList<String> skuList = skuDetails.getStringArrayList("DETAILS_LIST");
										work.Result = new ArrayList<Product>();
										List<Product> result = (List<Product>)work.Result;
										for(String skuStr : skuList){
											JSONObject object = new JSONObject(skuStr);
											result.add(new Product(
													object.getString("productId"),
													object.getString("price")
											));
										}
									}else{
										work.Exception = new BillingException(responseCode);
										work.Result = null;
									}
								}else if (work.WorkType==WorkType_PurchaseItem){
									Product item = (Product)work.WorkParams;
									Bundle buyIntentBundle = sBillingService.getBuyIntent(3, work.Context.getPackageName(),
											   item.ProductID, "inapp", item.Token);
									int responseCode = buyIntentBundle.getInt("RESPONSE_CODE");
									if (responseCode==BILLING_RESPONSE_RESULT_OK){
										PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");
										Activity relatedActivity = (Activity)work.Context;
										relatedActivity.startIntentSenderForResult(pendingIntent.getIntentSender(),
												   ActivityCode_Purchase, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
												   Integer.valueOf(0));
										// result will be notified through onActivityResult, callback here only need handle error
									}else{
										work.Exception = new BillingException(responseCode);
										work.Result = null;
									}
								}else if (work.WorkType==WorkType_ConsumeItem){
									String purchaseToken = work.WorkParams.toString();
									int responseCode = sBillingService.consumePurchase(3, work.Context.getPackageName(), purchaseToken);
									if (responseCode==BILLING_RESPONSE_RESULT_OK){
										work.Result = true;
									}else{
										work.Exception = new BillingException(responseCode);
										work.Result = null;
									}
								}
							}catch(Exception e){
								work.Exception = e;
								work.Result = null;
							}
							sResultMessageHandler.sendMessage(sResultMessageHandler.obtainMessage(0, work));
						}
					}
					// Thread end
					sWorkThread = null;
				}
			};
			sWorkThread.start();
		}
	}
	
	public static void destoryResource(Context context){
		sForceStopWorkThreadFlag = true;
		sWorkQueue.clear();
		if (sBillingService!=null){
			try{
				context.unbindService(sBillingServiceCnn);
			}catch(Exception ex){}
		}
	}
	
	// check the purchase items and see if user have already pay for ad-free
	public static void isRemovedAD(Context context, ResultCallback callback){
		bindBillingService(context);
		startWork(new Work(context, WorkType_IsRemoveAD, callback));
	}
	
	public static void getItemsInfo(Context context, ArrayList<String> itemIDs, ResultCallback callback){
		bindBillingService(context);
		startWork(new Work(context, WorkType_GetItemsInfo, itemIDs, callback));
	}
	
	// callback here only handle error, the success result will be notified through onActivityResult
	public static void purchaseItem(Context context, Product item, ResultCallback callback){
		bindBillingService(context);
		startWork(new Work(context, WorkType_PurchaseItem, item, callback));
	}
	
	public static void consumeItem(Context context, String purchaseToken, ResultCallback callback){
		bindBillingService(context);
		startWork(new Work(context, WorkType_ConsumeItem, purchaseToken, callback));
	}
}