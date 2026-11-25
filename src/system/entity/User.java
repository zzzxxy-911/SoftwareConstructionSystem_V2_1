package system.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 用户实体类
 * 设计原则：仅用于承载数据，不包含业务逻辑
 */
public class User {
    private String username;
    private String password;
    private String role;
    private String registerTime;

    public User() {

    }

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.registerTime = generateTimeString();
    }

    /**
     * 生成当前时间字符串
     */
    private String generateTimeString() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    // ================= Get/Set 方法 =================
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getRegisterTime() {
        return registerTime;
    }
    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    @Override
    public String toString() {
        return "User{账号='" + username + "', 角色='" + role + "'}";
    }
}