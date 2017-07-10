package zhoukai;

import java.util.ResourceBundle;

public class RedisConfig {

	private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("redis");
	
	public static String HOST = getString("redis.host");
	
	public static int PORT = getInt("redis.port");
	
	public static String PASSWORD = getString("redis.password");
	
	public static int TIMEOUT = getInt("redis.timeout");
	
	public static int DATABASE = getInt("redis.default.db");
	
	public static int POOLMAXACTIVE = getInt("redis.pool.maxActive");
	
	public static int POOLMAXIDLE = getInt("redis.pool.maxidle");
	
	public static int POOLMAXWAIT = getInt("redis.pool.maxWait");
	
	public static boolean POOLTESTONBORROW = getBoolean("redis.pool.testOnBorrow");

	private static String getString(String key) {
		return BUNDLE.getString(key);
	}
	
	private static int getInt(String key) {
		return Integer.parseInt(BUNDLE.getString(key));
	}
	
	private static boolean getBoolean(String key) {
		return Boolean.parseBoolean(BUNDLE.getString(key));
	}
}
