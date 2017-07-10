package notice;

/**
 * 状态通知
 */
public interface StatusNotice {
    void execute();

    void start();

    void shutdown();
}
