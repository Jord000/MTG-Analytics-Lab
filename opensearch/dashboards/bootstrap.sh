#!/bin/sh

set -e

echo "Waiting for Dashboards..."

until curl -s http://dashboards:5601/api/status >/dev/null
do
    sleep 2
done

echo "Importing dashboard..."

curl \
  -X POST \
  http://dashboards:5601/api/saved_objects/_import?overwrite=true \
  -H "osd-xsrf: true" \
  --form file=@/dashboards/exports/commander-meta-dashboard.ndjson

echo "Dashboard imported!"