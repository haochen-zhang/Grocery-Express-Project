A5 Repo for CS6310 Fall 2022 - Group 37
# Note please run all scripts from the project root directory

### To build:

There are several images associated with the project.
There is a "master" image with contains both client and server code.
To build that:

```
docker build -t gatech/groupproject -f Dockerfile ./
```

There are separate client and server images.
To build those:

Client:
```
docker build -t gatech/groupproject/client -f Client.docker ./
```

Server:
```
docker build -t gatech/groupproject/server -f Server.docker ./
```

### To try out the project interactively

If you want use the master image, you will need to use `tmux` in order to start the client and server in separate windows.

Instructions:

1) Run the new container interactively (ex. `docker run -ti gatech/groupproject bash`)
2) We need to run the client and server in separate terminal windows. To do this I have already included tmux. Here's how to use it:
3) Run `tmux`. This will open up a new tmux session that has a green bar at the bottom. Note that we are currently on window 0.
4) `Ctrl + B`, `C` to create a new window 1. 
5) `Ctrl + B`, `0` to switch back to window 0. 
6) Run `/usr/local/openjdk-17/bin/java -jar server.jar`. Note that we must specify the full path to java, have not found a workaround for that so far. This will launch the server.
7) `Ctrl + B`, `1` to swtich back to window 1.
8) Run `/usr/local/openjdk-17/bin/java -jar client.jar`. This will launch a client and will automatically connect to the server. Now you may run commands as the client. 
9) To quit, right now just use `Ctrl + C` since quitting is broken (working on it).
10) To leave tmux, you can either keep typing `exit` or use `Ctrl + D` to detach from the session.
11) Note that you can create multiple clients at once as well, if you'd like.


If you want to use the client and server images separately (RECOMMENDED):

1) Run the server and detach: `docker run -d gatech/groupproject/server`
2) The previous command will return the ID of the container, use that in the following command.
3) Get the IP of the server: `docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <containerID>`
4) To interactively run client: `docker run -ti gatech/groupproject/client bash`
5) To connect while in client: `java -jar client.jar <serverIP>`
6) Repeat from instruction 4 in a new terminal window to start multiple clients.

### Testing Scripts

For tests between a server and a single client, use scenarios 71-84 that we have developed.

To run: `./scripts/test-clientserver-singleclient.sh <scenario>`

For the test between a server and 3 simultaneous clients, run as follows:

To run: `./scripts/test-clientserver-multiclient.sh`

You can check the results in the generated `docker_results` directory. It follows the same format as A3.

Please note, some tests the expected results and actual results may not be exactly the same, however this is not necessarily indicative of a failure. Due to the nature of the client-server architecture, outputs can slightly change depedning on the order and timing of commands being received. Manual inspection may be required to fully determine case result.

Another important note, if the scripts are not able to complete within the alotted 30 seconds on your system, the amount of time alotted can be changed directly in the test-clientserver-singleclient.sh and test-clientserver-multiclient.sh scripts. This may be necessary depending on hardware differences.

### Available Commands
```
make_store,<Store>,<InitialRevenue>,<xLocation>,<yLocation>
display_stores
sell_item,<Store>,<Item>,<Weight>
display_items,<Store>
make_pilot,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<TaxId>,<LicenseId>,<ExperienceLevel>
display_pilots
make_drone,<Store>,<DroneId>,<WeightCapacity>,<NumberOfDeliveries>,<speed>,<refuelThreshold> # makes a normal drone
make_drone,<Store>,<DroneId>,<WeightCapacity>,<NumberOfDeliveries>,<speed>,<refuelThreshold>,<rechargeRate> # makes a solar drone
display_drones,<Store>
fly_drone,<Store>,<DroneId>,<PilotAccount>
make_customer,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<CustomerRating>,<Credits>,<xLocation>,<yLocation>
display_customers
start_order,<Store>,<OrderId>,<DroneId>,<CustomerAccount>
display_orders,<Store>
request_item,<Store>,<OrderId>,<Item>,<Quantity>,<UnitPrice>
purchase_order,<Store>,<OrderId>,<Timestamp>
cancel_order,<Store>,<OrderId>
transfer_order,<Store>,<OrderId>,<DroneId>
display_efficiency
stop
stop,<waitSecs> # optional parameter, stop after waitSecs
deliver_fuel,<fromDroneID>,<toDroneID>
make_refueling_station,<Store>,<ID>,<Capacity>,<xLocation>,<yLocation>
adjust_refueling_station,<Store>,<ID>,<xLocation>,<yLocation>
remove_refueling_station,<Store>,<ID>
display_refueling_stations,<Store>
set_time,<Timestamp>
display_performance
```

### Performance
Our group decided to implement a client-server architecture in order to improve the performance of the application.
In order to measure and compare the performance of the two types of architectures, we created a metric that tracks the 
frequency of a function being called, the total time taken, and the average time taken to complete the function.

The image below shows the result of the experiment on average:
![](https://i.imgur.com/F5IzCph.png)

### Conclusion
While the results show that there is little to no evidence suggesting that the client-server architecture improves the 
performance of the application, our group believe that this is due to the small scale of the application.
As the size of the application and the number of users increase, we are confident that we will see more obvious performance improvements.


### Misc Notes
When an order is purchased, the drone will go to the Store to pickup the order and then will move to the Customer to drop off the Order. This process is not instantaneous, so the notification that the Order has been delivered will occur later in time than when the Order is purchased.
The same thing applies to delivering fuel from one drone to another.