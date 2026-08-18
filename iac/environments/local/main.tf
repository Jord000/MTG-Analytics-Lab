module "mtg_namespace" {
  source = "../../modules/namespaces"

  name = "mtg-analytics"

  labels = {
    environment = "local"
    application = "mtg-analytics"
  }
}

module "game_api_config" {
  source = "../../modules/config"

  name      = "game-api-config"
  namespace = module.mtg_namespace.name

  data = {
    SPRING_PROFILES_ACTIVE                   = "dev"
    SPRING_APPLICATION_NAME                  = "game-api"
    GAME_SERVICE_MTG_GAME_ENTRIES_INDEX_NAME = "mtg-game-entries"
    GAME_SERVICE_MOST_RECENT_ENTRY_AMOUNT    = "50"
  }
}

module "opensearch_secret" {
  source = "../../modules/secrets"

  name      = "opensearch-credentials"
  namespace = module.mtg_namespace.name

  data = {
    username = "admin"
    password = var.opensearch_admin_password
  }
}


module "opensearch_storage" {
  source = "../../modules/storage"

  name      = "opensearch-data"
  namespace = module.mtg_namespace.name

  storage_size  = "5Gi"
  storage_class = "standard"
}