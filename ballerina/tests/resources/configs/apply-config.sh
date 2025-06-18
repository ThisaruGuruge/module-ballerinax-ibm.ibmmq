#!/bin/bash

echo "Waiting for QMGR to be ready..."
until echo "DISPLAY QMSTATUS" | runmqsc QM1 > /dev/null 2>&1; do
    echo "Queue Manager not ready yet..."
    sleep 2
done

echo "Applying MQSC config scripts..."
runmqsc QM1 < /etc/configs/config.mqsc
runmqsc QM1 < /etc/configs/topic-config.mqsc

tail -f /dev/null
