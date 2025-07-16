# Install Ingress-NGINX with Helm

Install Ingress-NGINX using locally chart and custom values:

```
# add the ingress-nginx repository
helm repo add ingress-nginx https://kubernetes.github.io/ingress-nginx

helm install ingress-nginx-controller ingress-nginx/
```
