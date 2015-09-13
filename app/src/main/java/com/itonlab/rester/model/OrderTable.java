package com.itonlab.rester.model;

public class OrderTable {
    public static final String TABLE_NAME = "'order'";

    public static class Columns {
        public Columns() {
        }

        public static final String _ID = "id";
        public static final String _TOTAL = "total";
        public static final String _ORDER_TIME = "order_time";
        public static final String _SERVED = "served";
    }
}
