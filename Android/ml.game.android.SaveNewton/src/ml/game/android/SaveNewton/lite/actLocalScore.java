package ml.game.android.SaveNewton.lite;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class actLocalScore extends Activity{
    private ListView listMain;
    private TextView tipNoScore;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.localscore);
        initLayoutAndControl();
        ADManager.loadAD(this, R.id.adView);
        initData();
    }
    
    private void initLayoutAndControl(){
        RelativeLayout titleLayout = (RelativeLayout)this.findViewById(R.id.actLocalScore_titleLy);
        ImageView imgTitle = new ImageView(this);
        imgTitle.setScaleType(ScaleType.CENTER);
        imgTitle.setImageResource(R.drawable.leaderboard);
        RelativeLayout.LayoutParams imgTitleLy = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgTitleLy.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        imgTitleLy.addRule(RelativeLayout.CENTER_VERTICAL);
        imgTitleLy.leftMargin = 10;
        titleLayout.addView(imgTitle, imgTitleLy);
        ImageView imgBtnClose = new ImageView(this);
        imgBtnClose.setScaleType(ScaleType.FIT_CENTER);
        imgBtnClose.setImageResource(R.drawable.btnclose);
        RelativeLayout.LayoutParams imgBtnCloseLy = new RelativeLayout.LayoutParams(
                GameResource.SmallBtnSize, GameResource.SmallBtnSize);
        imgBtnCloseLy.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        imgBtnCloseLy.addRule(RelativeLayout.CENTER_VERTICAL);
        imgBtnCloseLy.rightMargin = 10;
        titleLayout.addView(imgBtnClose, imgBtnCloseLy);
        imgBtnClose.setOnClickListener(btnClose_Click);
        listMain = (ListView)this.findViewById(R.id.actLocalScore_mainList);
        tipNoScore = (TextView)this.findViewById(R.id.actLocalScore_tipNoData);
    }
    
    private void initData(){
        List<Map<String,String>> data = DataAccess.getScoreList(this);
        if (data!=null && data.size()>0){
            listMain.setVisibility(View.VISIBLE);
            tipNoScore.setVisibility(View.GONE);
            listMain.setAdapter(new SimpleAdapter(this, data, R.layout.localscorelistitem,
                    new String[]{"rank","name","score"},
                    new int[]{R.id.listScore_txtRank, R.id.listScore_txtName, R.id.listScore_txtScore}));
        }else{
            listMain.setVisibility(View.GONE);
            tipNoScore.setVisibility(View.VISIBLE);
        }
    }
    
    private OnClickListener btnClose_Click = new OnClickListener(){
        @Override
        public void onClick(View v) {
            actLocalScore.this.finish();
        }
    };
}