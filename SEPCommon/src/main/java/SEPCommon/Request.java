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
	FetchAuctions,			//Acitve, ActiveAndEnded, MyBids, MyAuctions, SavedAuctions
	DeleteOrder,
	SendRating,
	FetchRatings,			// 2 Typen, alle Bewertungen und Durchschnitt
	FetchOrders
}
