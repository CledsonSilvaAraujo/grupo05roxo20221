package br.uff.ic.lek.game;

import br.uff.ic.lek.Alquimia;
import br.uff.ic.lek.InterfaceAndroidFireBase;
import br.uff.ic.lek.InterfaceLibGDX;
import br.uff.ic.lek.utils.ClassToast;

import com.badlogic.gdx.graphics.Color;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ClassThreadComandos extends Thread implements InterfaceLibGDX {
	public static final ConcurrentLinkedQueue<ClassComandos> filaComandos = new ConcurrentLinkedQueue<ClassComandos>();
	public static ClassThreadComandos produtorConsumidor;
	public static InterfaceAndroidFireBase objetoAndroidFireBase;
	public static Alquimia screenJogo;

	private int contador = 0;

	public ClassThreadComandos(Alquimia screenJogo) {
		ClassThreadComandos.screenJogo = screenJogo;

		ClassThreadComandos.objetoAndroidFireBase.setLibGDXScreen(this);
		this.start();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (!filaComandos.isEmpty()) {
				ClassComandos elementoFilaComandos = filaComandos.poll();
				if (elementoFilaComandos != null)	processaCmd(elementoFilaComandos);
			} else {
				contador++;
				if (contador % 100 == 0) System.out.println("idle "+ contador);
			}
		}
	}

	@Override
	public void enqueueMessage(String querySource, String registrationTime, String authUID, String cmd, String lastUpdateTime) {
		System.out.println("chegou msg do FireBase module " + " registrationTime=" + registrationTime + " authUID=" + authUID + " cmd=" + cmd);
		
		ClassComandos elementoFilaComandos = new ClassComandos(querySource, registrationTime, authUID, cmd, lastUpdateTime);
		ClassThreadComandos.filaComandos.add(elementoFilaComandos);
	}

	@Override
	public void parseCmd(String authUID, String cmdJson)  {
		Object obj = ClassMessage.decodeCurrentPos(cmdJson);

		System.out.println("YOUKNOW: " + authUID);

		Boolean isMyPlayerData = authUID.equals(World.world.getMainPlayer().getAuthUID());
		Boolean isMoveTo = ((ClassMessage) obj).getCmd().contentEquals("MOVETO");

		if (isMyPlayerData && isMoveTo) {
			float x = ((ClassMessage) obj).getPx();
			float y = ((ClassMessage) obj).getPy();
			World.world.worldController.comandoMoveTo(x,y);
		}

		System.out.println(
			" class ClassMessage" +
			"\n cmd " + ((ClassMessage) obj).getCmd() +
			" x=" + ((ClassMessage) obj).getPx() +
			" y=" + ((ClassMessage) obj).getPy() +
			" cardNumber=" + ((ClassMessage) obj).getCardNumber() +
			" uID=" + ((ClassMessage) obj).getuID()
		);
	}

	private void processaCmd(ClassComandos elementoFilaComandos) {
		String cmd = elementoFilaComandos.getCmd();

		if (cmd == null) return;

		String querySource = elementoFilaComandos.getQuerySource();

		if (querySource == InterfaceLibGDX.MY_PLAYER_DATA) {
			Color backgroundColor = new Color(1f, 1f, 1f, 0.9f);
			Color fontColor = new Color(1, 0, 0, 0.9f);
			ClassToast.toastRich(
				querySource + " " + cmd,
				backgroundColor, fontColor, ClassToast.LONG
			);
		} else {
			Color backgroundColor = new Color(0f, 0f, 0f, 0.9f);
			Color fontColor = new Color(1, 1, 1, 0.9f);
			ClassToast.toastRich(
				querySource + " " + cmd,
				backgroundColor, fontColor, ClassToast.LONG
			);
		}

		System.out.println("DEVICE_ID: " + ClassThreadComandos.objetoAndroidFireBase.getDeviceId());

		parseCmd(ClassThreadComandos.objetoAndroidFireBase.getDeviceId(), cmd);
	}
}
