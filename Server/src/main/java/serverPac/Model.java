package serverPac;

import serverPac.services.netService.NetService;

public class Model {
    NetService netService = new NetService();

    public Model() {
    }

    public void startChannels(int commandPORT, int carrierPORT) {
        netService.startCommServer(commandPORT);
        // netService.startCarrierServer(carrierPORT);
    }
}
