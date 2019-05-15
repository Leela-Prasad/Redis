package cart.client;
import java.util.Map;

import cart.data.ItemNotFoundException;
import cart.data.CartNotFoundException;
import cart.data.ShoppingCartData;


public class Client {
	
	//Example simulating a user on a website adding products to a cart
	//The client uses database 1

	public static void main(String[] args) {
		
		//instantiate the data class, with a default of 300 seconds
		ShoppingCartData cart = new ShoppingCartData(300,1);
		
		//user 7 adds different products to his cart
		cart.addToCart(7, 11, 3);
		cart.addToCart(7, 91, 6);
		cart.addToCart(7, 16, 9);
		
		//user changes the quantity of product 11 to 13
		try {
			cart.changeQuantity(7, 11, 13);
		}
		catch (CartNotFoundException e) {
			System.out.println("Error when changing quantity - cart not found");
		}
		catch (ItemNotFoundException e) {
			System.out.println("Error when changing quantity - item not found in cart");
		}
		
		//user removes product 16 from the cart
		try {
			cart.removeFromCart(7, 16);
		}
		catch (CartNotFoundException e) {
			System.out.println("Error when removing an item - cart not found");
		}
		catch (ItemNotFoundException e) {
			System.out.println("Error when removing an item - item not found in cart");
		}
		
		//retreive and print out the full cart
		try {
			Map<Integer,Integer> cartContents = cart.getCartContents(7);
			System.out.println("Contents of cart for user 7:");
			for (Integer productId : cartContents.keySet()) {
				System.out.println("Product " + productId + " - quantity " + cartContents.get(productId));
			}
		} catch (CartNotFoundException e) {
			System.out.println("Error when retrieving full cart - cart not found");
		}
		
		//remove cart when user checks out
		cart.destroyCart(7);
		
	}

	
	
	
	
	
}

