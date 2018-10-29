mkdir api-docs
cd api-docs
git clone git@github.com:cspath1/RT-Contracts.git
cd RT-Contracts
git checkout gh-pages
cd ../..
if [ -d "docs" ]; then
    cp -R docs/. api-docs/RT-Contracts
    cd api-docs/RT-Contracts
    git add .
    git commit -m "update docs"
    git push
fi

cd ../..
rm -rf api-docs