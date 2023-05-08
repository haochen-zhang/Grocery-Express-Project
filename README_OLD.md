A3 Repo for CS6310 Summer 2022
# Update history
```
1) add Point class and add location to Store and Customer
2) update Drone to support refuelRate fuelUseRate and speed
3) add class SolarPoweredDrone and its constructor
4) add class RefuelingStation and implement its DeliveryService make/display functions
5) add methods to adjust location / remove RefuelingStation
6) add method in Store class to find the nearest refuelingStation
```
# To Install Docker go to:
```
https://docs.docker.com/get-docker/
```

# Note please run all scripts from the project root directory

### To build:

```
docker build -t gatech/dronedelivery -f Dockerfile ./
```

### To test a specific scenario against the initial jar
#### Mac / Linux
```
./scripts/test.sh <scenario>
```
#### Windows
```
.\scripts\test.sh <scenario>
```

### To batch run the test scenarios
#### Mac / Linux
```
./scripts/batch.sh
```
#### Windows
```
.\scripts\batch.sh
```

### To run in interactive mode
#### Step 1 from the host
```
docker run -ti gatech/dronedelivery sh
```
#### Step 2 from the container
```
java -jar drone_delivery.jar
```
#### Step 3 from the jar
* From there you can run any of the commands listed from the assignment:
```
make_store,<Store>,<InitialRevenue>
display_stores
sell_item,<Store>,<Item>,<Weight>
display_items,<Store>
make_pilot,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<TaxId>,<LicenseId>,<ExperienceLevel>
display_pilots
make_drone,<Store>,<DroneId>,<WeightCapacity>,<NumberOfDeliveries>
display_drones,<Store>
fly_drone,<Store>,<DroneId>,<PilotAccount>
make_customer,<Account>,<FirstName>,<LastName>,<PhoneNumber>,<CustomerRating>,<Credits>
display_customers
start_order,<Store>,<OrderId>,<DroneId>,<CustomerAccount>
display_orders,<Store>
request_item,<Store>,<OrderId>,<Item>,<Quantity>,<UnitPrice>
purchase_order,<Store>,<OrderId>
cancel_order,<Store>,<OrderId>
transfer_order,<Store>,<OrderId>,<DroneId>
display_efficiency
stop
```

### To run & test in interactive mode

```
java -jar drone_delivery.jar < commands_00.txt > drone_delivery_00_results.txt
diff -s drone_delivery_00_results.txt drone_delivery_initial_00_results.txt > results_00.txt
cat results_00.txt
```

### To run a specific scenario with your jar and output to localhost
The "mkdir docker_results ; " would not be needed after the 1st run, but just in case you have not made the directory yet with another command. 
```
mkdir docker_results ; docker run gatech/dronedelivery sh -c 'java -jar drone_delivery.jar < commands_00.txt'  > docker_results/drone_delivery_00_results.txt
```

### If you get stuck in an infinite loop
Simply stop and remove the running container
```
docker ps
docker rm -f <container_id>
```

### To test with a clean image & container
After running the below command you will need to run the build command again
#### Windows
```
docker ps -aq | % { docker stop $_ } | % { docker rm -f $_ } | docker images -f "dangling=true" -aq | % { docker rmi -f $_ } | docker images gatech/* -aq | % { docker rmi -f $_ }
```
#### Mac
```
docker ps -aq | xargs docker stop | xargs docker rm -f && docker images -f "dangling=true" -aq | xargs docker rmi -f && docker images "gatech/*" -aq | xargs docker rmi -f
```

### To zip your code
You should validate your zip file has everything needed. Your src folder & everything under your src folder. Nothing else should be in your zip file. < filename > should be replaced with 2022-09-A3 
#### Mac / Linux
```
./scripts/zip.sh <filename>
```
#### Windows
```
.\scripts\zip.ps1 <filename>
```

### Goals for completion
```
This section is new & may be updated as the assignment progresses as needed.
1) All your test files that exist should come back with they are identical when doing the diff
2) EX: 'Files drone_delivery_00_results.txt and drone_delivery_initial_00_results.txt are identical'
3) The files that are hidden and not proviced yet (The missing ones out of the 65 total) will come back like the below when running the batch script
4) EX: 'File commands_10.txt does not exist.'
5) You should not need to or change the diff commands or docker files or any file outside your src directory
6) Double check your line endings in your testing / result files if needed, everything should have just LF not CLRF 
```

### Updated test commands
```
1) make a store
make_store,kroger,33000,1,1
> make_store,kroger,33000,1,1
OK:change_completed

2) display stores
display_stores
> display_stores
name:kroger,revenue:33000,location:(1,1)
OK:display_completed

3) make a customer
make_customer,aapple2,Alana,Apple,222-222-2222,4,100,1,1
> make_customer,aapple2,Alana,Apple,222-222-2222,4,100,1,1
OK:change_completed

4) display customers
display_customers
> display_customers
name:Alana_Apple,phone:222-222-2222,rating:4,credit:100,location(1,1)
OK:display_completed

5) make a drone for store
make_store,kroger,33000,1,1
> make_store,kroger,33000,1,1
OK:change_completed
make_drone,kroger,1,40,3,10,5,3
> make_drone,kroger,1,40,3,10,5,3
OK:change_completed

6) display drones for store
display_drones,kroger
> display_drones,kroger
droneID:1,total_cap:40,num_orders:0,remaining_cap:40,trips_left:3,refuel_rate:10,fuel_use_rate:5,speed:3
OK:display_completed

7) make a refueling station for store and duplication detection
make_store,kroger,33000,1,1
> make_store,kroger,33000,1,1
OK:change_completed
make_refueling_station,kroger,1,2,3,4
> make_refueling_station,kroger,1,2,3,4
OK:change_completed
make_refueling_station,kroger,1,5,6,7
> make_refueling_station,kroger,1,5,6,7
ERROR:refueling_station_identifier_already_exists

8) display refueling station for store
display_refueling_stations,kroger
> display_refueling_stations,kroger
id:1,location:(3,4),capacity:2
OK:display_completed

9) adjust the location of refueling station
adjust_refueling_station,kroger,1,1,1
> adjust_refueling_station,kroger,1,1,1
OK:change_completed

10) remove refueling station
remove_refueling_station,kroger,1
> remove_refueling_station,kroger,1
OK:change_completed
```

### How to run with new client-server architecture
With the changes that I have added in, you now must launch both a client (client.jar) and a server (server.jar).

Here is how it works:

1) Build the docker image (ex. `docker build -t gatech/groupproject -f Dockerfile ./`)
2) Run the new container interactively (ex. `docker run -ti gatech/groupproject bash`)
3) We need to run the client and server in separate terminal windows. To do this I have already included tmux. Here's how to use it:
4) Run `tmux`. This will open up a new tmux session that has a green bar at the bottom. Note that we are currently on window 0.
5) `Ctrl + B`, `C` to create a new window 1. 
6) `Ctrl + B`, `0` to switch back to window 0. 
7) Run `/usr/local/openjdk-17/bin/java -jar server.jar`. Note that we must specify the full path to java, have not found a workaround for that so far. This will launch the server.
8) `Ctrl + B`, `1` to swtich back to window 1.
9) Run `/usr/local/openjdk-17/bin/java -jar client.jar`. This will launch a client and will automatically connect to the server. Now you may run commands as the client. 
10) To quit, right now just use `Ctrl + C` since quitting is broken (working on it).
11) To leave tmux, you can either keep typing `exit` or use `Ctrl + D` to detach from the session.
12) Note that you can create multiple clients at once as well, if you'd like.

```
Commands

/usr/local/openjdk-17/bin/java -jar


NORMAL DRONE CLOSE DELIVERY
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,1,0
make_drone,kroger,1,40,3,3,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1670101149

SOLAR DRONE CLOSE DELIVERY
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,1,0
make_drone,kroger,1,40,3,3,1,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1670101149

NORMAL DRONE FAR DELIVERY (TOO FAR)
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,21,20
make_drone,kroger,1,40,3,3,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1670101149

SOLAR DRONE FAR DELIVERY
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,21,20
make_drone,kroger,1,40,3,3,1,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1670101149

NORMAL DRONE (AUTO-REFUEL WHEN LOW)
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,1,0
make_drone,kroger,1,40,2,3,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1670101149

DELIVER_FUEL
make_store,kroger,33000,0,0
make_customer,aapple2,Alana,Apple,2,4,100,1,0
make_drone,kroger,1,40,3,3,1
make_drone,kroger,2,40,3,3,1
sell_item,kroger,pear,1
make_pilot,1,a,b,1,1,1,1
fly_drone,kroger,1,1
start_order,kroger,1,1,aapple2
request_item,kroger,1,pear,1,1
purchase_order,kroger,1,1680101149
deliver_fuel,kroger,2,1

```

DOCKER SETUP (from root dir)

To build server: `docker build -t gatech/groupproject/server -f Server.docker ./`

To build client: `docker build -t gatech/groupproject/client -f Client.docker ./`

To run server: `docker run -d gatech/groupproject/server`

The previous command will return a container ID, use this in the subsequent command

To get IP of server: `docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' <containerID>`

To interactively run client: `docker run -ti gatech/groupproject/client bash`

To connect while in client: `java -jar client.jar <serverIP>`

Multiple clients may be ran simultaneously.

### Assignment Q&A Post
Please post any questions about docker or otherwise to the post linked below:  
[Link To Ed Discussion](https://edstem.org/us/courses/25307/discussion/1842797)
