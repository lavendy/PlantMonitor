package kr.ac.sch.cglab.plantmonitor;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;


public class LogoActivity extends Activity
{

    private final short LOGO_TIMEOUT = 1300;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);


        //LOGO_TIMEOUT 만큼 액티비티 생존후에 메인 액티비티로 이동
        Handler handler= new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //메인 액티비티 인텐트
                Intent mainIntent = new Intent(LogoActivity.this, MainActivity.class);  //메인 액티비티로 이동

                startActivity(mainIntent);

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, LOGO_TIMEOUT);
    }

}
