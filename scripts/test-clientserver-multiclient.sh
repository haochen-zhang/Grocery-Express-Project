#!/usr/bin/env bash

DIRECTORY="./docker_results"
COMMAND1="commands_multiclient1.txt"
COMMAND2="commands_multiclient2.txt"
COMMAND3="commands_multiclient3.txt"
RESULTS1="drone_delivery_clientserver_multiclient1_results.txt"
RESULTS2="drone_delivery_clientserver_multiclient2_results.txt"
RESULTS3="drone_delivery_clientserver_multiclient3_results.txt"
INITIAL1="drone_delivery_initial_clientserver_multiclient1_results.txt"
INITIAL2="drone_delivery_initial_clientserver_multiclient2_results.txt"
INITIAL3="drone_delivery_initial_clientserver_multiclient3_results.txt"
DIFFERENCE1="diff_results_clientserver_multiclient1.txt"
DIFFERENCE2="diff_results_clientserver_multiclient2.txt"
DIFFERENCE3="diff_results_clientserver_multiclient3.txt"

mkdir -p ${DIRECTORY}

if [[  -f "./test_scenarios/${COMMAND1}" ]]; then
  echo "Building containers"
  docker build -t gatech/groupproject/client -f Client.docker ./
  docker build -t gatech/groupproject/server -f Server.docker ./

  echo "Starting server"
  SERVER_ID=$(docker run --name dronedeliveryserver -d gatech/groupproject/server sh -c "java -jar server.jar")

  echo "Started server ID=${SERVER_ID}, getting IP"
  SERVER_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${SERVER_ID})

  echo "Got server IP=${SERVER_IP}, starting client1 and running test"
  docker run --name dronedeliveryclient1 -d gatech/groupproject/client sh -c "java -jar client.jar ${SERVER_IP} < ${COMMAND1} > ${RESULTS1}"

  echo "starting client2 and running test"
  docker run --name dronedeliveryclient2 -d gatech/groupproject/client sh -c "java -jar client.jar ${SERVER_IP} < ${COMMAND2} > ${RESULTS2}"

  echo "starting client3 and running test"
  docker run --name dronedeliveryclient3 -d gatech/groupproject/client sh -c "java -jar client.jar ${SERVER_IP} < ${COMMAND3} > ${RESULTS3}"

  echo "Waiting 30 seconds for commands to complete execution"
  sleep 30

  echo "Done waiting, copying results"
  docker cp dronedeliveryclient1:/usr/src/cs6310/${RESULTS1} ${DIRECTORY}
  docker cp dronedeliveryclient2:/usr/src/cs6310/${RESULTS2} ${DIRECTORY}
  docker cp dronedeliveryclient3:/usr/src/cs6310/${RESULTS3} ${DIRECTORY}

  echo "Cleanup"
  docker rm dronedeliveryserver > /dev/null
  docker rm dronedeliveryclient1 > /dev/null
  docker rm dronedeliveryclient2 > /dev/null
  docker rm dronedeliveryclient3 > /dev/null

  diff -s "${DIRECTORY}/${RESULTS1}" "test_results/${INITIAL1}" > "${DIRECTORY}/${DIFFERENCE1}" 
  diff -s "${DIRECTORY}/${RESULTS2}" "test_results/${INITIAL2}" > "${DIRECTORY}/${DIFFERENCE2}" 
  diff -s "${DIRECTORY}/${RESULTS3}" "test_results/${INITIAL3}" > "${DIRECTORY}/${DIFFERENCE3}" 

  FILE_CONTENTS1="${DIRECTORY}/${DIFFERENCE1}"
  FILE_CONTENTS2="${DIRECTORY}/${DIFFERENCE2}"
  FILE_CONTENTS3="${DIRECTORY}/${DIFFERENCE3}"
  echo "$(cat ${FILE_CONTENTS1})"
  echo "$(cat ${FILE_CONTENTS2})"
  echo "$(cat ${FILE_CONTENTS3})"
else
    echo "File ${COMMAND1} or ${COMMAND2} or ${COMMAND3} does not exist."
fi