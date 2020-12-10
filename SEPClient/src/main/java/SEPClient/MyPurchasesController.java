package SEPClient;

import java.io.IOException;

import SEPCommon.Customer;

public class MyPurchasesController {

	private static Customer customer = null;

	public static void setCustomer(Customer _customer) {
		customer = _customer;
	}

	public void initialize() throws IOException {

	}

}
