#!/bin/bash
#

mkdir -p logs/old

#sys variable for static files
export MONEY_HOME_DIR=${PWD}/statics

PROGRAM="compile exec:java -Dexec.mainClass=money.MoneyController"
CURDATE=$(date +"%Y_%m_%d_%H%M%S")
LOGFILE=logs/money$CURDATE.log
JA_PID=target/money3.pid
PROG_BIN="mvn $PROGRAM"
PID=`ps -aef | grep "$PROGRAM" | grep -v grep | awk '{print $2}'`

#move logs modified more than 24h ago to logs/old folder
find logs/ -maxdepth 1 -type f -mtime +1 -name "*.log*" -exec mv {} logs/old >/dev/null 2>&1 \;


case "$1" in
  start)

    if [ ! -z $PID ]
    then
        echo -n " "
        echo -n "Server money3 is running"
        echo " "
    else
        echo -n "Starting server money3: "
        echo ""
        $PROG_BIN > $LOGFILE 2>&1 &
        echo $! > $JA_PID
        echo "Server money3 started. PID:$!"
        echo "Logfile is : $LOGFILE"
        echo ""
    fi
    ;;
  stop)
    echo -n "Shutting down money3 server: "
    echo ""
    for i in $PID; do
        kill -9 $i
        rm -f $JA_PID
    done
    echo -n "Server money3 is down."
    echo ""
    ;;
  restart)
    $0 stop
    $0 start

    ;;
  status)
    STATUS=`ps -aef | grep "$PROGRAM" | grep -v grep | awk '{print $2}' | head -1`
    if [ ! -z $STATUS ]
    then
        echo -n "Server money3 is running"
        echo ""
        for i in $PID; do
            echo PID:$i
        done
        echo ""
    else
        echo -n "Server money3 is down."
        echo ""
    fi
    ;;
  *)
    echo "Usage: money3.sh {start|stop|restart|status}"
    exit 1

esac

exit 0