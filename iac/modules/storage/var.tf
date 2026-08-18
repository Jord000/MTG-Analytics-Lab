variable "name" {
  description = "Name of the PersistentVolumeClaim"
  type        = string
}

variable "namespace" {
  description = "Namespace containing the PersistentVolumeClaim"
  type        = string
}

variable "storage_size" {
  description = "Amount of storage requested"
  type        = string
  default     = "10i"
}

variable "storage_class" {
  description = "Kubernetes storage class"
  type        = string
  // minikube standard SC
  default     = "standard"
}