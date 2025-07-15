# Install PostgreSQL with Helm

- Apply secret for PostgreSQL credentials: `postgresql-credentials` in the `./k8s` folder.

- Install PostgreSQL using locally chart and custom values:

```
helm install pdf-postgresql postgresql/ --values=./postgresql/custom-values.yaml 
```
