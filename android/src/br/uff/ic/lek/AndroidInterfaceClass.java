package br.uff.ic.lek;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;

import br.uff.ic.lek.actors.Avatar;

// para espelhar a tela de seu celular no Ubuntu
// https://diolinux.com.br/tutoriais/espelhe-tela-do-seu-android-no-seu-linux-com-o-scrcpy.html


public class AndroidInterfaceClass extends Activity implements InterfaceAndroidFireBase {
	private static final String TAG = "JOGO";

	public static final boolean debugFazPrimeiraVez = false;
	public static InterfaceLibGDX gameLibGDX = null;

	private FirebaseAuth mAuth;

	private int fazSoUmaVez=0;

	public boolean waitingForTheFirstTime = false;

	FirebaseDatabase database;
	DatabaseReference myRef;
	DatabaseReference myRefInicial;
	DatabaseReference referencia = FirebaseDatabase.getInstance().getReference();

	String providerID = "";
	String uID = "";
	String email = "";
	String pwd = "";
	String playerNickName = "";

	int runningTimes = 0;
	boolean newAccount;

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
						// Email sent
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
			PlayerData pd = PlayerData.myPlayerData();
			pd.setAuthUID(uID);
			pd.setWriterUID(uID);
			pd.setGameState(PlayerData.States.WAITING);
			pd.setChat("empty");
			pd.setAvatarType("A");
			pd.setCmd("{cmd:BErinjela,px:1.1,py:2.2,pz:3.3,cardNumber:4,uID:"+uID+"}");
			Log.d(TAG,"WAITING");
			pd.setPlayerNickName(playerNickName);
			pd.setEmail(email);

			Calendar calendar = Calendar.getInstance();
			java.util.Date now = calendar.getTime();

			java.sql.Timestamp currentTimestamp = new java.sql.Timestamp(now.getTime());

			pd.setTimestamp(currentTimestamp);
			pd.setLastUpdateTime("" + now.getTime());
			pd.setRegistrationTime("" + now.getTime());
			pd.setStateAndLastTime(pd.getGameState()+"_"+pd.getLastUpdateTime());
			pd.setRunningTimes(this.runningTimes);
			Log.d(TAG, "local timestamp:" + currentTimestamp.toString());
			Log.d(TAG, "System timestamp:" + System.currentTimeMillis());

			myRef = database.getReference("players").child(uID);
			myRefInicial = database.getReference("playersData").child(uID);
			if (newAccount){
				Log.d(TAG, "CONTA NOVA:" + pd.getRegistrationTime());
				myRef.setValue(pd);
				myRefInicial.setValue(pd);
			} else {
				Log.d(TAG, "CONTA EXISTENTE:" + pd.getRegistrationTime());
				myRef.setValue(pd);
			}
			if (fazSoUmaVez == 0){
				fazSoUmaVez++;
			}
			Log.d(TAG," fazSoUmaVez:"+fazSoUmaVez);
		}
	}

	public AndroidInterfaceClass(String playerNickName, String emailCRC32, String pwdCRC32, int runningTimes) {
		this.playerNickName = playerNickName;
		this.runningTimes = runningTimes;

		Log.d(TAG, "construtor AndroidInterfaceClass execucoes:" +runningTimes+ " playerNickName="+playerNickName+" emailCRC32="+emailCRC32+" pwdCRC32="+pwdCRC32);

		database = FirebaseDatabase.getInstance();
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = mAuth.getCurrentUser();

		if (AndroidInterfaceClass.debugFazPrimeiraVez || currentUser == null) {
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

		updateRealTimeDatabaseUserData(currentUser);
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
		if (currentUser != null) reload();
	}

	@Override
	public void waitForMyMessages() {
		int ultimosUsuarios = 10;

		DatabaseReference players = referencia.child("players");
		Query playerPesquisa = players.startAt(uID).endAt(uID).orderByChild("authUID").limitToLast(ultimosUsuarios);

		playerPesquisa.addValueEventListener(
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
		int ultimosUsuarios = 10;
		DatabaseReference players = referencia.child("players");

		Query playerPesquisa = players.startAt("READYTOPLAY_-").endAt("READYTOPLAY_~").orderByChild("stateAndLastTime").limitToLast(ultimosUsuarios);

		playerPesquisa.addValueEventListener(
			new ValueEventListener() {
				Dictionary<String,String> getCmdDictionary(String cmd) {
					cmd = cmd.replaceAll("\\{","");
					cmd = cmd.replaceAll("\\}","");
					String[] params = cmd.split(",");
					Dictionary<String,String> dictionaryParams = new Hashtable<String,String>();
					for(String param : params){
						String[] data = param.split(":");
						String key = data[0];
						String value = data[1];
						dictionaryParams.put(key,value);
					}
					System.out.println("COMANDO: "+ cmd);
					System.out.println("DICIONARIO: "+ dictionaryParams);
					return  dictionaryParams;
				}

				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {

					for (DataSnapshot zoneSnapshot : dataSnapshot.getChildren()) {
						Log.d(TAG, "On DataChange for child "+zoneSnapshot.child("authUID").getValue());
						if (AndroidInterfaceClass.gameLibGDX == null) return;
						String registrationTime = zoneSnapshot.child("registrationTime").getValue().toString();
						String authUID = zoneSnapshot.child("authUID").getValue().toString();
						String lastUpdateTime = zoneSnapshot.child("lastUpdateTime").getValue().toString();

						String cmd = zoneSnapshot.child("cmd").getValue().toString();

						System.out.println("Teste: " + getCmdDictionary(cmd).get("event"));

						AndroidInterfaceClass.gameLibGDX.enqueueMessage(InterfaceLibGDX.ALL_PLAYERS_DATA, registrationTime, authUID, cmd, lastUpdateTime);
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
	public void writePlayerData(Avatar player){
		myRef = database.getReference("players").child(player.getAuthUID());
		myRef.setValue(player.getFirebaseData());
	}

	@Override
	public void setLibGDXScreen(InterfaceLibGDX iLibGDX) {
		Log.d(TAG, "chamou setLibGDXScreen");
		AndroidInterfaceClass.gameLibGDX = iLibGDX;
	}

	@Override
	public void finishAndRemoveTask(){
		this.finishAndRemoveTask();
	}
}




