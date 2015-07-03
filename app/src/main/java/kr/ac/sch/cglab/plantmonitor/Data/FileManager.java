package kr.ac.sch.cglab.plantmonitor.Data;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.File;

public class FileManager
{
    public static final String IMG_DIR = "test/test/";

    public void InitDB()
    {

    }

    public static Bitmap getImgFromFile(int index)
    {
        //�ε��� ��û�ϸ� ���� Ȥ�� ��񿡼� �̹��� ã�Ƽ�
        //�̹��� ����

        String imgPath = IMG_DIR + index + ".jpg";
        File imgFile = new File(imgPath);
        if(imgFile.exists())
        {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            return bitmap;
        }
        return null;
    }
}
