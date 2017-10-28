# Urho dashboard

Dashboard for iot devices and jaunasallu.

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Production Build


To compile clojurescript to javascript:

```
lein clean
lein cljsbuild once min
```

# Google cloud
## Create bucket
`gsutil mb -c coldline gs://<bucketname>`


## copy stuff to cloud
`gsutil cp -r . gs://<bucketname>`

`gsutil rsync -R . gs://<bucketname>`

## make it public
`gsutil iam ch allUsers:objectViewer gs://<bucketname>`

## Make web site index file
`gsutil web set -m index.html gs://<bucketname>`



