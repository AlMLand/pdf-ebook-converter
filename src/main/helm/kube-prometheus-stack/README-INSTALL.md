# Kube-prometheus-stack chart

## This chart deploys the kube-prometheus-stack, which includes Prometheus, Alertmanager, Grafana, and various exporters.

- version: 75.10.0
- grafana username and password are overridden in `values.yaml`

```
# if repository is not present
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

helm repo update

helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack --version 75.10.0
```
