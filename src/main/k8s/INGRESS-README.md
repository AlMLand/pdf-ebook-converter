# Ingress for Kubernetes

1. https://kubernetes.github.io/ingress-nginx/deploy/
    - before applying this ingress, install nginx ingress controller with:
       ```
       kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.3/deploy/static/provider/cloud/deploy.yaml
       ```

       ```
       kubectl delete -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/controller-v1.12.3/deploy/static/provider/cloud/deploy.yaml
       ```

2. create an admin secret

# Create authentication secret

```
kubectl create secret generic ingress-credentials --from-file=auth
```

### Credentials

- username : admin
- password : admin
