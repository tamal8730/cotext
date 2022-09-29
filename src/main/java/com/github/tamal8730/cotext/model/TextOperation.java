package com.github.tamal8730.cotext.model;

public class TextOperation {

    private String opName; //ins, del
    private String operand;
    private int position;

    public TextOperation() {
    }

    public TextOperation(String opName, String operand, int position) {
        this.opName = opName;
        this.operand = operand;
        this.position = position;
    }


    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public String getOperand() {
        return operand;
    }

    public void setOperand(String operand) {
        this.operand = operand;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
