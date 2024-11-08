package com.example.e_krushi.model;

public class Order {
    private String orderImage;
    private String orderStatus;
    private String orderName;
    private String orderPrice;
    private String formattedOrderDate;
    private String formattedOrderDateOnly;
    private String orderID;
    private String productID;

    public Order(String orderImage, String orderStatus, String orderName, String orderPrice, String formattedOrderDateOnly, String formattedOrderDate, String orderID, String productID) {
        this.orderImage = orderImage;
        this.orderStatus = orderStatus;
        this.orderName = orderName;
        this.orderPrice = orderPrice;
        this.orderID = orderID;
        this.formattedOrderDate = formattedOrderDate;
        this.formattedOrderDateOnly = formattedOrderDateOnly;
        this.productID = productID;
    }

    public String getOrderImage() {
        return orderImage;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public String getOrderName() {
        return orderName;
    }

    public String getName() {
        return orderName;
    }

    public String getImage() {
        return orderImage;
    }
    public String getOrderPrice() {
        return orderPrice;
    }
    public String getOrderDate() {
        return formattedOrderDateOnly;
    }
    public String expectedDate() {
        return formattedOrderDate;
    }
    public String orderID() {
        return orderID;
    }
    public String productID() {
        return productID;
    }
}
