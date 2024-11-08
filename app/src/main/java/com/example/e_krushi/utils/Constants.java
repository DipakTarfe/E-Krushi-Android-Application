package com.example.e_krushi.utils;

public class Constants {
    public static String API_BASE_URL = "https://ekrushi01.000webhostapp.com/";
    public static String GET_CATEGORIES_URL = API_BASE_URL + "/services/listCategory";
    public static String GET_PRODUCTS_URL = API_BASE_URL + "/services/listProduct";
    public static String GET_OFFERS_URL = API_BASE_URL + "/services/listFeaturedNews";
    public static String GET_PRODUCT_DETAILS_URL = API_BASE_URL + "/services/getProductDetails?id=";
    public static String POST_ORDER_URL = API_BASE_URL + "/services/submitProductOrder";
    public static String PAYMENT_URL = API_BASE_URL + "/services/paymentPage?code=";

    public static String NEWS_IMAGE_URL = API_BASE_URL + "/uploads/news/";
    public static String CATEGORIES_IMAGE_URL = API_BASE_URL + "/uploads/category/";
    public static String PRODUCTS_IMAGE_URL = API_BASE_URL + "/uploads/product/";
    public static String SIGNUP_URL = API_BASE_URL + "/signup.php";
    public static String LOGIN_URL = API_BASE_URL + "/login.php";
    public static String USER_DETAILS_URL = API_BASE_URL + "/getUserDetails.php";
    public static String FORGET_PASSWORD_URL = API_BASE_URL + "/forgetPassword.php";
    public static String VERIFY_OTP_URL = API_BASE_URL + "/verifyOTP.php";
    public static String SET_NEW_PASSWORD_URL = API_BASE_URL + "/newPassword.php";
    public static String ADD_TO_CART_URL = API_BASE_URL + "/addToCart.php";
    public static String FETCH_CART_PRODUCT_URL = API_BASE_URL + "/fetchCart.php";
    public static String DELETE_CART_PRODUCT_URL = API_BASE_URL + "/deleteCartProduct.php";
    public static String FETCH_ORDER_DETAILS_URL = API_BASE_URL + "/getOrderDetails.php";
    public static String UPDATE_PROFILE_IMAGE_URL = API_BASE_URL + "/profileImageUpdate.php";
    public static String PROFILE_IMAGE_URL = API_BASE_URL + "/uploads/";
    public static String CANCEL_ORDER_URL = API_BASE_URL + "/cancelOrder.php";
    public static String MARK_DELIVERED_URL = API_BASE_URL + "/markDelivered.php";
}
