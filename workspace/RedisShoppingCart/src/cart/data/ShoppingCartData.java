package cart.data;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import redis.clients.jedis.Jedis;

public class ShoppingCartData {
	
	private int defaultExpiryTimeInSeconds;
	private int databaseId;
	
	private String getKey(Integer userId){
		return "cart:" + userId;
	}
	
	private void switchDb(Jedis jedis) {
		jedis.select(databaseId);
	}
	
	private void resetExpiry(Jedis jedis, String key) {
		jedis.expire(key, defaultExpiryTimeInSeconds);
	}
	
	public ShoppingCartData(int defaultExpiryTimeInSeconds, int databaseId) {
		this.defaultExpiryTimeInSeconds = defaultExpiryTimeInSeconds;
		this.databaseId = databaseId;
	}
	
	public void addToCart(Integer userId, Integer productId, Integer quantity) {
		try (Jedis jedis = new Jedis("localhost")) {
			switchDb(jedis);
			String key = getKey(userId);
			jedis.hset(key, productId.toString(), quantity.toString());
			resetExpiry(jedis,key);
		}
	}
	
	public void changeQuantity(Integer userId, Integer productId ,Integer quantity) throws ItemNotFoundException, CartNotFoundException {
		try (Jedis jedis = new Jedis("localhost")) {
			switchDb(jedis);
			String key = getKey(userId);
			if(!jedis.exists(key)) {
				throw new CartNotFoundException();
			}
			
			if(!jedis.hexists(key, productId.toString())) {
				throw new ItemNotFoundException();
			}
			
			jedis.hset(key, productId.toString(), quantity.toString());
			resetExpiry(jedis,key);
			
		}	
	}
	
	public void removeFromCart(Integer userId, Integer productId) throws ItemNotFoundException, CartNotFoundException {
		try (Jedis jedis = new Jedis("localhost")) {
			switchDb(jedis);
			String key = getKey(userId);
			if(!jedis.exists(key)) {
				throw new CartNotFoundException();
			}
			
			if(!jedis.hexists(key, productId.toString())) {
				throw new ItemNotFoundException();
			}
			jedis.hdel(key, productId.toString());
			resetExpiry(jedis,key);
		}	
	}
	
	public Map<Integer,Integer> getCartContents(Integer userId) throws CartNotFoundException {
		try (Jedis jedis = new Jedis("localhost")) {
			switchDb(jedis);
			String key = getKey(userId);
			
			if(!jedis.exists(key)) {
				throw new CartNotFoundException();
			}
			
			Map<String, String> rawData = jedis.hgetAll(key);
			Map<Integer,Integer> cartContents = new HashMap<>();
			Iterator<String> it = rawData.keySet().iterator();
			while(it.hasNext()) {
				String k = it.next();
				cartContents.put(Integer.valueOf(k), Integer.valueOf(rawData.get(k)));
			}
			
			return cartContents;
		}
	}
	
	public void destroyCart(Integer userId) {
		try (Jedis jedis = new Jedis("localhost")) {
			switchDb(jedis);
			String key = getKey(userId);
			jedis.del(key);
		}
	}
}
