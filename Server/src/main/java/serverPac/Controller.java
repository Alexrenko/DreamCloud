package serverPac;

public class Controller {
    Model model;

    //Это здесь временно. Нужно будет перенести в GUI
    private static final int commandPORT = 8188;
    private static final int carrierPORT = 8189;

    public Controller() {
        model = new Model();
        model.startChannels(commandPORT, carrierPORT);
    }
}
