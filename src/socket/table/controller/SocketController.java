package socket.table.controller;

/*
----------------------------------------------------------------------------
Author(s):     Maxwell Pettit

Date:          4/1/2019

Description:   SocketTables provide a socket based communication protocol 
               for performing simple in-memory CRUD (Create, Read, Update, 
               Delete) operations. SocketTables are designed to use JSON 
               messages to provide access to a key-value mapping on a 
               Python server.
----------------------------------------------------------------------------
*/

import com.studiohartman.jamepad.ControllerAxis;
import com.studiohartman.jamepad.ControllerButton;
import com.studiohartman.jamepad.ControllerIndex;
import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerUnpluggedException;

import socket.table.client.SocketTableClient;

public class SocketController {

	public static void main(String[] args) {
		ControllerManager controllers = new ControllerManager();
		controllers.initSDLGamepad();

		ControllerIndex controller = controllers.getControllerIndex(0);

		SocketTableClient client = new SocketTableClient();

		while (true) {

			controllers.update();

			try {
				if (controller.isButtonJustPressed(ControllerButton.A)) {
					client.updateBoolean("BTN_A", true);
				}
				if (controller.isButtonJustPressed(ControllerButton.B)) {
					client.updateBoolean("BTN_B", true);
				}
				if (controller.isButtonJustPressed(ControllerButton.X)) {
					client.updateBoolean("BTN_X", true);
				}
				if (controller.isButtonJustPressed(ControllerButton.Y)) {
					client.updateBoolean("BTN_Y", true);
				}
				if (controller.isButtonJustPressed(ControllerButton.START)) {
					client.updateBoolean("BTN_START", true);
				}
				if (controller.isButtonJustPressed(ControllerButton.BACK)) {
					client.updateBoolean("BTN_SELECT", true);
				}

				client.updateDouble("leftY", controller.getAxisState(ControllerAxis.LEFTY));
				client.updateDouble("leftX", controller.getAxisState(ControllerAxis.LEFTX));
				client.updateDouble("rightY", controller.getAxisState(ControllerAxis.RIGHTY));
				client.updateDouble("rightX", controller.getAxisState(ControllerAxis.RIGHTX));

			} catch (ControllerUnpluggedException e) {
				System.out.print("Controller unplugged");
				break;
			}

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		controllers.quitSDLGamepad();

	}
}
