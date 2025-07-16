# Reloader chart

## This chart deploys the reloader.

```
# if repository is not present
helm repo add stakater https://stakater.github.io/stakater-charts

helm repo update

helm install reloader stakater/reloader -n {{NAMESPACE}}
```
