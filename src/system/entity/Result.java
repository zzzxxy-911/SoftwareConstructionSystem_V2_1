package system.entity;

/**
 * 统一结果封装类
 * 使用泛型支持返回任意类型数据
 * 设计原则：仅负责封装状态、消息、数据，不含业务逻辑
 */
@SuppressWarnings("unused")
public class Result<T> {
    private boolean success;
    private String msg;
    private T data;

    public Result() {

    }

    public Result(boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    // ================= 静态工厂方法 =================

    /**
     * 成功，携带返回数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(true, "操作成功", data);
    }

    /**
     * 成功，仅提示信息
     */
    public static <T> Result<T> success(String msg) {
        return new Result<>(true, msg, null);
    }

    /**
     * 成功，自定义消息+数据
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(true, msg, data);
    }

    /**
     * 失败，携带错误信息
     */
    public static <T> Result<T> fail(String msg) {
        return new Result<>(false, msg, null);
    }

    // ================= Get/Set 方法 =================
    public boolean isSuccess() {
        return success;
    }
    public void setSuccess(boolean success) {
        this.success = success;
    }
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public T getData() {
        return data;
    }
    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{success=" + success + ", msg='" + msg + "', data=" + data + '}';
    }
}
