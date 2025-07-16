# Before you begin to install the PDF Converter, create an `pdf-converter-ingress-credentials` secret in your Kubernetes cluster.

## Create authentication secret

```
kubectl create secret generic pdf-converter-ingress-credentials --from-file=auth
```

### Credentials

- username : admin
- password : admin

## Install PDF Converter with Helm

```
helm install pdf-converter pdf-converter/
```