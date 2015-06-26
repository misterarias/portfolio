SNAPSHOT_NAME=WORKING

cluster-snapshot:
	@${TOPDIR}/vagrant/snapshot.sh take 1 3 ${SNAPSHOT_NAME}
	
cluster-start:
	@echo -e "${GREENCOLOR}+++ Restoring 3-node cluster${ENDCOLOR}"
	@${TOPDIR}/vagrant/snapshot.sh go 1 3 ${SNAPSHOT_NAME}

cluster-stop:
	@echo -e "${REDCOLOR}+++ Suspending 3-node cluster${ENDCOLOR}"
	@(cd ${TOPDIR}/vagrant && vagrant suspend)

