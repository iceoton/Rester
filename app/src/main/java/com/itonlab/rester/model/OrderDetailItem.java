package com.itonlab.rester.model;

public class OrderDetailItem {
    private int preOderId;
    private int menuId;
    private String name;
    private double price;
    private int amount;

    public int getPreOderId() {
        return preOderId;
    }

    public void setPreOderId(int preOderId) {
        this.preOderId = preOderId;
    }

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
