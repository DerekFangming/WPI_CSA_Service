#!/bin/bash
#
# Backup a Postgresql database into a daily file.
#

sleep 5

BACKUP_DIR=$1
FILE_NAME=$2
TOOL_DIR=$3
DB_NAME=$4
USER=$5

#DAYS_TO_KEEP=31
#FILE_SUFFIX=_pg_backup.sql


OUTPUT_FILE=${BACKUP_DIR}/${FILE_NAME}

# do the database backup (dump)
# use this command for a database server on localhost. add other options if need be.
${TOOL_DIR}/pg_dump -U ${USER} ${DB_NAME} -F p -f ${OUTPUT_FILE}

# gzip the mysql database dump file
gzip -f $OUTPUT_FILE

# show the user the result
echo "${OUTPUT_FILE}.gz was created:"
#ls -l ${OUTPUT_FILE}.gz

# prune old backups
#find $BACKUP_DIR -maxdepth 1 -mtime +$DAYS_TO_KEEP -name "*${FILE_SUFFIX}.gz" -exec rm -rf '{}' ';'