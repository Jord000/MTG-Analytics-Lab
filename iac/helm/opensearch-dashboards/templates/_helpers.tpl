{{/*
Expand the name of the chart.
*/}}
{{- define "opensearch-dashboards.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "opensearch-dashboards.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- include "opensearch-dashboards.name" . }}
{{- end }}
{{- end }}

{{/*
Common labels.
*/}}
{{- define "opensearch-dashboards.labels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
{{ include "opensearch-dashboards.selectorLabels" . }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels.
*/}}
{{- define "opensearch-dashboards.selectorLabels" -}}
app.kubernetes.io/name: {{ include "opensearch-dashboards.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}