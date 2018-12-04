#!/usr/bin/env bash
# Generate the docs
gradle dokka

# Create a temp folder
mkdir api-docs
cd api-docs

# Clone the project in the temp folder
git clone git@github.com:cspath1/RT-Contracts.git
cd RT-Contracts

# Go to the GitHub pages branch
git checkout gh-pages

# If the docs folder exists, copy contents into gh-pages branch
# and commit and push the files
cd ../..
if [ -d "docs" ]; then
    cp -R docs/. api-docs/RT-Contracts
    cd api-docs/RT-Contracts
    git add .
    git commit -m "update docs"
    git push
fi

# Delete the temporary folder
cd ../..
rm -rf api-docs

# Delete the docs folder
rm -rf docs