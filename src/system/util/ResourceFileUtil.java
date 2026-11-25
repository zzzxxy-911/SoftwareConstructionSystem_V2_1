package system.util;

import system.entity.CampusResource;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ResourceFileUtil {
    private static final String FILE_PATH = "resource.txt";

    public static List<CampusResource> loadResources() {
        List<CampusResource> list = new ArrayList<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return list;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    String desc = data.length == 6 ? data[5] : "";
                    list.add(new CampusResource(data[0], data[1], data[2], data[3], data[4], desc));
                }
            }
        } catch (IOException e) {
            System.out.println("读取资源失败:" + e.getMessage());
        }
        return list;
    }

    public static void saveResources(List<CampusResource> list) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (CampusResource res : list) {
                String desc = res.getDescription() == null ? "" : res.getDescription();
                writer.write(res.getId() + "," + res.getName() + "," + res.getType() + ","
                        + res.getStatus() + "," + res.getLocation() + "," + desc);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("保存资源失败:" + e.getMessage());
        }
    }
}