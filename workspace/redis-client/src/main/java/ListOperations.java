import java.util.List;

import redis.clients.jedis.Jedis;

public class ListOperations {

	public static void main(String[] args) {

		try(Jedis jedis = new Jedis("localhost")) {
			jedis.del("infoRequests");
			Long listSize = jedis.lpush("infoRequests", "first@abc.com");
			jedis.lpush("infoRequests", "second@abc.com");
			listSize = jedis.lpush("infoRequests", "third@abc.com");
			System.out.println(listSize);
			
			String removedItem = jedis.lpop("infoRequests");
			System.out.println("Removed Item ::" + removedItem);
			
			//Here -1 is end of the list i.e., (n-1)
			List<String> requests = jedis.lrange("infoRequests", 0, -1);
			System.out.println(requests);
		}
		
	}

}
