#!/bin/sh

set -e

AUTH="admin:MyStrongPassword123!"
BASE_URL="http://dashboards:5601"

echo "Waiting for Dashboards..."

until curl -sf \
  -u "$AUTH" \
  -H "osd-xsrf: true" \
  "$BASE_URL/api/status" >/dev/null
do
    sleep 2
done

sleep 5

echo "Importing dashboard..."

RESPONSE=$(curl -s \
  -X POST \
  -u "$AUTH" \
  -H "osd-xsrf: true" \
  --form 'file=@/dashboards/dashboards.ndjson' \
  "$BASE_URL/api/saved_objects/_import?overwrite=true")

echo "$RESPONSE"

echo "$RESPONSE" | grep '"success":true' >/dev/null || {
    echo "❌ Dashboard import failed"
    exit 1
}

echo "✅ Dashboard imported successfully"