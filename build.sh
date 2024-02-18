SINOPAC_ROOT=/usr/AP
SINOPAC_PATH=${SINOPAC_ROOT}/sinopac
SINOPAC_LOG_PATH=${SINOPAC_PATH}/logs

PID=$(ps -ef | grep java | grep api | awk '{print $2}')

echo rebooting please wait...

kill -15 $PID

git --work-tree=${SINOPAC_PATH} pull

cd ${SINOPAC_PATH} 

touch nohup.out

mvn clean install

mkdir -p ${SINOPAC_LOG_PATH}
sudo touch ${SINOPAC_LOG_PATH}/info.log

cd ${SINOPAC_PATH}/target

sudo nohup java \
	 -jar api.jar --spring.profiles.active=dev \
	 > /dev/null 2>&1 &
	 
#tail -f ${SINOPAC_LOG_PATH}/info.log