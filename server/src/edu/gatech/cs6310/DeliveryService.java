package edu.gatech.cs6310;
import java.util.Scanner;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class DeliveryService {

    private String errorCodeToString(ErrorCode code) {
        switch (code) {
            case STORE_ID_DUPLICATE:
                return "ERROR:store_identifier_already_exists";
            case STORE_ID_NOT_FOUND:
                return "ERROR:store_identifier_does_not_exist";
            case ITEM_ID_DUPLICATE:
                return "ERROR:item_identifier_already_exists";
            case ITEM_ID_NOT_FOUND:
                return "ERROR:item_identifier_does_not_exist";
            case PILOT_ID_DUPLICATE:
                return "ERROR:pilot_identifier_already_exists";
            case PILOT_ID_NOT_FOUND:
                return "ERROR:pilot_identifier_does_not_exist";
            case DRONE_ID_DUPLICATE:
                return "ERROR:drone_identifier_already_exists";
            case DRONE_ID_NOT_FOUND:
                return "ERROR:drone_identifier_does_not_exist";
            case ORDER_ID_DUPLICATE:
                return "ERROR:order_identifier_already_exists";
            case ORDER_ID_NOT_FOUND:
                return "ERROR:order_identifier_does_not_exist";
            case DRONE_NEEDS_PILOT:
                return "ERROR:drone_needs_pilot";
            case DRONE_NEEDS_FUEL:
                return "ERROR:drone_needs_fuel";
            case CUSTOMER_ID_DUPLICATE:
                return "ERROR:customer_identifier_already_exists";
            case CUSTOMER_ID_NOT_FOUND:
                return "ERROR:customer_identifier_does_not_exist";
            case DRONE_INSUFFICIENT_CAPACITY:
                return "ERROR:drone_cant_carry_new_item";
            case NEW_DRONE_INSUFFICIENT_CAPACITY:
                return "ERROR:new_drone_does_not_have_enough_capacity";
            case ITEM_ALREADY_ORDERED:
                return "ERROR:item_already_ordered";
            case CUSTOMER_INSUFFICIENT_CREDIT:
                return "ERROR:customer_cant_afford_new_item";
            case DRONE_NO_CHANGE:
                return "OK:new_drone_is_current_drone_no_change";
            case PILOT_LICENSE_DUPLICATE:
                return "ERROR:pilot_license_already_exists";
            case REFUELING_STATION_ID_DUPLICATE:
                return "ERROR:refueling_station_identifier_already_exists";
            case REFUELING_STATION_ID_NOT_FOUND:
                return "ERROR:refueling_station_not_found";
            case NOT_ENOUGH_FUEL:
                return "ERROR:drone_not_enough_fuel";
            case TRIP_UNROUTABLE:
                return "ERROR:drone_trip_unroutable";
            case TIME_IN_PAST:
                return "ERROR:time_has_already_past";
            case OK:
                return "OK:";
            case UNKNOWN_ERROR:
            default:
                return "UNKNOWN ERROR OCURRED";
        }
    }

    public void commandLoop(long startTime) {
        // Handling between client and server
        // Connections are established on a separate thread from this one, which is main
        // And each connection has its own thread as well for full concurrency
        // Commands issued here are threadsafe due to using the ConcurrentLinkedQueue.
        Queue<CommandResponse> commands = new ConcurrentLinkedQueue<CommandResponse>();
        ConnectionManager connectionManager = new ConnectionManager(6310, commands);
        connectionManager.start();

        CommandResponse input = null;
        String[] tokens;
        final String DELIMITER = ",";

        StoreManager storeManager = StoreManager.getInstance();
        CustomerManager customerManager = CustomerManager.getInstance();
        PilotManager pilotManager = PilotManager.getInstance();
        PerformanceManager performanceManager = PerformanceManager.getInstance();

        // Used to redirct System.out so that we can send this to the client
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outStream);
        PrintStream stdOut = System.out;

        Timestamp serviceTime = new Timestamp(startTime);
        TimeManager timeManager = TimeManager.generateInstance(serviceTime);

        while (true) {
            storeManager.update(serviceTime);
            try {
                java.lang.Thread.sleep(100);
            } catch (InterruptedException i) {
                i.printStackTrace();
                System.out.println("Interrupted exception");
            }
            serviceTime.incrementByMinutes(1);
            timeManager.setTime(serviceTime);
            //System.out.println(serviceTime);
            System.setOut(printStream);
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                if (commands.isEmpty()) {
                    System.out.flush();
                    System.setOut(stdOut);
                    System.out.print(outStream.toString());
                    outStream.reset();
                    continue;
                }
                input = commands.remove();
                tokens = input.getCommand().split(DELIMITER);
                System.out.println("> " + input.getCommand());
                String returnString = "default";

                String functionName = tokens[0];
                performanceManager.startTracking();

                if (tokens[0].equals("make_store")) {
                    //System.out.println("store: " + tokens[1] + ", revenue: " + tokens[2], + "position:);
                    returnString = errorCodeToString(storeManager.makeStore(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_stores")) {
                    //System.out.println("no parameters needed");
                    storeManager.displayStores();
                    returnString = "OK:display_completed";

                } else if (tokens[0].equals("sell_item")) {
                    //System.out.println("store: " + tokens[1] + ", item: " + tokens[2] + ", weight: " + tokens[3]);
                    returnString = errorCodeToString(storeManager.sellItem(tokens[1], tokens[2], Integer.parseInt(tokens[3])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_items")) {
                    //System.out.println("store: " + tokens[1]);
                    returnString = errorCodeToString(storeManager.displayItems(tokens[1]));
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("make_pilot")) {
                    //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                    //System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ", license: " + tokens[6] + ", experience: " + tokens[7]);
                    returnString = errorCodeToString(pilotManager.makePilot(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5], tokens[6], Integer.parseInt(tokens[7])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_pilots")) {
                    //System.out.println("no parameters needed");
                    pilotManager.displayPilots();
                    returnString = "OK:display_completed";


                } else if (tokens[0].equals("make_drone")) {
                    // New handlling - if there are 6 (7 counting make_drone) arguments it is to create a normal drone
                    // If there are 7 (8 counting make_drone) it is to create a solar drone
                    if (tokens.length == 7) {
                        // Normal Drone
                        returnString = errorCodeToString(storeManager.makeDrone(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]),
                                Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6])));
                    } else if (tokens.length == 8) {
                        // Solar Drone
                        returnString = errorCodeToString(storeManager.makeDrone(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]),
                                Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7])));
                    }
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_drones")) {
                    //System.out.println("store: " + tokens[1]);
                    returnString = errorCodeToString(storeManager.displayDrones(tokens[1]));
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("fly_drone")) {
                    //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
                    returnString = errorCodeToString(storeManager.flyDrone(tokens[1], Integer.parseInt(tokens[2]), tokens[3]));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("make_customer")) {
                    //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                    //System.out.println(", phone: " + tokens[4] + ", rating: " + tokens[5] + ", credit: " + tokens[6]);
                    returnString = errorCodeToString(customerManager.makeCustomer(tokens[1], tokens[2], tokens[3], tokens[4],
                            Integer.parseInt(tokens[5]), Integer.parseInt(tokens[6]), Integer.parseInt(tokens[7]), Integer.parseInt(tokens[8])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_customers")) {
                    //System.out.println("no parameters needed");
                    customerManager.displayCustomers();
                    returnString = "OK:display_completed";
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("start_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", drone: " + tokens[3] + ", customer: " + tokens[4]);
                    returnString = errorCodeToString(storeManager.startOrder(tokens[1], tokens[2], Integer.parseInt(tokens[3]), tokens[4]));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_orders")) {
                    //System.out.println("store: " + tokens[1]);
                    returnString = errorCodeToString(storeManager.displayOrders(tokens[1]));
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("request_item")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
                    returnString = errorCodeToString(storeManager.requestItem(tokens[1], tokens[2], tokens[3], Integer.parseInt(tokens[4]), Integer.parseInt(tokens[5])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("purchase_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                    Timestamp purchaseTime = new Timestamp(Long.parseLong(tokens[3]));
                    if (serviceTime.havePassed(purchaseTime)) {
                        returnString = errorCodeToString(ErrorCode.TIME_IN_PAST);
                    } else {
                        returnString = errorCodeToString(storeManager.purchaseOrder(tokens[1], tokens[2], purchaseTime, input));
                        if (returnString.equals("OK:")) {
                            returnString += "order_scheduled";
                        }
                    }

                } else if (tokens[0].equals("cancel_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                    returnString = errorCodeToString(storeManager.cancelOrder(tokens[1], tokens[2]));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("transfer_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", new_drone: " + tokens[3]);
                    returnString = errorCodeToString(storeManager.transferOrder(tokens[1], tokens[2], Integer.parseInt(tokens[3])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("display_efficiency")) {
                    //System.out.println("no parameters needed");
                    storeManager.displayEfficiency();
                    returnString = "OK:display_completed";

                } else if (tokens[0].equals("make_refueling_station")) {
                    returnString = errorCodeToString(storeManager.makeRefuelingStation(tokens[1], tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4]),
                            Integer.parseInt(tokens[5])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }

                } else if (tokens[0].equals("adjust_refueling_station")) {
                    returnString = errorCodeToString(storeManager.adjustRefuelingStation(tokens[1], tokens[2], Integer.parseInt(tokens[3]), Integer.parseInt(tokens[4])));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }
                } else if (tokens[0].equals("remove_refueling_station")) {
                    returnString = errorCodeToString(storeManager.removeRefuelingStation(tokens[1], tokens[2]));
                    if (returnString.equals("OK:")) {
                        returnString += "change_completed";
                    }
                } else if (tokens[0].equals("display_refueling_stations")) {
                    //System.out.println("store: " + tokens[1]);
                    returnString = errorCodeToString(storeManager.displayRefuelingStations(tokens[1]));
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("display_performance")) {
                    returnString = errorCodeToString(performanceManager.displayPerformance());
                    if (returnString.equals("OK:")) {
                        returnString += "display_completed";
                    }

                } else if (tokens[0].equals("deliver_fuel")) {
                    // store, fromID, toID
                    returnString = errorCodeToString(storeManager.deliverFuel(tokens[1], Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]), input));
                    if (returnString.equals("OK:")) {
                        returnString += "delivery_scheduled";
                    }

                } else if (tokens[0].equals("set_time")) {
                    serviceTime = new Timestamp(Long.parseLong(tokens[1]));

                } else if (tokens[0].equals("stop")) {
                    System.out.println("stop acknowledged");

                    break;

                } else {
                    //System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
                performanceManager.endTracking(functionName);

                if (!returnString.equals("default")) {
                    System.out.println(returnString);
                }

            } catch (Exception e) {
                //System.out.println(e.getStackTrace());
                e.printStackTrace();
                System.out.println();
            }
            System.out.flush();
            System.setOut(stdOut);
            System.out.print(outStream.toString());
            input.setResponse(outStream.toString());
            outStream.reset();
        }

        System.out.flush();
        System.setOut(stdOut);
        System.out.print(outStream.toString());
        connectionManager.shutdown();
        System.exit(0);
    }
}
