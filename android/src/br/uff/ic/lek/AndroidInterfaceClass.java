package br.uff.ic.lek;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import com.onesignal.OSDeviceState;
import com.onesignal.OneSignal;

import br.uff.ic.lek.actors.Avatar;
import br.uff.ic.lek.game.World;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;


public class AndroidInterfaceClass extends Activity implements InterfaceAndroidFireBase {
	public static final String DEVICE_ID = OneSignal.getDeviceState().getUserId();

	public static final List<String> DEVICES = AndroidLauncher.DEVICES;

	private static final String TAG = "JOGO";

	private static InterfaceLibGDX gameLibGDX = null;

	private FirebaseDatabase database;
	private DatabaseReference myRef;
	private DatabaseReference myRefInicial;
	private DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();
	private FirebaseAuth mAuth;

	private String providerID = "";
	private String uID = "";
	private String email = "";
	private String pwd = "";
	private String playerNickName = "";

	private boolean newAccount;
	private int runningTimes = 0;
	private int fazSoUmaVez=0;

	public AndroidInterfaceClass(String playerNickName, String emailCRC32, String pwdCRC32, int runningTimes) {
		this.playerNickName = playerNickName;
		this.runningTimes = runningTimes;

		Log.d(TAG, "construtor AndroidInterfaceClass execucoes:" +runningTimes+ " playerNickName="+playerNickName+" emailCRC32="+emailCRC32+" pwdCRC32="+pwdCRC32);

		database = FirebaseDatabase.getInstance();
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = mAuth.getCurrentUser();

		if (currentUser == null) {
			try {
				this.createAccount(emailCRC32, pwdCRC32);
				Log.d(TAG, "criou um novo auth com email:" + emailCRC32 + " pwd:" + pwdCRC32);
				this.signIn(emailCRC32, pwdCRC32);
				newAccount = true;
			} catch (Exception e) {
				Log.d(TAG, "Exception signIn " + e.getMessage());
			}
		} else {
			newAccount=false;
		}

		currentUserDefined(currentUser);

//		updateRealTimeDatabaseUserData(currentUser);
	}

	@Override
	public String getDeviceId() {
		return AndroidInterfaceClass.DEVICE_ID;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mAuth = FirebaseAuth.getInstance();
	}

	@Override
	public void onStart() {
		super.onStart();
		FirebaseUser currentUser = mAuth.getCurrentUser();
	}

	@Override
	public void waitForMyMessages() {
		int ultimosUsuarios = 10;

		DatabaseReference players = referencia.child("players");
		Query playerQuery = players.orderByChild("gameState").equalTo("WAITING");

		playerQuery.addValueEventListener(
			new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
						Log.d(TAG, "UID listener dos " + ultimosUsuarios + " users ordem lastUpdateTime " + zoneSnapshot.child("cmd").getValue() + " " + zoneSnapshot.child("lastUpdateTime").getValue());

						if (AndroidInterfaceClass.gameLibGDX == null) return;

						String registrationTime = "" + zoneSnapshot.child("registrationTime").getValue();
						String authUID = "" + zoneSnapshot.child("authUID").getValue();
						String cmd = "" + zoneSnapshot.child("cmd").getValue();
						String lastUpdateTime = "" + zoneSnapshot.child("lastUpdateTime").getValue();
						
						World.world.worldController.onNotification(getCmdDictionary(cmd));

						AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.MY_PLAYER_DATA, registrationTime, authUID, cmd, lastUpdateTime);
					}
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
				}
			}
		);
	}

	@Override
	public void waitForPlayers(){
		DatabaseReference parties = referencia.child("parties");
		DatabaseReference players = referencia.child("players");

		Query playerCreationQuery = parties.orderByChild("one");
		Query playerUpdateQuery = players.orderByChild("gameState").equalTo("PLAYING");

		ValueEventListener onlinePlayersCreation = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
					if (AndroidInterfaceClass.gameLibGDX == null) return;

					for (DataSnapshot player : zoneSnapshot.getChildren()) {
						if (AndroidInterfaceClass.DEVICE_ID.equals(player.getKey())) continue;

						Log.d(TAG, "on data creation for " + player.getKey());

						String cmd = (String) player.child("cmd").getValue();

						Dictionary<String,String> dic = getCmdDictionary(cmd);

						World.world.createOnlinePlayer(
							player.getKey(),
							Float.parseFloat(dic.get("px")),
							Float.parseFloat(dic.get("py"))
						);

						AndroidInterfaceClass.gameLibGDX.enqueueMessage(
							InterfaceLibGDX.ALL_PLAYERS_DATA,
							null,
							AndroidInterfaceClass.DEVICE_ID,
							cmd,
							null
						);
					}
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
			}
		};

		ValueEventListener onlinePlayersUpdate = new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {

				for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
					if (AndroidInterfaceClass.gameLibGDX == null) return;
					if (AndroidInterfaceClass.DEVICE_ID.equals(zoneSnapshot.getKey())) continue;

					Log.d(TAG, "on data change for " + zoneSnapshot.getKey());

					String cmd = (String) zoneSnapshot.child("cmd").getValue();
					Dictionary<String,String> dic = getCmdDictionary(cmd);

					World.world.setOnlinePlayerTarget(
						zoneSnapshot.getKey(),
						Float.parseFloat(dic.get("tx")),
						Float.parseFloat(dic.get("ty"))
					);
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				Log.i(TAG, "playerPesquisa.addValueEventListener onCancelled", databaseError.toException());
			}
		};

		playerCreationQuery.addValueEventListener(onlinePlayersCreation);
		playerUpdateQuery.addValueEventListener(onlinePlayersUpdate);
	}

	@Override
	public void writePlayerData(Avatar player){
		player.setAuthUID(AndroidInterfaceClass.DEVICE_ID);
		myRef = database.getReference("players").child(AndroidInterfaceClass.DEVICE_ID);
		myRef.setValue(player.getFirebaseData());
	}

	@Override
	public void writePartyData(Avatar player) {
		myRef = database.getReference("parties")
				.child(player.getFirebaseData().party)
				.child(player.getAuthUID());
		myRef.setValue(player.getFirebaseData());
	}

	@Override
	public void setLibGDXScreen(InterfaceLibGDX iLibGDX) {
		AndroidInterfaceClass.gameLibGDX = iLibGDX;
	}

	@Override
	public void finishAndRemoveTask(){
		this.finishAndRemoveTask();
	}

	private void createAccount(String email, String password) {
		mAuth.createUserWithEmailAndPassword(email, password)
			.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {
						Log.d(TAG, "createUserWithEmail:success");
						FirebaseUser currentUser = mAuth.getCurrentUser();
						updateUI(currentUser);
					} else {
						Log.w(TAG, "createUserWithEmail:failure", task.getException());
						updateUI(null);
					}
				}
			});
	}

	private void signIn(String email, String password) {
		System.out.println("*********************************");
		System.out.println("***** "+email+" ***** "+password);

		mAuth.signInWithEmailAndPassword(email, password)
			.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
				@Override
				public void onComplete(@NonNull Task<AuthResult> task) {
					if (task.isSuccessful()) {
						Log.d(TAG, "signInWithEmail:success"+email+" "+password);
						FirebaseUser currentUser = mAuth.getCurrentUser();
						updateUI(currentUser);
					} else {
						System.out.println("*********************************");
						System.out.println("***** "+email+" ***** "+password);
						Log.d(TAG, "signInWithEmail:failure"+email+" "+password, task.getException());
						updateUI(null);
					}
				}
			});
	}

	private void sendEmailVerification() {
		final FirebaseUser user = mAuth.getCurrentUser();
		user.sendEmailVerification()
			.addOnCompleteListener(this, new OnCompleteListener<Void>() {
				@Override
				public void onComplete(@NonNull Task<Void> task) {
				}
			});
	}

	private void updateUI(FirebaseUser currentUser) {
		if (currentUser == null) return;
		providerID = currentUser.getProviderId();
		uID = currentUser.getUid();
		email = currentUser.getEmail();
		Log.d(TAG, "updateUI providerID:" + providerID + " uID:" + uID + " email:" + email);
		currentUserDefined(currentUser);
		updateRealTimeDatabaseUserData(currentUser);
	}

	private void currentUserDefined(FirebaseUser currentUser) {
		if (currentUser == null) {
			Log.d(TAG, "currentUser eh null");
			return;
		}

		email = currentUser.getEmail();
		try {
			final String before = email.split("@")[0];
			playerNickName = before;
		} catch (Exception e) {
			Log.d(TAG, "nao encontrou @");
		}

		Log.d(TAG, "playerNickName:" + playerNickName);
		uID = currentUser.getUid();
		providerID = currentUser.getProviderId();
		Log.d(TAG, "currentUser " + email + " " + providerID);
		String original = email;
		String _pwd = original.replace("@gmail.com", "");
		pwd = _pwd;
		Log.d(TAG, "Use SQLite to save email=" + email + " pwd=" + pwd + " uID=" + uID);
	}

	private void updateRealTimeDatabaseUserData(FirebaseUser currentUser) {
		if (currentUser != null) {
			PlayerData pd = new PlayerData();
			pd.gameState = PlayerData.States.READY;
			pd.cmd = "{cmd:startup,px:1.1,py:2.2,pz:3.3}";
			pd.nickName = playerNickName;

			Log.d(TAG,"WAITING");

			myRef = database.getReference("players").child(AndroidInterfaceClass.DEVICE_ID);
			myRef.setValue(pd);
		}
	}

	private Dictionary<String,String> getCmdDictionary(String cmd) {
		if (cmd == null) return null;
		cmd = cmd.replaceAll("\\{","");
		cmd = cmd.replaceAll("\\}","");
		String[] params = cmd.split(",");

		Dictionary<String,String> dictionaryParams = new Hashtable<String,String>();

		for(String param : params) {
			String[] data = param.split(":");
			String key = data[0];
			String value = data[1];
			dictionaryParams.put(key,value);
		}

		System.out.println("original command: "+ cmd);
		System.out.println("command in dictionary: "+ dictionaryParams);
		return  dictionaryParams;
	}
}






