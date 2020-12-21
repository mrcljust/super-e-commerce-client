package SEPCommon;

public enum Request {
	RegisterUser,
	LoginUser,
	EditUser,
	IncreaseWallet,
	DecreaseWallet,
	LastViewedProducts,
	AddLastViewedProduct,
	FetchProducts,
	AddItem,
	AddItems,
	BuyItem,
	DeleteUser,
	GetUserData,
	CreateAuction,
	SendBid,
	FetchAuctions,			//AuctionType Enum, MyBids, MyAuctions, SavedAuctions
	SaveAuction,
	DeleteOrder,
	SendRating,
	FetchRatings,			// 2 Typen, alle Bewertungen und Durchschnitt
	FetchOrders,
	GetServerDateTime //aktuelle Zeit vom Server
}
