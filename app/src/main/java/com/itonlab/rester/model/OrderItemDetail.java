package com.itonlab.rester.model;

/**
 * คลาสตัวแทนรายละเอียดของ order ต่างๆ ที่จะแสดงในหน้า summary และ history detail
 */
public class OrderItemDetail {
    private int preOderId;
    private String menuCode;
    private String name;
    private double price;
    private int quantity;
    // รสชาติเพิ่มเติม
    private String option;

    public int getPreOderId() {
        return preOderId;
    }

    public void setPreOderId(int preOderId) {
        this.preOderId = preOderId;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
