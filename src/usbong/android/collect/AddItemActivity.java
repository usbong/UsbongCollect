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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import usbong.android.utils.UsbongConstants;
import usbong.android.utils.UsbongUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.text.InputType;
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
public class AddItemActivity extends AppCompatActivity/*Activity*/ 
{	
	//added by Mike, 20170406
    private Button booksButton;
    private Button combosButton;
    
    //added by Mike, 20170414
    private Button rotateCapturedPhotoButton;
    private final int DEFAULT_ROTATION=-90;
    private int rotation=0;
	
	private boolean isSendingData;

	//edited by Mike, 20170225
	private static int currPreference=UsbongConstants.defaultPreference; 	
	private static int currModeOfPayment=UsbongConstants.defaultModeOfPayment; 
	
	private String productDetails; //added by Mike, 20170221
		
	private Button addButton;
	
	//added by Mike, 20170309
	private Button photoCaptureButton;
	private ImageView myImageView;
	public boolean performedCapturePhoto;
	public static Intent photoCaptureIntent;
	private String myPictureName="default"; //change this later in the code
	private List<String> attachmentFilePaths;
				
	public static String timeStamp;
		
	//added by Mike, 20170327
	private Button captureISBN10Button;
	private Button captureISBN13Button;	
	public static Intent captureISBN10Intent;
	public static Intent captureISBN13Intent;
	
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
                
        //added by Mike, 25 Sept. 2015
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
//        getSupportActionBar().setDisplayUseLogoEnabled(true);        


	    setContentView(R.layout.add_item);	        
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

    //added by Mike, 20170406
    public void updateCategoryTypeface() {
        if (UsbongUtils.currCategory.equals(UsbongConstants.ITEMS_LIST_BOOKS)) {
            booksButton.setTypeface(Typeface.DEFAULT_BOLD);
            combosButton.setTypeface(Typeface.DEFAULT);
        }
        else {
            booksButton.setTypeface(Typeface.DEFAULT);
            combosButton.setTypeface(Typeface.DEFAULT_BOLD);            
        }
    }
    
    public void updateCategoryItemsList() {
        //added by Mike, 20170413
    	TextView bookTitleTextView = ((TextView)findViewById(R.id.book_title));
    	TextView nameOfPrincipalAuthorTextView = ((TextView)findViewById(R.id.name_of_principal_author));
    	TextView priceTextView = ((TextView)findViewById(R.id.price));

    	switch (UsbongUtils.currCategory) {
    		case UsbongConstants.ITEMS_LIST_COMBOS:
	        	bookTitleTextView.setHint("Book Titles");
	        	nameOfPrincipalAuthorTextView.setHint("Names of Principal Authors");
	        	priceTextView.setHint("Total Price");
	        	break;
    		case UsbongConstants.ITEMS_LIST_BOOKS:
	        	bookTitleTextView.setHint(UsbongConstants.DEFAULT_BOOK_TITLE_HINT);
	        	nameOfPrincipalAuthorTextView.setHint(UsbongConstants.DEFAULT_NAME_OF_PRINCIPAL_AUTHOR_HINT);
	        	priceTextView.setHint(UsbongConstants.DEFAULT_PRICE_HINT);
	        	break;

    	}
    }
    
    /*
     * Initialize this activity
     */
    public void init()
    {   
    	//added by Mike, 20170330
//    	loadData();
    	
    	attachmentFilePaths = new ArrayList<String>();            	

    	initTakePhotoScreen();
    	        
    	//added by Mike, 20170403
	    RadioGroup formatRadioButtonGroup = ((RadioGroup)findViewById(R.id.language_radiogroup));
		((RadioButton)formatRadioButtonGroup.getChildAt(0)).setChecked(true);		    	  
    	
		//added by Mike, 20170406
		booksButton = (Button)findViewById(R.id.books_button);
        booksButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	UsbongUtils.currCategory = UsbongConstants.ITEMS_LIST_BOOKS;            
            	updateCategoryTypeface();
            	updateCategoryItemsList();
            }
        });    

        combosButton = (Button)findViewById(R.id.combos_button);
        combosButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	UsbongUtils.currCategory = UsbongConstants.ITEMS_LIST_COMBOS;            
            	updateCategoryTypeface();
            	updateCategoryItemsList();
            }
        });    

        rotateCapturedPhotoButton = (Button)findViewById(R.id.rotate_captured_photo_button);
        rotateCapturedPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
        		rotation=(rotation+DEFAULT_ROTATION)%360;
            	myImageView.setRotation(rotation); //added by Mike, rotate counter-clockwise once        	        		
/*
            	if (!hasRotatedPhoto) {
            		hasRotatedPhoto=true;
            		myImageView.setRotation(-90); //added by Mike, rotate counter-clockwise once        	        		
            	}
            	else {
            		hasRotatedPhoto=false;
            		myImageView.setRotation(90); //added by Mike, rotate clockwise once        	        		
            	}
*/            	
/*
            	String path = UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName +".jpg";
	        	Bitmap myBitmap = BitmapFactory.decodeFile(path);

	        	if(myBitmap != null)
	        	{
//	        		myImageView.setImageBitmap(myBitmap);
	        		myImageView.setRotation(-90); //added by Mike, rotate counter-clockwise once        	        		

	        		//save rotated image in the sdcard
	        		try {
		    			File sdImageMainDirectory = new File(UsbongUtils.BASE_FILE_PATH_TEMP);
						File outputFile= new File(sdImageMainDirectory, myPictureName  +".jpg" );
		        		
		        		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
					  	  
						BufferedOutputStream bos = new BufferedOutputStream(
								fileOutputStream);
		        		
		        		Matrix matrix = new Matrix();
			            matrix.postRotate(-90);
			            Bitmap myBitmapRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
			            myBitmapRotated.compress(CompressFormat.JPEG, 50, bos); //50 quality
			            
						bos.flush();
						bos.close();
						
						File imageFile = new File(UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName+".jpg");				
						
						if(imageFile.exists())
						{
							System.out.println("FILE EXISTS!");
						}	        			
	        		}
	        		catch (Exception e) {
	        			e.printStackTrace();
	        		}
	 			}
*/	 			
            }
        });    
                
    	//added by Mike, 20170403
    	addButton = (Button)findViewById(R.id.add_button);    	
    	addButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {		
					if (verifyFields()) {		
				    	try {	    	
							//read actual file, and write to temp file first
				 			InputStreamReader reader = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+".txt");
				 			BufferedReader br = new BufferedReader(reader);
				 	    	String currLineString;        	
	
				 	    	//write to actual usbong.config file
							PrintWriter out = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+".txt"+"TEMP");
				 	    	while((currLineString=br.readLine())!=null)
				 	    	{ 	
								out.println(currLineString);			 	    		
				 	    	}			 	    	
				 	    	out.println("--");
				 	    	out.println("Title: "+
									((EditText)findViewById(R.id.book_title)).getText().toString());
				 	    	out.println("Author: "+
									((EditText)findViewById(R.id.name_of_principal_author)).getText().toString());
				 	    	out.println("Price: "+
									((EditText)findViewById(R.id.price)).getText().toString());
	
							RadioGroup languageRadioButtonGroup = (RadioGroup)findViewById(R.id.language_radiogroup);
							int languageRadioButtonID = languageRadioButtonGroup.getCheckedRadioButtonId();				
							RadioButton languageRadioButton = (RadioButton) languageRadioButtonGroup.findViewById(languageRadioButtonID);
							String languageSelectedText = languageRadioButton.getText().toString();	 
		
							if (languageSelectedText.equals("Other")) {
					 	    	out.println("Language: "+
										((EditText)findViewById(R.id.other_language)).getText().toString());
							}
							else {
					 	    	out.println("Language: English");
							}			 	    	
				 	    	out.close();
				 	    	
				 	    	//copy temp file to actual usbong.config file
				 			InputStreamReader reader2 = UsbongUtils.getFileFromSDCardAsReader(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+".txt"+"TEMP");	
				 			BufferedReader br2 = new BufferedReader(reader2);    		
				 	    	String currLineString2;        	
	
				 	    	//write to actual usbong.config file
							PrintWriter out2 = UsbongUtils.getFileFromSDCardAsWriter(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+".txt");
	
				 	    	while((currLineString2=br2.readLine())!=null)
				 	    	{ 	
								out2.println(currLineString2);			 	    		
				 	    	}			 	    	
				 	    	out2.close();
				 	    	
//				 	    	UsbongUtils.deleteRecursive(new File(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongConstants.ITEMS_LIST_BOOKS+".txt"+"TEMP"));
				 	    	UsbongUtils.deleteRecursive(new File(UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+".txt"+"TEMP"));
				    	}
				 		catch(Exception e) {
				 			e.printStackTrace();
				 		}	

				    	//added by Mike, 20170414
				       	String path = UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName +".jpg";
			        	Bitmap myBitmap = BitmapFactory.decodeFile(path);

			        	if(myBitmap != null)
			        	{
//			        		myImageView.setImageBitmap(myBitmap);
			        		myImageView.setRotation(rotation); //added by Mike, rotate counter-clockwise once        	        		

			        		//save rotated image in the sdcard
			        		try {
				    			File sdImageMainDirectory = new File(UsbongUtils.BASE_FILE_PATH_TEMP);
								File outputFile= new File(sdImageMainDirectory, myPictureName  +".jpg" );
				        		
				        		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
							  	  
								BufferedOutputStream bos = new BufferedOutputStream(
										fileOutputStream);
				        		
				        		Matrix matrix = new Matrix();
					            matrix.postRotate(-90);
					            Bitmap myBitmapRotated = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);
					            myBitmapRotated.compress(CompressFormat.JPEG, 50, bos); //50 quality
					            
								bos.flush();
								bos.close();
								
								File imageFile = new File(UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName+".jpg");				
								
								if(imageFile.exists())
								{
									System.out.println("FILE EXISTS!");
								}	        			
			        		}
			        		catch (Exception e) {
			        			e.printStackTrace();
			        		}
			 			}
			        	
				    	//added by Mike, 20170406
				    	//copy image file to correct destination
//						String path = UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName +".jpg";								
						String destDir = UsbongUtils.USBONG_TREES_FILE_PATH+UsbongUtils.currCategory+"/";
						String destFilename = ((EditText)findViewById(R.id.book_title)).getText().toString()+".jpg";				    	
						
						File imageFile = new File(path);	
				    	
				        if(imageFile.exists())
				        {
				        	UsbongUtils.copyFileToDestInSDCard(myPictureName +".jpg", UsbongUtils.BASE_FILE_PATH_TEMP, destDir);				        
				    	}
				        imageFile.renameTo(new File(destDir, destFilename));				        
/*					    
					    //added by Mike, 20170310
						//Reference: http://stackoverflow.com/questions/2264622/android-multiple-email-attachments-using-intent
						//last accessed: 14 March 2012
						//has to be an ArrayList
					    ArrayList<Uri> uris = new ArrayList<Uri>();
					    //convert from paths to Android friendly Parcelable Uri's
					    for (String file : attachmentFilePaths)
					    {
					        File fileIn = new File(file);		        
					        if (fileIn.exists()) { //added by Mike, May 13, 2012		        		        
						        Uri u = Uri.fromFile(fileIn);
						        uris.add(u);
//						        System.out.println(">>>>>>>>>>>>>>>>>> u: "+u);
					        }
					    }
					    i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
					    
					    try {
					    	isSendingData=true; //added by Mike, 20170225
					        startActivityForResult(Intent.createChooser(i, "Sending email..."), 1); 
					    } catch (android.content.ActivityNotFoundException ex) {
					        Toast.makeText(AddItemActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
					    }	
*/					    
				        
				        //added by Mike, 20170413
						finish();    
						Intent toUsbongDecisionTreeEngineActivityIntent = new Intent(AddItemActivity.this, UsbongDecisionTreeEngineActivity.class);
						toUsbongDecisionTreeEngineActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
						startActivity(toUsbongDecisionTreeEngineActivityIntent);
					}
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
    	
    	TextView bookTitleTextView = ((TextView)findViewById(R.id.book_title));
		String bookTitle = bookTitleTextView.getText().toString();	
		if (bookTitle.trim().equals("")) {
			bookTitleTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			bookTitleTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

    	TextView nameOfPrincipalAuthorTextView = ((TextView)findViewById(R.id.name_of_principal_author));
		String firstNameOfPrincipalAuthor = nameOfPrincipalAuthorTextView.getText().toString();	
		if (firstNameOfPrincipalAuthor.trim().equals("")) {
			nameOfPrincipalAuthorTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			nameOfPrincipalAuthorTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

    	TextView priceTextView = ((TextView)findViewById(R.id.price));
		String price = priceTextView.getText().toString();	
		if (price.trim().equals("")) {
			priceTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
			allFieldsAreFilledUp=false;
		}
		else {
			priceTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
		}

		//added by Mike, 20170306
		RadioGroup languageRadioButtonGroup = (RadioGroup)findViewById(R.id.language_radiogroup);
		int languageRadioButtonID = languageRadioButtonGroup.getCheckedRadioButtonId();				
		RadioButton languageRadioButton = (RadioButton) languageRadioButtonGroup.findViewById(languageRadioButtonID);
		String languageSelectedText = languageRadioButton.getText().toString();	 

		if (languageSelectedText.equals("Other")) {
			TextView otherLanguageTextView = ((TextView)findViewById(R.id.other_language));
			String otherLanguage = otherLanguageTextView.getText().toString();	
			if (otherLanguage.trim().equals("")) {
				otherLanguageTextView.setBackgroundColor(Color.parseColor("#fff9b6")); 
				allFieldsAreFilledUp=false;
			}
			else {
				otherLanguageTextView.setBackgroundColor(Color.parseColor("#EEEEEE")); 
			}			
		}
						
		if (!allFieldsAreFilledUp) {
	        Toast.makeText(AddItemActivity.this, "Please fill up all required fields.", Toast.LENGTH_LONG).show();
	        return false;
		}
		return true;
    }
/*    
    public void saveData() {
		//save data 
        //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
        //; last accessed: 20150609
        //answer by Elenasys
        //added by Mike, 20170207
        SharedPreferences.Editor editor = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE).edit();
        editor.putString("firstName", ((TextView)findViewById(R.id.first_name)).getText().toString());
        editor.putString("surname", ((TextView)findViewById(R.id.surname)).getText().toString());
        editor.putString("contactNumber", ((TextView)findViewById(R.id.contact_number)).getText().toString());

        editor.putString("bookTitle", ((TextView)findViewById(R.id.book_title)).getText().toString());
        editor.putString("firstNameOfPrincipalAuthor", ((TextView)findViewById(R.id.first_name_of_principal_author)).getText().toString());
        editor.putString("surNameOfPrincipalAuthor", ((TextView)findViewById(R.id.surname_of_principal_author)).getText().toString());
        editor.putString("publisher", ((TextView)findViewById(R.id.publisher)).getText().toString());

		RadioGroup languageRadioButtonGroup = (RadioGroup)findViewById(R.id.language_radiogroup);
		int languageRadioButtonID = languageRadioButtonGroup.getCheckedRadioButtonId();				
		RadioButton languageRadioButton = (RadioButton) languageRadioButtonGroup.findViewById(languageRadioButtonID);
		String languageSelectedText;
		if (languageRadioButton!=null) {
			languageSelectedText = languageRadioButton.getText().toString();	 			
		}
		else {
			languageSelectedText = ((RadioButton) languageRadioButtonGroup.getChildAt(0)).getText().toString();
		}
        editor.putString("language", languageSelectedText);

		if (languageSelectedText.equals("Other")) {
	        editor.putString("otherLanguage", ((TextView)findViewById(R.id.other_language)).getText().toString());
		}
		
		RadioGroup formatRadioButtonGroup = (RadioGroup)findViewById(R.id.format_radiogroup);
		int formatRadioButtonID = formatRadioButtonGroup.getCheckedRadioButtonId();				
		RadioButton formatRadioButton = (RadioButton) formatRadioButtonGroup.findViewById(formatRadioButtonID);
		String formatSelectedText;
		if (formatRadioButton!=null) {
			formatSelectedText = formatRadioButton.getText().toString();	 			
		}
		else {
			formatSelectedText = ((RadioButton) formatRadioButtonGroup.getChildAt(0)).getText().toString();
		}
		editor.putString("format", formatSelectedText);

		RadioGroup itemTypeRadioButtonGroup = (RadioGroup)findViewById(R.id.item_type_radiogroup);
		int itemTypeRadioButtonID = itemTypeRadioButtonGroup.getCheckedRadioButtonId();				
		RadioButton itemTypeRadioButton = (RadioButton) itemTypeRadioButtonGroup.findViewById(itemTypeRadioButtonID);
		String itemTypeSelectedText;
		if (itemTypeRadioButton!=null) {
			itemTypeSelectedText = itemTypeRadioButton.getText().toString();	 			
		}
		else {
			itemTypeSelectedText = ((RadioButton) itemTypeRadioButtonGroup.getChildAt(0)).getText().toString();
		}
		editor.putString("bookType", itemTypeSelectedText);

		RadioGroup totalBudgetRadioButtonGroup = (RadioGroup)findViewById(R.id.total_budget_radiogroup);
		int totalBudgetRadioButtonID = totalBudgetRadioButtonGroup.getCheckedRadioButtonId();				
		RadioButton totalBudgetRadioButton = (RadioButton) itemTypeRadioButtonGroup.findViewById(totalBudgetRadioButtonID);
		String totalBudgetSelectedText;
		if (totalBudgetRadioButton!=null) {
			totalBudgetSelectedText = totalBudgetRadioButton.getText().toString();	 			
		}
		else {
			totalBudgetSelectedText = ((RadioButton) totalBudgetRadioButtonGroup.getChildAt(0)).getText().toString();
		}
		editor.putString("totalBudget", totalBudgetSelectedText);
		
		String isbn_10String = ((TextView)findViewById(R.id.isbn_10)).getText().toString();
        editor.putString("isbn_10", isbn_10String);

		String isbn_13String = ((TextView)findViewById(R.id.isbn_13)).getText().toString();
        editor.putString("isbn_13", isbn_13String);
        editor.putString("numberOfCopies", ((TextView)findViewById(R.id.number_of_copies)).getText().toString());
		String commentsString = ((TextView)findViewById(R.id.comments)).getText().toString();					
        editor.putString("comments", commentsString);
        editor.commit();		
    }
*/    
/*
    public void loadData() {
	    //Reference: http://stackoverflow.com/questions/23024831/android-shared-preferences-example
        //; last accessed: 20150609
        //answer by Elenasys
        //added by Mike, 20150207
        SharedPreferences prefs = getSharedPreferences(UsbongConstants.MY_ACCOUNT_DETAILS, MODE_PRIVATE);
        if (prefs!=null) {        	
	    	//added by Mike, 20170328
	    	if (getIntent().getBooleanExtra("newAddItemActivity", false)) {
	        	//added by Mike, 20170310
	        	UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH_TEMP));
    		
      	        ((EditText)findViewById(R.id.first_name)).setText(prefs.getString("firstName", ""));//"" is the default value.
      	        ((EditText)findViewById(R.id.surname)).setText(prefs.getString("surname", ""));//"" is the default value.
      	        ((EditText)findViewById(R.id.contact_number)).setText(prefs.getString("contactNumber", ""));//"" is the default value.    	            	      

      	        //added by Mike, 20170303
    	        RadioGroup languageRadioButtonGroup = ((RadioGroup)findViewById(R.id.language_radiogroup));
    		    ((RadioButton)languageRadioButtonGroup.getChildAt(0)).setChecked(true);

    	        //added by Mike, 20170303
    	        RadioGroup formatRadioButtonGroup = ((RadioGroup)findViewById(R.id.format_radiogroup));
    		    ((RadioButton)formatRadioButtonGroup.getChildAt(0)).setChecked(true);

    	        //added by Mike, 20170303
    	        RadioGroup itemTypeRadioButtonGroup = ((RadioGroup)findViewById(R.id.item_type_radiogroup));
    		    ((RadioButton)itemTypeRadioButtonGroup.getChildAt(0)).setChecked(true);

    	        //added by Mike, 20170330
    	        RadioGroup totalBudgetRadioButtonGroup = ((RadioGroup)findViewById(R.id.total_budget_radiogroup));
    		    ((RadioButton)totalBudgetRadioButtonGroup.getChildAt(0)).setChecked(true);
    		    
    		    reset();
	    	}
	        else {
		      ((EditText)findViewById(R.id.first_name)).setText(prefs.getString("firstName", ""));//"" is the default value.
		      ((EditText)findViewById(R.id.surname)).setText(prefs.getString("surname", "")); //"" is the default value.
		      ((EditText)findViewById(R.id.contact_number)).setText(prefs.getString("contactNumber", "")); //"" is the default value
	
		      ((EditText)findViewById(R.id.book_title)).setText(prefs.getString("bookTitle", "")); //"" is the default value
		      ((EditText)findViewById(R.id.first_name_of_principal_author)).setText(prefs.getString("firstNameOfPrincipalAuthor", "")); //"" is the default value
		      ((EditText)findViewById(R.id.surname_of_principal_author)).setText(prefs.getString("surNameOfPrincipalAuthor", "")); //"" is the default value
		      ((EditText)findViewById(R.id.publisher)).setText(prefs.getString("publisher", "")); //"" is the default value
		      
		      RadioGroup languageRadioButtonGroup = ((RadioGroup)findViewById(R.id.language_radiogroup));
			  for (int i=0; i<languageRadioButtonGroup.getChildCount(); i++) {
			      if (((RadioButton)languageRadioButtonGroup.getChildAt(i)).getText().equals(prefs.getString("language", ""))) {
					  ((RadioButton)languageRadioButtonGroup.getChildAt(i)).setChecked(true);		    	  
			      }			  
			  }
	
			  if (prefs.getString("language", "").equals("Other")) {
			      ((EditText)findViewById(R.id.other_language)).setText(prefs.getString("otherLanguage", "")); //"" is the default value
			  }
	
		      RadioGroup formatRadioButtonGroup = ((RadioGroup)findViewById(R.id.format_radiogroup));
			  for (int i=0; i<formatRadioButtonGroup.getChildCount(); i++) {
			      if (((RadioButton)formatRadioButtonGroup.getChildAt(i)).getText().equals(prefs.getString("format", ""))) {
					  ((RadioButton)formatRadioButtonGroup.getChildAt(i)).setChecked(true);		    	  
			      }			  
			  }
	
		      RadioGroup itemTypeRadioButtonGroup = ((RadioGroup)findViewById(R.id.item_type_radiogroup));
			  for (int i=0; i<itemTypeRadioButtonGroup.getChildCount(); i++) {
			      if (((RadioButton)itemTypeRadioButtonGroup.getChildAt(i)).getText().equals(prefs.getString("bookType", ""))) {
					  ((RadioButton)itemTypeRadioButtonGroup.getChildAt(i)).setChecked(true);		    	  
			      }			  
			  }
	
		      RadioGroup totalBudgetRadioButtonGroup = ((RadioGroup)findViewById(R.id.total_budget_radiogroup));
			  for (int i=0; i<totalBudgetRadioButtonGroup.getChildCount(); i++) {
			      if (((RadioButton)totalBudgetRadioButtonGroup.getChildAt(i)).getText().equals(prefs.getString("totalBudget", ""))) {
					  ((RadioButton)totalBudgetRadioButtonGroup.getChildAt(i)).setChecked(true);		    	  
			      }			  
			  }
			  
		      ((EditText)findViewById(R.id.isbn_10)).setText(prefs.getString("isbn_10", "")); //"" is the default value
		      ((EditText)findViewById(R.id.isbn_13)).setText(prefs.getString("isbn_13", "")); //"" is the default value
		      
		      ((EditText)findViewById(R.id.number_of_copies)).setText(prefs.getString("numberOfCopies", "")); //"" is the default value
		      ((EditText)findViewById(R.id.comments)).setText(prefs.getString("comments", "")); //"" is the default value
	        }
    	}
    }
*/    
    public void reset() {
    	//added by Mike, 20170403
    	UsbongUtils.deleteRecursive(new File(UsbongUtils.BASE_FILE_PATH_TEMP));

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
				Intent toUsbongDecisionTreeEngineActivityIntent = new Intent(AddItemActivity.this, UsbongDecisionTreeEngineActivity.class);
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
		    	new AlertDialog.Builder(AddItemActivity.this).setTitle("About")
				.setMessage(UsbongUtils.readTextFileInAssetsFolder(AddItemActivity.this,"credits.txt")) //don't add a '/', otherwise the file would not be found
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

	//added by Mike, 20170309
    @Override
    public void onRestart() 
    {
        super.onRestart();
        
    	initTakePhotoScreen();
    }
    
	//added by Mike, 20170309
    public void initTakePhotoScreen()
    {
//    	myPictureName=currUsbongNode; //make the name of the picture the name of the currUsbongNode
    	myPictureName=UsbongUtils.processStringToBeFilenameReady(/*((TextView)findViewById(R.id.book_title)).getText().toString()+*/UsbongUtils.getDateTimeStamp()); 
    	
//		String path = "/sdcard/usbong/"+ UsbongUtils.getTimeStamp() +"/"+ myPictureName +".jpg";
		String path = UsbongUtils.BASE_FILE_PATH_TEMP + myPictureName +".jpg";		
		//only add path if it's not already in attachmentFilePaths

		if (!attachmentFilePaths.contains(path)) {
			attachmentFilePaths.add(path);
		}
		
    	myImageView = (ImageView) findViewById(R.id.CameraImage);

    	File imageFile = new File(path);
        
        if(imageFile.exists())
        {
        
        	Bitmap myBitmap = BitmapFactory.decodeFile(path);
        	if(myBitmap != null)
        	{
        		myImageView.setImageBitmap(myBitmap);
/*        		myImageView.setRotation(90);//added by Mike, rotate counter-clockwise once        	
*/        		
 			}
 
        	//Read more: http://www.brighthub.com/mobile/google-android/articles/64048.aspx#ixzz0yXLCazcU                	  
    	}

        photoCaptureButton = (Button)findViewById(R.id.photo_capture_button);
		photoCaptureIntent = new Intent().setClass(this, CameraActivity.class);
		photoCaptureIntent.putExtra("myPictureName",myPictureName);
		photoCaptureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(photoCaptureIntent);
			}
    	});

    }
}