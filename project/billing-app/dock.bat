docker build -t pr-billing-app .
docker tag pr-billing-app tvv766/pr-billing-app
docker push tvv766/pr-billing-app