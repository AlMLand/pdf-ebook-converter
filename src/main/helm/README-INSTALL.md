# Install sequence

1. Kube-prometheus-stack chart

### This chart deploys the kube-prometheus-stack, which includes Prometheus, Alertmanager, Grafana, and various exporters.

- grafana username and password are overridden in `values.yaml`

```
# if repository is not present
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

helm repo update

helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack --version 75.10.0 -n {{NAMESPACE}}
```

2. Reloader chart

### This chart deploys the reloader.

```
# if repository is not present
helm repo add stakater https://stakater.github.io/stakater-charts

helm repo update

helm install reloader stakater/reloader -n {{NAMESPACE}}
```
3. Ingress-NGINX chart

### Install Ingress-NGINX using locally chart and custom values:

```
# add the ingress-nginx repository
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

helm install ingress-nginx-controller ingress-nginx/
```

4. PostgreSQL chart

### Install PostgreSQL using locally chart and custom values:

```
# add the ingress-nginx repository
helm repo add bitnami https://charts.bitnami.com/bitnami

helm repo update

helm install pdf-postgresql postgresql/ --values=./postgresql/custom-values.yaml 
```

5. PDF-Converter chart

## Before you begin to install the PDF Converter, create an `pdf-converter-ingress-credentials` secret in your Kubernetes cluster.

### Create authentication secret

```
kubectl create secret generic pdf-converter-ingress-credentials --from-file=auth
```

### Credentials

- username : admin
- password : admin

### Install PDF Converter with Helm

```
helm install pdf-converter pdf-converter/
```