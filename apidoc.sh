rm -rf resources/www
rm README-API.md
npm install apidoc
npm install apidoc-markdown2
./node_modules/.bin/apidoc -i src -o ./resources/www
./node_modules/.bin/apidoc-markdown2 -p ./resources/www -o README-API.md
