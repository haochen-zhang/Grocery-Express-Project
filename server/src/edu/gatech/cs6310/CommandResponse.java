package edu.gatech.cs6310;

public class CommandResponse {
    
    private String command;
    //private String response;
    private ClientOutputHandler outputHandler;
    private Store store;

    public CommandResponse(String command, ClientOutputHandler outputHandler) {
        this.command = command;
        //this.response = null;
        this.outputHandler = outputHandler;
    }

    public String getCommand() {
        return this.command;
    }

    public void setResponse(String response) {
        //this.response = response;
        outputHandler.send(response);
    }

    public void setStore(Store store) {
        // I do not like this in terms of coupling but don't have a better way in mind to figure out late fees right now
        this.store = store;
    }

//    public void assessLateFee(String storeName, int fee) {
//        StoreManager.getStoreByName(storeName).assessLateFee(fee);
//    }

    //public boolean hasResponse() {
    //    return this.response != null;
    //}

    //public String getResponse() {
    //    return this.response;
    //}
}