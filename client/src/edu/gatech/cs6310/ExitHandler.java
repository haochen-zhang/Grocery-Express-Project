package edu.gatech.cs6310;

public class ExitHandler {

    private boolean shouldExit;

    public ExitHandler() {
        shouldExit = false;
    }

    public void setExit() {
        this.shouldExit = true;
    }

    public boolean getExit() {
        return this.shouldExit;
    }
}