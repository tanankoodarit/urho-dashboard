#!/usr/bin/env bash
if [[ $# -eq 0 ]] ; then
    echo 'Provide bucket name as argument'
    exit 1
fi

lein clean

lein cljsbuild once min

cd resources/public

gsutil rsync -R . gs://$1

cd ../..