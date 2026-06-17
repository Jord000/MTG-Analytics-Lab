#!/bin/sh

AUTH="admin:MyStrongPassword123!"

echo "Using local Opensearch seed script to load test data into OpenSearch container"
echo "at http://opensearch:9200"
echo "Waiting for OpenSearch..."

until curl -s -u "$AUTH" http://opensearch:9200/_cluster/health | grep -q '"status"'
do
  sleep 2
done

echo "Checking index..."

STATUS=$(curl -s -o /dev/null -w "%{http_code}" -u "$AUTH" \
  http://opensearch:9200/mtg-game-entries)

if [ "$STATUS" = "404" ]; then
  echo "Creating index..."

  curl -X PUT http://opensearch:9200/mtg-game-entries -u "$AUTH" \
    -H "Content-Type: application/json" \
    -d '{}'
else
  echo "Index already exists"
fi

echo "Loading seed data..."

curl -X POST http://opensearch:9200/mtg-game-entries/_bulk -u "$AUTH" \
  -H "Content-Type: application/json" \
  --data-binary @/seed/testgames.ndjson

echo "Complete"
