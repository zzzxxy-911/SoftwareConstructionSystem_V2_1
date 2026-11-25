package system.util;

import system.entity.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static final String FILE_PATH = "user.txt";

    public static List<User> loadUsers() {
        List<User> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 4) {
                    User user = new User();
                    user.setUsername(data[0]);
                    user.setPassword(data[1]);
                    user.setRole(data[2]);
                    user.setRegisterTime(data[3]);
                    list.add(user);
                }
            }
        } catch (IOException e) {
            System.out.println("读取用户失败:" + e.getMessage());
        }
        return list;
    }

    public static void saveUser(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(user.getUsername() + "," + user.getPassword() + ","
                    + user.getRole() + "," + user.getRegisterTime());
            writer.newLine();
        } catch (IOException e) { System.out.println("保存用户失败:" + e.getMessage()); }
    }
}