Aviasales/JetRadar Android SDK 2.0
=================

Aviasales/JetRadar Android SDK is a framework integrating flight search engine into your app. When your customer books a flight, we pay you a [commission fee](https://www.travelpayouts.com). Framework is based on leading flight search engines [Aviasales](http://www.aviasales.ru) and [JetRadar](http://www.jetradar.com).

SDK supports all Android devices with Android 2.3 (API 9) and higher.

The framework consists of:
* search API library for search server interaction;
* user interface [template project](https://github.com/KosyanMedia/Aviasales-Android-SDK/wiki/Template-project-screens);
* [demo application](https://github.com/KosyanMedia/Aviasales-Android-SDK/tree/master/demo) based on template project.

![][1]

Learn more and complete integration with [Aviasales Android SDK Documentation](https://github.com/KosyanMedia/Aviasales-Android-SDK/wiki/Aviasales-Android-SDK-Documentation).
<br>Learn more about earnings in [Travelpayouts FAQ](https://support.travelpayouts.com/hc/en-us/articles/203955613-Commission-and-payments).

More languages: [RUS] [Документация Aviasaels Android SDK](https://github.com/KosyanMedia/Aviasales-Android-SDK/wiki/%D0%94%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%B0%D1%86%D0%B8%D1%8F-Aviasales-Android-SDK).


## Installation

### Add gradle dependencies 

To add Aviasales SDK Library to your project use gradle:

```gradle
repositories {
    maven { url 'http://android.aviasales.ru/repositories/' }
}

dependencies {
    compile 'ru.aviasales.template:aviasalesSdk:2.0.5-sdk'
}
```



If you want to use complete Aviasales SDK Template, you can add it like this  :

```gradle
repositories {
    maven { url 'http://android.aviasales.ru/repositories/' }
}

dependencies {
    compile 'ru.aviasales.template:aviasalesSdkTemplate:2.0.5-sdk'
}
```

### Initialization of Aviasales SDK

Before any interaction with Aviasales SDK or Aviasales Template you should initialize it 
```java
  		AviasalesSDK.getInstance().init(this, new IdentificationData(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN)); 
```

Change TRAVEL_PAYOUTS_MARKER and TRAVEL_PAYOUTS_TOKEN to your marker and token params. You can get them at [Travelpayouts.com](https://www.travelpayouts.com/developers/api):



## Example of adding Aviasales Template to your project 

### Add `AviasalesFragment` to your activity 

Add to main activity layout `activity_main.xml` the `FrameLayout` for fragments 

```xml
 	<FrameLayout
		android:id="@+id/fragment_place"
		android:layout_width="match_parent"
		android:layout_height="match_parent"/>
```

Add fragment to `MainActivity`

```java	
  public class MainActivity extends AppCompatActivity {
  	//Replace these variables on your TravelPayouts marker and token
  	private final static String TRAVEL_PAYOUTS_MARKER = "your_travel_payouts_marker";
	private final static String TRAVEL_PAYOUTS_TOKEN = "your_travel_payouts_token";
  	private AviasalesFragment aviasalesFragment;
    ...
  
  	@Override
  	protected void onCreate(Bundle savedInstanceState) {
  		super.onCreate(savedInstanceState);
  
   		// Initialization of AviasalesSDK. 
		AviasalesSDK.getInstance().init(this, new IdentificationData(TRAVEL_PAYOUTS_MARKER, TRAVEL_PAYOUTS_TOKEN));
  		setContentView(R.layout.activity_main);
     
  		initFragment();
 	}
      ...
  
  	private void initFragment() {
  		FragmentManager fm = getSupportFragmentManager();
  
  		aviasalesFragment = (AviasalesFragment) fm.findFragmentByTag(AviasalesFragment.TAG); // finding fragment by tag
  
  
  		if (aviasalesFragment == null) { 
  			aviasalesFragment = (AviasalesFragment) AviasalesFragment.newInstance();
  		}
  
  		FragmentTransaction fragmentTransaction = fm.beginTransaction(); // adding fragment to fragment manager
  		fragmentTransaction.replace(R.id.fragment_place, aviasalesFragment, AviasalesFragment.TAG);
  		fragmentTransaction.commit();
  	}
}
```

### Specify permissions

Don't forget to specify permissions `INTERNET` and `ACCESS_NETWORK_STATE` by adding `<uses-permission>` elements as children of the `<manifest>` element. 

```xml
	<uses-permission android:name="android.permission.INTERNET"/>
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
```


### Adding onBackPressed 

For proper back navigation between aviasales child fragments add fragment `onBackPressed` inside of activity `onBackPressed` 

```java
	@Override
	public void onBackPressed() {

    ...
		if (!aviasalesFragment.onBackPressed()) {
			super.onBackPressed();
		}
		...
		
	}
```

### Customization

For proper customization of Aviasales Template use `AviasalesTemplateTheme` or extend your theme from it. For example, in `AndroidManifest.xml` specify your theme

```xml    
    <application
        android:icon="@drawable/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/AppTheme" >
        <activity
            ...
```

and in `styles.xml` extend your theme from `AviasalesTemplateTheme`

```xml
	<style name="AppTheme" parent="AviasalesTemplateTheme">
            ...
	</style>
```

To change colors of your app override `colorAsPrimary`, `colorAsPrimaryDark` and `colorAviasalesMain`  in `colors.xml`. This is main Aviasales Template colors

```xml
    <color name="colorAsPrimary">#3F51B5</color>
    <color name="colorAsPrimaryDark">#3F51B5</color>
    <color name="colorAviasalesMain">#3F51B5</color>

```

For more information see the [demo project](https://github.com/KosyanMedia/Aviasales-Android-SDK/tree/master/demo)

### [Aviasales Android API](https://github.com/KosyanMedia/Aviasales-Android-SDK/wiki/Aviasales-Android-SDK-API-documentation)
###[Template project screens](https://github.com/KosyanMedia/Aviasales-Android-SDK/wiki/Template-project-screens)


[1]: /screenshots/screen.gif "Screenshot1"
