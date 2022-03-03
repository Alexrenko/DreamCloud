package serverPac.services;

import serverPac.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private static Map<String, String> registeredClients = new HashMap<>();
    private static ArrayList<Client> authorizedClients = new ArrayList<>();

    static {
        registeredClients.put("login", "pass");
    }

    public boolean isRegistered(String login, String password) {
        for(Map.Entry<String, String> entry : registeredClients.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (login.equals(key) && password.equals(value))
                return true;
        }
        return false;
        //если такой пользователь зарегистрирован, то:
        // - отправляем команду об успешной авторизации и запрос канала передачи файлов
        // - создаем нового пользователя и заносим его в базу данных авторизованных пользователей
    }


}
