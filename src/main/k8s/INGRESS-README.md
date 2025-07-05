# Create authentication secret for admin access

## Ingress Configuration for Kubernetes

```
kubectl create secret generic ingress-credentials --from-file=auth
```

### Credentials

- username : admin
- password : admin
