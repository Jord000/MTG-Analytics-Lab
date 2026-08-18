output "name" {
  description = "Name of the PersistentVolumeClaim"
  value       = kubernetes_persistent_volume_claim.this.metadata[0].name
}