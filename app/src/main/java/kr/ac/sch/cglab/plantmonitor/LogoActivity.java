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


        //LOGO_TIMEOUT ��ŭ ��Ƽ��Ƽ �����Ŀ� ���� ��Ƽ��Ƽ�� �̵�
        Handler handler= new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                //���� ��Ƽ��Ƽ ����Ʈ
                Intent mainIntent = new Intent(LogoActivity.this, MainActivity.class);  //���� ��Ƽ��Ƽ�� �̵�

                startActivity(mainIntent);

                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        }, LOGO_TIMEOUT);
    }

}
