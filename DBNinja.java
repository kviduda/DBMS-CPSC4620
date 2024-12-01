package cpsc4620;

import java.io.IOException;
import java.sql.*;
import java.util.*;
import java.util.Date;

/*
 * This file is where most of your code changes will occur You will write the code to retrieve
 * information from the database, or save information to the database
 * 
 * The class has several hard coded static variables used for the connection, you will need to
 * change those to your connection information
 * 
 * This class also has static string variables for pickup, delivery and dine-in. If your database
 * stores the strings differently (i.e "pick-up" vs "pickup") changing these static variables will
 * ensure that the comparison is checking for the right string in other places in the program. You
 * will also need to use these strings if you store this as boolean fields or an integer.
 * 
 * 
 */

/**
 * A utility class to help add and retrieve information from the database
 */

public final class DBNinja {
	private static Connection conn;

	// Change these variables to however you record dine-in, pick-up and delivery, and sizes and crusts
	public final static String pickup = "pickup";
	public final static String delivery = "delivery";
	public final static String dine_in = "dinein";

	public final static String size_s = "Small";
	public final static String size_m = "Medium";
	public final static String size_l = "Large";
	public final static String size_xl = "XLarge";

	public final static String crust_thin = "Thin";
	public final static String crust_orig = "Original";
	public final static String crust_pan = "Pan";
	public final static String crust_gf = "Gluten-Free";



	
	private static boolean connect_to_db() throws SQLException, IOException {

		try {
			conn = DBConnector.make_connection();
			return true;
		} catch (SQLException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

	}

	
	public static void addOrder(Order od) throws SQLException, IOException 
	{
		/*
		 * add code to add the order to the DB. Remember that we're not just
		 * adding the order to the order DB table, but we're also recording
		 * the necessary data for the delivery, dinein, and pickup tables
		 * 
		 */
	
        
		try {
			connect_to_db();

			String sql = "insert into ordertable ( Order_ID,Customer_ID,Order_TimeStamp, Order_PriceToCustomer,Order_PriceToBusiness, Order_Type, Order_IsComplete) values(?,?,?,?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, od.getOrderID());
			preparedStatement.setInt(2, od.getCustID());
			preparedStatement.setString(3, od.getDate());
			preparedStatement.setDouble(4, od.getCustPrice());
			preparedStatement.setDouble(5, od.getBusPrice());
			preparedStatement.setString(6, od.getOrderType());
			preparedStatement.setInt(7, od.getIsComplete());
			preparedStatement.executeUpdate();


			conn.close();
		}
		
		catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			try {
				conn.close();
			} 
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void addPizza(Pizza p) throws SQLException, IOException
	{

		/*
		 * Add the code needed to insert the pizza into into the database.
		 * Keep in mind adding pizza discounts and toppings associated with the pizza,
		 * there are other methods below that may help with that process.
		 * 
		 */
		try {
			connect_to_db();
			String sql = "insert into pizza(Order_ID,Pizza_PriceToBusiness, Pizza_PriceToCustomer,Pizza_Status,Pizza_CrustType,Pizza_Size) values(?, ?, ?,?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, p.getOrderID());
			preparedStatement.setDouble(2, p.getBusPrice());
			preparedStatement.setDouble(3, p.getCustPrice());
			preparedStatement.setString(4, p.getPizzaState());
			preparedStatement.setString(5, p.getCrustType());
			preparedStatement.setString(6, p.getSize());
			preparedStatement.executeUpdate();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	
	public static void useTopping(Pizza p, Topping t, boolean isDoubled) throws SQLException, IOException //this method will update toppings inventory in SQL and add entities to the Pizzatops table. Pass in the p pizza that is using t topping
	{

		/*
		 * This method should do 2 two things.
		 * - update the topping inventory every time we use t topping (accounting for extra toppings as well)
		 * - connect the topping to the pizza
		 *   What that means will be specific to your yimplementatinon.
		 * 
		 * Ideally, you should't let toppings go negative....but this should be dealt with BEFORE calling this method.
		 * 
		 */
		
		try {
			connect_to_db();

			double sizeAmount = 0.0;
			if (p.getSize() == size_s) {
				sizeAmount = t.getPerAMT();
			} else if (p.getSize() == size_m) {
				sizeAmount = t.getMedAMT();
			} else if (p.getSize() == size_l) {
				sizeAmount = t.getLgAMT();
			} else if (p.getSize() == size_xl) {
				sizeAmount = t.getXLAMT();
			}

			if (!isDoubled) 
			{
				if (t.getCurINVT() - sizeAmount < 0) 
				{
					System.out.println("We don't have enough of that topping to add it...");
				} 
				else 
				{
					String updateStatement = null;
					updateStatement = "update topping set Topping_CurInvLevel=Topping_CurInvLevel-" + sizeAmount + " where Topping_ID= " + t.getTopID();
					PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
					preparedStatement.executeUpdate();
			    }
		    }
			
			else 
			{

				if (t.getCurINVT() - 2 * sizeAmount < 0) 
				{
					System.out.println("We don't have enough of that topping to add it...");
				} else 
				{
					String updateStatement = null;
					updateStatement = "update topping set Topping_CurInvLevel=Topping_CurInvLevel- " + 2 * sizeAmount + " where Topping_ID= " + t.getTopID();
					PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
					preparedStatement.executeUpdate();
				}
			}


			conn.close();

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void updatePizzaTopping(ArrayList<Integer[]> pizzatop) {
		try {
			connect_to_db();
			String sql = "insert into pizza_topping ( Pizza_ID, Topping_ID, Is_Double) values(?, ?,?)";

			
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			for (Integer[] discdata : pizzatop) {
	
				preparedStatement.setInt(1, discdata[0]);
				preparedStatement.setInt(2, discdata[1]);
				preparedStatement.setInt(3, discdata[2]);
				preparedStatement.executeUpdate();
			}
			/*
			 * Adds toAdd amount of topping to topping t.
			 */
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			    }
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void addPizzaDiscount(ArrayList<Integer[]> pizzadisc) {
		try {
			connect_to_db();

			String sql = "insert into pizza_discount ( Pizza_ID, Discount_ID) values(?, ?)";
			
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			for (Integer[] discdata : pizzadisc) {
				
				preparedStatement.setInt(1, discdata[0]);
				preparedStatement.setInt(2, discdata[1]);
				preparedStatement.executeUpdate();
			}

			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
	public static void usePizzaDiscount(Pizza p, Discount d) throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * This method connects a discount with a Pizza in the database.
		 * 
		 * What that means will be specific to your implementatinon.
		 */
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void useOrderDiscount(Order od, Discount d) throws SQLException, IOException
	{
		

		/*
		 * This method connects a discount with an order in the database
		 *
		 * You might use this, you might not depending on where / how to want to update
		 * this information in the dabast
		 */


		try {
			int OrderID =1;
			int DiscountID=1;
			OrderID = od.getOrderID();
			DiscountID=d.getDiscountID();
			connect_to_db();
			String sql = "insert into order_discount ( Order_ID, Discount_ID) values(?, ?)";

			//Connection conn = DBConnector.make_connection();
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, OrderID);
			preparedStatement.setInt(2, DiscountID);
			preparedStatement.executeUpdate();
			/*
			 * Adds toAdd amount of topping to topping t.
			 */
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void updateOrder(Order o, int oid) {
		try {
			connect_to_db();
			String sql = "update ordertable SET Order_Type = ? WHERE Order_ID = ?;";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, o.getOrderType());
			ps.setInt(2, oid);
			ps.executeUpdate();

			sql = "update ordertable SET Order_PriceToBusiness = ? WHERE Order_ID = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, o.getBusPrice());
			ps.setInt(2, oid);
			ps.executeUpdate();

			sql = "update ordertable SET Order_PriceToCustomer = ? WHERE Order_ID = ?";
			
			ps = conn.prepareStatement(sql);
			ps.setDouble(1, o.getCustPrice());
			ps.setInt(2, oid);
			ps.executeUpdate();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void addCustomer(Customer c) throws SQLException, IOException {

		/*
		 * This method adds a new customer to the database.
		 * 
		 */
		/*
		 * This method adds a new customer to the database.
		 *
		 */
		try {
			connect_to_db();
			int CustomerID = -1;
			String customerquery = "SELECT * FROM customer where Customer_ID = (SELECT MAX(Customer_ID) from customer)\n";
			PreparedStatement custps = conn.prepareStatement(customerquery);
			ResultSet custset = custps.executeQuery();

			while (custset.next()) {
				CustomerID = Integer.parseInt(custset.getString("Customer_ID"));
			}
			CustomerID = CustomerID + 1;

			String sql = "insert into customer(Customer_ID,Customer_FName, Customer_LName, Customer_Phone) values(?,?, ?, ?)";

			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setInt(1, c.getCustID());
			preparedStatement.setString(2, c.getFName());
			preparedStatement.setString(3, c.getLName());
			preparedStatement.setString(4, c.getPhone());
			preparedStatement.executeUpdate();

			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}

	public static void completeOrder(Order o) throws SQLException, IOException {

		/*
		 * Find the specifed order in the database and mark that order as complete in the database.
		 * 
		 */
		try {
			connect_to_db();

			String updateStatement = "update ordertable set Order_IsComplete = 1 where Order_ID = " + o.getOrderID() + " ;";

			PreparedStatement preparedStatement = conn.prepareStatement(updateStatement);
			String completed = "Completed By Kitchen";
			preparedStatement.executeUpdate();
			String updatePizzaStatement = "update pizza set Pizza_Status = ?  where Order_ID = ?";

			PreparedStatement pizzaPreparedStatement = conn.prepareStatement(updatePizzaStatement);
			pizzaPreparedStatement.setString(1,completed);
			pizzaPreparedStatement.setInt(2, o.getOrderID());

			pizzaPreparedStatement.executeUpdate();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}


		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}


	public static ArrayList<Order> getOrders(boolean openOnly) throws SQLException, IOException {

		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
		ArrayList<Order> orders = new ArrayList<Order>();
		try {

			connect_to_db();

			String selectQuery = "select * from ordertable";
			if (openOnly) {
				selectQuery += " where Order_IsComplete = 0 ";
			}

			// selectQuery = "select * from ordertable where Order_IsComplete= ?"
			
			selectQuery += " order by Order_TimeStamp desc;";

			Statement statement = conn.createStatement();

			ResultSet record = statement.executeQuery(selectQuery);

			while (record.next()) {
				Integer orderId = record.getInt("Order_ID");
				Integer customerId = record.getInt("Customer_ID");
				String orderType = record.getString("Order_Type");
				String orderTimeStamp = record.getString("Order_TimeStamp");
				Integer OrderCompleteState = record.getInt("Order_IsComplete");
				Double orderPrice = record.getDouble("Order_PriceToBusiness");
				Double orderCost = record.getDouble("Order_PriceToCustomer");
				orders.add(
						new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));
			}
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return orders;


		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
		public static ArrayList<Order> getCompletedOrders(boolean openOnly) throws SQLException, IOException {

		/*
		 * Return an arraylist of all of the orders.
		 * 	openOnly == true => only return a list of open (ie orders that have not been marked as completed)
		 *           == false => return a list of all the orders in the database
		 * Remember that in Java, we account for supertypes and subtypes
		 * which means that when we create an arrayList of orders, that really
		 * means we have an arrayList of dineinOrders, deliveryOrders, and pickupOrders.
		 * 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */
        ArrayList<Order> orders = new ArrayList<Order>();
		try {
			connect_to_db();

			String selectQuery = "select * from ordertable";
			if (!openOnly) {
				selectQuery += " where Order_IsComplete = 1" ;
			}
			
			selectQuery += " order by Order_TimeStamp desc;";

			Statement statement = conn.createStatement();

			ResultSet record = statement.executeQuery(selectQuery);

			while (record.next()) {
				Integer orderId = record.getInt("Order_ID");
				Integer customerId = record.getInt("Customer_ID");
				String orderType = record.getString("Order_Type");
				String orderTimeStamp = record.getString("Order_TimeStamp");
				Integer OrderCompleteState = record.getInt("Order_IsComplete");
				Double orderPrice = record.getDouble("Order_PriceToBusiness");
				Double orderCost = record.getDouble("Order_PriceToCustomer");
				orders.add(
						new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));
			}
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return orders;


		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	public static Order getLastOrder(){
		/*
		 * Query the database for the LAST order added
		 * then return an Order object for that order.
		 * NOTE...there should ALWAYS be a "last order"!
		 */
		




		 return null;
	}

	public static ArrayList<Order> getOrdersByDate(String date){
		/*
		 * Query the database for ALL the orders placed on a specific date
		 * and return a list of those orders.
		 *  
		 */
		ArrayList<Order> orders = new ArrayList<Order>();
		try {
			connect_to_db();

			String selectQuery = "select * from ordertable";
			if (date != null) {
				selectQuery += " where (Order_TimeStamp >= '" + date + " 00:00:00')";
			}
			
			selectQuery += " order by Order_TimeStamp desc;";

			Statement statement = conn.createStatement();

			ResultSet record = statement.executeQuery(selectQuery);

			while (record.next()) {
				Integer orderId = record.getInt("Order_ID");
				Integer customerId = record.getInt("Customer_ID");
				String orderType = record.getString("Order_Type");
				String orderTimeStamp = record.getString("Order_TimeStamp");
				Integer OrderCompleteState = record.getInt("Order_IsComplete");
				Double orderPrice = record.getDouble("Order_PriceToBusiness");
				Double orderCost = record.getDouble("Order_PriceToCustomer");
				orders.add(
						new Order(orderId, customerId, orderType, orderTimeStamp, orderCost, orderPrice, OrderCompleteState));
			}
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return orders;






		 
	}
		
	public static ArrayList<Discount> getDiscountList() throws SQLException, IOException {

		/* 
		 * Query the database for all the available discounts and 
		 * return them in an arrayList of discounts.
		 * 
		*/

		ArrayList<Discount> discs = new ArrayList<Discount>();
		connect_to_db();
		/*
		 * Query the database for all the available discounts and
		 * return them in an arrayList of discounts.
		 *
		 */
		try {
			String getDiscountssql = "SELECT * FROM discount";
			PreparedStatement dpreparedStatement = conn.prepareStatement(getDiscountssql);
			ResultSet discount = dpreparedStatement.executeQuery();
			while (discount.next()) {
				int discountID = discount.getInt("Discount_ID");
				String discountName = discount.getString("Discount_Name");
				boolean dollar_Off = discount.getBoolean("Dollar_Off");
				double amount = discount.getDouble("Percentage_Off");
				discs.add(new Discount(discountID, discountName, amount, dollar_Off));
			}
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return discs;
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		
	}

	public static Discount findDiscountByName(String name){
		/*
		 * Query the database for a discount using it's name.
		 * If found, then return an OrderDiscount object for the discount.
		 * If it's not found....then return null
		 *  
		 */




		 return null;
	}


	public static ArrayList<Customer> getCustomerList() throws SQLException, IOException {

		/*
		 * Query the data for all the customers and return an arrayList of all the customers. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		*/

       ArrayList<Customer> custs = new ArrayList<Customer>();
		connect_to_db();
		try {
			String sql = "SELECT * FROM customer";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);

			ResultSet records = preparedStatement.executeQuery();
			while (records.next()) {
				int custID = records.getInt("Customer_ID");
				String fName = records.getString("Customer_FName");
				String lName = records.getString("Customer_LName");
				String phone = records.getString("Customer_Phone");


				custs.add(
						new Customer(custID, fName, lName, phone));

			}
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}


		/*
		 * return an arrayList of all the customers. These customers should
		 *print in alphabetical order, so account for that as you see fit.
		 */


		//DO NOT FORGET TO CLOSE YOUR CONNECTION

		return custs;
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		
	}

	public static Customer findCustomerByPhone(String phoneNumber){
		/*
		 * Query the database for a customer using a phone number.
		 * If found, then return a Customer object for the customer.
		 * If it's not found....then return null
		 *  
		 */
		




		 return null;
	}


	public static ArrayList<Topping> getToppingList() throws SQLException, IOException {
		connect_to_db();
		/*
		 * Query the database for the aviable toppings and 
		 * return an arrayList of all the available toppings. 
		 * Don't forget to order the data coming from the database appropriately.
		 * 
		 */

		

		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return null;
	}
   public static Topping getToppingFromId(int toppingID) throws SQLException, IOException {
		connect_to_db();
		Topping t = null;
		try {
			String maxOrdSql = "SELECT * FROM topping where topping_Id = " + toppingID;
			PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
			ResultSet results = maxOrderstmt.executeQuery();

			while (results.next()) {
				int tID = results.getInt("Topping_Id");
				String toppingName = results.getString("Topping_Name");
				double toppingPriceToCustomer = results.getDouble("Topping_PriceToCustomer");
				double toppingPriceToBusiness = results.getDouble("Topping_PriceToBusiness");
				int toppingCurrentInvLvl = results.getInt("Topping_CurInvLevel");
				double toppingQuantityForPersonal = results.getDouble("Topping_QuantityForPersonal");
				double toppingQuantityForMedium = results.getDouble("Topping_QuantityForMedium");
				double toppingQuantityForLarge = results.getDouble("Topping_QuantityForLarge");
				double toppingQuantityForXLarge = results.getDouble("Topping_QuantityForXLarge");

				int toppingMinInvLvl = results.getInt("Topping_MinInvLevel");

				t = new Topping(tID, toppingName, toppingQuantityForPersonal,
						toppingQuantityForMedium, toppingQuantityForLarge, toppingQuantityForXLarge,
						toppingPriceToCustomer, toppingPriceToBusiness, toppingMinInvLvl, toppingCurrentInvLvl);
			}
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return t;
		//return null;
	}
	public static Topping findToppingByName(String name){
		/*
		 * Query the database for the topping using it's name.
		 * If found, then return a Topping object for the topping.
		 * If it's not found....then return null
		 *  
		 */
		




		 return null;
	}


	public static void addToInventory(Topping t, double quantity) throws SQLException, IOException {

		/*
		 * Updates the quantity of the topping in the database by the amount specified.
		 * 
		 * */

        try {
			connect_to_db();
			String sql = "UPDATE topping SET Topping_CurInvLevel = Topping_CurInvLevel+ ? WHERE Topping_ID = ?";
			//Connection conn = DBConnector.make_connection();
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			preparedStatement.setDouble(1, quantity);
			preparedStatement.setInt(2, t.getTopID());
			preparedStatement.executeUpdate();
			/*
			 * Adds toAdd amount of topping to topping t.
			 */
			conn.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static double getBaseCustPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		/* 
		 * Query the database fro the base customer price for that size and crust pizza.
		 * 
		*/
		double bp = 0.0;
		/*
		 * Query the database fro the base customer price for that size and crust pizza.
		 *
		 */
		try {
			String selectQuery = "select * from baseprice;";

			//System.out.println("crust" + crust);
			//System.out.println("size" + size);
			PreparedStatement statement = conn.prepareStatement(selectQuery);
			ResultSet record = statement.executeQuery(selectQuery);
			while (record.next()) {
				String crusttype = record.getString("Pizza_CrustType");
				String sizebase = record.getString("Pizza_Size");
//				System.out.println("crusttyp" + crusttype);
//				System.out.println("siebase" + sizebase);

				if (crusttype.equals(crust) && sizebase.equals(size)) {
					bp = record.getDouble("Baseprice_ToCustomer");
				}
			}
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return bp;
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		
	}

	public static double getBaseBusPrice(String size, String crust) throws SQLException, IOException {
		connect_to_db();
		
		
		double bp = 0.0;
		/*
		 * Query the database fro the base business price for that size and crust pizza.
		 *
		 */
		try {
			String selectQuery = "select * from baseprice;";


			PreparedStatement statement = conn.prepareStatement(selectQuery);
			ResultSet record = statement.executeQuery(selectQuery);
			while (record.next()) {
				String crusttype = record.getString("Pizza_CrustType");
				String sizebase = record.getString("Pizza_Size");


				if (crusttype.equals(crust) && sizebase.equals(size)) {

					bp = record.getDouble("Baseprice_ToBusiness");
				}
			}
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} finally
		  {
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		  }
		return bp;
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		
	}

	public static void printInventory() throws SQLException, IOException {

		/*
		 * Queries the database and prints the current topping list with quantities.
		 *  
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
        try {
			connect_to_db();

			String sql = "SELECT Topping_ID, Topping_Name, Topping_CurInvLevel FROM topping order by Topping_ID";
			PreparedStatement preparedStatement = conn.prepareStatement(sql);
			ResultSet results = preparedStatement.executeQuery();
			System.out.println("Available Toppings:");
			System.out.println("ID\t name\t\t\t\tCurInvT");
			while (results.next()) {

				System.out.println(results.getString("Topping_ID")+ "\t "+results.getString("Topping_Name") + "\t\t\t"+results.getString("Topping_CurInvLevel"));
			}

			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION


	}
	
	public static void printToppingPopReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ToppingPopularity view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */

        try {
			String maxOrdSql = "SELECT * FROM ToppingPopularity";
			PreparedStatement prepared = conn.prepareStatement(maxOrdSql);
			ResultSet report = prepared.executeQuery();
			// int maxOrderID = -1;
			System.out.printf("%-20s  %-4s %n", "Topping", "ToppingCount");
			while (report.next()) {
				String topping = report.getString("Topping");
				String toppingCountStr = report.getString("ToppingCount");
				int toppingCount = Integer.parseInt(toppingCountStr);
				System.out.printf("%-20s  %-4d %n", topping, toppingCount);
			}

			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printProfitByPizzaReport() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByPizza view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		
		try {
			String maxOrdSql = "SELECT * FROM ProfitByPizza";
			PreparedStatement prepared = conn.prepareStatement(maxOrdSql);
			ResultSet report = prepared.executeQuery();
			System.out.println("Pizza Size\t Pizza Crust\t Profit\t Last Order Date");
			while (report.next()) {

				String size = report.getString("Size");
				String crust = report.getString("Crust");
				Double profit = report.getDouble("Profit");
				String orderDate = report.getString("LastOrderDate");

				System.out.println( size +"\t\t"+ crust+"\t\t"+ profit+"\t\t"+orderDate);
			}
			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
	}
	
	public static void printProfitByOrderType() throws SQLException, IOException
	{
		connect_to_db();
		/*
		 * Prints the ProfitByOrderType view. Remember that this view
		 * needs to exist in your DB, so be sure you've run your createViews.sql
		 * files on your testing DB if you haven't already.
		 * 
		 * The result should be readable and sorted as indicated in the prompt.
		 * 
		 */
		
		
		try {
			String maxOrdSql = "SELECT * FROM ProfitByOrder";
			PreparedStatement prepared = conn.prepareStatement(maxOrdSql);
			ResultSet report = prepared.executeQuery();
			System.out.println("Customer Type\t Order Month\t Total Order Price\t Total Order Cost\t Profit");

			while (report.next()) {
				String customerType = report.getString("CustomerType");
				String orderMonth = report.getString("OrderMonth");
				Double totalPrice = report.getDouble("TotalOrderPrice");
				Double totalCost = report.getDouble("TotalOrderCost");
				Double profit = report.getDouble("Profit");
				System.out.println( customerType +"\t\t\t"+ orderMonth+ "\t\t\t" + totalPrice+"\t\t\t"+totalCost+"\t\t\t"+ profit);

			}

			//DO NOT FORGET TO CLOSE YOUR CONNECTION
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		
		//DO NOT FORGET TO CLOSE YOUR CONNECTION	
	}
	
	public static int getNextOrderID() throws SQLException, IOException {

		connect_to_db();
		int maxOrderID = -1;
		try {
			String maxOrdSql = "SELECT * FROM ordertable where Order_ID = (SELECT MAX(Order_ID) from ordertable)\n";
			PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
			ResultSet maxOrder = maxOrderstmt.executeQuery();

			while (maxOrder.next()) {
				maxOrderID = Integer.parseInt(maxOrder.getString("Order_ID"));
			}

			maxOrderID = maxOrderID + 1;

			//DO NOT FORGET TO CLOSE YOUR CONNECTION
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		return maxOrderID;
	}

	public static void dineIn(int orderId, Integer tableNumber) throws SQLException, IOException {
		connect_to_db();
		try {
			String insertStatement = "INSERT INTO dinein" + "(Order_ID,Table_Num) " + "VALUES (?, ?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);

			preparedStatement.setInt(1, orderId);
			preparedStatement.setInt(2, tableNumber);
			preparedStatement.executeUpdate();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void updatePickUp(int maxOrderID,int customerID) throws SQLException, IOException {
		connect_to_db();
		try {
			String insertStatement = "INSERT INTO pickup" + "(Order_ID,Customer_ID,Order_IsPicked) " + "VALUES (?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);
			preparedStatement.setInt(1, maxOrderID);
			preparedStatement.setInt(2, customerID);
			preparedStatement.setInt(3, 1);
			preparedStatement.executeUpdate();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	public static void updateDelivery(int maxOrderID, String customerAddress) throws SQLException, IOException {
		connect_to_db();
		try {
			String insertStatement = "INSERT INTO delivery" + "(Order_ID,Order_IsDelivered, Customer_Address) " + "VALUES (?,?,?)";
			PreparedStatement preparedStatement = conn.prepareStatement(insertStatement);
			preparedStatement.setInt(1, maxOrderID);
			preparedStatement.setInt(2, 1);
			preparedStatement.setString(3, customerAddress);
			preparedStatement.executeUpdate();
			conn.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
		public static int getMaxPizzaID() {
		int maxOrderID = -1;
		try {
			connect_to_db();
			String maxOrdSql = "SELECT * FROM pizza where Pizza_ID = (SELECT MAX(Pizza_ID) from pizza)";
			PreparedStatement maxOrderstmt = conn.prepareStatement(maxOrdSql);
			ResultSet maxOrder = maxOrderstmt.executeQuery();

			while (maxOrder.next())
				maxOrderID = Integer.parseInt(maxOrder.getString("Pizza_ID"));

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		//DO NOT FORGET TO CLOSE YOUR CONNECTION
		return maxOrderID;
	}
	public static String getCustomerName(int CustID) throws SQLException, IOException
	{
	/*
		 * This is a helper method to fetch and format the name of a customer
		 * based on a customer ID. This is an example of how to interact with 
		 * your database from Java.  It's used in the model solution for this project...so the code works!
		 * 
		 * OF COURSE....this code would only work in your application if the table & field names match!
		 *
		 */

		 connect_to_db();

		/* 
		 * an example query using a constructed string...
		 * remember, this style of query construction could be subject to sql injection attacks!
		 * 
		 */
		String cname1 = "";
		String query = "Select Customer_Fname, Customer_Lname From customer WHERE Customer_ID=" + CustID + ";";
		Statement stmt = conn.createStatement();
		ResultSet rset = stmt.executeQuery(query);
		
		while(rset.next())
		{
			cname1 = rset.getString(1) + " " + rset.getString(2); 
		}

		/* 
		* an example of the same query using a prepared statement...
		* 
		*/
		// String cname2 = "";
		// PreparedStatement os;
		// ResultSet rset2;
		// String query2;
		// query2 = "Select FName, LName From customer WHERE CustID=?;";
		// os = conn.prepareStatement(query2);
		// os.setInt(1, CustID);
		// rset2 = os.executeQuery();
		// while(rset2.next())
		// {
		// 	cname2 = rset2.getString("FName") + " " + rset2.getString("LName"); // note the use of field names in the getSting methods
		// }

		// conn.close();
		return cname1; // OR cname2
	}

	/*
	 * The next 3 private methods help get the individual components of a SQL datetime object. 
	 * You're welcome to keep them or remove them.
	 */
	private static int getYear(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(0,4));
	}
	private static int getMonth(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(5, 7));
	}
	private static int getDay(String date)// assumes date format 'YYYY-MM-DD HH:mm:ss'
	{
		return Integer.parseInt(date.substring(8, 10));
	}

	public static boolean checkDate(int year, int month, int day, String dateOfOrder)
	{
		if(getYear(dateOfOrder) > year)
			return true;
		else if(getYear(dateOfOrder) < year)
			return false;
		else
		{
			if(getMonth(dateOfOrder) > month)
				return true;
			else if(getMonth(dateOfOrder) < month)
				return false;
			else
			{
				if(getDay(dateOfOrder) >= day)
					return true;
				else
					return false;
			}
		}
	}


}