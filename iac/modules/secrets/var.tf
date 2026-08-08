variable "name" {
  description = "Name of the Kubernetes Secret"
  type        = string
}

variable "namespace" {
  description = "Namespace containing the Secret"
  type        = string
}

variable "data" {
  description = "Secret data"
  type        = map(string)
  sensitive   = true
}