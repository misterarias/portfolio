ES_ADDRESS=http://${ES_MASTER}:9200

es-cluster-state:
	@echo -e "${GREENCOLOR}+++ Getting ES cluster state${ENDCOLOR}" ;\
	curl -XGET ${ES_ADDRESS}/_cluster/state?pretty=1

es-search:
	@echo -e "${GREENCOLOR}+++ Getting ES items${ENDCOLOR}";\
	curl -XPOST ${ES_ADDRESS}/_search?pretty=1

es-search-%:
	@echo -e "${GREENCOLOR}+++ Getting ES items that match '$(subst es-search-,,$@)'${ENDCOLOR}" ;\
	curl -XPOST ${ES_ADDRESS}/_search?q=$(subst es-search-,,$@)&pretty=1

es-register-snapshot:
	@echo -e "${GREENCOLOR}+++ Registering ES snapshot 'my-backup''${ENDCOLOR}" ;\
	curl -XPUT ${ES_ADDRESS}/_snapshot/my_backup -d '{ "type": "fs", "settings": { "location": "/data/backups/my_backup", "compress": true } }'

es-save-snapshot:
	@echo -e "${GREENCOLOR}+++ Saving current data to 'snapshot_1' in 'my-backup''${ENDCOLOR}" ;\
	curl -XPUT ${ES_ADDRESS}/_snapshot/my_backup/snapshot_1?wait_for_completion=true

es-get-snapshots:
	@echo -e "${GREENCOLOR}+++ getting all snapshots for 'my-backup''${ENDCOLOR}" ;\
	curl -XGET ${ES_ADDRESS}/_snapshot/my_backup/_all

es-restore-snapshot:
	@echo -e "${GREENCOLOR}+++ restoring snapshot 'snapshot_1' from 'my-backup''${ENDCOLOR}" ;\
	curl -XPOST ${ES_ADDRESS}/_snapshot/my_backup/snapshot_1/_restore
