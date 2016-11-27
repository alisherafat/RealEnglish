package ir.realenglish.app.view;

import android.os.Bundle;
import android.widget.ImageView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.koushikdutta.ion.Ion;

import ir.realenglish.app.R;

public class ImageActivity extends BaseActivity {
    ImageView imgExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        setToolBar();

        imgExample = (ImageView) findViewById(R.id.imgThumbnail);

        ColorGenerator generator = ColorGenerator.MATERIAL;
        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .width(100)
                .height(100)
                .endConfig()
                .rect();
        int color = generator.getColor("a");

        Ion.with(imgExample).placeholder(builder.build("H", color)).fadeIn(false).load("http://10.0.2.2:8585/file/image/user/1.png");


    }

}
