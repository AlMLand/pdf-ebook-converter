# Install sequence



### 1. K8s-namespaces chart

This chart creates the required namespaces in the k8s cluster.

```
helm install k8s-namespaces k8s-namespaces/
```



### 2. Kube-prometheus-stack chart

This chart deploys the kube-prometheus-stack, which includes Prometheus, Alertmanager, Grafana, and various exporters.

- namespace = `monitoring`
- grafana username and password are overridden in `values.yaml`

```
# if repository is not present
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts

helm repo update

# install kube-prometheus-stack chart with custom values
helm install kube-prometheus-stack prometheus-community/kube-prometheus-stack --version 75.10.0 -n monitoring --values=./kube-prometheus-stack/values.yaml

# uninstall kube-prometheus-stack chart
helm uninstall kube-prometheus-stack -n monitoring
```



### 3. Reloader chart

This chart deploys the reloader.

- namespace = `reloader`

```
# if repository is not present
helm repo add stakater https://stakater.github.io/stakater-charts

helm repo update

helm install reloader stakater/reloader -n reloader
```



### 4. Ingress-NGINX chart

Install Ingress-NGINX using locally chart and custom values:

- namespace = `ingress-nginx`

```
# add the ingress-nginx repository
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm repo update

helm install ingress-nginx-controller ingress-nginx/ -n ingress-nginx
```



### 5. PostgreSQL chart

Install PostgreSQL using locally chart and custom values:

- namespace = `postgresql`

```
# add the ingress-nginx repository
helm repo add bitnami https://charts.bitnami.com/bitnami

helm repo update

helm install pdf-postgresql postgresql/ --values=./postgresql/custom-values.yaml -n postgresql
```



### 6. PDF-Converter chart

Before you begin to install the PDF Converter, create an `pdf-converter-ingress-credentials` secret in your Kubernetes cluster.

Create authentication secret

```
kubectl create secret generic pdf-converter-ingress-credentials --from-file=./pdf-converter/auth
```

Credentials

- username : admin
- password : admin

Install PDF Converter with Helm

- namespace = `integration` or `production`, depending on your environment.
To activate the integration or production environment, use the `--values` flag to specify the appropriate values file.

```
helm install pdf-converter pdf-converter/ `WITH` -n integration --values=./pdf-converter/values-integration.yaml `OR` -n production --values=./pdf-converter/values-production.yaml
```