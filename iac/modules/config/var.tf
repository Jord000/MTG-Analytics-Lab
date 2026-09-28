variable "name" {
  description = "Name of the ConfigMap"
  type        = string
}

variable "namespace" {
  description = "Namespace containing the ConfigMap"
  type        = string
}

variable "data" {
  description = "Configuration values"
  type        = map(string)
}