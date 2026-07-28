resource "kubernetes_namespace" "mtg" {
  metadata {
    name = "mtg-analytics"
  }
}