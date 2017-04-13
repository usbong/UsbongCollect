/*
 * Copyright 2016 Michael Syson
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *     
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package usbong.android.collect;

import java.io.File;

import usbong.android.features.node.QRCodeReaderActivity;
import usbong.android.utils.UsbongConstants;
import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/*
 * This is Usbong's Main Menu activity. 
 */
public class BuyActivity extends AppCompatActivity/*Activity*/ 
{	
	private final static int BUY_SCREEN=0;
	private final static int ACCOUNT_SCREEN=1;	
	private static int currScreen;
	
	private boolean isSendingData;

	//edited by Mike, 20170225
	private static int currPreference=UsbongConstants.defaultPreference; 	
	private static int currModeOfPayment=UsbongConstants.defaultModeOfPayment; 
	
	private String productDetails; //added by Mike, 20170221
		
	private Button confirmButton;
	private Button buyButton; //added by Mike, 20170220	
	private Button backButton;

/*	private Button sellButton;
	private Button requestButton;
*/	
//	private static BuyActivity instance;
				
	public static String timeStamp;
	
//	private static Date startTime;	
	
	protected UsbongDecisionTreeEngineActivity myUsbongDecisionTreeEngineActivity;
	protected SettingsActivity mySettingsActivity;
	
	private static Activity myActivityInstance;
	private ProgressDialog myProgressDialog;
	
    private AlertDialog inAppSettingsDialog; //added by Mike, 20160417

    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        super.onCreate(savedInstanceState);

        //added by Mike, 27 Sept. 2015
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        myActivityInstance = this;
        
        currScreen=BUY_SCREEN;//default; added by Mike, 20170220
        
        //added by Mike, 25 Sept. 2015
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
//        getSupportActionBar().setDisplayUseLogoEnabled(true);        


	    setContentView(R.layout.ecommerce_text_image_display_screen);	        
//	    setContentView(R.layout.buy);	        
/*//commented out by Mike, 20170216
            //added by Mike, 20161117
        	Bundle extras = getIntent().getExtras();
        	if (extras!=null) {
	        	String message = extras.getString("completed_tree");

	        	if (message.equals("true")) {
			        AppRater.showRateDialog(this); 
	        	}	        		
        	}
*/        	
	        reset();
	        init();
    }
    
    public Activity getInstance() {
//    	return instance;
    	return myActivityInstance;
    }
    
    /*
     * Initialize this activity
     */
    public void init()
    {    	
    	//added by Mike, 20170310
    	UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH_TEMP));

    	if (currScreen==BUY_SCREEN) {
    		TextView myTextImageDisplayTextView = (TextView)findViewById(R.id.text_image_display_textview);
        	myTextImageDisplayTextView = (TextView) UsbongUtils.applyTagsInView(UsbongDecisionTreeEngineActivity.getInstance(), myTextImageDisplayTextView, UsbongUtils.IS_TEXTVIEW, getIntent().getStringExtra(UsbongConstants.ITEM_VARIABLE_NAME));        	

        	productDetails = myTextImageDisplayTextView.getText().toString();//added by Mike, 20170221

    		ImageView myTextImageDisplayImageView = (ImageView)findViewById(R.id.image_display_imageview);

    		String path = UsbongUtils.USBONG_TREES_FILE_PATH + UsbongUtils.currCategory+"/"+getIntent().getStringExtra(UsbongConstants.ITEM_IMAGE_NAME);
    		File imageFile = new File(path);	                    
            if(imageFile.exists())
            {	                    
            	Bitmap myBitmap = BitmapFactory.decodeFile(path);
            	if(myBitmap != null)
            	{
            		//reduce to 70% size; bitmaps produce larger than actual image size
                	Bitmap rescaledMyBitmap = Bitmap.createScaledBitmap(
                								myBitmap, 
                								myBitmap.getWidth()/10*7, 
                								myBitmap.getHeight()/10*7, 
                								false);
                	myTextImageDisplayImageView.setImageBitmap(rescaledMyBitmap);
     			}	             
            	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
        	}
            else {
            	try {
            		//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
                    Resources myRes = getResources();
                	Drawable myDrawableImage = Drawable.createFromStream(myRes.getAssets().open(UsbongConstants.NO_IMAGE_FILENAME), null);       	                    	
                	myTextImageDisplayImageView.setImageDrawable(myDrawableImage);                		            		
            	}
            	catch (Exception e) {
            		e.printStackTrace();
            	}
            }
/*    		
    		//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
            Resources myRes = getResources();
            try {
                Drawable myDrawableImage = Drawable.createFromStream(myRes.getAssets().open(getIntent().getStringExtra(UsbongConstants.ITEM_IMAGE_NAME)), null); //edited by Mike, 20170202        	
            
                if (myDrawableImage!=null) {
            		myTextImageDisplayImageView.setImageDrawable(myDrawableImage);        	
                }
                else {
            		//Reference: http://www.anddev.org/tinytut_-_get_resources_by_name__getidentifier_-t460.html; last accessed 14 Sept 2011
        			myDrawableImage = myRes.getDrawable(myRes.getIdentifier("no_image", "drawable", UsbongUtils.myPackageName));
        			myTextImageDisplayImageView.setImageDrawable(myDrawableImage);		        		        	        	
                }
            }
            catch (Exception e) {
            	e.printStackTrace();
            }            		
*/            
    	}
    	else { //if account screen
		    //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
	        //; last accessed: 20150609
	        //answer by Elenasys
	        //added by Mike, 20150207
	        SharedPreferences prefs = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE);
	        if (prefs!=null) {
		      ((TextView)findViewById(R.id.first_name)).setText(prefs.getString("firstName", ""));//"" is the default value.
		      ((TextView)findViewById(R.id.surname)).setText(prefs.getString("surname", "")); //"" is the default value.
		      ((TextView)findViewById(R.id.contact_number)).setText(prefs.getString("contactNumber", "")); //"" is the default value
		      
		      //added by Mike, 20170223
		      RadioGroup preferenceRadioButtonGroup = ((RadioGroup)findViewById(R.id.preference_radiogroup));
			  ((RadioButton)preferenceRadioButtonGroup.getChildAt(prefs.getInt("preference", UsbongConstants.defaultPreference))).setChecked(true);
		      	
			  ((TextView)findViewById(R.id.address)).setText(prefs.getString("shippingAddress", "")); //"" is the default value

		      //added by Mike, 20170223
			  RadioGroup modeOfPaymentRadioButtonGroup = ((RadioGroup)findViewById(R.id.mode_of_payment_radiogroup));
			  ((RadioButton)modeOfPaymentRadioButtonGroup.getChildAt(prefs.getInt("modeOfPayment", UsbongConstants.defaultModeOfPayment))).setChecked(true);
	        }
    	}    	
    	
/*
    	//added by Mike, 20160126
    	buyButton = (Button)findViewById(R.id.buy_button);
    	if (buyButton!=null) {
        	buyButton.setOnClickListener(new OnClickListener() {
    			@Override
    			public void onClick(View v) {		
    			    setContentView(R.layout.account);	        
    			}
        	});    	    		
    	}
*/         
    	//added by Mike, 20160126
    	confirmButton = (Button)findViewById(R.id.confirm_button);    	
    	confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				if (currScreen==BUY_SCREEN) {
					currScreen=ACCOUNT_SCREEN;
					setContentView(R.layout.account);	
					init();
				}
				else {
					if (verifyFields()) {			
						//save data 
				        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
				        //; last accessed: 20150609
				        //answer by Elenasys
				        //added by Mike, 20170207
				        SharedPreferences.Editor editor = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE).edit();
				        editor.putString("firstName", ((TextView)findViewById(R.id.first_name)).getText().toString());
				        editor.putString("surname", ((TextView)findViewById(R.id.surname)).getText().toString());
				        editor.putString("contactNumber", ((TextView)findViewById(R.id.contact_number)).getText().toString());

				        RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.preference_radiogroup);				        		
						for (int i=0; i< radioButtonGroup.getChildCount(); i++) {
							if (((RadioButton)radioButtonGroup.getChildAt(i)).isChecked()) {
								currPreference=i;
							}
						}
						editor.putInt("preference", currPreference); //added by Mike, 20170223				        
						
				        editor.putString("shippingAddress", ((TextView)findViewById(R.id.address)).getText().toString());
						
						RadioGroup paymentMethodRadioButtonGroup = (RadioGroup)findViewById(R.id.mode_of_payment_radiogroup);
						for (int i=0; i< paymentMethodRadioButtonGroup.getChildCount(); i++) {
							if (((RadioButton)paymentMethodRadioButtonGroup.getChildAt(i)).isChecked()) {
								currModeOfPayment=i;
							}
						}
						editor.putInt("modeOfPayment", currModeOfPayment); //added by Mike, 20170223
				        editor.commit();				    	
										        
						StringBuffer buySummary = new StringBuffer();
						buySummary.append("-Purchase Order Summary-\n");					
						buySummary.append(productDetails+"\n--\n");
											
						buySummary.append("Customer Name: "+
								((TextView)findViewById(R.id.surname)).getText().toString()+", "+
								((TextView)findViewById(R.id.first_name)).getText().toString()+"\n");    	
	
						buySummary.append("Contact Number: "+
								((TextView)findViewById(R.id.contact_number)).getText().toString()+"\n");    	

						String quantity = ((TextView)findViewById(R.id.quantity)).getText().toString();
						if (quantity.trim().equals("")) {
							buySummary.append("Quantity: "+ "1"
									+"\n");    	    		
    					}
						else {
							buySummary.append("Quantity: "+ quantity
									+"\n");    	    									
						}

						
//						RadioGroup radioButtonGroup = (RadioGroup)findViewById(R.id.preference_radiogroup);
						int radioButtonID = radioButtonGroup.getCheckedRadioButtonId();				
	//					RadioButton r = (RadioButton) radioButtonGroup.getChildAt(radioButtonID); 
						RadioButton radioButton = (RadioButton) radioButtonGroup.findViewById(radioButtonID);
						String selectedText = radioButton.getText().toString();	 
						buySummary.append("Preference: "+selectedText+"\n");    	
	
						buySummary.append("Address: "+
								((TextView)findViewById(R.id.address)).getText().toString()+"\n");    	
						
//						RadioGroup paymentMethodRadioButtonGroup = (RadioGroup)findViewById(R.id.mode_of_payment_radiogroup);
						int paymentMethodRadioButtonID = paymentMethodRadioButtonGroup.getCheckedRadioButtonId();					
						RadioButton paymentMethodRadioButton = (RadioButton) paymentMethodRadioButtonGroup.findViewById(paymentMethodRadioButtonID);
						String paymentMethodSelectedText = paymentMethodRadioButton.getText().toString();	 
	
						buySummary.append("Payment Method: "+paymentMethodSelectedText+"\n");    	
	
						String additionalInstructionsString = ((TextView)findViewById(R.id.additional_instructions)).getText().toString();					
						if (additionalInstructionsString.trim().equals("")) {
							buySummary.append("Additional Instructions: "+
									"N/A\n");    							
						}
						else {
							buySummary.append("Additional Instructions: "+
									additionalInstructionsString+"\n");    							
						}
						
						String additionalInquiriesString = ((TextView)findViewById(R.id.additional_inquiries)).getText().toString();					
						if (additionalInquiriesString.trim().equals("")) {
							buySummary.append("Additional Inquiries: "+
									"N/A\n");    							
						}
						else {
							buySummary.append("Additional Inquiries: "+
									additionalInquiriesString+"\n");    							
						}
	/*
						//added by Mike, 20170221
						UsbongUtils.generateDateTimeStamp();
						buySummary.append("\nPurchase Order ID:\n"+UsbongUtils.getDateTimeStamp()+"\n");					
	*/					
						buySummary.append("-End of Summary-");    							
											
						//http://stackoverflow.com/questions/2197741/how-can-i-send-emails-from-my-android-application;
						//answer by: Jeremy Logan, 20100204
						//added by Mike, 20170220
					    Intent i = new Intent(Intent.ACTION_SEND);
					    i.setType("message/rfc822"); //remove all non-email apps that support send intent from chooser
					    i.putExtra(Intent.EXTRA_EMAIL  , new String[]{UsbongConstants.EMAIL_ADDRESS});
					    i.putExtra(Intent.EXTRA_SUBJECT, "Purchase Order: "+productDetails.substring(0,productDetails.indexOf("\n")).replace("Title: ",""));
					    i.putExtra(Intent.EXTRA_TEXT   , buySummary.toString());
					    try {
					    	isSendingData=true; //added by Mike, 20170225
					        startActivityForResult(Intent.createChooser(i, "Sending email..."), 1); 
					    } catch (android.content.ActivityNotFoundException ex) {
					        Toast.makeText(BuyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					    }	
					}
				}				
			    
/*
				reset(); //generate new timestamp
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent().setClass(BuyActivity.getInstance(), UsbongDecisionTreeEngineActivity.class);
				toUsbongDecisionTreeEngineActivityIntent.putExtra("currScreen","0"); //make currScreen=0; meaning very first screen				
				toUsbongDecisionTreeEngineActivityIntent.putExtra("utreeToLoad",UsbongConstants.TREE_TYPE_BUY); 								
				startActivityForResult(toUsbongDecisionTreeEngineActivityIntent,1);
*/				
			}
    	});    	
/*    	
    	//added by Mike, 20160126
    	backButton = (Button)findViewById(R.id.back_button);
    	backButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
				
				
				//TODO: store product details later
			    setContentView(R.layout.ecommerce_text_image_display_screen);	        			    								
			}
    	});    	
*/    	
    }
    
    public boolean verifyFields() {
    	boolean allFieldsAreFilledUp=true;
    	
    	TextView surnameTextView = ((TextView)findViewById(R.id.surname));
		String surname = surnameTextView.getText().toString();	
		if (surname.trim().equals("")) {
			surnameTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			surnameTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

    	TextView firstnameTextView = ((TextView)findViewById(R.id.first_name));
		String firstname = firstnameTextView.getText().toString();
		if (firstname.trim().equals("")) {
			firstnameTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			firstnameTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

    	TextView contactNumberTextView = ((TextView)findViewById(R.id.contact_number));
		String contactNumber = contactNumberTextView.getText().toString();
		if (contactNumber.trim().equals("")) {
			contactNumberTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			contactNumberTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

    	TextView addressTextView = ((TextView)findViewById(R.id.address));
		String address = addressTextView.getText().toString();
		if (address.trim().equals("")) {
			addressTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			addressTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}
				
		if (!allFieldsAreFilledUp) {
	        Toast.makeText(BuyActivity.this, "Please fill up all required fields.", Toast.LENGTH_LONG).show();
	        return false;
		}
		return true;
    }
    
    public void reset() {
    	UsbongUtils.generateDateTimeStamp(); //create a new timestamp for this "New Entry"
    }

    //added by Mike, 29 July 2015
    //Reference: http://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android;
    //last accessed: 29 Sept. 2015; answer by Nishant, 2 May 2012; edited by Daniel Nugent, 9 July 2015
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
            	if (myProgressDialog!=null) { 
            		myProgressDialog.dismiss();
            	}
//                String result=data.getStringExtra("result");

            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }            

            //added by Mike, 20170225
	    	if (isSendingData) {
	    		isSendingData=false;
	
		        //added by Mike, 20170225
				finish();    
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent(BuyActivity.this, UsbongDecisionTreeEngineActivity.class);
				toUsbongDecisionTreeEngineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				startActivity(toUsbongDecisionTreeEngineActivityIntent);
	    	}
        }
    }//onActivityResult

    //added by Mike, July 2, 2015
    @Override
	public void onBackPressed() {
/*
    	//edited by Mike, 20160417
		if ((mTts!=null) && (mTts.isSpeaking())) {
			mTts.stop();
		}
		//edited by Mike, 20160417
		if ((myMediaPlayer!=null) && (myMediaPlayer.isPlaying())) {
			myMediaPlayer.stop();
		}
*/
    	//added by Mike, 20170216
		//return to UsbongDecisionTreeEngineActivity
		finish();
		Intent toUsbongDecisionTreeEngineActivityIntent = new Intent(getInstance(), UsbongDecisionTreeEngineActivity.class);
		toUsbongDecisionTreeEngineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(toUsbongDecisionTreeEngineActivityIntent);
    	
/*
    	//Reference: http://stackoverflow.com/questions/11495188/how-to-put-application-to-background
    	//; last accessed: 14 April 2015, answer by: JavaCoderEx
    	Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        startActivity(i);    
*/        
    }
    
    //added by Mike, 25 Sept. 2015
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.standard_menu, menu);
		return super.onCreateOptionsMenu(menu); 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{		
		switch(item.getItemId())
		{
/*		
			case(R.id.settings):
				//Reference: http://stackoverflow.com/questions/16954196/alertdialog-with-checkbox-in-android;
				//last accessed: 20160408; answer by: kamal; edited by: Empty2K12
				final CharSequence[] items = {UsbongConstants.AUTO_NARRATE_STRING, UsbongConstants.AUTO_PLAY_STRING, UsbongConstants.AUTO_LOOP_STRING};
				// arraylist to keep the selected items
				UsbongDecisionTreeEngineActivity.selectedSettingsItems=new ArrayList<Integer>();
				
				//check saved settings
				if (UsbongUtils.IS_IN_AUTO_NARRATE_MODE) {					
					UsbongDecisionTreeEngineActivity.selectedSettingsItems.add(UsbongConstants.AUTO_NARRATE);			
				}
				if (UsbongUtils.IS_IN_AUTO_PLAY_MODE) {
					UsbongDecisionTreeEngineActivity.selectedSettingsItems.add(UsbongConstants.AUTO_PLAY);	
					UsbongDecisionTreeEngineActivity.selectedSettingsItems.add(UsbongConstants.AUTO_NARRATE); //if AUTO_PLAY is checked, AUTO_NARRATE should also be checked
		    	}	        				
				if (UsbongUtils.IS_IN_AUTO_LOOP_MODE) {					
					UsbongDecisionTreeEngineActivity.selectedSettingsItems.add(UsbongConstants.AUTO_LOOP);			
				}
			    
				UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean = new boolean[items.length];
			    for(int k=0; k<items.length; k++) {
			    	UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean[k] = false;			    		
			    }
			    for(int i=0; i<UsbongDecisionTreeEngineActivity.selectedSettingsItems.size(); i++) {
			    	UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean[UsbongDecisionTreeEngineActivity.selectedSettingsItems.get(i)] = true;
			    }
			    		    
			    inAppSettingsDialog = new AlertDialog.Builder(this)
				.setTitle("Settings")
				.setMultiChoiceItems(items, UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean, new DialogInterface.OnMultiChoiceClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
				    	Log.d(">>>","onClick");

				    	if (isChecked) {
				            // If the user checked the item, add it to the selected items
				    		UsbongDecisionTreeEngineActivity.selectedSettingsItems.add(indexSelected);
				            if ((indexSelected==UsbongConstants.AUTO_PLAY) 
					        		&& !UsbongDecisionTreeEngineActivity.selectedSettingsItems.contains(UsbongConstants.AUTO_NARRATE)) {
				                final ListView list = inAppSettingsDialog.getListView();
				                list.setItemChecked(UsbongConstants.AUTO_NARRATE, true);
				            }				           
				        } else if (UsbongDecisionTreeEngineActivity.selectedSettingsItems.contains(indexSelected)) {
				        	if ((indexSelected==UsbongConstants.AUTO_NARRATE) 
				        		&& UsbongDecisionTreeEngineActivity.selectedSettingsItems.contains(UsbongConstants.AUTO_PLAY)) {
				                final ListView list = inAppSettingsDialog.getListView();
				                list.setItemChecked(indexSelected, false);
				        	}
				        	else {        	
					            // Else, if the item is already in the array, remove it
				        		UsbongDecisionTreeEngineActivity.selectedSettingsItems.remove(Integer.valueOf(indexSelected));
				        	}
				        }
				        
				        //updated selectedSettingsItemsInBoolean
					    for(int k=0; k<items.length; k++) {
					    	UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean[k] = false;			    		
					    }
					    for(int i=0; i<UsbongDecisionTreeEngineActivity.selectedSettingsItems.size(); i++) {
					    	UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean[UsbongDecisionTreeEngineActivity.selectedSettingsItems.get(i)] = true;
					    }
				    }
				}).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int id) {
				    	 try {	    	
				 			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config");	
				 			BufferedReader br = new BufferedReader(reader);    		
				 	    	String currLineString;        	

				 	    	//write first to a temporary file
							PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config" +"TEMP");

				 	    	while((currLineString=br.readLine())!=null)
				 	    	{ 	
				 	    		Log.d(">>>", "currLineString: "+currLineString);
								if ((currLineString.contains("IS_IN_AUTO_NARRATE_MODE="))
								|| (currLineString.contains("IS_IN_AUTO_PLAY_MODE="))
								|| (currLineString.contains("IS_IN_AUTO_LOOP_MODE="))) {
									continue;
								}	
								else {
									out.println(currLineString);			 	    		
								}
				 	    	}	        				

							for (int i=0; i<items.length; i++) {
								Log.d(">>>>", i+"");
								if (UsbongDecisionTreeEngineActivity.selectedSettingsItemsInBoolean[i]==true) {
									if (i==UsbongConstants.AUTO_NARRATE) {
							    		out.println("IS_IN_AUTO_NARRATE_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=true;							
									}								
									else if (i==UsbongConstants.AUTO_PLAY) {
							    		out.println("IS_IN_AUTO_PLAY_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=true;						
									}	
									else if (i==UsbongConstants.AUTO_LOOP) {
							    		out.println("IS_IN_AUTO_LOOP_MODE=ON");
							    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=true;						
									}
								}
								else {
									if (i==UsbongConstants.AUTO_NARRATE) {
							    		out.println("IS_IN_AUTO_NARRATE_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_NARRATE_MODE=false;															
									}							
									else if (i==UsbongConstants.AUTO_PLAY) {
							    		out.println("IS_IN_AUTO_PLAY_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_PLAY_MODE=false;	
									}
									else if (i==UsbongConstants.AUTO_LOOP) {
							    		out.println("IS_IN_AUTO_LOOP_MODE=OFF");
							    		UsbongUtils.IS_IN_AUTO_LOOP_MODE=false;	
									}
								}				
							}					
					    	out.close(); //remember to close
					    	
					    	//copy temp file to actual usbong.config file
				 			InputStreamReader reader2 = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.BASE_FILE_PATH + "usbong.config"+"TEMP");	
				 			BufferedReader br2 = new BufferedReader(reader2);    		
				 	    	String currLineString2;        	

				 	    	//write to actual usbong.config file
							PrintWriter out2 = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.BASE_FILE_PATH + "usbong.config");

				 	    	while((currLineString2=br2.readLine())!=null)
				 	    	{ 	
								out2.println(currLineString2);			 	    		
				 	    	}			 	    	
				 	    	out2.close();
				 	    	
				 	    	UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH + "usbong.config"+"TEMP"));
				 		}
				 		catch(Exception e) {
				 			e.printStackTrace();
				 		}			 		
				    }
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int id) {
				        //  Your code when user clicked on Cancel
				    }
				}).create();
				inAppSettingsDialog.show();
					return true;
*/					
			case(R.id.add_item): //added by Mike, 20170308
				finish();
				//added by Mike, 20170216
				Intent toAddItemActivityIntent = new Intent().setClass(getInstance(), AddItemActivity.class);
				toAddItemActivityIntent.putExtra("newAddItemActivity", true); //added by Mike, 20170328
				toAddItemActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(toAddItemActivityIntent);
				return true;
			case(R.id.request):
				finish();
				//added by Mike, 20170216
				Intent toRequestActivityIntent = new Intent().setClass(getInstance(), RequestActivity.class);
				toRequestActivityIntent.putExtra("newRequestActivity", true); //added by Mike, 20170330
				toRequestActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(toRequestActivityIntent);
				return true;
			case(R.id.about):
		    	new AlertDialog.Builder(BuyActivity.this).setTitle("About")
				.setMessage(UsbongUtils.readTextFileInAssetsFolder(BuyActivity.this,"credits.txt")) //don't add a '/', otherwise the file would not be found
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				return true;
			case(R.id.account):
				final EditText firstName = new EditText(this);
				firstName.setHint("First Name");
				final EditText surName = new EditText(this);
				surName.setHint("Surname");
				final EditText contactNumber = new EditText(this);
				contactNumber.setHint("Contact Number");
				contactNumber.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
				
				//added by Mike, 20170223
				final RadioGroup preference = new RadioGroup(this);
				preference.setOrientation(RadioGroup.HORIZONTAL);
				
				RadioButton meetup = new AppCompatRadioButton(this);
				meetup.setText("Meet-up");
				preference.addView(meetup);
								
				RadioButton shipping = new AppCompatRadioButton(this);
				shipping.setText("Shipping");
				preference.addView(shipping);				
				
				final EditText shippingAddress = new EditText(this);
				shippingAddress.setHint("Shipping Address");
				shippingAddress.setMinLines(5);

				//added by Mike, 20170223
				final RadioGroup modeOfPayment = new RadioGroup(this);
				modeOfPayment.setOrientation(RadioGroup.VERTICAL);
				
				RadioButton cashUponMeetup = new AppCompatRadioButton(this);
				cashUponMeetup.setText("Cash upon meet-up");
				modeOfPayment.addView(cashUponMeetup);
									
				RadioButton bankDeposit = new AppCompatRadioButton(this);
				bankDeposit.setText("Bank Deposit");
				modeOfPayment.addView(bankDeposit);

				RadioButton peraPadala = new AppCompatRadioButton(this);
				peraPadala.setText("Pera Padala");
				modeOfPayment.addView(peraPadala);

			    //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
		        //; last accessed: 20150609
		        //answer by Elenasys
		        //added by Mike, 20150207
		        SharedPreferences prefs = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE);
		        if (prefs!=null) {
		          firstName.setText(prefs.getString("firstName", ""));//"" is the default value.
		          surName.setText(prefs.getString("surname", "")); //"" is the default value.
		          contactNumber.setText(prefs.getString("contactNumber", "")); //"" is the default value.

		          //added by Mike, 20170223
		          ((RadioButton)preference.getChildAt(prefs.getInt("preference", UsbongConstants.defaultPreference))).setChecked(true);
				  		          
		          shippingAddress.setText(prefs.getString("shippingAddress", "")); //"" is the default value.
		          
			      //added by Mike, 20170223				  
		          ((RadioButton)modeOfPayment.getChildAt(prefs.getInt("modeOfPayment", UsbongConstants.defaultModeOfPayment))).setChecked(true);
		        }
				
				LinearLayout ll=new LinearLayout(this);
				ll.setOrientation(LinearLayout.VERTICAL);
				ll.addView(firstName);
				ll.addView(surName);
				ll.addView(contactNumber);
				ll.addView(preference);
				ll.addView(shippingAddress);				
				ll.addView(modeOfPayment);

				new AlertDialog.Builder(this).setTitle("My Account")
				.setView(ll)
				.setNegativeButton("Cancel",  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
				        //ACTION
				    }
				})
				.setPositiveButton("Save & Exit",  new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int id) {
				        //ACTION
				        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
				        //; last accessed: 20150609
				        //answer by Elenasys
				        //added by Mike, 20170207
				        SharedPreferences.Editor editor = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE).edit();
				        editor.putString("firstName", firstName.getText().toString());
				        editor.putString("surname", surName.getText().toString());
				        editor.putString("contactNumber", contactNumber.getText().toString());

				        for (int i=0; i< preference.getChildCount(); i++) {
				        	if (((RadioButton)preference.getChildAt(i)).isChecked()) {
				        		currPreference=i;
				        	}
				        }
				        editor.putInt("preference", currPreference); //added by Mike, 20170223				        
				        
				        editor.putString("shippingAddress", shippingAddress.getText().toString());

				        for (int i=0; i< modeOfPayment.getChildCount(); i++) {
				        	if (((RadioButton)modeOfPayment.getChildAt(i)).isChecked()) {
				        		currModeOfPayment=i;
				        	}
				        }
				        editor.putInt("modeOfPayment", currModeOfPayment); //added by Mike, 20170223
				        editor.commit();		
				        
				        if (currScreen!=BUY_SCREEN) {
					        //added by Mike, 20170222
					        setContentView(R.layout.account);	
					        init();				        	
				        }
				    }
				}).show();
				return true;
			case android.R.id.home: //added by Mike, 22 Sept. 2015
/*//commented out by Mike, 201702014; UsbongDecisionTreeEngineActivity is already the main menu				
				processReturnToMainMenuActivity();
*/				    	//added by Mike, 20170216
				//return to UsbongDecisionTreeEngineActivity
				finish();
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent(getInstance(), UsbongDecisionTreeEngineActivity.class);
				toUsbongDecisionTreeEngineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				startActivity(toUsbongDecisionTreeEngineActivityIntent);
		        return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}