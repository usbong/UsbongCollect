package usbong.android.features.node;

import usbong.android.collect.BuyActivity;
import usbong.android.collect.RequestActivity;
import usbong.android.collect.SellActivity;
import usbong.android.collect.UsbongDecisionTreeEngineActivity;
import usbong.android.utils.UsbongConstants;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;

/*
 * Reference: answer by Seshu Vinay at stackoverflow
 * http://stackoverflow.com/questions/8831050/android-how-to-read-qr-code-in-my-application;
 * last accessed: Dec. 23, 2012
 */

public class QRCodeReaderActivity extends Activity
{
	public static String myQRCodeReaderName;
	public static int callingActivity;
/*
	private String currentWord;
	private String timeStamp;
*/
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		// Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        try {
            Intent intent = new Intent(
                    "com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, 0);
        } catch (Exception e) {

            Uri marketUri = Uri
                    .parse("market://details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW,
                    marketUri);
            startActivity(marketIntent);
        }

        myQRCodeReaderName=this.getIntent().getStringExtra("myQRCodeReaderName");
        callingActivity=this.getIntent().getIntExtra("callingActivity",UsbongConstants.BUY_ACTIVITY); //BUY_ACTIVITY is the default

/*
        new AlertDialog.Builder(QRCodeReaderActivity.this).setTitle("Usbong Tip")
//			.setMessage(currentWord)
        	.setMessage("Touch the screen with your stylus or finger and start drawing! When you're done, hit the menu button and save your work.")
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();		
*/		
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String content = data.getStringExtra("SCAN_RESULT");
/*                
                //added by Mike, Dec. 23, 2012
                UsbongDecisionTreeEngineActivity.setQRCodeContent(content);
*/                
			    //removed by Mike, Sept. 21, 2013
//				UsbongDecisionTreeEngineActivity.setCurrScreen(UsbongDecisionTreeEngineActivity.QR_CODE_READER_SCREEN);				
/*
                this.finish();
*/
                //edited by Mike, 20170330
                Intent toCallingActivityIntent;
                switch (callingActivity) {
                	case UsbongConstants.BUY_ACTIVITY:
                		toCallingActivityIntent = new Intent(QRCodeReaderActivity.this, BuyActivity.class);
                		processCallingActivityIntent(toCallingActivityIntent, content);
                		break;
                	case UsbongConstants.SELL_ACTIVITY:
                		toCallingActivityIntent = new Intent(QRCodeReaderActivity.this, SellActivity.class);
                		processCallingActivityIntent(toCallingActivityIntent, content);
                		break;
                	case UsbongConstants.REQUEST_ACTIVITY:
                		toCallingActivityIntent = new Intent(QRCodeReaderActivity.this, RequestActivity.class);
                		processCallingActivityIntent(toCallingActivityIntent, content);
                		break;
                	case UsbongConstants.ITEM_LIST_ACTIVITY:
                		toCallingActivityIntent = new Intent(QRCodeReaderActivity.this, UsbongDecisionTreeEngineActivity.class);
                		processCallingActivityIntent(toCallingActivityIntent, content);
                		break;
                }
			}
			if(resultCode == RESULT_CANCELED){
				//handle cancel
				this.finish();
			}
        }
	}
	
	public void processCallingActivityIntent(Intent toCallingActivityIntent, String content) {
        toCallingActivityIntent.putExtra("myQRCodeReaderName", myQRCodeReaderName);
        toCallingActivityIntent.putExtra("scan_result", content);
        toCallingActivityIntent.putExtra("fromQRCodeReader", true);				
		finish();    
		toCallingActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(toCallingActivityIntent);
	}
}