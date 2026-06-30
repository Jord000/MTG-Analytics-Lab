#!/bin/sh

set -e

AUTH="admin:MyStrongPassword123!"
INDEX="mtg-game-entries"
BASE_URL="http://opensearch:9200"

echo "🚀 Starting OpenSearch dev bootstrap..."
echo "Target: $BASE_URL"

# --------------------------------------------
# 1. Wait for OpenSearch to be ready
# --------------------------------------------
echo "⏳ Waiting for OpenSearch cluster..."

until curl -s -u "$AUTH" "$BASE_URL/_cluster/health" | grep -q '"status"'
do
  sleep 2
done

echo "✅ OpenSearch is responding"

# --------------------------------------------
# 2. Delete existing index
# --------------------------------------------
echo "🧨 Deleting existing index (if any)..."

curl -s -X DELETE "$BASE_URL/$INDEX" -u "$AUTH" >/dev/null || true

# --------------------------------------------
# 3. Create index
# --------------------------------------------
echo "📦 Creating index with mapping..."

curl -s -X PUT "$BASE_URL/$INDEX" \
    -u "$AUTH" \
    -H "Content-Type: application/json" \
    -d '{
      "mappings": {
        "properties": {
              "player": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword"
                  }
                }
              },
              "commander": {
                "type": "text",
                "fields": {
                  "keyword": {
                    "type": "keyword"
                  }
                }
              },
              "colorIdentity": {
                "type": "keyword"
              },
              "win": {
                "type": "boolean"
              },
              "numberOfTurnsPlayed": {
                "type": "long"
              },
              "createdAt": {
                "type": "date"
        }
      }
    }
  }' >/dev/null

# --------------------------------------------
# 4. Verify mapping
# --------------------------------------------
echo "🔍 Verifying mapping..."

COLOR_TYPE=$(curl -s -u "$AUTH" "$BASE_URL/$INDEX/_mapping" \
    | grep -o '"colorIdentity":{"type":"keyword"}')

if [ -z "$COLOR_TYPE" ]; then
    echo "❌ Mapping verification failed."
    curl -s -u "$AUTH" "$BASE_URL/$INDEX/_mapping"
    exit 1
fi

echo "✅ Mapping verified"

# --------------------------------------------
# 5. Bulk import
# --------------------------------------------
echo "🌱 Loading seed data..."

BULK_RESPONSE=$(curl -s \
    -u "$AUTH" \
    -X POST "$BASE_URL/$INDEX/_bulk" \
    -H "Content-Type: application/json" \
    --data-binary @/seed/testgames.ndjson)

if echo "$BULK_RESPONSE" | grep -q '"errors":true'; then
    echo "❌ Bulk import failed."
    echo
    echo "$BULK_RESPONSE"
    exit 1
fi

echo "✅ Bulk import successful"

# --------------------------------------------
# 6. Refresh index
# --------------------------------------------
echo "🔄 Refreshing index..."

curl -s \
    -u "$AUTH" \
    -X POST "$BASE_URL/$INDEX/_refresh" >/dev/null

# --------------------------------------------
# 7. Count documents
# --------------------------------------------
COUNT_RESPONSE=$(curl -s -u "$AUTH" "$BASE_URL/$INDEX/_count")

COUNT=$(echo "$COUNT_RESPONSE" | sed -n 's/.*"count":[ ]*\([0-9]*\).*/\1/p')

echo "📊 Indexed documents: $COUNT"

if [ "$COUNT" -eq 0 ]; then
    echo
    echo "❌ Count returned zero after successful bulk import."
    echo
    echo "Indices:"
    curl -s -u "$AUTH" "$BASE_URL/_cat/indices?v"

    echo
    echo "Search response:"
    curl -s -u "$AUTH" "$BASE_URL/$INDEX/_search?size=1"

    exit 1
fi

echo "🎉 Bootstrap complete. System ready."