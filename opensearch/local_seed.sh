#!/bin/sh

echo "Using local Opensearch seed script to load test data into OpenSearch container"
echo "at http://opensearch:9200"
echo "Waiting for OpenSearch..."

until curl -s http://opensearch:9200 >/dev/null
do
  sleep 2
done

echo "Creating index..."

curl -X PUT http://opensearch:9200/game-entries \
  -H "Content-Type: application/json" \
  -d '{}'

echo "Loading seed data..."

curl -X POST http://opensearch:9200/game-entries/_bulk \
  -H "Content-Type: application/json" \
  --data-binary @/seed/testgames.ndjson

echo "Complete"