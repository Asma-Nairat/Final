package com.javaapp3;

import javafx.beans.property.SimpleStringProperty;

public class Payment {
    private final SimpleStringProperty date;
    private final SimpleStringProperty amount;
    private final SimpleStringProperty month;

    public Payment(String date, String amount, String month) {
        this.date = new SimpleStringProperty(date);
        this.amount = new SimpleStringProperty(amount);
        this.month = new SimpleStringProperty(month);
    }

    public SimpleStringProperty dateProperty() { return date; }
    public SimpleStringProperty amountProperty() { return amount; }
    public SimpleStringProperty monthProperty() { return month; }
}
