package SEPCommon;

public enum ShippingType {
	Shipping { //Versand
		public String toString()
		{
			return "Versand";
		}
	},
	PickUp { //Abholung
		public String toString()
		{
			return "Abholung";
		}
	}
}
