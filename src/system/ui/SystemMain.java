package system.ui;

import system.entity.CampusResource;
import system.entity.Result;
import system.entity.User;
import system.exception.*;
import system.util.FileUtil;
import system.util.ResourceFileUtil;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

/**
 * 统一返回结果封装 Result
 * 规范落实：业务抛出异常、顶层捕获并转为 Result
 */
public class SystemMain {

    private final static List<User> userList = FileUtil.loadUsers();
    private final static List<CampusResource> resourceList = ResourceFileUtil.loadResources();
    private static User currentUser = null;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean isRunning = true;

        while (isRunning) {
            try {
                if (currentUser == null) {
                    isRunning = displayGuestMenu(scanner);
                } else if ("admin".equals(currentUser.getRole())) {
                    displayAdminMenu(scanner);
                } else if ("student".equals(currentUser.getRole())) {
                    displayStudentMenu(scanner);
                }
            } catch (BusinessException e) {
                // 核心：统一捕获业务异常并转为 Result.fail 处理
                Result<Void> failResult = Result.fail(e.getMessage());
                System.out.println("【操作提示】" + failResult.getMsg());
            } catch (Exception e) {
                System.out.println("【系统错误】系统异常，请联系管理员。");
            }
        }
        scanner.close();
    }

    // ================= 菜单展示层 =================

    private static boolean displayGuestMenu(Scanner scanner) {
        System.out.println("\n=== 校园资源系统 ===");
        System.out.println("1. 登录  2. 注册  3. 退出");
        System.out.print("请输入：");
        String choice = scanner.nextLine();

        Result<?> result;
        switch (choice) {
            case "1":
                result = executeLogin(scanner);break;
            case "2":
                result = executeRegister(scanner);break;
            case "3":
                System.out.println("系统已退出。");return false;
            default:
                System.out.println("无效选项！");return true;
        }
        displayResult(result);
        return true;
    }

    private static void displayAdminMenu(Scanner scanner) {
        System.out.println("\n--- 管理员菜单 ---");
        System.out.println("1. 新增资源  2. 查询全部资源  3. 修改资源  4. 删除资源  5. 注销");
        System.out.print("请输入：");
        String choice = scanner.nextLine();

        Result<?> result;
        switch (choice) {
            case "1":
                result = addResource(scanner); break;
            case "2":
                result = displayResourceList(); break;
            case "3":
                result = updateResource(scanner); break;
            case "4":
                result = deleteResource(scanner); break;
            case "5":
                currentUser = null; result = Result.success("已注销。"); break;
            default:
                result = Result.fail("无效选项！");
        }
        displayResult(result);
    }

    private static void displayStudentMenu(Scanner scanner) {
        System.out.println("\n--- 学生菜单 ---");
        System.out.println("1. 查询可预约资源  2. 测试越权异常  3. 注销");
        System.out.print("请输入：");
        String choice = scanner.nextLine();

        Result<?> result;
        switch (choice) {
            case "1":
                result = displayAvailableResource(); break;
            case "2":
                throw new PermissionDeniedException("学生无权执行此操作！");
            case "3":
                currentUser = null; result = Result.success("已注销。"); break;
            default:
                result = Result.fail("无效选项！");
        }
        displayResult(result);
    }

    /**
     * 统一处理 Result 对象的展示
     */
    private static void displayResult(Result<?> result) {
        if (result.isSuccess()) {
            System.out.println("【成功】" + result.getMsg());
            if (result.getData() != null) {
                if (result.getData() instanceof List) {
                    for (Object obj : (List<?>) result.getData()) {
                        System.out.println(obj);
                    }
                } else {
                    System.out.println(result.getData());
                }
            }
        } else {
            System.out.println("【失败】" + result.getMsg());
        }
    }

    // ================= 业务逻辑层（返回 Result） =================

    private static Result<Void> addResource(Scanner scanner) {
        System.out.print("输入资源名称：");
        String name = scanner.nextLine();
        if (name.trim().isEmpty()) {
            throw new ParamEmptyException("资源名称不能为空！");
        }
        System.out.print("输入类型：");
        String type = scanner.nextLine();
        System.out.print("输入位置：");
        String location = scanner.nextLine();
        System.out.print("输入描述(可留空)：");
        String desc = scanner.nextLine();

        String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        CampusResource res = new CampusResource(id, name, type, "AVAILABLE", location, desc);
        resourceList.add(res);
        ResourceFileUtil.saveResources(resourceList);
        return Result.success("资源新增成功，ID：" + id);
    }

    private static Result<List<CampusResource>> displayResourceList() {
        if (resourceList.isEmpty()) {
            return Result.fail("系统暂无资源。");
        }
        return Result.success("查询成功", resourceList);
    }

    private static Result<List<CampusResource>> displayAvailableResource() {
        List<CampusResource> availableList = new java.util.ArrayList<>();
        for (CampusResource r : resourceList) {
            if ("AVAILABLE".equals(r.getStatus())) {
                availableList.add(r);
            }
        }
        if (availableList.isEmpty()) {
            return Result.fail("当前没有可预约的资源。");
        }
        return Result.success("查询成功", availableList);
    }

    private static Result<Void> updateResource(Scanner scanner) {
        System.out.print("请输入要修改的资源ID：");
        String id = scanner.nextLine();
        CampusResource target = queryResourceById(id);
        if (target == null) {
            throw new ResourceNotFoundException("找不到该ID对应的资源！");
        }
        System.out.print("请输入新名称："); target.setName(scanner.nextLine());
        System.out.print("请输入新状态(AVAILABLE/OCCUPIED/DISABLED)：");
        String status = scanner.nextLine();
        if (!status.equals("AVAILABLE") && !status.equals("OCCUPIED") && !status.equals("DISABLED")) {
            throw new BusinessException("状态非法！只能输入规定状态。");
        }
        target.setStatus(status);
        ResourceFileUtil.saveResources(resourceList);
        return Result.success("资源更新成功！");
    }

    private static Result<Void> deleteResource(Scanner scanner) {
        System.out.print("请输入要删除的资源ID：");
        String id = scanner.nextLine();
        CampusResource target = queryResourceById(id);
        if (target == null) {
            throw new ResourceNotFoundException("找不到该ID对应的资源！");
        }
        resourceList.remove(target);
        ResourceFileUtil.saveResources(resourceList);
        return Result.success("资源删除成功！");
    }

    private static CampusResource queryResourceById(String id) {
        for (CampusResource r : resourceList) {
            if (r.getId().equals(id)) return r;
        }
        return null;
    }

    // ================= 用户认证模块 =================

    private static Result<User> executeLogin(Scanner scanner) {
        System.out.print("请输入账号：");
        String username = scanner.nextLine();
        System.out.print("请输入密码：");
        String password = scanner.nextLine();

        User targetUser = null;
        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                targetUser = user;
                break;
            }
        }
        if (targetUser == null) {
            throw new UserNotExistsException("用户不存在！");
        }
        if (!targetUser.getPassword().equals(password)){
            throw new WrongPasswordException("密码错误！");
        }

        currentUser = targetUser;
        return Result.success("登录成功！欢迎，" + currentUser.getUsername(), targetUser);
    }

    private static Result<Void> executeRegister(Scanner scanner) {
        System.out.print("请输入新账号：");
        String username = scanner.nextLine();
        if (username.trim().isEmpty()) {
            throw new ParamEmptyException("账号不能为空！");
        }

        for (User user : userList) {
            if (user.getUsername().equals(username)) {
                throw new UserExistsException("账号已存在！");
            }
        }

        System.out.print("请输入新密码：");
        String password = scanner.nextLine();
        if (password.length() < 6) {
            throw new ParamEmptyException("密码不能少于6位！");
        }

        System.out.print("请输入角色(admin/student)：");
        String role = scanner.nextLine();

        User newUser = new User(username, password, role);
        userList.add(newUser);
        FileUtil.saveUser(newUser);
        return Result.success("注册成功！请登录。");
    }
}