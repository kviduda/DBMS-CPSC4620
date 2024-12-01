package cpsc4620;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * This file is where the front end magic happens.
 * 
 * You will have to write the methods for each of the menu options.
 * 
 * This file should not need to access your DB at all, it should make calls to the DBNinja that will do all the connections.
 * 
 * You can add and remove methods as you see necessary. But you MUST have all of the menu methods (including exit!)
 * 
 * Simply removing menu methods because you don't know how to implement it will result in a major error penalty (akin to your program crashing)
 * 
 * Speaking of crashing. Your program shouldn't do it. Use exceptions, or if statements, or whatever it is you need to do to keep your program from breaking.
 * 
 */

public class Menu {

	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

	public static void main(String[] args) throws SQLException, IOException {

		System.out.println("Welcome to Pizzas-R-Us!");

		int menu_option = 0;

		// present a menu of options and take their selection

		PrintMenu();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		String option = reader.readLine();
		menu_option = Integer.parseInt(option);

		while (menu_option != 9) {
			switch (menu_option) {
				case 1:// enter order
					EnterOrder();
					break;
				case 2:// view customers
					viewCustomers();
					break;
				case 3:// enter customer
					EnterCustomer();
					break;
				case 4:// view order
						// open/closed/date
					ViewOrders();
					break;
				case 5:// mark order as complete
					MarkOrderAsComplete();
					break;
				case 6:// view inventory levels
					ViewInventoryLevels();
					break;
				case 7:// add to inventory
					AddInventory();
					break;
				case 8:// view reports
					PrintReports();
					break;
			}
			PrintMenu();
			option = reader.readLine();
			menu_option = Integer.parseInt(option);
		}

	}

	public static boolean isUserInputValid(String regex, String input) {
		if (input.isEmpty()) {
			return false;
		}

		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		return matcher.find();
	}

	// allow for a new order to be placed
	public static void EnterOrder() throws SQLException, IOException {

		/*
		 * EnterOrder should do the following:
		 * 
		 * Ask if the order is delivery, pickup, or dinein
		 * if dine in....ask for table number
		 * if pickup...
		 * if delivery...
		 * 
		 * Then, build the pizza(s) for the order (there's a method for this)
		 * until there are no more pizzas for the order
		 * add the pizzas to the order
		 *
		 * Apply order discounts as needed (including to the DB)
		 * 
		 * return to menu
		 * 
		 * make sure you use the prompts below in the correct order!
		 */

		// User Input Prompts...

		//BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

		int type ;
		String userChoice;
		String regex = "^([1-3]?)$";
		boolean isValid = false;
		try{
		while (!isValid) {
			System.out.println(
					"Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.)Delivery\nEnter the number of your choice:");
			// userChoice = reader.readLine().trim();
			userChoice = reader.readLine();
			//System.out.println(userChoice);
			isValid = isUserInputValid(regex, userChoice);
			System.out.println(isValid);
			if (isValid) {

				type = Integer.parseInt(userChoice);
			} else
				System.out.println("Invalid Input!! \nPlease Provide Valid Input");

		}
	     }
		catch(NullPointerException e){
           e.printStackTrace();
		}
		// System.out.println(
		// "Is this order for: \n1.) Dine-in\n2.) Pick-up\n3.) Delivery\nEnter the
		// number of your choice:");
		// userChoice = reader.readLine();
		// type = Integer.parseInt(userChoice);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime present = LocalDateTime.now();
		String timestamp = dtf.format(present);
		int maxOrderID = DBNinja.getNextOrderID();
		String orderType = null;
		int customerID = 1;
		Order od = null;

		if (type == 1) {
			System.out.println("What is the table number for this order?");
			Integer tableNumber = Integer.parseInt(reader.readLine());
			od = new Order(0, customerID, "dinein", timestamp, 0.0, 0.0, 0);
			orderType = "dinein";
			od.setOrderID(maxOrderID);
			DBNinja.addOrder(od);
			od.setOrderType(orderType);
			DBNinja.dineIn(maxOrderID, tableNumber);
			buildPizzaType(maxOrderID, od);

		} else if (type == 2) {
			userChoice = "n";
			regex = "^^([YyNn]?)$";
			isValid = false;
			while (!isValid) {
				System.out.println("Is this order for an existing customer? Answer y/n: ");
				userChoice = reader.readLine().trim();
				isValid = isUserInputValid(regex, userChoice);
				if (isValid) {
					break;
				} else
					System.out.println("Invalid Input!! \nPlease Provide Valid Input");

			}

			if (userChoice.equals("N") || userChoice.equals("n")) {
				Menu.EnterCustomer();
				ArrayList<Customer> customer;
				customer = DBNinja.getCustomerList();
				customerID = customer.get(customer.size() - 1).getCustID();
			} else {
				System.out.println("Here's a list of the current customers: ");
				Menu.viewCustomers();
				System.out.println("Which customer is this order for? Enter customer ID: ");
				customerID = Integer.parseInt(reader.readLine());
			}
			orderType = "pickup";
			od = new Order(0, customerID, "pickup", timestamp, 0.0, 0.0, 0);
			DBNinja.addOrder(od);
			od.setOrderID(maxOrderID);
			od.setOrderType(orderType);
			DBNinja.updatePickUp(maxOrderID, customerID);
			buildPizzaType(maxOrderID, od);

		} else {
			userChoice = "n";
			regex = "^^([YyNn]?)$";
			isValid = false;
			while (!isValid) {
				System.out.println("Is this order for an existing customer? Answer y/n: ");
				userChoice = reader.readLine().trim();
				isValid = isUserInputValid(regex, userChoice);
				if (isValid) {
					break;
				} else
					System.out.println("Invalid Input!! \nPlease Provide Valid Input");

			}
			String customerAddress = null;
			if (userChoice.equals("N") || userChoice.equals("n")) {
				Menu.EnterCustomer();
				ArrayList<Customer> customer;
				customer = DBNinja.getCustomerList();
				customerID = customer.get(customer.size() - 1).getCustID();
			} else {
				System.out.println("Here's a list of the current customers: ");
				viewCustomers();
				System.out.println("Which customer is this order for? Enter customer ID: ");
				customerID = Integer.parseInt(reader.readLine());

			}
			System.out.println("What is the House/Apt Number for this order? (e.g., 111)");
			String housenum = reader.readLine();
			System.out.println("What is the Street for this order? (e.g., Smile Street)");
			String street = reader.readLine();
			System.out.println("What is the City for this order? (e.g., Greenville)");
			String city = reader.readLine();
			System.out.println("What is the State for this order? (e.g., SC)");
			String state = reader.readLine();
			System.out.println("What is the Zip Code for this order? (e.g., 20605)");
			String zip = reader.readLine();
			customerAddress = housenum + " " + street + " " + city + " " + state + " " + zip;

			od = new Order(0, customerID, "delivery", timestamp, 0.0, 0.0, 0);
			od.setOrderID(maxOrderID);
			DBNinja.addOrder(od);
			orderType = "delivery";
			od.setOrderType(orderType);
			DBNinja.updateDelivery(maxOrderID, customerAddress);
			buildPizzaType(maxOrderID, od);

		}

	}

	public static void buildPizzaType(int maxOrderID, Order od) throws SQLException, IOException {
		/*
		 * This is a helper function we have created for calling the build
		 * pizza method for the each order type
		 */
		int flag = 1;
		// ArrayList<Pizza> pizzas =new ArrayList<Pizza>();
		double priToCus = 0.0;
		double priToBus = 0.0;
		System.out.println("Let's build a pizza!");
		while (flag != -1) {
			ArrayList<Integer[]> discountPizza = new ArrayList<Integer[]>();
			ArrayList<Integer[]> toppingPizza = new ArrayList<Integer[]>();
			Pizza p = buildPizza(maxOrderID);
			priToCus = priToCus + p.getCustPrice();
			priToBus = priToBus + p.getBusPrice();
			od.setCustPrice(priToCus);
			od.setBusPrice(priToBus);
			DBNinja.addPizza(p);
			DBNinja.updatePizzaTopping(toppingPizza);
			DBNinja.addPizzaDiscount(discountPizza);
			System.out.println(
					"Enter -1 to stop adding pizzas...Enter anything else to continue adding pizzas to the order.");
			flag = Integer.parseInt(reader.readLine());
		}
		System.out.println("Do you want to add discounts to this order? Enter y/n?");
		String ordDisChoice = reader.readLine();
		ArrayList<Integer[]> orderDiscountmap = new ArrayList<Integer[]>();
		if (ordDisChoice.equals("Y") || ordDisChoice.equals("y")) {
			System.out.println("Getting discount list...");
			int discountflag = 1;
			while (discountflag != -1) {
				ArrayList<Discount> discorder = new ArrayList<Discount>();
				Discount d1 = null;
				discorder = DBNinja.getDiscountList();
				for (Discount discount : discorder) {
					System.out.println(discount.toString());
				}
				System.out.println(
						"Which Order Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int DiscountID = Integer.parseInt(reader.readLine());
				double custPrice = od.getCustPrice();
				if (DiscountID != -1) {
					for (Discount discount : discorder) {
						if (discount.getDiscountID() == DiscountID) {
							d1 = discount;
						}
					}
					if (d1.isPercent()) {
						od.setCustPrice(custPrice - ((custPrice * d1.getAmount())));
					} else {
						od.setCustPrice(custPrice - d1.getAmount());
					}

					Integer mapping[] = { maxOrderID, d1.getDiscountID() };
					orderDiscountmap.add(mapping);
					DBNinja.useOrderDiscount(od, d1);
				} else
					discountflag = -1;
			}
		}
		DBNinja.updateOrder(od, maxOrderID);
		System.out.println("Finished adding order...Returning to menu...");

	}

	public static void viewCustomers() throws SQLException, IOException {
		/*
		 * Simply print out all of the customers from the database.
		 */

		ArrayList<Customer> customers = null;
		customers = DBNinja.getCustomerList();
		for (Customer customer : customers) {
			System.out.println(customer.toString());
		}

	}

	// Enter a new customer in the database
	public static void EnterCustomer() throws SQLException, IOException {
		/*
		 * Ask for the name of the customer:
		 * First Name <space> Last Name
		 * 
		 * Ask for the phone number.
		 * (##########) (No dash/space)
		 * 
		 * Once you get the name and phone number, add it to the DB
		 */

		// User Input Prompts...
		System.out.println("What is this customer's name (first <space> last)");
		String fullName = reader.readLine();
		String[] names = fullName.trim().split("\\s+");
		System.out.println("What is this customer's phone number (##########) (No dash/space)");
		String phone = reader.readLine();
		Customer customer = new Customer(0, names[0], names[1], phone);
		DBNinja.addCustomer(customer);

	}

	// View any orders that are not marked as completed
	public static void ViewOrders() throws SQLException, IOException {
		/*
		 * This method allows the user to select between three different views of the
		 * Order history:
		 * The program must display:
		 * a. all open orders
		 * b. all completed orders
		 * c. all the orders (open and completed) since a specific date (inclusive)
		 * 
		 * After displaying the list of orders (in a condensed format) must allow the
		 * user to select a specific order for viewing its details.
		 * The details include the full order type information, the pizza information
		 * (including pizza discounts), and the order discounts.
		 * 
		 */
		String userChoice = "n";
		String regex = "^^([aAbBcCdD]?)$";
		Boolean isValid = false;
		while (!isValid) {
			System.out.println(
					"Would you like to:\n(a)display all orders [open or closed]\n(b) display all open orders\n(c) display all completed orders\n(d) display orders since a specific date");
			userChoice = reader.readLine().trim();
			isValid = isUserInputValid(regex, userChoice);
			if (isValid) {
				break;
			} else
				System.out.println("Invalid Input!! \nPlease Provide Valid Input");

		}

		try {
			char s = userChoice.charAt(0);
			switch (Character.toLowerCase(s)) {
				case 'a':
					viewTotalOrders();
					break;
				case 'b':
					viewOpenOrders();
					break;
				case 'c':
					viewCompletedOrders();
					break;
				case 'd':
					viewOrdersSinceDate();
					break;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// User Input Prompts...

	}

	public static void viewOpenOrders() throws SQLException, IOException {
		ArrayList<Order> orders = null;
		orders = DBNinja.getOrders(true);
		for (Order order : orders) {
			System.out.println(order.toSimplePrint());
		}
		System.out.print("Which order do you wish to see in detail? Enter the number(-1 to exit):\n ");

		int orderID = Integer.parseInt(reader.readLine());
		if (orderID != -1) {
			Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(Order::getOrderID, x -> x));
			// resultMap.get(orderID);
			System.out.println(resultMap.get(orderID).toString());
		}
	}

	public static void viewCompletedOrders() throws SQLException, IOException {
		ArrayList<Order> orders = null;
		orders = DBNinja.getCompletedOrders(false);
		for (Order order : orders) {
			System.out.println(order.toSimplePrint());
		}
		System.out.print("Which order do you wish to see in detail? Enter the number(-1 to exit):\n ");
		int orderID = Integer.parseInt(reader.readLine());
		if (orderID != -1) {
			Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(Order::getOrderID, x -> x));
			System.out.println(resultMap.get(orderID).toString());
		}
	}

	public static void viewOrdersSinceDate() throws SQLException, IOException {
		ArrayList<Order> orders = null;
		System.out.println("What is the date you want to restrict by? (FORMAT= YYYY-MM-DD)");
		String dateString = reader.readLine();
		orders = DBNinja.getOrdersByDate(dateString);
		for (Order order : orders) {
			System.out.println(order.toSimplePrint());
		}
		System.out.print("Which order do you wish to see in detail? Enter the number(-1 to exit):\n ");
		int orderID = Integer.parseInt(reader.readLine());
		if (orderID != -1) {
			Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(Order::getOrderID, x -> x));
			System.out.println(resultMap.get(orderID).toString());
		}
	}

	public static void viewTotalOrders() throws SQLException, IOException {
		{
			ArrayList<Order> orders = null;
			orders = DBNinja.getOrders(false);
			for (Order order : orders) {
				System.out.println(order.toSimplePrint());
			}
			System.out.print("Which order do you wish to see in detail? Enter the number(-1 to exit):\n ");

			int orderID = Integer.parseInt(reader.readLine());
			if (orderID != -1) {
				Map<Integer, Order> resultMap = orders.stream().collect(Collectors.toMap(Order::getOrderID, x -> x));
				System.out.println(resultMap.get(orderID).toString());
			}
		}
	}

	// When an order is completed, we need to make sure it is marked as complete
	public static void MarkOrderAsComplete() throws SQLException, IOException {
		/*
		 * All orders that are created through java (part 3, not the orders from part 2)
		 * should start as incomplete
		 * 
		 * When this method is called, you should print all of the "opoen" orders marked
		 * and allow the user to choose which of the incomplete orders they wish to mark
		 * as complete
		 * 
		 */
		ArrayList<Order> orders = DBNinja.getOrders(true);
		if (!orders.isEmpty()) {
			for (Order order : orders) {
				System.out.println(order.toSimplePrint());
			}
			System.out.println("Which order would you like mark as complete? Enter the OrderID: ");
			// reader.readLine();
			Integer orderId = Integer.parseInt(reader.readLine());
			Order order = null;
			int index = 0;
			for (int i = 0; i < orders.size(); i++) {
				if (orders.get(i).getOrderID() == orderId) {
					index = i;
				}
			}
			order = orders.get(index);
			DBNinja.completeOrder(order);
		} else
			System.out.println("There are no open orders currently... returning to menu...");

	}

	public static void ViewInventoryLevels() throws SQLException, IOException {
		/*
		 * Print the inventory. Display the topping ID, name, and current inventory
		 */

		DBNinja.printInventory();

	}

	public static void AddInventory() throws SQLException, IOException {
		/*
		 * This should print the current inventory and then ask the user which topping
		 * (by ID) they want to add more to and how much to add
		 */
		System.out.println("Current Inventory Levels:");
		Topping t = null;
		Menu.ViewInventoryLevels();
		do {
			System.out.println("Which topping do you want to add inventory to? Enter the number: ");
			int toppingID = Integer.parseInt(reader.readLine());
			t = DBNinja.getToppingFromId(toppingID);
			if (t == null) {
				System.out.println("Incorrect entry, not an option");
			}
		} while (t == null);

		System.out.println("How many units would you like to add? ");
		double quantity = Float.parseFloat(reader.readLine());
		DBNinja.addToInventory(t, quantity);

		// User Input Prompts...

	}

	// A method that builds a pizza. Used in our add new order method
	public static Pizza buildPizza(int orderID) throws SQLException, IOException {

		/*
		 * This is a helper method for first menu option.
		 * 
		 * It should ask which size pizza the user wants and the crustType.
		 * 
		 * Once the pizza is created, it should be added to the DB.
		 * 
		 * We also need to add toppings to the pizza. (Which means we not only need to
		 * add toppings here, but also our bridge table)
		 * 
		 * We then need to add pizza discounts (again, to here and to the database)
		 * 
		 * Once the discounts are added, we can return the pizza
		 */

		String userChoice = "1";
		String regex = "^([1-4]?)$";
		Boolean isValid = false;
		while (!isValid) {

			System.out.println("What size is the pizza?");
			System.out.println("1." + DBNinja.size_s);
			System.out.println("2." + DBNinja.size_m);
			System.out.println("3." + DBNinja.size_l);
			System.out.println("4." + DBNinja.size_xl);
			System.out.println("Enter the corresponding number: ");
			userChoice = reader.readLine().trim();
			isValid = isUserInputValid(regex, userChoice);
			// System.out.println(isValid);
			if (isValid) {
				break;
			} else
				System.out.println("Invalid Input!! \nPlease Provide Valid Input");

		}
		int s = 1;
		s = Integer.parseInt(userChoice);

		String size = null;
		String crust = null;

		if (s == 1)
			size = DBNinja.size_s;
		else if (s == 2)
			size = DBNinja.size_m;
		else if (s == 3)
			size = DBNinja.size_l;
		else if (s == 4)
			size = DBNinja.size_xl;

		isValid = false;
		while (!isValid) {
			System.out.println("What crust for this pizza?");
			System.out.println("1." + DBNinja.crust_thin);
			System.out.println("2." + DBNinja.crust_orig);
			System.out.println("3." + DBNinja.crust_pan);
			System.out.println("4." + DBNinja.crust_gf);
			System.out.println("Enter the corresponding number: ");
			userChoice = reader.readLine().trim();
			isValid = isUserInputValid(regex, userChoice);
			// System.out.println(isValid);
			if (isValid) {
				break;
			} else
				System.out.println("Invalid Input!! \nPlease Provide Valid Input");

		}

		int c = 1;

		c = Integer.parseInt(userChoice);

		if (c == 1)
			crust = DBNinja.crust_thin;
		else if (c == 2)
			crust = DBNinja.crust_orig;
		else if (c == 3)
			crust = DBNinja.crust_pan;
		else if (c == 4)
			crust = DBNinja.crust_gf;

		double basepricecustomer = DBNinja.getBaseCustPrice(size, crust);
		double basepricebusiness = DBNinja.getBaseBusPrice(size, crust);

		int pizzaId = DBNinja.getMaxPizzaID();

		// int maxOrderID =0 ;
		String timestamp = null;
		Pizza p = new Pizza(pizzaId + 1, size, crust, orderID, "Processing", timestamp, basepricecustomer,
				basepricebusiness);

		ArrayList<Integer[]> pizzadisc = new ArrayList<Integer[]>();
		ArrayList<Integer[]> pizzatop = new ArrayList<Integer[]>();

		int toppingID = 1;
		while (toppingID != -1) {
			ViewInventoryLevels();
			System.out.println("Which topping do you want to add? Enter the TopID. Enter -1 to stop adding toppings: ");
			userChoice = reader.readLine();
			toppingID = Integer.parseInt(userChoice);
			if (toppingID != -1) {
				Topping t = DBNinja.getToppingFromId(toppingID);
				System.out.println("Do you want to add extra topping? Enter y/n");
				String damt = reader.readLine();
				List pizzatopping = new ArrayList();
				if (damt.equals("y") || damt.equals("Y")) {
					// System.out.println("Available Toppings:");
					// ViewInventoryLevels();
					DBNinja.useTopping(p, t, true);
					p.addToppings(t, true);
					Integer mapping[] = { p.getPizzaID(), t.getTopID(), 1 };
					pizzatopping.add(mapping);
					pizzatop.add(mapping);

				} else {
					DBNinja.useTopping(p, t, false);
					p.addToppings(t, false);
					Integer mapping[] = { p.getPizzaID(), t.getTopID(), 0 };
					pizzatopping.add(mapping);
					pizzatop.add(mapping);

				}
			} else {
				break;
			}
		}

		// ArrayList<Integer> discountList = new ArrayList<Integer>();
		System.out.println("Do you want to add discounts to this Pizza? Enter y/n?");
		userChoice = reader.readLine();
		if (userChoice.equals("Y") || userChoice.equals("y")) {
			System.out.println("Getting discount list...");
			int discountflag = 1;
			while (discountflag != -1) {
				ArrayList<Discount> disc = new ArrayList<Discount>();
				Discount d = null;
				disc = DBNinja.getDiscountList();
				for (Discount discount : disc) {
					System.out.println(discount.toString());
				}
				System.out.println(
						"Which Pizza Discount do you want to add? Enter the DiscountID. Enter -1 to stop adding Discounts: ");
				int DiscountID = Integer.parseInt(reader.readLine());
				double custPrice1 = p.getCustPrice();

				if (DiscountID != -1) {
					for (Discount discount : disc) {
						if (discount.getDiscountID() == DiscountID) {
							d = discount;
						}
					}
					if (d.isPercent()) {
						p.setCustPrice(custPrice1 - ((custPrice1 * d.getAmount())));
					} else {
						p.setCustPrice(custPrice1 - d.getAmount());
						;
					}

					Integer mapping[] = { p.getPizzaID(), d.getDiscountID() };
					List pizzaDiscountmap = new ArrayList();
					pizzaDiscountmap.add(mapping);
					pizzadisc.add(mapping);

				} else
					discountflag = -1;

			}

		}
		// Syst

		// User Input Prompts...

		return p;

	}

	public static void PrintReports() throws SQLException, NumberFormatException, IOException {
		/*
		 * This method asks the use which report they want to see and calls the DBNinja
		 * method to print the appropriate report.
		 * 
		 */

		// User Input Prompts...
		char s = '1';
		String userChoice = "n";
		String regex = "^^([aAbBcC]?)$";
		Boolean isValid = false;
		System.out.println(
				"Which report do you wish to print? Enter\n(a) ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");
		userChoice = reader.readLine().trim();
		isValid = isUserInputValid(regex, userChoice);
		if (isValid) {
			s = userChoice.charAt(0);
		} else {
			System.out.println("I don't understand that input... returning to menu...");
		}

		// System.out.println("Which report do you wish to print? Enter\n(a)
		// ToppingPopularity\n(b) ProfitByPizza\n(c) ProfitByOrderType:");

		switch (Character.toLowerCase(s)) {
			case 'a':
				DBNinja.printToppingPopReport();
				break;
			case 'b':
				DBNinja.printProfitByPizzaReport();
				break;
			case 'c':
				DBNinja.printProfitByOrderType();
				break;

		}

	}

	// Prompt - NO CODE SHOULD TAKE PLACE BELOW THIS LINE
	// DO NOT EDIT ANYTHING BELOW HERE, THIS IS NEEDED TESTING.
	// IF YOU EDIT SOMETHING BELOW, IT BREAKS THE AUTOGRADER WHICH MEANS YOUR GRADE
	// WILL BE A 0 (zero)!!

	public static void PrintMenu() {
		System.out.println("\n\nPlease enter a menu option:");
		System.out.println("1. Enter a new order");
		System.out.println("2. View Customers ");
		System.out.println("3. Enter a new Customer ");
		System.out.println("4. View orders");
		System.out.println("5. Mark an order as completed");
		System.out.println("6. View Inventory Levels");
		System.out.println("7. Add Inventory");
		System.out.println("8. View Reports");
		System.out.println("9. Exit\n\n");
		System.out.println("Enter your option: ");
	}

	/*
	 * autograder controls....do not modiify!
	 */

	public final static String autograder_seed = "6f1b7ea9aac470402d48f7916ea6a010";

	private static void autograder_compilation_check() {

		try {
			Order o = null;
			Pizza p = null;
			Topping t = null;
			Discount d = null;
			Customer c = null;
			ArrayList<Order> alo = null;
			ArrayList<Discount> ald = null;
			ArrayList<Customer> alc = null;
			ArrayList<Topping> alt = null;
			double v = 0.0;
			String s = "";

			DBNinja.addOrder(o);
			DBNinja.addPizza(p);
			DBNinja.useTopping(p, t, false);
			DBNinja.usePizzaDiscount(p, d);
			DBNinja.useOrderDiscount(o, d);
			DBNinja.addCustomer(c);
			DBNinja.completeOrder(o);
			alo = DBNinja.getOrders(false);
			o = DBNinja.getLastOrder();
			alo = DBNinja.getOrdersByDate("01/01/1999");
			ald = DBNinja.getDiscountList();
			d = DBNinja.findDiscountByName("Discount");
			alc = DBNinja.getCustomerList();
			c = DBNinja.findCustomerByPhone("0000000000");
			alt = DBNinja.getToppingList();
			t = DBNinja.findToppingByName("Topping");
			DBNinja.addToInventory(t, 1000.0);
			v = DBNinja.getBaseCustPrice("size", "crust");
			v = DBNinja.getBaseBusPrice("size", "crust");
			DBNinja.printInventory();
			DBNinja.printToppingPopReport();
			DBNinja.printProfitByPizzaReport();
			DBNinja.printProfitByOrderType();
			s = DBNinja.getCustomerName(0);
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

}
