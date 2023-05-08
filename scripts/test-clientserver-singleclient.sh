#!/usr/bin/env bash

SCENARIO=$1
DIRECTORY="./docker_results"
COMMAND="commands_${SCENARIO}.txt"
RESULTS="drone_delivery_clientserver_${SCENARIO}_results.txt"
INITIAL="drone_delivery_initial_clientserver_${SCENARIO}_results.txt"
DIFFERENCE="diff_results_clientserver_${SCENARIO}.txt"

mkdir -p ${DIRECTORY}

if [[  -f "./test_scenarios/${COMMAND}" ]]; then
  echo "Building containers"
  docker build -t gatech/groupproject/client -f Client.docker ./
  docker build -t gatech/groupproject/server -f Server.docker ./

  echo "Starting server"
  SERVER_ID=$(docker run --name dronedeliveryserver -d gatech/groupproject/server sh -c "java -jar server.jar")

  echo "Started server ID=${SERVER_ID}, getting IP"
  SERVER_IP=$(docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' ${SERVER_ID})

  echo "Got server IP=${SERVER_IP}, starting client and running test"
  docker run --name dronedeliveryclient -d gatech/groupproject/client sh -c "java -jar client.jar ${SERVER_IP} < ${COMMAND} > ${RESULTS}"

  echo "Waiting 30 seconds for commands to complete execution"
  sleep 30

  echo "Done waiting, copying results"
  docker cp dronedeliveryclient:/usr/src/cs6310/${RESULTS} ${DIRECTORY}

  echo "Cleanup"
  docker rm dronedeliveryserver > /dev/null
  docker rm dronedeliveryclient > /dev/null

  diff -s "${DIRECTORY}/${RESULTS}" "test_results/${INITIAL}" > "${DIRECTORY}/${DIFFERENCE}" 

  FILE_CONTENTS="${DIRECTORY}/${DIFFERENCE}"
  echo "$(cat ${FILE_CONTENTS})"
else
    echo "File ${COMMAND} does not exist."
fi