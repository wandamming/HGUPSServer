package com.hgups.express.business.ec.shopify.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class Order implements Serializable {

    /**
     * order : {"line_items":[{"title":"Big Brown Bear Boots","price":74.99,"grams":"1300","quantity":3,"tax_lines":[{"price":13.5,"rate":0.06,"title":"State tax"}]}],"transactions":[{"kind":"sale","status":"success","amount":238.47}],"total_tax":13.5,"currency":"EUR"}
     */

    private OrderBean order = new OrderBean();

    @Data
    public static class OrderBean implements Serializable {

        public OrderBean() {
            OrderBean.LineItemsBean lineItemsBean = new OrderBean.LineItemsBean();
            OrderBean.TransactionsBean transactionsBean = new OrderBean.TransactionsBean();
            line_items.add(lineItemsBean);
            transactions.add(transactionsBean);
        }

        /**
         * line_items : [{"title":"Big Brown Bear Boots","price":74.99,"grams":"1300","quantity":3,"tax_lines":[{"price":13.5,"rate":0.06,"title":"State tax"}]}]
         * transactions : [{"kind":"sale","status":"success","amount":238.47}]
         * total_tax : 13.5
         * currency : EUR
         */

        private double total_tax = 13.5;
        private String currency = "EUR";
        private List<LineItemsBean> line_items = new ArrayList<>();
        private List<TransactionsBean> transactions = new ArrayList<>();

        @Data
        public static class LineItemsBean implements Serializable {
            /**
             * title : Big Brown Bear Boots
             * price : 74.99
             * grams : 1300
             * quantity : 3
             * tax_lines : [{"price":13.5,"rate":0.06,"title":"State tax"}]
             */

            private String title = "Big Brown Bear Boots";
            private double price = 74.99;
            private String grams = "1300";
            private int quantity = 3;
            private List<TaxLinesBean> tax_lines;

            @Data
            public static class TaxLinesBean implements Serializable {
                /**
                 * price : 13.5
                 * rate : 0.06
                 * title : State tax
                 */

                private double price = 13.5;
                private double rate = 0.06;
                private String title = "State tax";
            }
        }

        @Data
        public static class TransactionsBean implements Serializable {
            /**
             * kind : sale
             * status : success
             * amount : 238.47
             */

            private String kind = "sale";
            private String status = "success";
            private double amount = 238.47;
        }
    }
}
