package ml.game.android.SaveNewton;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;

import com.android.vending.billing.IInAppBillingService;

public class BillingManager{
	private static final int WorkType_IsRemoveAD = 0;
	private static final int WorkType_PurchaseItem = 1;
	
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
			@SuppressWarnings("unchecked")
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
							if (work.WorkType==WorkType_IsRemoveAD){
								try {
									Bundle puchasedItem = sBillingService.getPurchases(3, work.Context.getPackageName(), 
											"inapp", null);
									if (puchasedItem.getInt("RESPONSE_CODE")==0){
										ArrayList<String> ownedSkus =
												puchasedItem.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
										if (ownedSkus.contains("")){ // TODO
											DataAccess.GDShowAD = 0;
											work.Result = true;
										}else{
											work.Result = false;
										}
									}else{
										work.Exception = new Exception("unknown error");
										work.Result = null;
									}
								} catch (RemoteException e) {
									work.Exception = e;
									work.Result = null;
								}
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
}