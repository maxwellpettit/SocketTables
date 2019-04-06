package socket.table.controller;

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

		// Print a message when the "A" button is pressed. Exit if the "B" button is
		// pressed or the controller disconnects.
		ControllerIndex controller = controllers.getControllerIndex(0);

		SocketTableClient client = new SocketTableClient();

		while (true) {
			// If using ControllerIndex, you should call update() to check if a new
			// controller was plugged in or unplugged at this index.
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
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		controllers.quitSDLGamepad();

	}
}
