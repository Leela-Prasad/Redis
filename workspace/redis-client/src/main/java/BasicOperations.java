import redis.clients.jedis.Jedis;

public class BasicOperations {

	public static void main(String[] args) {

		try(Jedis jedis = new Jedis("localhost")) {
			String value = jedis.get("account:20:status");
			
			jedis.set("key1", "value1");
			jedis.mset("key2","value2", "key3", "value3");
			
			System.out.println(value);
		}
		
	}

}
