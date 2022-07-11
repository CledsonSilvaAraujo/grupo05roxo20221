package br.uff.ic.lek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;
import com.onesignal.client.ApiClient;
import com.onesignal.client.ApiException;
import com.onesignal.client.Configuration;
import com.onesignal.client.auth.*;
import com.onesignal.client.model.*;
import com.onesignal.client.api.DefaultApi;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.CRC32;


public class AndroidLauncher extends AndroidApplication {
	public static final List<String> DEVICES = new ArrayList<String>();

	private static final String TAG = "JOGO";
	private static final String ONESIGNAL_APP_ID = "92576ca0-c121-46bb-a1bc-24ca96e1173d";
	private static final String ONESIGNAL_API_KEY = "NmRkYTkyZGEtNDQwYS00OTFkLTlhNjgtMDNmYWQxODlhZmY1";
	private static final String ONESIGNAL_USER_KEY_TOKEN = "YmE4OWIwNDctYzMyYi00MWFkLWI2YmQtYmRjYTA3MDVhZmFh";

	protected String playerNickName;
	protected String emailCRC32;
	protected String pwdCRC32;
	protected String sharedPreferencesName = "ALCH0005";

	protected int runningTimes;

	private FirebaseDatabase database;
	private DatabaseReference myRef;
	private DatabaseReference myRefInicial;
	private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

	@Override
	public void onCreate (Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);

		defaultAccountGenerator();
		Log.d(TAG, "################ playerNickName=<"+playerNickName+">################");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useWakelock = true;
		config.useAccelerometer = true;
		useImmersiveMode (true);

		OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
		OneSignal.initWithContext(this);
		OneSignal.setAppId(ONESIGNAL_APP_ID);
		try {
			invitePlayers();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		super.onCreate(savedInstanceState);
		initialize(
			new Alquimia(new AndroidInterfaceClass(playerNickName, emailCRC32, pwdCRC32, runningTimes)),
			config
		);
	}

	private void invitePlayers () throws ApiException {
		ApiClient defaultClient = Configuration.getDefaultApiClient();
		defaultClient.setBasePath("https://onesignal.com/api/v1");

		String appKeyToken = ONESIGNAL_API_KEY;
		String userKeyToken = ONESIGNAL_USER_KEY_TOKEN;

		HttpBearerAuth appKey = (HttpBearerAuth) defaultClient.getAuthentication("app_key");
		appKey.setBearerToken(appKeyToken);
		HttpBearerAuth userKey = (HttpBearerAuth) defaultClient.getAuthentication("user_key");
		userKey.setBearerToken(userKeyToken);

		DefaultApi apiInstance = new DefaultApi(defaultClient);

		Notification notification = new Notification();
		notification.setAppId(ONESIGNAL_APP_ID);
		StringMap message = new StringMap();
		message.setPt("Venha jogar Alquimia!");
		message.setEn("Come play Alquimia!");
		notification.setContents(message);

		String appId = ONESIGNAL_APP_ID;
		OSDeviceState thisDevice = OneSignal.getDeviceState();
		String thisDeviceId = thisDevice.getUserId();
		PlayerSlice players = null;

		try {
			players = apiInstance.getPlayers(appId, 300, 0);
		} catch (ApiException e) {
			e.printStackTrace();
		}

		System.out.println("THIS DEVICE: " + thisDeviceId);
		for (Player player: players.getPlayers()) {
			if (!player.getId().equals(thisDeviceId))
				notification.addIncludePlayerIdsItem(player.getId());
			AndroidLauncher.DEVICES.add(player.getId());
		}

		try {
			CreateNotificationSuccessResponse result = apiInstance.createNotification(notification);
			System.out.println("result is:");
			System.out.println(result);
		} catch (ApiException e) {
			System.err.println("Exception when calling DefaultApi#createNotification");
			System.err.println("Status code: " + e.getCode());
			System.err.println("Reason: " + e.getResponseBody());
			System.err.println("Response headers: " + e.getResponseHeaders());
			e.printStackTrace();
		}
	}

	private void defaultAccountGenerator() {
		Date date = Calendar.getInstance().getTime();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String strDate = dateFormat.format(date);
		byte[] byteArray = strDate.getBytes();
		CRC32 crc32 = new CRC32();
		crc32.update(byteArray);
		long crc32long = crc32.getValue();

		emailCRC32 = "alch" + crc32long + "@gmail.com";
		pwdCRC32 = "alch" + crc32long;
		playerNickName = "" + crc32long;//primeira aposta para apelido do jogador
		runningTimes = 0;

		Log.d(TAG, "################ gen<"+playerNickName+">################");
		Log.d(TAG, "################ gen<"+pwdCRC32+">################");
		Log.d(TAG, "################ gen<"+emailCRC32+">################");

		SharedPreferences sh = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
		String savedPlayerNickName = sh.getString("playerNickName", "");
		String savedPwdCRC32 = sh.getString("pwdCRC32", "");
		String savedEmailCRC32 = sh.getString("emailCRC32", "");

		runningTimes = sh.getInt("runningTimes", 0);

		if ("".equals(savedPlayerNickName)) {
			runningTimes = 1;

			SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
			SharedPreferences.Editor myEdit = sharedPreferences.edit();

			myEdit.putString("playerNickName", playerNickName);
			myEdit.putString("emailCRC32", emailCRC32);
			myEdit.putString("pwdCRC32", pwdCRC32);
			myEdit.putInt("runningTimes", new Integer(runningTimes));
			myEdit.apply();

			savedPlayerNickName=playerNickName;
			savedPwdCRC32= pwdCRC32;
			savedEmailCRC32=emailCRC32;

			Log.d(TAG, "################ gravando ################");
		} else {
			playerNickName=savedPlayerNickName;
			pwdCRC32= savedPwdCRC32;
			emailCRC32=savedEmailCRC32;

			SharedPreferences sharedPreferences = getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
			SharedPreferences.Editor myEdit = sharedPreferences.edit();

			myEdit.putInt("runningTimes", new Integer(++runningTimes));
			myEdit.apply();
		}

		Log.d(TAG, "################ saved<"+playerNickName+">################");
		Log.d(TAG, "################ saved<"+pwdCRC32+">################");
		Log.d(TAG, "################ saved<"+emailCRC32+">################");
		Log.d(TAG, "################ run "+runningTimes+" X ################");
	}
}
