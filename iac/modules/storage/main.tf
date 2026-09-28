resource "kubernetes_persistent_volume_claim" "this" {
  metadata {
    name      = var.name
    namespace = var.namespace
  }

  spec {
    /*
    REFERENCE
    ReadWriteOnce (RWO): A single node within the cluster can mount the volume, and any Pods residing on that node can use the volume.
    ReadOnlyMany (ROX): Multiple nodes can mount the volume in read-only mode, and Pods residing on those nodes can use it.
    ReadWriteMany (RWX): Multiple nodes can mount the volume in read-write mode, and Pods residing on those nodes can use it.
    ReadWriteOncePod (RWOP): Only a single Pod is allowed to use the volume.
    */
    access_modes       = ["ReadWriteOnce"]
    storage_class_name = var.storage_class

    resources {
      requests = {
        storage = var.storage_size
      }
    }
  }
}