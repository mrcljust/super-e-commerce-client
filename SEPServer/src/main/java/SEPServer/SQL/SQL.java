package SEPServer.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import SEPCommon.Response;

import java.sql.Date;

public class SQL{

	private boolean isConnected;

	public void connect() {

	}

	public Response registerUser(User user) {

	}

	public Response loginUser(User user) {

	}

	public Response editUser(User user) {

	}

	public Response deleteUser(User user) {

	}

	public Response increaseWallet(User user, double amount) {

	}

	public Response decreaseWallet(User user, double amount) {

	}

	public Response fetchProducts() {
		return Response.Success;
	}

	public Response fetchProducts(String category) {

	}

	public Response fetchProducts(String searchString) {

	}

	public Response fetchLastViewedProducts(User user) {

	}

	public Response addItem(User seller, Product product) {

	}

	public Response addItem(User seller, Product[] products) {

	}

	public Response buyItem(User buyer, Product product) {

	}

	public User getUserDataByEmail(String email) {

	}

	public User getUserDataByUsername(String username) {

	}
}
