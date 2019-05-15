package cart.test;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import cart.data.CartNotFoundException;
import cart.data.ItemNotFoundException;
import cart.data.ShoppingCartData;
import redis.clients.jedis.Jedis;

public class CartTest {
	
	//This test uses database 2

	@Test
	public void testAddToCartAndGetContents() {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(60,2);
			cart.destroyCart(1);
			cart.destroyCart(2);
			
			cart.addToCart(1, 2, 3);
			cart.addToCart(1, 4, 5);
			cart.addToCart(2, 6, 7);
			
			try {
				Map<Integer, Integer> cartContents1 = cart.getCartContents(1);
				assertTrue(cartContents1.keySet().size() ==2);
				assertTrue(cartContents1.get(2) == 3);
				assertTrue(cartContents1.get(4) == 5);
			} 
			catch (CartNotFoundException e) {
				fail("cart not found");
			}
			try {
				Map<Integer, Integer> cartContents2 = cart.getCartContents(2);
				assertTrue(cartContents2.size() ==1);
				assertTrue(cartContents2.get(6) == 7);
			} 
			catch (CartNotFoundException e) {
				fail("cart not found");
			}
		}
		
	}
	
	@Test
	public void testChangeItemQuantityOk() {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(60,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			cart.changeQuantity(1, 2, 4);
			Map<Integer, Integer> cartContents = cart.getCartContents(1);
			assertTrue(cartContents.get(2) == 4);
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
		catch (CartNotFoundException e) {
			fail("cart not found");
		}
	}
	
	@Test(expected=CartNotFoundException.class)
	public void testChangeItemQuantityExpired() throws CartNotFoundException  {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(3,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			Thread.sleep(5000);
			cart.changeQuantity(1, 2, 4);
			Map<Integer, Integer> cartContents = cart.getCartContents(1);
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
		catch (InterruptedException e) {
			fail("thread was interrupted");
		}
	}
	
	@Test(expected=CartNotFoundException.class)
	public void testChangeItemQuantityNoCart() throws CartNotFoundException {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(60,2);
			cart.destroyCart(1);
			cart.changeQuantity(1, 2, 4);
			
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
	}

	@Test(expected=ItemNotFoundException.class)
	public void testChangeItemQuantityNoItem() throws ItemNotFoundException {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(60,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			cart.changeQuantity(1, 3, 4);
		} 
		
		catch (CartNotFoundException e) {
			fail("cart not found");
		} 
	}
	
	@Test
	public void removeFromCartOk() {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(60,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			cart.addToCart(1, 4, 5);
			cart.removeFromCart(1, 2);
			Map<Integer, Integer> cartContents = cart.getCartContents(1);
			assertTrue(cartContents.size() == 1);
			assertTrue(cartContents.get(4) == 5);
		} 
		
		catch (CartNotFoundException e) {
			fail("cart not found");
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
	}
	
	@Test(expected=CartNotFoundException.class)
	public void removeFromCartExpired() throws CartNotFoundException {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(3,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			Thread.sleep(5000);
			cart.removeFromCart(1, 2);
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
		catch (InterruptedException e) {
			fail("thread was interrupted");
		} 
	}
	
	@Test(expected=CartNotFoundException.class)
	public void removeFromCartNoCart() throws CartNotFoundException {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(3,2);
			cart.destroyCart(1);
			cart.removeFromCart(1, 2);
		} 
		catch (ItemNotFoundException e) {
			fail("item not found");
		} 
	}
	
	@Test(expected=ItemNotFoundException.class)
	public void removeFromCarNoItem() throws ItemNotFoundException {
		try (Jedis jedis = new Jedis()) {
			ShoppingCartData cart = new ShoppingCartData(3,2);
			cart.destroyCart(1);
			cart.addToCart(1, 2, 3);
			cart.removeFromCart(1, 4);
		} 
		catch (CartNotFoundException e) {
			fail("cart not found");
		} 
	}
	
}
