package br.uff.ic.lek;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;

import br.uff.ic.lek.Alquimia;

// https://developers.google.com/android/guides/client-auth
// https://www.geeksforgeeks.org/create-and-add-data-to-firebase-firestore-in-android/


public class AndroidLauncher extends AndroidApplication {
	private static final String TAG = "JOGO";
	protected String playerNickName;
	protected String emailCRC32;
	protected String pwdCRC32;
	protected int runningTimes;
	protected String sharedPreferencesName = "ALCH0005";


	protected void defaultAccountGenerator() {
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

		if (AndroidInterfaceClass.debugFazPrimeiraVez || "".equals(savedPlayerNickName)) {
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

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		defaultAccountGenerator();
		Log.d(TAG, "################ playerNickName=<"+playerNickName+">################");

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();

		config.useWakelock = true;
		config.useAccelerometer = true;
		useImmersiveMode (true);
		initialize(
			new Alquimia(new AndroidInterfaceClass(playerNickName, emailCRC32, pwdCRC32, runningTimes)),
			config
		);
	}
}
