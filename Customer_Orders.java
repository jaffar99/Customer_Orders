
/*============================================================================



============================================================================*/

import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.sql.*;
import java.util.Date;

/*============================================================================
CLASS Customer_Orders
============================================================================*/

public class Customer_Orders {
	private Connection conDB; // Connection to the database system.
	private String url; // URL: Which database?

	private Integer custID; // Who are we tallying?
	private String custName;
	private String city;// Name of that customer.
	int row, year, weight;
	String category, language, title, club;
	double price = 0.0;

	// Constructor
	public Customer_Orders(String[] args) throws IOException {
		// Set up the DB connection.
		try {
			// Register the driver with DriverManager.
			Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (InstantiationException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			System.exit(0);
		}

		// URL: Which database?
		url = "jdbc:db2:c3421a";

		// Initialize the connection.
		try {
			// Connect with a fall-thru id & password
			conDB = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.print("\nSQL: database connection error.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Let's have autocommit turned off. No particular reason here.
		try {
			conDB.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.print("\nFailed trying to turn autocommit off.\n");
			e.printStackTrace();
			System.exit(0);
		}

		// Who are we tallying?
		if (args.length != 1) {
			// Don't know what's wanted. Bail.
			System.out.println("\nUsage: java Customer_Orders cust#");
			System.exit(0);
		} else {
			try {

				custID = new Integer(args[0]);
			} catch (NumberFormatException e) {
				System.out.println("\nUsage: java Customer_Orders cust#");
				System.out.println("Provide an INT for the cust#.");
				System.exit(0);
			}
		}
	}

	public boolean customerCheck() { // checks the customer ID 
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.

		boolean inDB = false; // Return.

		queryText = "SELECT name,city      " + "FROM yrb_customer " + "WHERE cid = ?     ";

		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, custID.intValue());
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Any answer?
		try {
			if (answers.next()) {
				inDB = true; // IF ID EXISTS DISPLAY CID, NAME AND CITY
				custName = answers.getString("name");
				city = answers.getString("city");
				System.out.println("cID " + "   NAME     " + " City");
				System.out.println(custID + "  " + custName + "  " + city);

			} else {
				inDB = false;
				custName = null;
				city = null;
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		return inDB;
	}

	public void update(String n, String c) {  // updates the new name and city
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "update yrb_customer set name=?, city=? where cid =?";

		System.out.println(queryText);
		// Prepare the query.
		try {
			conDB.setAutoCommit(true);
			querySt = conDB.prepareStatement(queryText);
			querySt.setString(1, n.toString());
			querySt.setString(2, c.toString());
			querySt.setInt(3, custID.intValue());
			querySt.executeUpdate();
			System.out.println("Updated Successfully");
			conDB.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);

		}
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	public void displayallCategory() {   // displays all category
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "select Row_Number() over (order by cat ASC) as ROW, cat from yrb_category";
		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}
		try {
			System.out.println("************ Book Categories ***********");

			while (answers.next()) {
				int row = answers.getInt("ROW");
				String cat = answers.getString("cat");
				System.out.println(row + "   " + cat);
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	public void displaycat(int cat) { // display the selected category
		String result = "";
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "select cat from (select Row_Number() over (order by cat ASC) as ROW, cat from yrb_category) where row=?";
		// Prepare the query.
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, cat);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}
		try {
			if (answers.next()) {

				category = answers.getString("cat");
				System.out.println("Category " + category.toUpperCase() + " is selected ");
			} else
				System.out.println("Invalid choice");
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	public boolean bookinfo(String bookname) { // prints all the books with the given title and category
		title = bookname;
		boolean exists = false;
		String result = "";
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "select RowNumber() over (order by cat ASC) as sNo, b.title, b.year, b.language, b.cat, b.weight,o.club "
				+ "from yrb_book b,yrb_offer o , yrb_member m"

				+ " where m.cid=? and m.club=o.club and o.title= b.title and o.year=b.year and b.cat=? and b.title=? ";
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, custID.intValue());
			querySt.setString(2, category.toString());
			querySt.setString(3, title.toString());

			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}
		try {
			System.out.println("sno     TITLE    YEAR  LANGUAGE  CAT   WEIGHT");
			System.out.println("---  ----------- ----- -------- ------ ------");
			while (answers.next()) {

				exists = true;
				row = answers.getInt("sNo");
				year = answers.getInt("YEAR");
				language = answers.getString("LANGUAGE");
				weight = answers.getInt("WEIGHT");

				System.out.println(
						row + "  " + title + " " + year + " " + language + " " + category + " " + " " + " " + weight);
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
		return (exists);
	}

	public double minimumprice(int sno) {  // calculates the minimum price

		String result = "";
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "  with offers (sno, cid, title,year,language,cat,weight,club,price) as ( "
				+ "				     select RowNumber() over (order by cat ASC) as sNo, cid,b.title, b.year, b.language, b.cat, b.weight, o.club, o.price "
				+ "				     from yrb_book b,yrb_offer o , yrb_member m "
				+ "				     where m.cid=?  and m.club=o.club and o.title= b.title and o.year=b.year and b.cat=? and b.title=?) "
				+ "				    select * from offers   where title in "
				+ "             ( select title from offers where sno=?) " + "             and price = "
				+ "             ( select min(price) from offers)";
		try {
			querySt = conDB.prepareStatement(queryText);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Execute the query.
		try {
			querySt.setInt(1, custID.intValue());
			querySt.setString(2, category.toString());
			querySt.setString(3, title.toString());
			querySt.setInt(4, sno);
			answers = querySt.executeQuery();
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in execute");
			System.out.println(e.toString());
			System.exit(0);
		}
		try {
			if (answers.next()) {
				club = answers.getString("club");
				price = answers.getDouble("price");
				System.out.println(" The minimum price for " + title + " is " + price + ", offered by " + club);
			}
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in cursor.");
			System.out.println(e.toString());
			System.exit(0);
		}

		// Close the cursor.
		try {
			answers.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing cursor.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

		// We're done with the handle.
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}
		return (price);

	}

	public void insertpurchase(int qty) {  // inserts the purchase

		String result = "";
		String queryText = ""; // The SQL text.
		PreparedStatement querySt = null; // The query handle.
		ResultSet answers = null; // A cursor.
		queryText = "insert into yrb_purchase (cid,club,title,year,when,qnty) values (?,?,?,?,?,?)";
		System.out.println(queryText);
		// Prepare the query.
		try {
			conDB.setAutoCommit(true);
			querySt = conDB.prepareStatement(queryText);
			querySt.setInt(1, custID.intValue());
			querySt.setString(2, club.toString());
			querySt.setString(3, title.toString());
			querySt.setInt(4, year);
			Date date = new Date();
			long time = date.getTime();
			Timestamp when = new Timestamp(time);
			querySt.setTimestamp(5, when);
			querySt.setInt(6, qty);
			querySt.executeUpdate();
			System.out.println("Updated Successfully");
			conDB.setAutoCommit(false);
		} catch (SQLException e) {
			System.out.println("SQL#1 failed in prepare");
			System.out.println(e.toString());
			System.exit(0);

		}
		try {
			querySt.close();
		} catch (SQLException e) {
			System.out.print("SQL#1 failed closing the handle.\n");
			System.out.println(e.toString());
			System.exit(0);
		}

	}

	public static void main(String args[]) throws IOException {   // all inputs are taken inside main method to avoid scanner class complexities in try-- catch blocks
		String choice;
		args = new String[1]; // sTRING ARRAY CONTAINING CUSTOMER ID
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter Customer ID");
		args[0] = sc.nextLine();
		Customer_Orders ct = new Customer_Orders(args);
		boolean b = ct.customerCheck(); // CHECKING IF ID EXISTS
		while (b != true) {
			System.out.println("Invalid!,  Enter again"); // PROMPTING USER TO ENTER AGAIN IF ID DOESN'T EXIST
			args[0] = sc.nextLine();
			ct = new Customer_Orders(args);
			b = ct.customerCheck();
		}
		if (b == true) // IF ID EXISTS
		{
			System.out.println(" Would you like to update the customer information? (Y/N) "); // IF USER WANTS TO UPDATE
			choice = sc.nextLine();
			while (choice.matches("[^Y,y,N,n]")) // INCASE CHOICE IS NOT ONE AMONG Y, N, y, n
			{
				System.out.println("Invalid, Enter Y/y or N/n only. Enter again"); // ENTER AGAIN
				choice = sc.nextLine();

			}
			if (choice.matches("[Y,y]")) {// IF YES, CALL UPDATE FUNCTION

				System.out.println("Enter Customer's Name ");

				String tempname = sc.nextLine();
				System.out.println("Enter Customer's City ");
				String tempcity = sc.nextLine();
				ct.update(tempname, tempcity);

			}
			if (choice.matches("[^Y,y,N,n]"))
				System.exit(0);
			ct.displayallCategory();
			boolean cont = true;
			while (cont) {
				boolean exists = false;
				while (!exists) {
					System.out.println("Choose the category number or enter 0 to exit");
					int cat = sc.nextInt();
					if (cat == 0)
						System.exit(0);
					sc.nextLine();
					ct.displaycat(cat);
					System.out.println("Enter the title of Book");
					String bookname = sc.nextLine();
					exists = ct.bookinfo(bookname);
					if (!exists) {
						System.out.println("The book with given title and Category doesn' exist. Try Again ");

					}
				}
				System.out.println();
				System.out.println("PRESS Y to CONTINUE the purchase or N to EXIT");
				if (sc.nextLine().matches("[^Y,y]"))
					System.exit(0);
				else {
					System.out.println("Choose the book to purchase by its serial number");
					int sno = sc.nextInt();
					sc.nextLine();
					double p = ct.minimumprice(sno);
					System.out.println("Enter the number of books (Quantity) you want to buy");
					int qty = sc.nextInt();
					sc.nextLine();
					System.out.println("the total price for this book is " + (p * qty));
					System.out.println("Would you like to purchase the book/books? (Y/N)");
					String confirm = sc.nextLine();
					if (confirm.matches("[Y,y]")) {
						ct.insertpurchase(qty);
						System.out.println("Thank you for your purchase. would you like to continue? press Y/N ");
						if (sc.nextLine().matches("[Y,y]"))
							cont = true;
						else {System.out.println("Good Bye!");
							cont = false;}
					} 

				}

			}
		}

		sc.close();

	}
}
