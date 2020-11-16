package SEPServer.SQL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Vector;

import SEPCommon.Response;
import SEPCommon.User;
import SEPCommon.Product;

import java.sql.Date;

public class SQL{

	private boolean isConnected;

	public void connect() {

	}

	public Response registerUser(User user) {
		return Response.Success;
	}

	public Response loginUser(User user) {
		return Response.Success;
	}

	public Response editUser(User user) {
		return Response.Success;
	}

	public Response deleteUser(User user) {
		return Response.Success;
	}

	public Response increaseWallet(User user, double amount) {
		return Response.Success;
	}

	public Response decreaseWallet(User user, double amount) {
		return Response.Success;
	}

	public Response fetchProducts() {
		return Response.Success;
	}

	public Response fetchProductsByCategory(String category) {
		return Response.Success;
	}

	public Response fetchProductsByString(String searchString) {
		return Response.Success;
	}

	public Response fetchLastViewedProducts(User user) {
		return Response.Success;
	}

	public Response addItem(User seller, Product product) {
		return Response.Success;
	}

	public Response addItem(User seller, Product[] products) {
		return Response.Success;
	}

	public Response buyItem(User buyer, Product product) {
		return Response.Success;
	}

	public User getUserDataByEmail(String email) {
		return null;
	}

	public User getUserDataByUsername(String username) {
		return null;
	}
}
