## Runing services

### Starting gen service

The `gen` service will start with default port bound to `8080` and all interfaces

```bash
sbt gen/run

```

to change the gen host port run with:

```bash
sbt gen/run -DPORT=8080

```

### Starting api service

The `api` service will start with default port bound to `8081` and all interfaces

```bash
sbt api/run

```

to change the `gen` service location or `api` host port run with:

```bash
sbt api/run -DGEN_URL=http://localhost:8080 -DPORT=8081

```

## Api endpoints

```bash
# All data with given size
GET localhost:8081/1000

# Selected fields
GET localhost:8081/fields/_id,longitude/1000

# Simple math computations
GET localhost:8081/compute/sqrt(_id),longitude**2/1000


```

### Note on `/` escaping 
to properly use `/` as a divide use `%2F` like so:
```bash
GET localhost:8081/compute/sqrt(_id),longitude%2F2/1000

```
