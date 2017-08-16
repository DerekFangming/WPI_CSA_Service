#!/bin/bash
#
# Backup a Postgresql database into a daily file.
#

sleep 5

BACKUP_DIR=/Users/Cyan/Documents/pg_backup
DAYS_TO_KEEP=0
FILE_SUFFIX=_pg_backup.sql
DATABASE=projectNing11
USER=postgres

FILE=`date +"%d"`${FILE_SUFFIX}

OUTPUT_FILE=${BACKUP_DIR}/${FILE}

# do the database backup (dump)
# use this command for a database server on localhost. add other options if need be.
/Library/PostgreSQL/9.5/bin/pg_dump -U ${USER} ${DATABASE} -F p -f ${OUTPUT_FILE}

# gzip the mysql database dump file
gzip -f $OUTPUT_FILE

# show the user the result
echo "${OUTPUT_FILE}.gz was created:"
ls -l ${OUTPUT_FILE}.gz

# prune old backups
find $BACKUP_DIR -maxdepth 1 -mtime +$DAYS_TO_KEEP -name "*${FILE_SUFFIX}.gz" -exec rm -rf '{}' ';'