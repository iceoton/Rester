package com.itonlab.rester.model;

/**
 * คลาสตัวแทนรายละเอียดของ order ต่างๆ ที่จะแสดงในหน้า summary และ history detail
 */
public class OrderItemDetail {
    private int preOderId;
    private String menuCode;
    private String nameTH;
    private String nameEN;
    private double price;
    private int quantity;
    // รสชาติเพิ่มเติม
    private String option;
    private boolean ordered;
    private boolean served = false;
    private PreOrderItem.Status status = PreOrderItem.Status.UNDONE;

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

    public String getNameTH() {
        return nameTH;
    }

    public void setNameTH(String nameTH) {
        this.nameTH = nameTH;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
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

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }

    public PreOrderItem.Status getStatus() {
        return status;
    }

    public void setStatus(PreOrderItem.Status status) {
        this.status = status;
    }
}
