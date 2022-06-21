/*
	Fábrica de Software para Educação
	Professor Lauro Kozovits, D.Sc.
	ProfessorKozovits@gmail.com
	Universidade Federal Fluminense, UFF
	Rio de Janeiro, Brasil
	Subprojeto: Alchemie Zwei

	Partes do software registradas no INPI como integrantes de alguns apps para smartphones
	Copyright @ 2016..2022

	Se você deseja usar partes do presente software em seu projeto, por favor mantenha esse cabeçalho e peça autorização de uso.
	If you wish to use parts of this software in your project, please keep this header and ask for authorization to use.
*/

package br.uff.ic.lek.game;

import br.uff.ic.lek.Alquimia;
import br.uff.ic.lek.InterfaceAndroidFireBase;
import br.uff.ic.lek.InterfaceLibGDX;
import br.uff.ic.lek.PlayerData;
import br.uff.ic.lek.utils.ClassToast;

import com.badlogic.gdx.graphics.Color;

import java.util.concurrent.ConcurrentLinkedQueue;


public class ClassThreadComandos extends Thread implements InterfaceLibGDX {
	public static final ConcurrentLinkedQueue<ClassComandos> filaComandos = new ConcurrentLinkedQueue<ClassComandos>();
	public static ClassThreadComandos produtorConsumidor;
	public static InterfaceAndroidFireBase objetoAndroidFireBase;
	public static Alquimia screenJogo;

	private int contador =0;
	private final int lineNumber=1;

	public ClassThreadComandos(Alquimia screenJogo) {
		ClassThreadComandos.screenJogo = screenJogo;

		ClassThreadComandos.objetoAndroidFireBase.setLibGDXScreen(this);
		this.start();
	}

	private void processaCmd(ClassComandos elementoFilaComandos) {
		String cmd = elementoFilaComandos.getCmd();
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

		parseCmd(elementoFilaComandos.getAuthUID(), cmd);
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
		PlayerData pd = PlayerData.myPlayerData();
		
		ClassComandos elementoFilaComandos = new ClassComandos(querySource, registrationTime, authUID, cmd, lastUpdateTime);
		ClassThreadComandos.filaComandos.add(elementoFilaComandos);
	}

	@Override
	public void parseCmd(String authUID, String cmdJson)  {
		Object obj = ClassMessage.decodeCurrentPos(ClassMessage.class, cmdJson);

		Boolean isMyPlayerData = authUID.equals(PlayerData.myPlayerData().getAuthUID());
		Boolean isMoveTo = ((ClassMessage) obj).getCmd().contentEquals("MOVETO");

		if (isMyPlayerData && isMoveTo) {
				float x = ((ClassMessage) obj).getPx();
				float y = ((ClassMessage) obj).getPy();
				World.world.worldController.comandoMoveTo(x,y);
		}

		System.out.println(
			"\nclasse " + ((ClassMessage) obj).getClssName() +
			" class " + ((ClassMessage) obj).getClss() +
			"\n cmd " + ((ClassMessage) obj).getCmd() +
			" x=" + ((ClassMessage) obj).getPx() +
			" y=" + ((ClassMessage) obj).getPy() +
			" z=" + ((ClassMessage) obj).getPz() +
			" cardNumber=" + ((ClassMessage) obj).getCardNumber() +
			" uID=" + ((ClassMessage) obj).getuID()
		);
	}
}
